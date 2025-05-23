export default [
  // 获取任务列表
  {
    url: '/api/job/list',
    method: 'get',
    response: ({ query }) => {
      const { pageNum = 1, numPerPage = 20 } = query;
      
      const list = [];
      const total = 100;
      
      // 生成模拟数据
      for (let i = 0; i < numPerPage; i++) {
        const index = (pageNum - 1) * numPerPage + i;
        if (index >= total) break;
        
        list.push({
          uuid: `job-${index}`,
          jobName: `测试任务 ${index}`,
          group: index % 3 === 0 ? 'DEFAULT' : index % 3 === 1 ? 'REPORT' : 'ADMIN',
          nextFireTime: new Date(Date.now() + index * 1000 * 60 * 60).toLocaleString(),
          numTriggers: Math.floor(Math.random() * 3) + 1,
          durability: Math.random() > 0.3,
          schedulerName: index % 2 === 0 ? 'QuartzScheduler' : 'ReportScheduler',
          state: ['NORMAL', 'PAUSED', 'COMPLETE'][Math.floor(Math.random() * 3)],
          description: `这是测试任务 ${index} 的描述信息`
        });
      }
      
      return {
        code: 200,
        data: {
          jobList: list,
          size: list.length,
          pageNum: parseInt(pageNum),
          pageCount: total
        }
      };
    }
  },
  
  // 添加任务
  {
    url: '/api/job/add',
    method: 'post',
    response: () => {
      return {
        code: 200,
        message: '添加任务成功',
        data: { success: true }
      };
    }
  },
  
  // 删除任务
  {
    url: '/api/job/delete',
    method: 'delete',
    response: ({ query }) => {
      return {
        code: 200,
        message: `成功删除任务 ${query.uuid}`,
        data: { success: true }
      };
    }
  },
  
  // 执行任务
  {
    url: '/api/job/start',
    method: 'post',
    response: ({ query }) => {
      return {
        code: 200,
        message: `成功执行任务 ${query.uuid}`,
        data: { success: true }
      };
    }
  },
  
  // 暂停任务
  {
    url: '/api/job/pause',
    method: 'post',
    response: ({ query }) => {
      return {
        code: 200,
        message: `成功暂停任务 ${query.uuid}`,
        data: { success: true }
      };
    }
  },
  
  // 恢复任务
  {
    url: '/api/job/resume',
    method: 'post',
    response: ({ query }) => {
      return {
        code: 200,
        message: `成功恢复任务 ${query.uuid}`,
        data: { success: true }
      };
    }
  },
  
  // 获取任务类型
  {
    url: '/api/job/types',
    method: 'get',
    response: () => {
      return {
        code: 200,
        data: [
          { uuid: '1', jobName: 'SampleJob', class: 'org.quartz.job.SampleJob' },
          { uuid: '2', jobName: 'DataProcessJob', class: 'org.quartz.job.DataProcessJob' },
          { uuid: '3', jobName: 'ReportJob', class: 'org.quartz.job.ReportJob' },
          { uuid: '4', jobName: 'EmailJob', class: 'org.quartz.job.EmailJob' }
        ]
      };
    }
  }
]; 