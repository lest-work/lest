package com.lest.common.core.utils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分页工具类
 *
 * @author yshan2028
 */
public class PageUtils {

    public static <T> PageInfo<T> startPage(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    public static void clearPage() {
        PageHelper.clearPage();
    }
}
