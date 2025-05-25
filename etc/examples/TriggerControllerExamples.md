# TriggerController API Examples

## 1. Get Triggers for a Job

```http
GET /api/trigger/list?jobId=123e4567-e89b-12d3-a456-426614174000
```

### Response:
```json
{
    "code": 200,
    "desc": "success",
    "data": {
        "triggers": [
            {
                "uuid": "456e7890-e89b-12d3-a456-426614174000",
                "jobId": "123e4567-e89b-12d3-a456-426614174000",
                "name": "myTrigger",
                "group": "DEFAULT",
                "jobName": "myJob",
                "jobGroup": "DEFAULT",
                "nextFireTime": "2024-01-15T10:30:00.000Z",
                "previousFireTime": "2024-01-15T09:30:00.000Z",
                "sTriggerState": "NORMAL"
            }
        ],
        "jobId": "123e4567-e89b-12d3-a456-426614174000",
        "jobName": "myJob"
    }
}
```

## 2. Add a Cron Trigger

```http
POST /api/trigger/add
Content-Type: application/json

{
    "name": "dailyTrigger",
    "group": "DEFAULT",
    "description": "Runs every day at 9 AM",
    "jobId": "123e4567-e89b-12d3-a456-426614174000",
    "dateFlag": 0,
    "cron": "0 0 9 * * ?"
}
```

### Response:
```json
{
    "code": 200,
    "desc": "success",
    "data": "Trigger added successfully"
}
```

## 3. Add a Simple Trigger

```http
POST /api/trigger/add
Content-Type: application/json

{
    "name": "onceTrigger",
    "group": "DEFAULT",
    "description": "Runs once at specified time",
    "jobId": "123e4567-e89b-12d3-a456-426614174000",
    "dateFlag": 1,
    "date": "2024-01-20T15:30:00.000Z"
}
```

### Response:
```json
{
    "code": 200,
    "desc": "success",
    "data": "Trigger added successfully"
}
```

## 4. Delete a Trigger

```http
DELETE /api/trigger/delete?uuid=456e7890-e89b-12d3-a456-426614174000
```

### Response:
```json
{
    "code": 200,
    "desc": "success",
    "data": "Trigger deleted successfully"
}
```

## Error Responses

### Validation Error:
```json
{
    "code": 400,
    "desc": "Validation failed: {name=Trigger name cannot be blank}",
    "data": null
}
```

### Job Not Found:
```json
{
    "code": 400,
    "desc": "Job not found with ID: 123e4567-e89b-12d3-a456-426614174000",
    "data": null
}
```

### No Quartz Instance:
```json
{
    "code": 500,
    "desc": "Please configure Quartz instance first",
    "data": null
}
```

## Cron Expression Examples

- Every minute: `0 * * * * ?`
- Every hour: `0 0 * * * ?`
- Every day at 9 AM: `0 0 9 * * ?`
- Every Monday at 10 AM: `0 0 10 ? * MON`
- Every 15 minutes: `0 0/15 * * * ?`
- Last day of month at noon: `0 0 12 L * ?` 