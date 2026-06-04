package com.lest.common.core.exception.user;

import com.lest.common.core.exception.base.BaseException;

/**
 * 用户信息异常类
 * 
 * @author yshan2028
 */
public class UserException extends BaseException
{
    private static final long serialVersionUID = 1L;

    public UserException(String code, Object[] args)
    {
        super("user", code, args, null);
    }
}
