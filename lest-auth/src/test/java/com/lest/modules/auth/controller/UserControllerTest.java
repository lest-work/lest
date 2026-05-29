package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.UserDTO;
import com.lest.modules.auth.entity.vo.UserVO;
import com.lest.modules.auth.service.UserService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class UserControllerTest {

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
    private UserService userService;

    private UserVO mockUserVO(Long id) {
        return UserVO.builder()
                .id(id)
                .username("user_" + id)
                .nickname("用户" + id)
                .email("user" + id + "@example.com")
                .phone("1380000000" + id)
                .status(1)
                .orgId(1L)
                .orgName("技术部")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /auth/user/page - 分页查询用户")
    class Page {

        @Test
        @DisplayName("默认分页参数查询")
        void shouldPageWithDefaults() throws Exception {
            List<UserVO> records = Arrays.asList(mockUserVO(1L), mockUserVO(2L));
            PageResult<UserVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(userService.page(1, 10, null, null, null, null, null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/user/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].username").value("user_1"))
                    .andExpect(jsonPath("$.data.records[1].username").value("user_2"));
        }

        @Test
        @DisplayName("按用户名和状态筛选")
        void shouldPageWithFilters() throws Exception {
            List<UserVO> records = Arrays.asList(mockUserVO(1L));
            PageResult<UserVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(userService.page(1, 10, "admin", null, null, null, 1, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/user/page")
                            .param("username", "admin")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));

            verify(userService).page(1, 10, "admin", null, null, null, 1, null);
        }
    }

    @Nested
    @DisplayName("GET /auth/user - 查询所有用户")
    class ListAll {

        @Test
        @DisplayName("返回所有用户列表")
        void shouldListAll() throws Exception {
            List<UserVO> users = Arrays.asList(mockUserVO(1L), mockUserVO(2L), mockUserVO(3L));
            when(userService.listAll()).thenReturn(users);

            mockMvc.perform(get("/auth/user"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(3));
        }
    }

    @Nested
    @DisplayName("GET /auth/user/{id} - 根据ID查询用户")
    class GetById {

        @Test
        @DisplayName("查询存在的用户")
        void shouldGetById() throws Exception {
            when(userService.getById(1L)).thenReturn(mockUserVO(1L));

            mockMvc.perform(get("/auth/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("user_1"));
        }

        @Test
        @DisplayName("查询不存在的用户")
        void shouldReturnNullWhenNotFound() throws Exception {
            when(userService.getById(999L)).thenReturn(null);

            mockMvc.perform(get("/auth/user/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /auth/user/{id}/roles - 获取用户角色ID列表")
    class GetRoleIds {

        @Test
        @DisplayName("获取用户角色ID")
        void shouldGetRoleIds() throws Exception {
            when(userService.getRoleIds(1L)).thenReturn(Arrays.asList(1L, 2L, 3L));

            mockMvc.perform(get("/auth/user/1/roles"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0]").value(1))
                    .andExpect(jsonPath("$.data[1]").value(2))
                    .andExpect(jsonPath("$.data[2]").value(3));
        }
    }

    @Nested
    @DisplayName("POST /auth/user - 创建用户")
    class Create {

        @Test
        @DisplayName("成功创建用户")
        void shouldCreateUser() throws Exception {
            UserDTO dto = new UserDTO();
            dto.setUsername("newuser");
            dto.setNickname("新用户");
            dto.setEmail("new@example.com");
            dto.setPhone("13900000000");
            dto.setStatus(1);
            dto.setOrgId(1L);

            when(userService.create(any(UserDTO.class))).thenReturn(100L);

            mockMvc.perform(post("/auth/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));

            verify(userService, times(1)).create(any(UserDTO.class));
        }

        @Test
        @DisplayName("用户名格式不正确时创建失败")
        void shouldFailWithInvalidUsername() throws Exception {
            UserDTO dto = new UserDTO();
            dto.setUsername("ab"); // 少于4位

            mockMvc.perform(post("/auth/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("邮箱格式不正确时创建失败")
        void shouldFailWithInvalidEmail() throws Exception {
            UserDTO dto = new UserDTO();
            dto.setUsername("validuser");
            dto.setEmail("not-an-email");

            mockMvc.perform(post("/auth/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /auth/user - 更新用户")
    class Update {

        @Test
        @DisplayName("成功更新用户")
        void shouldUpdateUser() throws Exception {
            UserDTO dto = new UserDTO();
            dto.setId(1L);
            dto.setUsername("admin");
            dto.setNickname("管理员更新");
            dto.setEmail("admin@example.com");

            doNothing().when(userService).update(any(UserDTO.class));

            mockMvc.perform(put("/auth/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).update(any(UserDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/user/{id} - 删除用户")
    class Delete {

        @Test
        @DisplayName("成功删除用户")
        void shouldDeleteUser() throws Exception {
            doNothing().when(userService).delete(1L);

            mockMvc.perform(delete("/auth/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).delete(1L);
        }
    }

    @Nested
    @DisplayName("DELETE /auth/user/batch - 批量删除用户")
    class BatchDelete {

        @Test
        @DisplayName("批量删除用户")
        void shouldBatchDelete() throws Exception {
            Long[] ids = {1L, 2L, 3L};
            doNothing().when(userService).batchDelete(ids);

            mockMvc.perform(delete("/auth/user/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).batchDelete(ids);
        }
    }

    @Nested
    @DisplayName("PUT /auth/user/{id}/status - 修改用户状态")
    class UpdateStatus {

        @Test
        @DisplayName("启用用户")
        void shouldEnableUser() throws Exception {
            doNothing().when(userService).updateStatus(1L, 1);

            mockMvc.perform(put("/auth/user/1/status")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).updateStatus(1L, 1);
        }

        @Test
        @DisplayName("禁用用户")
        void shouldDisableUser() throws Exception {
            doNothing().when(userService).updateStatus(1L, 0);

            mockMvc.perform(put("/auth/user/1/status")
                            .param("status", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).updateStatus(1L, 0);
        }
    }

    @Nested
    @DisplayName("PUT /auth/user/{id}/password - 重置用户密码")
    class ResetPassword {

        @Test
        @DisplayName("重置为默认密码")
        void shouldResetToDefaultPassword() throws Exception {
            doNothing().when(userService).resetPassword(1L, null);

            mockMvc.perform(put("/auth/user/1/password"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).resetPassword(1L, null);
        }

        @Test
        @DisplayName("重置为指定密码")
        void shouldResetToSpecificPassword() throws Exception {
            doNothing().when(userService).resetPassword(1L, "customPass123");

            mockMvc.perform(put("/auth/user/1/password")
                            .param("password", "customPass123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).resetPassword(1L, "customPass123");
        }
    }

    @Nested
    @DisplayName("PUT /auth/user/{id}/roles - 分配用户角色")
    class AssignRoles {

        @Test
        @DisplayName("成功分配角色")
        void shouldAssignRoles() throws Exception {
            Long[] roleIds = {1L, 2L};
            doNothing().when(userService).assignRoles(1L, roleIds);

            mockMvc.perform(put("/auth/user/1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(roleIds)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService, times(1)).assignRoles(1L, roleIds);
        }
    }
}
