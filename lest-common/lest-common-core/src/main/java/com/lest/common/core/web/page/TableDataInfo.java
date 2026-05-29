package com.lest.common.core.web.page;

import com.github.pagehelper.Page;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据
 *
 * @author yshan2028
 */
public class TableDataInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private List<?> rows;
    private int code;
    private String msg;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> TableDataInfo build(List<T> list, long total) {
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("查询成功");
        data.setRows(list);
        data.setTotal(total);
        return data;
    }

    public static <T> TableDataInfo build(List<T> list) {
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("查询成功");
        data.setRows(list);
        data.setTotal(list.size());
        return data;
    }

    public static TableDataInfo build() {
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("查询成功");
        return data;
    }

    public static <T> TableDataInfo build(Page<?> page) {
        TableDataInfo data = new TableDataInfo();
        data.setCode(200);
        data.setMsg("查询成功");
        data.setRows(page.getResult());
        data.setTotal(page.getTotal());
        return data;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
