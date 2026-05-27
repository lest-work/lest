package com.lest.modules.release.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lest.common.base.Result;
import com.lest.common.security.util.SecurityUtils;
import com.lest.modules.release.entity.dto.AddReleaseIssueDTO;
import com.lest.modules.release.entity.dto.CreateReleaseArtifactDTO;
import com.lest.modules.release.entity.dto.CreateReleasePlanDTO;
import com.lest.modules.release.entity.dto.ReleaseArtifactDTO;
import com.lest.modules.release.entity.dto.ReleaseIssueDTO;
import com.lest.modules.release.entity.dto.ReleasePlanDTO;
import com.lest.modules.release.entity.dto.ReleasePlanQueryDTO;
import com.lest.modules.release.entity.dto.UpdateReleasePlanDTO;
import com.lest.modules.release.entity.vo.ReleaseArtifactVO;
import com.lest.modules.release.entity.vo.ReleaseIssueVO;
import com.lest.modules.release.entity.vo.ReleasePlanVO;
import com.lest.modules.release.service.ReleasePlanService;
import com.lest.modules.release.service.ReleaseIssueService;
import com.lest.modules.release.service.ReleaseArtifactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/release")
@RequiredArgsConstructor
public class ReleasePlanController {

    private final ReleasePlanService releasePlanService;
    private final ReleaseArtifactService releaseArtifactService;
    private final ReleaseIssueService releaseIssueService;

    @PostMapping("/plan")
    public Result<Void> create(@Valid @RequestBody CreateReleasePlanDTO dto) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.create(dto, userId);
        return Result.success();
    }

    @PutMapping("/plan")
    public Result<Void> update(@Valid @RequestBody UpdateReleasePlanDTO dto) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.update(dto, userId);
        return Result.success();
    }

    @DeleteMapping("/plan/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        releasePlanService.delete(id);
        return Result.success();
    }

    @GetMapping("/plan/{id}")
    public Result<ReleasePlanDTO> getById(@PathVariable Long id) {
        return Result.success(releasePlanService.getById(id));
    }

    @PostMapping("/plan/page")
    public Result<Page<ReleasePlanVO>> pageQuery(@RequestBody ReleasePlanQueryDTO dto) {
        return Result.success(releasePlanService.pageQuery(dto));
    }

    @PostMapping("/plan/{id}/publish")
    public Result<Void> publish(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.publish(id, userId);
        return Result.success();
    }

    @PostMapping("/plan/{id}/archive")
    public Result<Void> archive(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.archive(id, userId);
        return Result.success();
    }

    @PostMapping("/plan/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.restore(id, userId);
        return Result.success();
    }

    @PostMapping("/plan/{id}/build/start")
    public Result<Void> startBuild(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.startBuild(id, userId);
        return Result.success();
    }

    @PostMapping("/plan/{id}/build/complete")
    public Result<Void> completeBuild(@PathVariable Long id, @RequestParam(required = false) String downloadUrl) {
        Long userId = SecurityUtils.getUserId();
        releasePlanService.completeBuild(id, userId, downloadUrl);
        return Result.success();
    }

    @GetMapping("/plan/upcoming")
    public Result<List<ReleasePlanDTO>> getUpcoming() {
        return Result.success(releasePlanService.getUpcoming());
    }

    @GetMapping("/plan/recent")
    public Result<List<ReleasePlanDTO>> getRecent(@RequestParam(required = false) Long projectId,
                                                  @RequestParam(required = false) Integer limit) {
        return Result.success(releasePlanService.getRecent(projectId, limit));
    }

    @PostMapping("/artifact")
    public Result<Void> createArtifact(@Valid @RequestBody CreateReleaseArtifactDTO dto) {
        Long userId = SecurityUtils.getUserId();
        releaseArtifactService.create(dto, userId);
        return Result.success();
    }

    @PostMapping("/artifact/page")
    public Result<Page<ReleaseArtifactVO>> pageArtifact(@RequestBody ReleaseArtifactDTO dto) {
        return Result.success(releaseArtifactService.pageQuery(dto));
    }

    @DeleteMapping("/artifact/{id}")
    public Result<Void> deleteArtifact(@PathVariable Long id) {
        releaseArtifactService.delete(id);
        return Result.success();
    }

    @PostMapping("/issue")
    public Result<Void> addIssue(@Valid @RequestBody AddReleaseIssueDTO dto) {
        Long userId = SecurityUtils.getUserId();
        releaseIssueService.add(dto, userId);
        return Result.success();
    }

    @PostMapping("/issue/batch")
    public Result<Void> batchAddIssues(@RequestParam Long releaseId,
                                       @RequestParam(required = false) Long[] taskIds,
                                       @RequestParam(required = false) Long[] issueIds,
                                       @RequestParam Integer category,
                                       @RequestParam(required = false) String notes) {
        Long userId = SecurityUtils.getUserId();
        releaseIssueService.batchAdd(releaseId, taskIds, issueIds, category, notes, userId);
        return Result.success();
    }

    @PostMapping("/issue/page")
    public Result<Page<ReleaseIssueVO>> pageIssue(@RequestBody ReleaseIssueDTO dto) {
        return Result.success(releaseIssueService.pageQuery(dto));
    }

    @DeleteMapping("/issue/{id}")
    public Result<Void> removeIssue(@PathVariable Long id) {
        releaseIssueService.remove(id);
        return Result.success();
    }
}
