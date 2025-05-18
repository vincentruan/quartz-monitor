export default [
  // 获取统计信息
  {
    url: '/api/dashboard/stats',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: {
          jobCount: Math.floor(Math.random() * 100) + 10,
          triggerCount: Math.floor(Math.random() * 150) + 20,
          schedulerStatus: ['运行中', '已暂停'][Math.floor(Math.random() * 2)]
        }
      };
    }
  },
  
  // 获取最近执行的任务
  {
    url: '/api/dashboard/recent-jobs',
    method: 'get',
    response: () => {
      const recentJobs = [];
      const statuses = ['成功', '失败', '运行中'];
      const now = new Date();
      
      for (let i = 0; i < 5; i++) {
        const time = new Date(now.getTime() - i * 30 * 60 * 1000);
        
        recentJobs.push({
          jobName: ['SampleJob', 'DataProcessJob', 'ReportJob', 'EmailJob', 'CleanupJob'][i],
          group: ['DEFAULT', 'REPORT', 'ADMIN', 'EMAIL'][i % 4],
          fireTime: time.toLocaleString(),
          status: statuses[Math.floor(Math.random() * statuses.length)]
        });
      }
      
      return {
        code: 200,
        data: recentJobs
      };
    }
  }
]; 