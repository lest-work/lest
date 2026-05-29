package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysDept;
import com.lest.system.api.factory.RemoteOrganizationFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机构服务 Feign 客户端
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteOrganizationService", value = "lest-auth", fallbackFactory = RemoteOrganizationFallbackFactory.class)
public interface RemoteOrganizationService {

    /**
     * 获取机构树
     */
    @GetMapping("/auth/organization/tree")
    R<List<SysDept>> getTree(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取所有机构
     */
    @GetMapping("/auth/organization")
    R<List<SysDept>> listAll(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据ID查询机构
     */
    @GetMapping("/auth/organization/{id}")
    R<SysDept> getById(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
