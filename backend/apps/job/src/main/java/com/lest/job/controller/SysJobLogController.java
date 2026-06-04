package com.lest.job.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lest.common.core.utils.poi.ExcelUtil;
import com.lest.common.core.web.controller.BaseController;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.log.annotation.Log;
import com.lest.common.log.enums.BusinessType;
import com.lest.common.security.utils.SecurityUtils;
import com.lest.job.domain.SysJobLog;
import com.lest.job.service.ISysJobLogService;

/**
 * 定时任务调度日志接口<br>
 * 仅平台管理员可操作。
 *
 * @author yshan2028
 */
@RestController
@RequestMapping("/job/log")
public class SysJobLogController extends BaseController
{
    @Autowired
    private ISysJobLogService jobLogService;

    @GetMapping("/list")
    public TableDataInfo list(SysJobLog sysJobLog)
    {
        if (!SecurityUtils.isPlatformAdmin()) { return TableDataInfo.empty(); }
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        return getDataTable(list);
    }

    @Log(title = "调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog)
    {
        if (!SecurityUtils.isPlatformAdmin()) { return; }
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<>(SysJobLog.class);
        util.exportExcel(response, list, "调度日志");
    }

    @GetMapping("/{jobLogId}")
    public AjaxResult getInfo(@PathVariable Long jobLogId)
    {
        if (!SecurityUtils.isPlatformAdmin()) { return error("无权限"); }
        return success(jobLogService.selectJobLogById(jobLogId));
    }

    @Log(title = "调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobLogIds}")
    public AjaxResult remove(@PathVariable Long[] jobLogIds)
    {
        if (!SecurityUtils.isPlatformAdmin()) { return error("无权限"); }
        return toAjax(jobLogService.deleteJobLogByIds(jobLogIds));
    }

    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean()
    {
        if (!SecurityUtils.isPlatformAdmin()) { return error("无权限"); }
        jobLogService.cleanJobLog();
        return success();
    }
}
