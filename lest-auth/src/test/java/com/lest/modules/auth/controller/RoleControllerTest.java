package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.RoleDTO;
import com.lest.modules.auth.entity.vo.RoleVO;
import com.lest.modules.auth.service.RoleService;
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
class RoleControllerTest {

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
    private RoleService roleService;

    private RoleVO mockRoleVO(Long id) {
        return RoleVO.builder()
                .id(id)
                .roleCode("ROLE_" + id)
                .roleName("角色" + id)
                .description("这是角色" + id + "的描述")
                .status(1)
                .isSuper(0)
                .sort(id.intValue())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /auth/role/page - 分页查询角色")
    class Page {

        @Test
        @DisplayName("默认分页参数")
        void shouldPageWithDefaults() throws Exception {
            List<RoleVO> records = Arrays.asList(mockRoleVO(1L), mockRoleVO(2L));
            PageResult<RoleVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(roleService.page(1, 10, null, null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/role/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].roleCode").value("ROLE_1"));
        }

        @Test
        @DisplayName("按角色编码筛选")
        void shouldPageWithRoleCode() throws Exception {
            List<RoleVO> records = Arrays.asList(mockRoleVO(1L));
            PageResult<RoleVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(roleService.page(1, 10, "ADMIN", null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/role/page")
                            .param("roleCode", "ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));
        }
    }

    @Nested
    @DisplayName("GET /auth/role - 查询所有角色")
    class ListAll {

        @Test
        @DisplayName("返回所有角色列表")
        void shouldListAll() throws Exception {
            List<RoleVO> roles = Arrays.asList(mockRoleVO(1L), mockRoleVO(2L));
            when(roleService.listAll()).thenReturn(roles);

            mockMvc.perform(get("/auth/role"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2));
        }
    }

    @Nested
    @DisplayName("GET /auth/role/{id} - 根据ID查询角色")
    class GetById {

        @Test
        @DisplayName("查询存在的角色")
        void shouldGetById() throws Exception {
            when(roleService.getById(1L)).thenReturn(mockRoleVO(1L));

            mockMvc.perform(get("/auth/role/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.roleCode").value("ROLE_1"));
        }
    }

    @Nested
    @DisplayName("POST /auth/role - 创建角色")
    class Create {

        @Test
        @DisplayName("成功创建角色")
        void shouldCreateRole() throws Exception {
            RoleDTO dto = new RoleDTO();
            dto.setRoleCode("TEST_ROLE");
            dto.setRoleName("测试角色");
            dto.setDescription("测试角色描述");
            dto.setStatus(1);
            dto.setSort(1);

            when(roleService.create(any(RoleDTO.class))).thenReturn(100L);

            mockMvc.perform(post("/auth/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));
        }

        @Test
        @DisplayName("角色编码格式不正确时创建失败")
        void shouldFailWithInvalidRoleCode() throws Exception {
            RoleDTO dto = new RoleDTO();
            dto.setRoleCode("123-invalid"); // 包含数字开头

            mockMvc.perform(post("/auth/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("角色名称为空时创建失败")
        void shouldFailWithEmptyRoleName() throws Exception {
            RoleDTO dto = new RoleDTO();
            dto.setRoleCode("VALID_CODE");

            mockMvc.perform(post("/auth/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /auth/role - 更新角色")
    class Update {

        @Test
        @DisplayName("成功更新角色")
        void shouldUpdateRole() throws Exception {
            RoleDTO dto = new RoleDTO();
            dto.setId(1L);
            dto.setRoleCode("UPDATED_ROLE");
            dto.setRoleName("更新后的角色");

            doNothing().when(roleService).update(any(RoleDTO.class));

            mockMvc.perform(put("/auth/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(roleService, times(1)).update(any(RoleDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/role/{id} - 删除角色")
    class Delete {

        @Test
        @DisplayName("删除单个角色")
        void shouldDeleteSingleRole() throws Exception {
            doNothing().when(roleService).delete(1L, null);

            mockMvc.perform(delete("/auth/role/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(roleService, times(1)).delete(1L, null);
        }

        @Test
        @DisplayName("批量删除角色")
        void shouldBatchDeleteRoles() throws Exception {
            doNothing().when(roleService).delete(eq(1L), anyList());

            mockMvc.perform(delete("/auth/role/1")
                            .param("ids", "2,3,4"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(roleService).delete(eq(1L), argThat(list ->
                list != null && list.size() == 3 && list.contains(2L) && list.contains(3L) && list.contains(4L)
            ));
        }
    }
}
