import job from './job'
import trigger from './trigger'
import cron from './cron'
import dashboard from './dashboard'

export default [
  ...job,
  ...trigger,
  ...cron,
  ...dashboard
] 