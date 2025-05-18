export default [
    // 验证Cron表达式
    {
      url: '/api/json/parseCronExp',
      method: 'post',
      response: ({ body }) => {
        const { cronExpression } = body;
        
        if (!cronExpression || cronExpression.trim() === '') {
          return {
            code: 500,
            result: {
              statusCode: "500",
              message: "Cron表达式不能为空"
            }
          };
        }
        
        // 简单验证Cron表达式格式
        const parts = cronExpression.split(' ');
        if (parts.length < 6 || parts.length > 7) {
          return {
            code: 500,
            result: {
              statusCode: "500",
              message: "Cron表达式格式不正确"
            }
          };
        }
        
        // 解析Cron表达式
        const now = new Date();
        const schedulerNextResults = [];
        
        for (let i = 1; i <= 10; i++) {
          const nextDate = new Date(now.getTime() + i * 60 * 60 * 1000);
          schedulerNextResults.push(nextDate.toLocaleString());
        }
        
        return {
          code: 200,
          result: {
            statusCode: "200",
            message: "解析成功"
          },
          secLabel: parts[0] || '0',
          minLabel: parts[1] || '0',
          hhLabel: parts[2] || '0',
          dayLabel: parts[3] || '*',
          monthLabel: parts[4] || '*',
          weekLabel: parts[5] || '?',
          yearLabel: parts.length > 6 ? parts[6] : '',
          startDate: now.toLocaleString(),
          schedulerNextResults,
          // 分钟设置
          minuteexptype: parts[1] === '*' ? '0' : '1',
          startMinute: '0',
          everyMinute: '1',
          assignMins: parts[1].includes(',') ? parts[1].split(',').map(Number) : [0],
          // 小时设置
          hourexptype: parts[2] === '*' ? '0' : '1',
          assignHours: parts[2].includes(',') ? parts[2].split(',').map(Number) : [0],
          // 日期设置
          dayexptype: parts[3] === '*' ? '0' : '1',
          assignDays: parts[3].includes(',') ? parts[3].split(',').map(Number) : [1],
          // 月份设置
          monthexptype: parts[4] === '*' ? '0' : '1',
          assignMonths: parts[4].includes(',') ? parts[4].split(',').map(Number) : [1],
          // 星期设置
          useweekck: parts[5] !== '?' ? '1' : '0',
          weekexptype: parts[5] === '*' ? '0' : '1',
          assignWeeks: parts[5].includes(',') ? parts[5].split(',').map(Number) : [1],
          // 年份设置
          useyearck: parts.length > 6 && parts[6] !== '' && parts[6] !== '*' ? '1' : '0',
          yearexptype: parts.length > 6 && parts[6] !== '*' ? '1' : '0',
          yearSet: parts.length > 6 ? parts[6] : ''
        };
      }
    },
    
    // 生成Cron表达式
    {
      url: '/api/json/generateCronExp',
      method: 'post',
      response: ({ body }) => {
        // 从表单数据生成Cron表达式
        const {
          cycle_exp_min_type, assign_exp_min_type,
          startMinute, everyMinute, assignMins,
          exp_hh_type_0, exp_hh_type_1, assignHours,
          exp_day_type_0, exp_day_type_1, assignDays,
          exp_month_type_0, exp_month_type_1, assignMonths,
          use_week_ck, exp_week_type_0, exp_week_type_1, assignWeeks,
          use_year_ck, exp_year_type_0, exp_year_type_1, yearCronExp
        } = body;
        
        // 构建Cron表达式
        let cronExpression = '0 '; // 秒总是0
        
        // 分钟
        if (cycle_exp_min_type === 'on') {
          cronExpression += `${startMinute}/${everyMinute} `;
        } else if (assign_exp_min_type === 'on' && assignMins && assignMins.length > 0) {
          cronExpression += `${assignMins.join(',')} `;
        } else {
          cronExpression += '* ';
        }
        
        // 小时
        if (exp_hh_type_0 === 'on') {
          cronExpression += '* ';
        } else if (exp_hh_type_1 === 'on' && assignHours && assignHours.length > 0) {
          cronExpression += `${assignHours.join(',')} `;
        } else {
          cronExpression += '* ';
        }
        
        // 日期
        if (exp_day_type_0 === 'on') {
          cronExpression += '* ';
        } else if (exp_day_type_1 === 'on' && assignDays && assignDays.length > 0) {
          cronExpression += `${assignDays.join(',')} `;
        } else {
          cronExpression += '* ';
        }
        
        // 月份
        if (exp_month_type_0 === 'on') {
          cronExpression += '* ';
        } else if (exp_month_type_1 === 'on' && assignMonths && assignMonths.length > 0) {
          cronExpression += `${assignMonths.join(',')} `;
        } else {
          cronExpression += '* ';
        }
        
        // 星期
        if (use_week_ck === 'on') {
          if (exp_week_type_0 === 'on') {
            cronExpression += '* ';
          } else if (exp_week_type_1 === 'on' && assignWeeks && assignWeeks.length > 0) {
            cronExpression += `${assignWeeks.join(',')} `;
          } else {
            cronExpression += '* ';
          }
        } else {
          cronExpression += '? ';
        }
        
        // 年份
        if (use_year_ck === 'on' && exp_year_type_1 === 'on' && yearCronExp) {
          cronExpression += yearCronExp;
        } else {
          cronExpression += '*';
        }
        
        // 生成下次执行时间
        const now = new Date();
        const schedulerNextResults = [];
        
        for (let i = 1; i <= 10; i++) {
          const nextDate = new Date(now.getTime() + i * 60 * 60 * 1000);
          schedulerNextResults.push(nextDate.toLocaleString());
        }
        
        return {
          code: 200,
          secLabel: '0',
          minLabel: cronExpression.split(' ')[1],
          hhLabel: cronExpression.split(' ')[2],
          dayLabel: cronExpression.split(' ')[3],
          monthLabel: cronExpression.split(' ')[4],
          weekLabel: cronExpression.split(' ')[5],
          yearLabel: cronExpression.split(' ')[6] || '*',
          cronExpression,
          startDate: now.toLocaleString(),
          schedulerNextResults
        };
      }
    }
]; 