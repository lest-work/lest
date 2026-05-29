package com.lest.common.core.web.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseEntity 基类
 *
 * @author yshan2028
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 请求参数 */
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }
}
