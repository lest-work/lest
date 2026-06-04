package com.lest.common.security.aspect;

import com.lest.common.security.annotation.RequireProjectRole;
import com.lest.common.security.permission.ProjectRoleChecker;
import com.lest.common.security.utils.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @RequireProjectRole 切面实现<br>
 * 在方法执行前校验当前用户是否属于指定项目的成员/角色。
 *
 * @author yshan2028
 */
@Aspect
@Component
public class RequireProjectRoleAspect
{
    private static final Logger log = LoggerFactory.getLogger(RequireProjectRoleAspect.class);

    @Autowired(required = false)
    private ProjectRoleChecker projectRoleChecker;

    @Before("@annotation(com.lest.common.security.annotation.RequireProjectRole)")
    public void before(JoinPoint point) throws Throwable
    {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RequireProjectRole annotation = method.getAnnotation(RequireProjectRole.class);

        if (annotation == null)
        {
            return;
        }

        // 如果没有 ProjectRoleChecker 实现，跳过检查（允许访问）
        if (projectRoleChecker == null)
        {
            log.warn("[RequireProjectRole] ProjectRoleChecker not available, allowing access");
            return;
        }

        // 获取当前用户 ID
        Long userId = SecurityUtils.getUserId();
        if (userId == null)
        {
            throw new com.lest.common.core.exception.ServiceException("用户未登录");
        }

        // 提取 projectId
        Long projectId = extractProjectId(point, signature, method);
        if (projectId == null)
        {
            log.warn("[RequireProjectRole] Cannot extract projectId from method parameters");
            return;
        }

        // 平台管理员跳过项目权限检查
        if (SecurityUtils.isPlatformAdmin())
        {
            log.debug("[RequireProjectRole] Platform admin, skipping project role check");
            return;
        }

        // 如果需要检查成员身份
        if (annotation.checkMembership())
        {
            if (!projectRoleChecker.isMember(projectId, userId))
            {
                log.warn("[RequireProjectRole] User {} is not a member of project {}", userId, projectId);
                throw new com.lest.common.core.exception.ServiceException("您不是该项目成员，无权访问");
            }
        }

        // 构建所需角色集合（合并 value 和 roles）
        java.util.Set<String> required = new java.util.HashSet<>();
        String singleRole = annotation.value();
        if (singleRole != null && !singleRole.isEmpty() && !"MEMBER".equals(singleRole))
        {
            required.add(singleRole);
        }
        String[] roles = annotation.roles();
        if (roles != null && roles.length > 0)
        {
            for (String r : roles)
            {
                if (r != null && !r.isEmpty())
                {
                    required.add(r);
                }
            }
        }

        if (required.isEmpty())
        {
            // 无特定角色要求，成员身份已满足
            return;
        }

        Set<String> userRoles = projectRoleChecker.getUserRoles(projectId, userId);
        if (userRoles == null || userRoles.isEmpty())
        {
            log.warn("[RequireProjectRole] User {} has no roles in project {}", userId, projectId);
            throw new com.lest.common.core.exception.ServiceException("您没有足够的项目权限");
        }

        for (String req : required)
        {
            if (userRoles.contains(req))
            {
                return; // 至少有一个角色匹配
            }
        }

        log.warn("[RequireProjectRole] User {} lacks required roles {} in project {}", userId, required, projectId);
        throw new com.lest.common.core.exception.ServiceException("您没有足够的项目权限");
    }

    /**
     * 从方法参数中提取 projectId<br>
     * 优先查找：path variable named "projectId" → 参数名为 "projectId" 的 Long 类型参数
     */
    private Long extractProjectId(JoinPoint point, MethodSignature signature, Method method)
    {
        Object[] args = point.getArgs();
        String[] paramNames = signature.getParameterNames();
        Class<?>[] paramTypes = signature.getParameterTypes();
        Annotation[][] paramAnnotations = signature.getMethod().getParameterAnnotations();

        for (int i = 0; i < paramNames.length; i++)
        {
            // 查找名为 projectId 的参数
            if ("projectId".equals(paramNames[i]) && paramTypes[i] == Long.class)
            {
                Object arg = args[i];
                if (arg instanceof Long)
                {
                    return (Long) arg;
                }
                if (arg instanceof Number)
                {
                    return ((Number) arg).longValue();
                }
            }
        }

        // 查找 @org.springframework.web.bind.annotation.PathVariable("projectId") 或 @RequestParam("projectId")
        for (int i = 0; i < paramAnnotations.length; i++)
        {
            for (Annotation ann : paramAnnotations[i])
            {
                String varName = getBindingVariableName(ann);
                if ("projectId".equals(varName) && args[i] != null)
                {
                    if (args[i] instanceof Long)
                    {
                        return (Long) args[i];
                    }
                    if (args[i] instanceof Number)
                    {
                        return ((Number) args[i]).longValue();
                    }
                }
            }
        }

        return null;
    }

    private String getBindingVariableName(Annotation ann)
    {
        try
        {
            Method value = ann.annotationType().getMethod("value");
            Object val = value.invoke(ann);
            if (val instanceof String && !((String) val).isEmpty())
            {
                return (String) val;
            }
        }
        catch (Exception ignored) {}
        return null;
    }
}
