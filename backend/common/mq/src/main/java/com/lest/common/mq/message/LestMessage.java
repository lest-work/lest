package com.lest.common.mq.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LEST 消息基类
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LestMessage implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String messageId;
    private String topic;
    private String key;
    private String type;
    private String source;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    private Map<String, Object> headers = new HashMap<>();
    private Object body;

    public LestMessage() {
        this.timestamp = new Date();
    }

    public LestMessage(String type, Object body) {
        this();
        this.type = type;
        this.body = body;
    }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public Map<String, Object> getHeaders() { return headers; }
    public void setHeaders(Map<String, Object> headers) { this.headers = headers; }
    public Object getBody() { return body; }
    public void setBody(Object body) { this.body = body; }

    public void putHeader(String key, Object value) { headers.put(key, value); }
    public Object getHeader(String key) { return headers.get(key); }
}
