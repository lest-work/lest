package com.lest.common.core.web.page;

import com.lest.common.core.utils.StringUtils;

/**
 * 分页请求参数基类
 *
 * @author yshan2028
 */
public class PageDomain {

    private Integer pageNum;
    private Integer pageSize;
    private String orderByColumn;
    private String isAsc = "asc";
    private String reasonable = "false";

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public String getIsAsc() {
        return isAsc;
    }

    public void setIsAsc(String isAsc) {
        if (StringUtils.isNotBlank(isAsc)) {
            if ("ascending".equals(isAsc)) {
                this.isAsc = "asc";
            } else if ("descending".equals(isAsc)) {
                this.isAsc = "desc";
            } else {
                this.isAsc = isAsc;
            }
        }
    }

    public String getReasonable() {
        return reasonable;
    }

    public void setReasonable(String reasonable) {
        this.reasonable = reasonable;
    }
}
