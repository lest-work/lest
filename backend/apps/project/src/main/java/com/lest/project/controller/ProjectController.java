package com.lest.project.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.annotation.RequireProjectRole;
import com.lest.project.controller.request.ProjectCreateRequest;
import com.lest.project.controller.request.ProjectMemberEmailInviteRequest;
import com.lest.project.controller.request.ProjectMemberInviteRequest;
import com.lest.project.controller.request.ProjectMemberRoleUpdateRequest;
import com.lest.project.controller.request.ProjectUpdateRequest;
import com.lest.project.domain.Project;
import com.lest.project.domain.ProjectMember;
import com.lest.project.service.IProjectService;

/**
 * 项目管理
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController
{
    private final IProjectService projectService;

    public ProjectController(IProjectService projectService)
    {
        this.projectService = projectService;
    }

    @GetMapping("/list")
    public TableDataInfo list(Project project)
    {
        startPage();
        List<Project> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(projectService.selectProjectById(id));
    }

    @Log(title = "项目管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody @Valid ProjectCreateRequest request)
    {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setTemplate(request.getTemplate());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        return toAjax(projectService.insertProject(project));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable Long id, @RequestBody @Valid ProjectUpdateRequest request)
    {
        Project project = new Project();
        project.setProjectId(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setTemplate(request.getTemplate());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        return toAjax(projectService.updateProject(project));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(projectService.deleteProjectById(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/archive")
    public AjaxResult archive(@PathVariable Long id)
    {
        return toAjax(projectService.archiveProject(id));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/unarchive")
    public AjaxResult unarchive(@PathVariable Long id)
    {
        return toAjax(projectService.unarchiveProject(id));
    }

    @RequireProjectRole(checkMembership = true)
    @GetMapping("/{id}/members")
    public AjaxResult memberList(@PathVariable Long id)
    {
        List<ProjectMember> list = projectService.selectMembersByProjectId(id);
        return success(list);
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目成员", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/members")
    public AjaxResult addMember(@PathVariable Long id, @RequestBody @Valid ProjectMemberInviteRequest request)
    {
        ProjectMember member = new ProjectMember();
        member.setProjectId(id);
        member.setUserId(request.getUserId());
        member.setRole(request.getRole());
        return toAjax(projectService.insertMember(member));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目成员", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/members/invite-by-email")
    public AjaxResult inviteMemberByEmail(@PathVariable Long id, @RequestBody @Valid ProjectMemberEmailInviteRequest request)
    {
        int rows = projectService.inviteMemberByEmail(id, request.getEmail(), request.getRole());
        return toAjax(rows);
    }

    /**
     * 为指定邮箱生成一次性邀请链接（不负责实际发送邮件）。
     *
     * 前端可从返回的 data.inviteUrl 中获取完整链接。
     */
    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目成员", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/members/invite-link")
    public AjaxResult createInviteLink(@PathVariable Long id, @RequestBody @Valid ProjectMemberEmailInviteRequest request)
    {
        // 默认有效期 7 天，可按需在 V1.0 固定
        String token = projectService.createEmailInvite(id, request.getEmail(), request.getRole(), 7 * 24 * 60);
        // 这里返回相对 URL，前端用 window.location.origin 拼接
        String inviteUrl = "/project/invites/" + token + "/accept";
        AjaxResult result = AjaxResult.success();
        result.put("inviteUrl", inviteUrl);
        result.put("token", token);
        return result;
    }

    /**
     * 接受项目邀请链接（需要登录），一次性使用。
     */
    @Log(title = "项目成员", businessType = BusinessType.INSERT)
    @PostMapping("/invites/{token}/accept")
    public AjaxResult acceptInvite(@PathVariable String token)
    {
        int delta = projectService.acceptInvite(token);
        return toAjax(delta);
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}/members/{userId}")
    public AjaxResult removeMember(@PathVariable Long id, @PathVariable Long userId)
    {
        return toAjax(projectService.deleteMember(id, userId));
    }

    @RequireProjectRole(roles = {"admin"}, checkMembership = false)
    @Log(title = "项目成员", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/members/{userId}/role")
    public AjaxResult updateMemberRole(@PathVariable Long id, @PathVariable Long userId,
                                       @RequestBody @Valid ProjectMemberRoleUpdateRequest request)
    {
        return toAjax(projectService.updateMemberRole(id, userId, request.getRole()));
    }
}
