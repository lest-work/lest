package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.Result;
import com.lest.modules.auth.entity.dto.LoginDTO;
import com.lest.modules.auth.entity.dto.PasswordChangeDTO;
import com.lest.modules.auth.entity.dto.TokenRefreshDTO;
import com.lest.modules.auth.entity.vo.CaptchaVO;
import com.lest.modules.auth.entity.vo.CurrentUserVO;
import com.lest.modules.auth.entity.vo.LoginVO;
import com.lest.modules.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ---- Mapper mock（避免 @MapperScan 触发 sqlSessionFactory 依赖）----
    @MockitoBean private com.lest.modules.auth.mapper.AuthCaptchaMapper authCaptchaMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysUserMapper sysUserMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysRoleMapper sysRoleMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysMenuMapper sysMenuMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysOrganizationMapper sysOrganizationMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysDictionaryMapper sysDictionaryMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysDictionaryDataMapper sysDictionaryDataMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysRoleMenuMapper sysRoleMenuMapper;
    @MockitoBean private com.lest.modules.auth.mapper.SysUserRoleMapper sysUserRoleMapper;

    @MockitoBean
    private AuthService authService;

    @Nested
    @DisplayName("GET /auth/captcha - 获取验证码")
    class GetCaptcha {

        @Test
        @DisplayName("成功获取验证码")
        void shouldReturnCaptcha() throws Exception {
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setUuid("test-uuid-123");
            captchaVO.setImage("data:image/png;base64,abc123");
            when(authService.getCaptcha()).thenReturn(captchaVO);

            mockMvc.perform(get("/auth/captcha"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.uuid").value("test-uuid-123"))
                    .andExpect(jsonPath("$.data.image").value("data:image/png;base64,abc123"));

            verify(authService, times(1)).getCaptcha();
        }
    }

    @Nested
    @DisplayName("POST /auth/login - 用户登录")
    class Login {

        @Test
        @DisplayName("登录成功返回Token")
        void shouldLoginSuccessfully() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("admin");
            loginDTO.setPassword("password123");
            loginDTO.setCaptcha("1234");
            loginDTO.setUuid("uuid-123");

            LoginVO loginVO = LoginVO.builder()
                    .accessToken("access-token-xyz")
                    .refreshToken("refresh-token-abc")
                    .expiresIn(900L)
                    .tokenType("Bearer")
                    .build();

            when(authService.login(any(LoginDTO.class))).thenReturn(loginVO);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"))
                    .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-abc"))
                    .andExpect(jsonPath("$.data.expiresIn").value(900));

            verify(authService, times(1)).login(any(LoginDTO.class));
        }

        @Test
        @DisplayName("用户名密码为空时登录失败")
        void shouldFailWhenCredentialsEmpty() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("");
            loginDTO.setPassword("");

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh - 刷新Token")
    class Refresh {

        @Test
        @DisplayName("刷新Token成功")
        void shouldRefreshToken() throws Exception {
            TokenRefreshDTO dto = new TokenRefreshDTO();
            dto.setRefreshToken("old-refresh-token");

            LoginVO loginVO = LoginVO.builder()
                    .accessToken("new-access-token")
                    .refreshToken("new-refresh-token")
                    .expiresIn(900L)
                    .tokenType("Bearer")
                    .build();

            when(authService.refreshToken(any(TokenRefreshDTO.class))).thenReturn(loginVO);

            mockMvc.perform(post("/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));

            verify(authService, times(1)).refreshToken(any(TokenRefreshDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /auth/current-user - 获取当前用户信息")
    class GetCurrentUser {

        @Test
        @DisplayName("成功获取当前用户信息")
        void shouldReturnCurrentUser() throws Exception {
            CurrentUserVO currentUserVO = CurrentUserVO.builder()
                    .id(1L)
                    .username("admin")
                    .nickname("管理员")
                    .email("admin@example.com")
                    .avatar("http://example.com/avatar.png")
                    .orgId(1L)
                    .orgName("技术部")
                    .build();

            when(authService.getCurrentUser()).thenReturn(currentUserVO);

            mockMvc.perform(get("/auth/current-user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("admin"))
                    .andExpect(jsonPath("$.data.nickname").value("管理员"));

            verify(authService, times(1)).getCurrentUser();
        }
    }

    @Nested
    @DisplayName("PUT /auth/password - 修改密码")
    class ChangePassword {

        @Test
        @DisplayName("修改密码成功")
        void shouldChangePassword() throws Exception {
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setOldPassword("oldPass123");
            dto.setNewPassword("newPass456");

            doNothing().when(authService).changePassword(any(PasswordChangeDTO.class));

            mockMvc.perform(put("/auth/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).changePassword(any(PasswordChangeDTO.class));
        }

        @Test
        @DisplayName("新旧密码为空时修改失败")
        void shouldFailWhenPasswordEmpty() throws Exception {
            PasswordChangeDTO dto = new PasswordChangeDTO();
            dto.setOldPassword("");
            dto.setNewPassword("");

            mockMvc.perform(put("/auth/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /auth/logout - 登出")
    class Logout {

        @Test
        @DisplayName("登出成功")
        void shouldLogout() throws Exception {
            doNothing().when(authService).logout(any(HttpServletRequest.class));

            mockMvc.perform(post("/auth/logout"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).logout(any(HttpServletRequest.class));
        }
    }
}
