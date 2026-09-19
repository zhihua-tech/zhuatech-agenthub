/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.controller;

import cn.zhuatech.agenthub.common.ApiResponse;
import cn.zhuatech.agenthub.service.AgentToolPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/agenthub")
public class AgentToolPlanController {
    private final AgentToolPlanService service;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentToolPlanController(AgentToolPlanService service) {
        this.service = service;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/tool-plan-preflight")
    public ApiResponse<AgentToolPlanService.PlanResult> evaluate(
            @Valid @RequestBody AgentToolPlanService.PlanRequest request) {
        return ApiResponse.ok("工具计划执行前检查完成", service.evaluate(request));
    }
}
