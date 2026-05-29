package com.lest.system.api;

import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.web.domain.R;
import com.lest.system.api.domain.SysDept;
import com.lest.system.api.factory.RemoteDeptFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门服务 Feign 客户端
 * lest-system 通过此接口调用 lest-auth 的部门管理功能
 *
 * @author yshan2028
 */
@FeignClient(contextId = "remoteDeptService", value = "lest-auth", fallbackFactory = RemoteDeptFallbackFactory.class)
public interface RemoteDeptService {

    /**
     * 获取部门树
     */
    @GetMapping("/auth/dept/tree")
    R<List<SysDept>> getTree(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 获取所有部门
     */
    @GetMapping("/auth/dept")
    R<List<SysDept>> listAll(
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 根据ID查询部门
     */
    @GetMapping("/auth/dept/{id}")
    R<SysDept> getById(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 新增部门
     */
    @PostMapping("/auth/dept")
    R<Long> create(
            @RequestBody SysDept dept,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 修改部门
     */
    @PutMapping("/auth/dept")
    R<Boolean> update(
            @RequestBody SysDept dept,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 删除部门
     */
    @DeleteMapping("/auth/dept/{id}")
    R<Boolean> delete(
            @PathVariable("id") Long id,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
