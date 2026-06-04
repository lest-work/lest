package com.lest.plugin.manager.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * 插件对象 plugin
 *
 * @author yshan2028
 */
public class Plugin implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 插件ID */
    private Long pluginId;

    /** 插件Key */
    private String pluginKey;

    /** 插件名称 */
    private String name;

    /** 版本号 */
    private String version;

    /** 描述 */
    private String description;

    /** 作者 */
    private String author;

    /** 是否启用 */
    private Integer isEnabled;

    /** 安装时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date installedAt;

    /** 启用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enabledAt;

    /** 插件清单 */
    private String manifest;

    public Long getPluginId()
    {
        return pluginId;
    }

    public void setPluginId(Long pluginId)
    {
        this.pluginId = pluginId;
    }

    public String getPluginKey()
    {
        return pluginKey;
    }

    public void setPluginKey(String pluginKey)
    {
        this.pluginKey = pluginKey;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Integer getIsEnabled()
    {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public Date getInstalledAt()
    {
        return installedAt;
    }

    public void setInstalledAt(Date installedAt)
    {
        this.installedAt = installedAt;
    }

    public Date getEnabledAt()
    {
        return enabledAt;
    }

    public void setEnabledAt(Date enabledAt)
    {
        this.enabledAt = enabledAt;
    }

    public String getManifest()
    {
        return manifest;
    }

    public void setManifest(String manifest)
    {
        this.manifest = manifest;
    }
}
