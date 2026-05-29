package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.service.MenuService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class RoleMenuControllerTest {

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

    @MockitoBean
    private MenuService menuService;

    private MenuVO mockMenuVO(Long id, Long parentId) {
        return MenuVO.builder()
                .id(id)
                .menuName("菜单" + id)
                .path("/menu" + id)
                .menuType(2)
                .parentId(parentId)
                .sort(id.intValue())
                .status(1)
                .build();
    }

    @Nested
    @DisplayName("GET /auth/role-menu/{roleId} - 获取角色已分配的菜单ID列表")
    class GetMenuIds {

        @Test
        @DisplayName("返回角色已分配的菜单ID列表")
        void shouldGetMenuIds() throws Exception {
            when(roleService.getMenuIds(1L)).thenReturn(List.of(1L, 2L, 3L));

            mockMvc.perform(get("/auth/role-menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0]").value(1))
                    .andExpect(jsonPath("$.data[1]").value(2))
                    .andExpect(jsonPath("$.data[2]").value(3));

            verify(roleService, times(1)).getMenuIds(1L);
        }

        @Test
        @DisplayName("角色没有分配菜单时返回空列表")
        void shouldReturnEmptyWhenNoMenus() throws Exception {
            when(roleService.getMenuIds(99L)).thenReturn(List.of());

            mockMvc.perform(get("/auth/role-menu/99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("PUT /auth/role-menu/{roleId} - 更新角色菜单权限")
    class UpdateMenus {

        @Test
        @DisplayName("成功更新角色菜单权限")
        void shouldUpdateMenus() throws Exception {
            Long[] menuIds = {1L, 2L, 3L};
            doNothing().when(roleService).assignMenus(eq(1L), any(Long[].class));

            mockMvc.perform(put("/auth/role-menu/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuIds)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(roleService, times(1)).assignMenus(eq(1L), any(Long[].class));
        }

        @Test
        @DisplayName("清空角色菜单权限（传空数组）")
        void shouldClearMenusWithEmptyArray() throws Exception {
            Long[] menuIds = {};
            doNothing().when(roleService).assignMenus(eq(1L), any(Long[].class));

            mockMvc.perform(put("/auth/role-menu/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(menuIds)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("GET /auth/role-menu/{roleId}/tree - 获取角色已分配的菜单树")
    class GetMenuTree {

        @Test
        @DisplayName("返回带选中状态的菜单树")
        void shouldReturnMenuTreeWithCheckedStatus() throws Exception {
            // 角色已分配菜单 1 和 3
            when(roleService.getMenuIds(1L)).thenReturn(List.of(1L, 3L));

            // 完整菜单树：菜单1（根）-> 菜单2、菜单3（子）
            MenuVO child2 = mockMenuVO(2L, 1L);
            MenuVO child3 = mockMenuVO(3L, 1L);
            MenuVO root = MenuVO.builder()
                    .id(1L)
                    .menuName("系统管理")
                    .menuType(1)
                    .parentId(0L)
                    .sort(1)
                    .status(1)
                    .children(List.of(child2, child3))
                    .build();
            when(menuService.getTree()).thenReturn(List.of(root));

            mockMvc.perform(get("/auth/role-menu/1/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].menuName").value("系统管理"))
                    // 菜单1 在已分配列表中，应被标记为选中
                    .andExpect(jsonPath("$.data[0].checked").value(true))
                    // 菜单2 不在已分配列表中，不应被标记
                    .andExpect(jsonPath("$.data[0].children[0].checked").doesNotExist())
                    // 菜单3 在已分配列表中，应被标记为选中
                    .andExpect(jsonPath("$.data[0].children[1].checked").value(true));

            verify(roleService, times(1)).getMenuIds(1L);
            verify(menuService, times(1)).getTree();
        }

        @Test
        @DisplayName("角色没有分配菜单时所有节点均未选中")
        void shouldReturnTreeWithNoChecked() throws Exception {
            when(roleService.getMenuIds(2L)).thenReturn(List.of());

            MenuVO menu = mockMenuVO(1L, 0L);
            when(menuService.getTree()).thenReturn(List.of(menu));

            mockMvc.perform(get("/auth/role-menu/2/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[0].checked").doesNotExist());
        }

        @Test
        @DisplayName("菜单树为空时返回空列表")
        void shouldReturnEmptyTreeWhenNoMenus() throws Exception {
            when(roleService.getMenuIds(1L)).thenReturn(List.of(1L));
            when(menuService.getTree()).thenReturn(List.of());

            mockMvc.perform(get("/auth/role-menu/1/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
