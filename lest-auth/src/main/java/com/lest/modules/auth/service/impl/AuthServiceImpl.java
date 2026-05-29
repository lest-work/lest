package com.lest.modules.auth.service.impl;

import com.lest.common.core.Assert;
import com.lest.modules.auth.common.ErrorCode;
import com.lest.modules.auth.entity.domain.SysOrganization;
import com.lest.modules.auth.entity.domain.SysRole;
import com.lest.modules.auth.entity.domain.SysUser;
import com.lest.modules.auth.entity.dto.LoginDTO;
import com.lest.modules.auth.entity.dto.PasswordChangeDTO;
import com.lest.modules.auth.entity.dto.TokenRefreshDTO;
import com.lest.modules.auth.entity.vo.CaptchaVO;
import com.lest.modules.auth.entity.vo.CurrentUserVO;
import com.lest.modules.auth.entity.vo.LoginVO;
import com.lest.modules.auth.entity.vo.RouteVO;
import com.lest.modules.auth.mapper.SysOrganizationMapper;
import com.lest.modules.auth.mapper.SysRoleMapper;
import com.lest.modules.auth.mapper.SysUserMapper;
import com.lest.modules.auth.security.JwtTokenProvider;
import com.lest.common.core.constant.CacheConstants;
import com.lest.common.core.constant.SecurityConstants;
import com.lest.common.core.utils.ServletUtils;
import com.lest.common.security.util.LoginUser;
import com.lest.modules.auth.service.AuthService;
import com.lest.modules.auth.service.MenuService;
import com.lest.modules.auth.service.UserService;
import com.lest.system.api.RemoteLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 认证服务实现
 *
 * @author yshan2028
 * @since 2026-05-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String CAPTCHA_CACHE_PREFIX = "captcha:";
    private static final int CAPTCHA_EXPIRE_SECONDS = 300;
    private static final int CAPTCHA_LENGTH = 4;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,32}$");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysOrganizationMapper organizationMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;
    private final MenuService menuService;
    private final RemoteLogService remoteLogService;

    @Value("${captcha.width:120}")
    private int captchaWidth;

    @Value("${captcha.height:40}")
    private int captchaHeight;

    /** 开发模式开关，跳过验证码验证 */
    @Value("${dev-mode.enabled:false}")
    private boolean devModeEnabled;

    @Override
    public CaptchaVO getCaptcha() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String code = generateCaptchaCode();

        String cacheKey = CAPTCHA_CACHE_PREFIX + uuid;
        redisTemplate.opsForValue().set(cacheKey, code, CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        String imageBase64 = generateCaptchaImage(code);

        log.debug("生成验证码: uuid={}", uuid);

        return CaptchaVO.builder()
                .uuid(uuid)
                .image(imageBase64)
                .expireSeconds((long) CAPTCHA_EXPIRE_SECONDS)
                .build();
    }

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String uuid = loginDTO.getUuid();
        String captcha = loginDTO.getCaptcha();

        // 开发模式下跳过验证码
        if (!devModeEnabled) {
            validateCaptcha(uuid, captcha);
        } else {
            log.warn("DEV_MODE: 跳过验证码验证");
        }

        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        Assert.notNull(user, ErrorCode.AUTH_USERNAME_PASSWORD_ERROR);
        Assert.isTrue(passwordEncoder.matches(password, user.getPassword()),
                ErrorCode.AUTH_USERNAME_PASSWORD_ERROR);
        Assert.isTrue(user.getStatus() == 1, ErrorCode.AUTH_ACCOUNT_DISABLED);

        // 删除验证码
        redisTemplate.delete(CAPTCHA_CACHE_PREFIX + uuid);

        // 更新最后登录时间
        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(updateUser);

        // 获取用户角色
        List<Long> roleIds = userService.getRoleIds(user.getId());
        String[] roles = roleIds.stream()
                .map(id -> {
                    SysRole role = roleMapper.selectById(id);
                    return role != null ? role.getRoleCode() : "";
                })
                .filter(r -> !r.isEmpty())
                .toArray(String[]::new);

        // 生成Token
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getNickname(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        log.info("用户登录成功: userId={}, username={}", user.getId(), username);
        recordLoginLog(username, 1, "登录成功");
        return LoginVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public LoginVO refreshToken(TokenRefreshDTO dto) {
        String refreshToken = dto.getRefreshToken();

        Assert.isTrue(jwtTokenProvider.validateToken(refreshToken), ErrorCode.AUTH_TOKEN_INVALID);
        Assert.isTrue(jwtTokenProvider.isRefreshToken(refreshToken), ErrorCode.AUTH_TOKEN_INVALID);
        Assert.isFalse(jwtTokenProvider.isRefreshTokenBlacklisted(refreshToken),
                ErrorCode.AUTH_REFRESH_TOKEN_USED);

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);
        Assert.isTrue(user.getStatus() == 1, ErrorCode.AUTH_ACCOUNT_DISABLED);

        // 将旧RefreshToken加入黑名单
        jwtTokenProvider.blacklistRefreshToken(refreshToken);

        // 获取用户角色
        List<Long> roleIds = userService.getRoleIds(userId);
        String[] roles = roleIds.stream()
                .map(id -> {
                    SysRole role = roleMapper.selectById(id);
                    return role != null ? role.getRoleCode() : "";
                })
                .filter(r -> !r.isEmpty())
                .toArray(String[]::new);

        // 生成新的Token
        String newAccessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getUsername(), user.getNickname(), roles);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        log.info("刷新Token成功: userId={}", userId);

        return LoginVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public CurrentUserVO getCurrentUser() {
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();

        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        // 获取角色列表
        List<Long> roleIds = userService.getRoleIds(userId);
        String[] roleNames = roleIds.stream()
                .map(id -> {
                    SysRole role = roleMapper.selectById(id);
                    return role != null ? role.getRoleCode() : "";
                })
                .filter(r -> !r.isEmpty())
                .toArray(String[]::new);

        // 获取权限列表
        List<String> permissions = menuService.getUserPermissions(userId);

        // 获取路由列表
        List<RouteVO> routes = menuService.getRoutes(userId);

        // 获取机构名称
        String orgName = null;
        if (user.getOrgId() != null) {
            SysOrganization org = organizationMapper.selectById(user.getOrgId());
            orgName = org != null ? org.getOrgName() : null;
        }

        return CurrentUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .sex(user.getSex())
                .orgId(user.getOrgId())
                .orgName(orgName)
                .roles(List.of(roleNames))
                .permissions(permissions)
                .routes(routes)
                .build();
    }

    @Override
    public void changePassword(PasswordChangeDTO dto) {
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();

        SysUser user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.USER_NOT_FOUND);

        Assert.isTrue(passwordEncoder.matches(dto.getOldPassword(), user.getPassword()),
                ErrorCode.AUTH_OLD_PASSWORD_ERROR);
        Assert.isTrue(PASSWORD_PATTERN.matcher(dto.getNewPassword()).matches(),
                ErrorCode.AUTH_PASSWORD_FORMAT_INVALID);
        Assert.isFalse(passwordEncoder.matches(dto.getNewPassword(), user.getPassword()),
                ErrorCode.AUTH_PASSWORD_SAME_AS_OLD);

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(updateUser);

        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    public void logout(HttpServletRequest request) {
        LoginUser loginUser = getLoginUser();
        String token = extractBearerToken(request);
        if (token != null) {
            try {
                if (jwtTokenProvider.validateToken(token) && jwtTokenProvider.isAccessToken(token)) {
                    jwtTokenProvider.blacklistAccessToken(token);
                }
            } catch (Exception e) {
                log.warn("登出时 access token 黑名单处理失败: {}", e.getMessage());
            }
        }
        log.info("用户登出: userId={}", loginUser.getUserId());
        recordLoginLog(loginUser.getUsername(), 1, "退出成功");
        SecurityContextHolder.clearContext();
    }

    /** 记录登录日志 */
    private void recordLoginLog(String username, Integer status, String msg) {
        try {
            jakarta.servlet.http.HttpServletRequest request =
                    (jakarta.servlet.http.HttpServletRequest) ServletUtils.getRequest();
            String ip = ServletUtils.getClientIP(request);
            remoteLogService.saveLoginLog(username, status, msg, ip, SecurityConstants.INNER);
        } catch (Exception e) {
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }

    /** 验证验证码 */
    private void validateCaptcha(String uuid, String captcha) {
        String cacheKey = CAPTCHA_CACHE_PREFIX + uuid;
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);

        Assert.notNull(cachedCode, ErrorCode.AUTH_CAPTCHA_NOT_FOUND);
        Assert.isTrue(cachedCode.equalsIgnoreCase(captcha), ErrorCode.AUTH_CAPTCHA_ERROR);

        redisTemplate.delete(cacheKey);
    }

    /** 生成验证码内容（使用 SecureRandom 防止预测） */
    private String generateCaptchaCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(chars.length());
            code.append(chars.charAt(index));
        }
        return code.toString();
    }

    /** 生成验证码图片（Base64编码） */
    private String generateCaptchaImage(String code) {
        try {
            BufferedImage image = new BufferedImage(captchaWidth, captchaHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, captchaWidth, captchaHeight);

            g.setFont(new Font("Arial", Font.BOLD, 24));

            g.setColor(Color.BLACK);
            int x = 10;
            for (int i = 0; i < code.length(); i++) {
                g.drawString(String.valueOf(code.charAt(i)), x, 28);
                x += 25;
            }

            g.setColor(new Color(200, 200, 200));
            for (int i = 0; i < 5; i++) {
                int x1 = (int) (Math.random() * captchaWidth);
                int y1 = (int) (Math.random() * captchaHeight);
                int x2 = (int) (Math.random() * captchaWidth);
                int y2 = (int) (Math.random() * captchaHeight);
                g.drawLine(x1, y1, x2, y2);
            }

            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            log.error("生成验证码图片失败", e);
            throw new RuntimeException("生成验证码图片失败");
        }
    }

    /** 从请求头提取 Bearer Token */
    private String extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /** 获取当前登录用户 */
    private LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            return (LoginUser) authentication.getPrincipal();
        }
        throw new RuntimeException("用户未登录");
    }
}
