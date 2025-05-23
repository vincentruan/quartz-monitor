export default [
  // 获取触发器列表
  {
    url: '/api/trigger/list',
    method: 'get',
    response: ({ query }) => {
      const { jobId } = query;
      
      // 生成触发器数据
      const list = [];
      const count = Math.floor(Math.random() * 3) + 1;
      
      for (let i = 0; i < count; i++) {
        const now = new Date();
        const prev = new Date(now.getTime() - (Math.random() * 1000 * 60 * 60 * 24));
        const next = new Date(now.getTime() + (Math.random() * 1000 * 60 * 60 * 24));
        const start = new Date(now.getTime() - (Math.random() * 1000 * 60 * 60 * 24 * 30));
        
        list.push({
          uuid: `trigger-${jobId}-${i}`,
          name: `trigger-${i}`,
          group: jobId.includes('DEFAULT') ? 'DEFAULT' : jobId.includes('REPORT') ? 'REPORT' : 'ADMIN',
          previousFireTime: prev.toLocaleString(),
          nextFireTime: next.toLocaleString(),
          startTime: start.toLocaleString(),
          description: `测试触发器 ${i}`
        });
      }
      
      return {
        code: 200,
        data: {
          triggerList: list,
          jobId
        }
      };
    }
  },
  
  // 添加触发器
  {
    url: '/api/trigger/add',
    method: 'post',
    response: () => {
      return {
        code: 200,
        message: '添加触发器成功',
        data: { success: true }
      };
    }
  },
  
  // 删除触发器
  {
    url: '/api/trigger/delete',
    method: 'delete',
    response: ({ query }) => {
      return {
        code: 200,
        message: `成功删除触发器 ${query.uuid}`,
        data: { success: true }
      };
    }
  }
]; 