# 企业 Agent 工具执行策略

Copyright © 2026 上海如静知华信息科技有限公司 · <https://www.zhuatech.cn/>

`POST /api/enterprise/agenthub/tool-execution-policy` 是 Agent 运行时与企业工具之间的执行门禁。它实际检查身份、租户一致性、工具允许清单、最小权限凭据、参数密钥、PII 授权、外发目标、任务预算、人工审批和幂等键。

接口返回 `ALLOW_READ / ALLOW_EXECUTE / REVIEW / REPLAY_SAFE / DENY`。读取工具可在满足权限后直接执行；写入、删除和外发动作必须携带幂等键并经过人工审批；重复请求返回首次执行结果，防止重复扣款、发信或改写业务数据；密钥泄露、越权、跨租户和预算超限会直接拒绝。

每个结果包含稳定的 `policyHash`、阻断原因与执行义务。生产接入时应把策略哈希、工具版本、参数摘要、审批记录、执行耗时、费用和补偿结果写入不可篡改审计存储。
