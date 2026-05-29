package com.lest.modules.system.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.modules.system.entity.dto.DeptDTO;
import com.lest.modules.system.entity.vo.DeptVO;
import com.lest.modules.system.service.DeptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.lest.common.security.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 *
 * @author yshan2028
 * @since 2026-05-28
 */
@Slf4j
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    private final DeptService deptService;

    /**
     * 分页查询部门列表
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) String deptCode,
            @RequestParam(required = false) Integer status) {
        startPage();
        List<DeptVO> list = deptService.selectDeptList(deptName, deptCode, status);
        return getDataTable(list);
    }

    /**
     * 获取部门树
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping("/tree")
    public Result<List<DeptVO>> tree() {
        return Result.ok(deptService.selectDeptTree());
    }

    /**
     * 获取部门下拉树
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping("/treeselect")
    public Result<List<DeptVO>> treeselect() {
        return Result.ok(deptService.selectDeptTree());
    }

    /**
     * 获取部门详情
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping("/{deptId}")
    public Result<DeptVO> getById(@PathVariable("deptId") Long deptId) {
        return Result.ok(deptService.getById(deptId));
    }

    /**
     * 新增部门
     */
    @RequiresPermissions("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> create(@Valid @RequestBody DeptDTO dto) {
        return Result.ok(deptService.createDept(dto));
    }

    /**
     * 修改部门
     */
    @RequiresPermissions("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@Valid @RequestBody DeptDTO dto) {
        deptService.updateDept(dto);
        return Result.ok();
    }

    /**
     * 删除部门
     */
    @RequiresPermissions("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Result<Void> delete(@PathVariable Long deptId) {
        deptService.deleteDept(deptId);
        return Result.ok();
    }

    /**
     * 获取角色部门树
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping("/roleDeptTreeselect/{roleId}")
    public Result<List<DeptVO>> roleDeptTreeselect(@PathVariable("roleId") Long roleId) {
        List<DeptVO> allDepts = deptService.selectDeptTree();
        List<Long> checkedDeptIds = deptService.getDeptIdsByRoleId(roleId);
        return Result.ok(deptService.buildRoleDeptTree(allDepts, checkedDeptIds));
    }

    /**
     * 查询部门下用户
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping("/list/exclude/{deptId}")
    public Result<List<DeptVO>> exclude(@PathVariable("deptId") Long deptId) {
        List<DeptVO> depts = deptService.selectDeptList(null, null, null);
        return Result.ok(deptService.filterExcludeDept(depts, deptId));
    }
}
