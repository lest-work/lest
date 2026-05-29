package com.lest.common.log.filter;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

/**
 * 过滤敏感字段
 *
 * @author yshan2028
 */
public class PropertyPreExcludeFilter extends SimpleBeanPropertyFilter {

    private final String[] excludeProperties;

    public PropertyPreExcludeFilter(String... excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    @Override
    protected boolean include(com.fasterxml.jackson.databind.ser.BeanPropertyWriter writer) {
        return !isExcluded(writer.getName());
    }

    @Override
    protected boolean include(com.fasterxml.jackson.databind.ser.PropertyWriter writer) {
        return !isExcluded(writer.getName());
    }

    private boolean isExcluded(String name) {
        for (String excluded : excludeProperties) {
            if (excluded.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
