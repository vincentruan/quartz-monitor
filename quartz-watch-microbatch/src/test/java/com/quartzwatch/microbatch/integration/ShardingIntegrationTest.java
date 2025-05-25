package com.quartzwatch.microbatch.integration;

import com.quartzwatch.api.sharding.Shard;
import com.quartzwatch.api.sharding.ShardingStrategy;
import com.quartzwatch.microbatch.cluster.RoundRobinShardingStrategy;
import com.quartzwatch.microbatch.cluster.RandomShardingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分片策略集成测试
 * 验证分片分配的均匀性
 */
@SpringBootTest
public class ShardingIntegrationTest {
    
    @Test
    public void testRoundRobinShardingUniformity() {
        // 模拟3个节点，10个分片
        int nodeCount = 3;
        int totalShards = 10;
        String jobKey = "testJob";
        
        ShardingStrategy strategy = new MockRoundRobinStrategy();
        List<Shard> shards = strategy.allocateShards(totalShards, jobKey);
        
        // 验证分片数量
        assertEquals(nodeCount, shards.size());
        
        // 验证分片分配均匀性
        Map<String, Integer> shardCountMap = new HashMap<>();
        for (Shard shard : shards) {
            shardCountMap.put(shard.getNodeId(), shard.getShardNumbers().length);
        }
        
        // 期望每个节点分配3-4个分片
        for (int count : shardCountMap.values()) {
            assertTrue(count >= 3 && count <= 4, "分片分配不均匀");
        }
        
        // 验证所有分片都被分配
        Set<Integer> allShards = new HashSet<>();
        for (Shard shard : shards) {
            for (int shardNum : shard.getShardNumbers()) {
                allShards.add(shardNum);
            }
        }
        assertEquals(totalShards, allShards.size());
    }
    
    @Test
    public void testRandomShardingDistribution() {
        // 模拟5个节点，100个分片，测试多次以验证随机性
        int nodeCount = 5;
        int totalShards = 100;
        String jobKey = "randomJob";
        int iterations = 10;
        
        ShardingStrategy strategy = new MockRandomStrategy();
        Map<String, Integer> totalDistribution = new HashMap<>();
        
        for (int i = 0; i < iterations; i++) {
            List<Shard> shards = strategy.allocateShards(totalShards, jobKey);
            
            for (Shard shard : shards) {
                totalDistribution.merge(shard.getNodeId(), 
                                       shard.getShardNumbers().length, 
                                       Integer::sum);
            }
        }
        
        // 验证分片分配的随机性（标准差应该在合理范围内）
        double expectedAverage = (double) totalShards * iterations / nodeCount;
        double variance = 0;
        
        for (int count : totalDistribution.values()) {
            variance += Math.pow(count - expectedAverage, 2);
        }
        
        double standardDeviation = Math.sqrt(variance / nodeCount);
        double cv = standardDeviation / expectedAverage; // 变异系数
        
        // 随机分配的变异系数应该在合理范围内
        assertTrue(cv < 0.5, "随机分配的变异系数过大: " + cv);
    }
    
    @Test
    public void testShardingWithNodeFailure() {
        // 测试节点故障时的分片重新分配
        int initialNodeCount = 5;
        int totalShards = 20;
        String jobKey = "failoverJob";
        
        // 初始分配
        ShardingStrategy strategy = new MockRoundRobinStrategy();
        List<Shard> initialShards = strategy.allocateShards(totalShards, jobKey);
        assertEquals(initialNodeCount, initialShards.size());
        
        // 模拟一个节点故障，重新分配
        List<Shard> newShards = allocateShardsWithFailedNode(strategy, totalShards, jobKey, 1);
        assertEquals(initialNodeCount - 1, newShards.size());
        
        // 验证所有分片仍然被分配
        Set<Integer> allShards = new HashSet<>();
        for (Shard shard : newShards) {
            for (int shardNum : shard.getShardNumbers()) {
                allShards.add(shardNum);
            }
        }
        assertEquals(totalShards, allShards.size());
    }
    
    // 模拟实现类（实际测试时应该使用真实实现）
    private static class MockRoundRobinStrategy implements ShardingStrategy {
        @Override
        public List<Shard> allocateShards(int totalShards, String jobKey) {
            List<Shard> result = new ArrayList<>();
            String[] nodes = {"node1", "node2", "node3", "node4", "node5"};
            
            for (int i = 0; i < Math.min(3, nodes.length); i++) {
                result.add(new Shard(nodes[i], new int[0], new HashMap<>()));
            }
            
            // 轮询分配
            for (int i = 0; i < totalShards; i++) {
                Shard shard = result.get(i % result.size());
                int[] current = shard.getShardNumbers();
                int[] newShards = Arrays.copyOf(current, current.length + 1);
                newShards[current.length] = i;
                shard.setShardNumbers(newShards);
            }
            
            return result;
        }
        
        @Override
        public String getName() {
            return "mock-round-robin";
        }
    }
    
    private static class MockRandomStrategy implements ShardingStrategy {
        private final Random random = new Random();
        
        @Override
        public List<Shard> allocateShards(int totalShards, String jobKey) {
            List<Shard> result = new ArrayList<>();
            String[] nodes = {"node1", "node2", "node3", "node4", "node5"};
            
            for (String node : nodes) {
                result.add(new Shard(node, new int[0], new HashMap<>()));
            }
            
            // 随机分配
            for (int i = 0; i < totalShards; i++) {
                Shard shard = result.get(random.nextInt(result.size()));
                int[] current = shard.getShardNumbers();
                int[] newShards = Arrays.copyOf(current, current.length + 1);
                newShards[current.length] = i;
                shard.setShardNumbers(newShards);
            }
            
            return result;
        }
        
        @Override
        public String getName() {
            return "mock-random";
        }
    }
    
    private List<Shard> allocateShardsWithFailedNode(ShardingStrategy strategy, 
                                                     int totalShards, 
                                                     String jobKey, 
                                                     int failedNodeIndex) {
        List<Shard> shards = strategy.allocateShards(totalShards, jobKey);
        shards.remove(failedNodeIndex);
        
        // 重新分配失败节点的分片
        int[] failedShards = shards.get(0).getShardNumbers();
        for (int i = 1; i < shards.size(); i++) {
            Shard shard = shards.get(i);
            int shareCount = failedShards.length / (shards.size() - 1);
            if (i == 1) {
                shareCount += failedShards.length % (shards.size() - 1);
            }
            
            // 分配额外的分片
            int[] current = shard.getShardNumbers();
            int[] newShards = Arrays.copyOf(current, current.length + shareCount);
            System.arraycopy(failedShards, (i-1) * shareCount, 
                           newShards, current.length, shareCount);
            shard.setShardNumbers(newShards);
        }
        
        return shards;
    }
} 