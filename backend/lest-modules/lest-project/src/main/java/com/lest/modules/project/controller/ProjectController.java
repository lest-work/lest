package com.lest.modules.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.modules.project.domain.Project;
import com.lest.modules.project.domain.ProjectMember;
import com.lest.modules.project.service.IProjectService;

/**
 * 项目管理
 * 
 * @author yshan2028
 */
@RestController
@RequestMapping("")
public class ProjectController extends BaseController
{
    @Autowired
    private IProjectService projectService;

    /**
     * 查询项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Project project)
    {
        startPage();
        List<Project> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }

    /**
     * 获取项目详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(projectService.selectProjectById(id));
    }

    /**
     * 新增项目
     */
    @PostMapping
    public AjaxResult add(@RequestBody Project project)
    {
        return toAjax(projectService.insertProject(project));
    }

    /**
     * 修改项目
     */
    @PutMapping
    public AjaxResult edit(@RequestBody Project project)
    {
        return toAjax(projectService.updateProject(project));
    }

    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(projectService.deleteProjectById(id));
    }

    /**
     * 归档项目
     */
    @PutMapping("/{id}/archive")
    public AjaxResult archive(@PathVariable Long id)
    {
        return toAjax(projectService.archiveProject(id));
    }

    /**
     * 取消归档项目
     */
    @PutMapping("/{id}/unarchive")
    public AjaxResult unarchive(@PathVariable Long id)
    {
        return toAjax(projectService.unarchiveProject(id));
    }

    /**
     * 查询项目成员列表
     */
    @GetMapping("/{id}/member/list")
    public AjaxResult memberList(@PathVariable Long id)
    {
        List<ProjectMember> list = projectService.selectMembersByProjectId(id);
        return success(list);
    }

    /**
     * 添加项目成员
     */
    @PostMapping("/{id}/member")
    public AjaxResult addMember(@PathVariable Long id, @RequestBody ProjectMember member)
    {
        member.setProjectId(id);
        return toAjax(projectService.insertMember(member));
    }

    /**
     * 移除项目成员
     */
    @DeleteMapping("/{id}/member/{userId}")
    public AjaxResult removeMember(@PathVariable Long id, @PathVariable Long userId)
    {
        return toAjax(projectService.deleteMember(id, userId));
    }

    /**
     * 修改项目成员角色
     */
    @PutMapping("/{id}/member/{userId}/role")
    public AjaxResult updateMemberRole(@PathVariable Long id, @PathVariable Long userId, @RequestBody ProjectMember member)
    {
        return toAjax(projectService.updateMemberRole(id, userId, member.getRole()));
    }
}
