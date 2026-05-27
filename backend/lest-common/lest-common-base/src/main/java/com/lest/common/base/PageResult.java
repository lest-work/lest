package com.lest.common.base;

import lombok.Data;

import java.util.List;

/**
 * 分页结果封装
 *
 * @author Lest
 * @since 2026-05-26
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;
    private int pages;
    private boolean hasPrevious;
    private boolean hasNext;

    public static <T> PageResult<T> of(List<T> records, long total, int page, int size) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        result.setHasPrevious(page > 1);
        result.setHasNext(page < result.getPages());
        return result;
    }

    public static <T> PageResult<T> empty(int page, int size) {
        return of(List.of(), 0, page, size);
    }
}
