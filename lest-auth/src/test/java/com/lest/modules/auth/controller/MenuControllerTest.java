package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.MenuDTO;
import com.lest.modules.auth.entity.vo.MenuVO;
import com.lest.modules.auth.entity.vo.RouteVO;
import com.lest.modules.auth.service.MenuService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class MenuControllerTest {

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
    private MenuService menuService;

    private MenuVO mockMenuVO(Long id) {
        return MenuVO.builder()
                .id(id)
                .menuName("菜单" + id)
                .path("/menu" + id)
                .component("Layout")
                .permission("system:menu:" + id)
                .menuType(1)
                .icon("icon-menu")
                .parentId(0L)
                .sort(id.intValue())
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private RouteVO mockRouteVO(Long id) {
        return RouteVO.builder()
                .path("/route" + id)
                .name("Route" + id)
                .component("Layout")
                .redirect("no-redirect")
                .build();
    }

    @Nested
    @DisplayName("GET /auth/menu/page - 分页查询菜单")
    class Page {

        @Test
        @DisplayName("默认分页参数")
        void shouldPageWithDefaults() throws Exception {
            List<MenuVO> records = Arrays.asList(mockMenuVO(1L), mockMenuVO(2L));
            PageResult<MenuVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(menuService.page(1, 10, null, null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/menu/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].menuName").value("菜单1"));
        }

        @Test
        @DisplayName("按菜单名筛选")
        void shouldPageWithMenuName() throws Exception {
            List<MenuVO> records = Arrays.asList(mockMenuVO(1L));
            PageResult<MenuVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(menuService.page(1, 10, "系统", null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/menu/page")
                            .param("menuName", "系统"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));
        }
    }

    @Nested
    @DisplayName("GET /auth/menu/tree - 获取菜单树")
    class GetTree {

        @Test
        @DisplayName("返回菜单树结构")
        void shouldReturnMenuTree() throws Exception {
            List<MenuVO> tree = Arrays.asList(mockMenuVO(1L), mockMenuVO(2L));
            when(menuService.getTree()).thenReturn(tree);

            mockMvc.perform(get("/auth/menu/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /auth/menu/routes - 获取路由列表")
    class GetRoutes {

        @Test
        @DisplayName("需要认证获取路由")
        void shouldGetRoutesForAuthenticatedUser() throws Exception {
            List<RouteVO> routes = Arrays.asList(mockRouteVO(1L), mockRouteVO(2L));
            when(menuService.getRoutes(1L)).thenReturn(routes);

            mockMvc.perform(get("/auth/menu/routes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("GET /auth/menu - 查询所有菜单")
    class ListAll {

        @Test
        @DisplayName("返回所有菜单列表")
        void shouldListAll() throws Exception {
            List<MenuVO> menus = Arrays.asList(mockMenuVO(1L), mockMenuVO(2L));
            when(menuService.listAll()).thenReturn(menus);

            mockMvc.perform(get("/auth/menu"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2));
        }
    }

    @Nested
    @DisplayName("GET /auth/menu/{id} - 根据ID查询菜单")
    class GetById {

        @Test
        @DisplayName("查询存在的菜单")
        void shouldGetById() throws Exception {
            when(menuService.getById(1L)).thenReturn(mockMenuVO(1L));

            mockMvc.perform(get("/auth/menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.menuName").value("菜单1"));
        }
    }

    @Nested
    @DisplayName("POST /auth/menu - 创建菜单")
    class Create {

        @Test
        @DisplayName("成功创建菜单")
        void shouldCreateMenu() throws Exception {
            MenuDTO dto = new MenuDTO();
            dto.setMenuName("新菜单");
            dto.setPath("/new-menu");
            dto.setComponent("Layout");
            dto.setMenuType(1);
            dto.setParentId(0L);
            dto.setSort(1);
            dto.setStatus(1);

            when(menuService.create(any(MenuDTO.class))).thenReturn(100L);

            mockMvc.perform(post("/auth/menu")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));
        }
    }

    @Nested
    @DisplayName("PUT /auth/menu - 更新菜单")
    class Update {

        @Test
        @DisplayName("成功更新菜单")
        void shouldUpdateMenu() throws Exception {
            MenuDTO dto = new MenuDTO();
            dto.setId(1L);
            dto.setMenuName("更新后的菜单");

            doNothing().when(menuService).update(any(MenuDTO.class));

            mockMvc.perform(put("/auth/menu")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(menuService, times(1)).update(any(MenuDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/menu/{id} - 删除菜单")
    class Delete {

        @Test
        @DisplayName("删除单个菜单")
        void shouldDeleteSingleMenu() throws Exception {
            doNothing().when(menuService).delete(1L, null);

            mockMvc.perform(delete("/auth/menu/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(menuService, times(1)).delete(1L, null);
        }

        @Test
        @DisplayName("批量删除菜单")
        void shouldBatchDeleteMenus() throws Exception {
            doNothing().when(menuService).delete(eq(1L), anyList());

            mockMvc.perform(delete("/auth/menu/1")
                            .param("ids", "2,3,4"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(menuService).delete(eq(1L), argThat(list ->
                list != null && list.size() == 3 && list.contains(2L) && list.contains(3L) && list.contains(4L)
            ));
        }
    }
}
