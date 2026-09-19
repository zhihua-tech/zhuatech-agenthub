/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.agenthub.controller;

import cn.zhuatech.agenthub.common.ApiResponse;
import cn.zhuatech.agenthub.service.AgentProductionAuthorizationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/enterprise/agenthub")
public class AgentProductionAuthorizationController {
    private final AgentProductionAuthorizationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AgentProductionAuthorizationController(AgentProductionAuthorizationService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/production-authorization")
    public ApiResponse<AgentProductionAuthorizationService.Assessment> assess(
            @Valid @RequestBody AgentProductionAuthorizationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
