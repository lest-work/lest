package com.lest.modules.project.controller;

import com.lest.common.core.PageResult;
import com.lest.common.core.Result;
import com.lest.modules.project.entity.dto.ProjectDTO;
import com.lest.modules.project.entity.dto.ProjectMemberDTO;
import com.lest.modules.project.entity.vo.ProjectMemberVO;
import com.lest.modules.project.entity.vo.ProjectVO;
import com.lest.modules.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目控制器
 */
@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 创建项目
     */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody ProjectDTO dto) {
        return Result.ok(projectService.create(dto));
    }

    /**
     * 分页查询项目
     */
    @GetMapping("/page")
    public Result<PageResult<ProjectVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return Result.ok(projectService.page(page, size, name, status));
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public Result<ProjectVO> getById(@PathVariable Long id) {
        return Result.ok(projectService.getById(id));
    }

    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProjectDTO dto) {
        dto.setId(id);
        projectService.update(dto);
        return Result.ok();
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }

    /**
     * 归档项目
     */
    @PutMapping("/{id}/archive")
    public Result<Void> archive(@PathVariable Long id) {
        projectService.archive(id);
        return Result.ok();
    }

    /**
     * 取消归档项目
     */
    @PutMapping("/{id}/unarchive")
    public Result<Void> unarchive(@PathVariable Long id) {
        projectService.unarchive(id);
        return Result.ok();
    }

    /**
     * 获取项目成员列表
     */
    @GetMapping("/{id}/member")
    public Result<PageResult<ProjectMemberVO>> listMembers(@PathVariable Long id) {
        return Result.ok(PageResult.of(projectService.listMembers(id), (long) projectService.listMembers(id).size(), 1, (int) Math.min(projectService.listMembers(id).size(), 100)));
    }

    /**
     * 添加项目成员
     */
    @PostMapping("/{id}/member")
    public Result<Void> addMember(@PathVariable Long id, @Valid @RequestBody ProjectMemberDTO dto) {
        projectService.addMember(id, dto);
        return Result.ok();
    }

    /**
     * 移除项目成员
     */
    @DeleteMapping("/{id}/member/{userId}")
    public Result<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        projectService.removeMember(id, userId);
        return Result.ok();
    }

    /**
     * 修改项目成员角色
     */
    @PutMapping("/{id}/member/{userId}/role")
    public Result<Void> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody com.lest.modules.project.entity.dto.MemberRoleDTO dto) {
        projectService.updateMemberRole(id, userId, dto.getRole());
        return Result.ok();
    }
}
