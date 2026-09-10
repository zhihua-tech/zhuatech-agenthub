# AgentHub API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 智能体治理态势 |
| GET | `/api/admin/work-orders` | 运行任务清单 |
| GET | `/api/shopfloor/dashboard` | 业务智能体工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交人工运行反馈 |
| POST | `/api/shopfloor/agent-preview` | 运行本地演示 AgentRuntime |
| POST | `/api/shopfloor/agent-preflight` | 执行前校验工具登记、敏感数据和人工审批要求 |
| POST | `/api/shopfloor/agent-budget` | 评估令牌、费用与工具调用预算，返回执行决策和整改建议 |
| POST | `/api/enterprise/agenthub/production-authorization` | 校验 Agent 生产授权条件 |
| POST | `/api/enterprise/agenthub/tool-execution-policy` | 执行工具权限、审批、预算和幂等策略 |
