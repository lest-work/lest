package com.lest.common.datascope.aspect;

import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.lest.common.core.constant.Constants;
import com.lest.common.core.utils.SecurityContextHolder;
import com.lest.common.core.utils.StringUtils;
import com.lest.common.core.web.domain.BaseEntity;
import com.lest.common.datascope.annotation.DataScope;
import com.lest.common.security.util.SecurityUtils;
import com.lest.common.security.util.LoginUser;

/**
 * 数据过滤处理
 *
 * @author yshan2028
 */
@Aspect
@Component
public class DataScopeAspect {

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser)) {
            Long userId = loginUser.getUserId();
            if (userId != null && userId != 1L) {
                String permission = StringUtils.defaultIfEmpty(controllerDataScope.permission(), SecurityContextHolder.getPermission());
                dataScopeFilter(joinPoint, loginUser, controllerDataScope.userAlias(), controllerDataScope.deptAlias(),
                        controllerDataScope.userField(), controllerDataScope.deptField(), permission);
            }
        }
    }

    /**
     * 数据范围过滤
     */
    public static void dataScopeFilter(JoinPoint joinPoint, LoginUser user, String userAlias, String deptAlias,
                                       String userField, String deptField, String permission) {
        StringBuilder sqlString = new StringBuilder();
        List<String> conditions = new ArrayList<>();
        List<String> scopeCustomIds = new ArrayList<>();

        if (user.getRoles() != null) {
            for (String role : user.getRoles()) {
                if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(role)) {
                    scopeCustomIds.add(role);
                }
            }
        }

        if (user.getRoles() != null) {
            for (String role : user.getRoles()) {
                String dataScope = role;
                if (conditions.contains(dataScope)) {
                    continue;
                }
                if (StringUtils.isNotEmpty(permission) && (user.getPermissions() == null || !user.getPermissions().contains(permission))) {
                    continue;
                }
                if (Constants.Dept.DATA_SCOPE_ALL.equals(dataScope)) {
                    sqlString = new StringBuilder();
                    conditions.add(dataScope);
                    break;
                } else if (Constants.Dept.DATA_SCOPE_CUSTOM.equals(dataScope)) {
                    if (scopeCustomIds.size() > 1) {
                        sqlString.append(StringUtils.format(" OR %s.%s IN ( SELECT dept_id FROM sys_role_dept WHERE role_id in (%s) ) ",
                                deptAlias, deptField, String.join(",", scopeCustomIds)));
                    } else {
                        sqlString.append(StringUtils.format(" OR %s.%s IN ( SELECT dept_id FROM sys_role_dept WHERE role_id = %s ) ",
                                deptAlias, deptField, String.join(",", scopeCustomIds)));
                    }
                } else if (Constants.Dept.DATA_SCOPE_DEPT.equals(dataScope)) {
                    sqlString.append(StringUtils.format(" OR %s.%s = %s ", deptAlias, deptField, user.getDeptId()));
                } else if (Constants.Dept.DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                    sqlString.append(StringUtils.format(" OR %s.%s IN ( SELECT dept_id FROM sys_dept WHERE dept_id = %s or find_in_set( %s , ancestors ) )",
                            deptAlias, deptField, user.getDeptId(), user.getDeptId()));
                } else if (Constants.Dept.DATA_SCOPE_SELF.equals(dataScope)) {
                    if (StringUtils.isNotBlank(userAlias)) {
                        sqlString.append(StringUtils.format(" OR %s.%s = %s ", userAlias, userField, user.getUserId()));
                    } else {
                        sqlString.append(StringUtils.format(" OR %s.%s = 0 ", deptAlias, deptField));
                    }
                }
                conditions.add(dataScope);
            }
        }

        if (StringUtils.isEmpty(conditions)) {
            sqlString.append(StringUtils.format(" OR %s.%s = 0 ", deptAlias, deptField));
        }

        if (StringUtils.isNotBlank(sqlString.toString())) {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity) {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put(DATA_SCOPE, " AND (" + sqlString.substring(4) + ")");
            }
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint) {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put(DATA_SCOPE, "");
        }
    }
}
