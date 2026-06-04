package com.lest.openapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.util.Date;

/**
 * Webhook端点对象 webhook_endpoint
 *
 * @author yshan2028
 */
public class WebhookEndpoint implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 端点ID */
    private Long webhookId;

    /** 用户ID */
    private Long userId;

    /** 端点名称 */
    private String name;

    /** 端点URL */
    private String url;

    /** 密钥 */
    private String secret;

    /** 事件类型 */
    private String events;

    /** 是否启用 */
    private Integer isEnabled;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    public Long getWebhookId()
    {
        return webhookId;
    }

    public void setWebhookId(Long webhookId)
    {
        this.webhookId = webhookId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }

    public String getEvents()
    {
        return events;
    }

    public void setEvents(String events)
    {
        this.events = events;
    }

    public Integer getIsEnabled()
    {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled)
    {
        this.isEnabled = isEnabled;
    }

    public Date getCreateAt()
    {
        return createAt;
    }

    public void setCreateAt(Date createAt)
    {
        this.createAt = createAt;
    }

    public Date getUpdateAt()
    {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt)
    {
        this.updateAt = updateAt;
    }
}
