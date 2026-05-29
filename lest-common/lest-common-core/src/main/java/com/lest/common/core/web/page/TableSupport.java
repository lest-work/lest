package com.lest.common.core.web.page;

import com.lest.common.core.utils.ServletUtils;

/**
 * 表格分页工具类
 *
 * @author yshan2028
 */
public class TableSupport {

    public static final String PAGE_NUM = "pageNum";
    public static final String PAGE_SIZE = "pageSize";
    public static final String ORDER_BY_COLUMN = "orderByColumn";
    public static final String IS_ASC = "isAsc";
    public static final String REASONABLE = "reasonable";

    public static PageDomain buildPageRequest() {
        Integer pageNum = ServletUtils.getParameterToInt(PAGE_NUM, 1);
        Integer pageSize = ServletUtils.getParameterToInt(PAGE_SIZE, 10);
        String orderByColumn = ServletUtils.getParameter(ORDER_BY_COLUMN);
        String isAsc = ServletUtils.getParameter(IS_ASC);
        String reasonable = ServletUtils.getParameter(REASONABLE);

        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(pageNum);
        pageDomain.setPageSize(pageSize);
        pageDomain.setOrderByColumn(orderByColumn);
        pageDomain.setIsAsc(isAsc);
        pageDomain.setReasonable(reasonable);
        return pageDomain;
    }

    public static PageDomain buildPageRequest(Integer defaultPageSize) {
        PageDomain pageDomain = buildPageRequest();
        if (pageDomain.getPageSize() == null || pageDomain.getPageSize() <= 0) {
            pageDomain.setPageSize(defaultPageSize);
        }
        return pageDomain;
    }
}
