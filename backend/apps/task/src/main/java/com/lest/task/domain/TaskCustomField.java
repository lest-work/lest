package com.lest.task.domain;

import java.io.Serializable;

/**
 * 任务自定义字段对象 task_custom_field
 *
 * @author yshan2028
 */
public class TaskCustomField implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 字段ID */
    private Long fieldId;

    /** 字段名称 */
    private String fieldName;

    /** 字段Key */
    private String fieldKey;

    /** 字段类型 */
    private String fieldType;

    /** 字段选项 */
    private String fieldOptions;

    /** 是否必填 */
    private Boolean required;

    /** 排序 */
    private Integer sortOrder;

    public Long getFieldId()
    {
        return fieldId;
    }

    public void setFieldId(Long fieldId)
    {
        this.fieldId = fieldId;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getFieldKey()
    {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey)
    {
        this.fieldKey = fieldKey;
    }

    public String getFieldType()
    {
        return fieldType;
    }

    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }

    public String getFieldOptions()
    {
        return fieldOptions;
    }

    public void setFieldOptions(String fieldOptions)
    {
        this.fieldOptions = fieldOptions;
    }

    public Boolean getRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }
}
