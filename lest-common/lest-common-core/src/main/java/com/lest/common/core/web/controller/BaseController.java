package com.lest.common.core.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lest.common.core.constant.HttpStatus;
import com.lest.common.core.web.domain.AjaxResult;
import com.lest.common.core.web.page.PageDomain;
import com.lest.common.core.web.page.TableDataInfo;
import com.lest.common.core.web.page.TableSupport;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.core.utils.StringUtils;

import java.util.List;

/**
 * Web 层通用数据处理
 *
 * @author yshan2028
 */
public class BaseController {

    /**
     * 获取分页参数并启动分页
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum != null && pageSize != null) {
            String orderBy = ServletUtils.getParameter("orderByColumn");
            String isAsc = ServletUtils.getParameter("isAsc");
            if (StringUtils.isNotBlank(orderBy)) {
                PageHelper.startPage(pageNum, pageSize, orderBy + " " + (isAsc == null ? "asc" : isAsc));
            } else {
                PageHelper.startPage(pageNum, pageSize);
            }
        }
    }

    /**
     * 获取分页参数并启动分页（带默认 pageSize）
     */
    protected void startPage(Integer defaultPageSize) {
        PageDomain pageDomain = TableSupport.buildPageRequest(defaultPageSize);
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (pageNum != null && pageSize != null) {
            String orderBy = ServletUtils.getParameter("orderByColumn");
            String isAsc = ServletUtils.getParameter("isAsc");
            if (StringUtils.isNotBlank(orderBy)) {
                PageHelper.startPage(pageNum, pageSize, orderBy + " " + (isAsc == null ? "asc" : isAsc));
            } else {
                PageHelper.startPage(pageNum, pageSize);
            }
        }
    }

    /**
     * 响应请求分页数据
     */
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(HttpStatus.SUCCESS);
        dataTable.setMsg("查询成功");
        dataTable.setRows(list);
        dataTable.setTotal(new PageInfo(list).getTotal());
        return dataTable;
    }

    /**
     * 响应请求分页数据
     */
    protected TableDataInfo getDataTable(List<?> list, long total) {
        TableDataInfo dataTable = new TableDataInfo();
        dataTable.setCode(HttpStatus.SUCCESS);
        dataTable.setMsg("查询成功");
        dataTable.setRows(list);
        dataTable.setTotal(total);
        return dataTable;
    }

    /**
     * 返回成功
     */
    protected AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 返回成功消息
     */
    protected AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 返回成功消息（带数据）
     */
    protected AjaxResult success(Object data) {
        return AjaxResult.success(data);
    }

    /**
     * 返回成功消息
     */
    protected AjaxResult success(String message, Object data) {
        return AjaxResult.success(message, data);
    }

    /**
     * 返回警告
     */
    protected AjaxResult warn() {
        return AjaxResult.warn();
    }

    /**
     * 返回警告消息
     */
    protected AjaxResult warn(String message) {
        return AjaxResult.warn(message);
    }

    /**
     * 返回错误
     */
    protected AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回错误消息
     */
    protected AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误消息
     */
    protected AjaxResult error(int code, String message) {
        return AjaxResult.error(code, message);
    }

    /**
     * 响应成功
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 响应成功
     */
    protected AjaxResult toAjax(boolean result) {
        return result ? success() : error();
    }

    /**
     * 响应成功（带消息）
     */
    protected AjaxResult toAjax(int rows, String message) {
        return rows > 0 ? success(message) : error(message);
    }
}
