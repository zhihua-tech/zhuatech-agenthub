# ZhuaTech AgentHub｜知华科技企业智能体开发与治理平台

ZhuaTech AgentHub 是面向企业 AI 团队与业务运营团队的 AgentOps 社区源码项目：把智能体目录、运行时、知识、工具、评测、审批、成本和审计证据统一到一个可治理的平台中。

[知华科技官网](https://www.zhuatech.cn/) · [架构设计](docs/architecture.md) · [API 摘要](docs/api.md) · [部署说明](deploy/README.md)

![AgentHub 企业智能体治理驾驶舱](docs/images/agenthub-governance-dashboard.png)

## 一条可控的智能体运行链

```text
业务目标 → 计划生成 → 知识引用 → 工具调用 → 人工审批 → 结果交付 → 评测归档
```

- 智能体目录、版本发布与业务负责人
- 多步骤任务、执行轨迹、人工接管与重试
- 工具权限、参数范围、敏感动作审批与预算
- 离线评测、线上采样、红队测试与质量基线
- 运行成功率、延迟、人工介入率与成本分析
- 可插拔 `AgentRuntime`；社区演示执行器不调用外部模型

![AgentHub 业务智能体移动工作台](docs/images/agenthub-run-h5.png)

## 新增：智能体执行前治理检查

`POST /api/shopfloor/agent-preflight` 会在任务进入运行时之前核对工具登记、外部写操作、敏感数据和人工审批要求，输出风险分、阻断工具、所需审批与下一步动作。社区版使用确定性规则，便于本地演示、自动测试和后续替换企业策略引擎。

### 运行预算护栏

预算评估接口会在任务入队前计算预计令牌利用率、模型费用和工具调用额度。任何一项超过上限都会阻止执行并返回具体原因；接近令牌上限的任务会进入观察状态，便于平台管理员在保障效果的同时控制企业 AI 成本。

## 技术基线

| 层级 | 方案 |
| --- | --- |
| Web | Vue 3、Pinia、Vue Router、Axios、Vite，响应式 H5 |
| API | Java 21、Spring Boot、Spring Security、JWT、JPA、Flyway |
| 数据 | MySQL 8；测试使用 H2 |
| Java 包 | `cn.zhuatech.agenthub` |
| 演示账号 | 管理端 `planner / Demo@2026`；业务端 `operator / Demo@2026` |

前端演示：`cd frontend && npm install && npm run dev:demo`，访问 `http://localhost:5173`。完整环境：复制 `.env.example` 为 `.env` 后执行 `docker compose up --build`。

## 使用许可与服务

本工程仅限个人学习、研究和非商业技术交流，**不得商用**。企业内部部署、生产使用、SaaS、项目交付、收费培训、品牌替换或商业再分发，均须事先获得上海如静知华信息科技有限公司书面授权，详见 [LICENSE](LICENSE)。

深度开发、私有化部署、智能体/模型接入及商业授权，请访问[知华科技官网](https://www.zhuatech.cn/)或扫码咨询。

| 微信咨询一 | 微信咨询二 |
| --- | --- |
| ![知华科技微信咨询二维码一](docs/images/zhuatech-wechat-consulting.png) | ![知华科技微信咨询二维码二](docs/images/zhuatech-wechat-consulting-2.png) |

SEO：Agent 平台源码、AgentOps、企业智能体治理、AI Agent 开源、智能体评测、Java Agent、Vue Agent 平台、知华科技。

## Agent 容量路由

新增 `POST /api/agenthub/insights/capacity-routing`，结合任务队列、执行器、模型并发、运行时长、SLA 和工具健康预测等待时间，输出 `NORMAL`、`ADD_CAPACITY` 或 `DEGRADE`。

## 企业级 Agent 生产授权

新增 `POST /api/enterprise/agenthub/production-authorization`，覆盖工具权限、凭据、安全评测、人工审批、审计、熔断、容量与成本，返回 `AUTHORIZE / PILOT / BLOCKED`。详见 [生产授权说明](docs/ENTERPRISE_AGENT_AUTHORIZATION.md)。

## 工具执行策略门禁

`POST /api/enterprise/agenthub/tool-execution-policy` 在每次真实工具调用前执行租户、允许清单、最小权限凭据、密钥与 PII 扫描、预算、人工审批、外发目标和幂等检查，支持只读放行、执行放行、人工复核、安全重放或拒绝。详见[工具执行策略说明](docs/ENTERPRISE_TOOL_POLICY.md)。
