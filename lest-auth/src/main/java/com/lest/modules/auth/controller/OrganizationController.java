package com.lest.modules.auth.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;
import com.lest.modules.auth.service.OrganizationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 机构控制器
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@RestController
@RequestMapping("/auth/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * 获取机构树
     *
     * @return 机构树列表
     */
    @GetMapping("/tree")
    public Result<List<OrganizationVO>> getTree() {
        return Result.ok(organizationService.getTree());
    }

    /**
     * 获取所有机构（统一返回 PageResult 格式）
     */
    @GetMapping
    public Result<PageResult<OrganizationVO>> listAll() {
        return Result.ok(PageResult.of(organizationService.listAll()));
    }

    /**
     * 分页查询机构（DB 分页）
     */
    @GetMapping("/page")
    public Result<PageResult<OrganizationVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orgName) {
        return Result.ok(organizationService.page(page, size, orgName));
    }

    /**
     * 根据ID查询机构
     *
     * @param id 机构ID
     * @return 机构VO
     */
    @GetMapping("/{id}")
    public Result<OrganizationVO> getById(@PathVariable Long id) {
        return Result.ok(organizationService.getById(id));
    }

    /**
     * 创建机构
     *
     * @param dto 机构DTO
     * @return 机构ID
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrganizationDTO dto) {
        log.info("创建机构: orgName={}", dto.getOrgName());
        return Result.ok(organizationService.create(dto));
    }

    /**
     * 更新机构
     *
     * @param dto 机构DTO
     * @return 操作结果
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody OrganizationDTO dto) {
        log.info("更新机构: orgId={}", dto.getId());
        organizationService.update(dto);
        return Result.ok();
    }

    /**
     * 删除机构
     *
     * @param id 机构ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除机构: orgId={}", id);
        organizationService.delete(id);
        return Result.ok();
    }
}
