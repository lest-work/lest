package com.lest.common.core.web.page;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import com.lest.common.core.constant.HttpStatus;

/**
 * 表格分页数据对象
 * 
 * @author yshan2028
 */
public class TableDataInfo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> records;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /**
     * 表格数据对象
     */
    public TableDataInfo()
    {
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<?> list, long total)
    {
        this.records = list;
        this.total = total;
    }

    public static TableDataInfo empty()
    {
        TableDataInfo data = new TableDataInfo();
        data.setCode(HttpStatus.SUCCESS);
        data.setMsg("查询成功");
        data.setRecords(Collections.emptyList());
        data.setTotal(0L);
        return data;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<?> getRecords()
    {
        return records;
    }

    public void setRecords(List<?> records)
    {
        this.records = records;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}