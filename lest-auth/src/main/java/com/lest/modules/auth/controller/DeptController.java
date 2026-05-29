package com.lest.modules.auth.controller;

import com.lest.common.core.Result;
import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;
import com.lest.modules.auth.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 部门控制器（供 lest-system 通过 Feign 调用）
 * 此接口使用 dept 语义，与 OrganizationController 共享同一个 Service
 *
 * @author yshan2028
 */
@Slf4j
@RestController
@RequestMapping("/auth/dept")
@RequiredArgsConstructor
public class DeptController {

    private final DepartmentService departmentService;

    /**
     * 获取部门树
     */
    @GetMapping("/tree")
    public Result<java.util.List<OrganizationVO>> getTree() {
        return Result.ok(departmentService.getTree());
    }

    /**
     * 获取所有部门
     */
    @GetMapping
    public Result<java.util.List<OrganizationVO>> listAll() {
        return Result.ok(departmentService.listAll());
    }

    /**
     * 根据ID查询部门
     */
    @GetMapping("/{id}")
    public Result<OrganizationVO> getById(@PathVariable Long id) {
        return Result.ok(departmentService.getById(id));
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody OrganizationDTO dto) {
        log.info("新增部门: deptName={}", dto.getOrgName());
        return Result.ok(departmentService.create(dto));
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody OrganizationDTO dto) {
        log.info("修改部门: deptId={}", dto.getId());
        departmentService.update(dto);
        return Result.ok();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除部门: deptId={}", id);
        departmentService.delete(id);
        return Result.ok();
    }
}
