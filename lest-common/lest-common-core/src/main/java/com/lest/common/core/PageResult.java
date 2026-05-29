package com.lest.common.core;

import java.util.List;

/**
 * 分页结果封装（支持 MyBatis Plus IPage）
 *
 * @author yshan2028
 */
public class PageResult<T> {

    private List<T> records;
    private long total;
    private int page;
    private int size;
    private int pages;
    private boolean hasPrevious;
    private boolean hasNext;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

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

    public static <T> PageResult<T> of(List<T> list) {
        return of(list, list.size(), 1, list.size());
    }
}
