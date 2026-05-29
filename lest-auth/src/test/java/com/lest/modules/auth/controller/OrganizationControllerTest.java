package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.OrganizationDTO;
import com.lest.modules.auth.entity.vo.OrganizationVO;
import com.lest.modules.auth.service.OrganizationService;
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
class OrganizationControllerTest {

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
    private OrganizationService organizationService;

    private OrganizationVO mockOrgVO(Long id) {
        return OrganizationVO.builder()
                .id(id)
                .orgName("机构" + id)
                .orgCode("ORG_" + id)
                .description("机构" + id + "描述")
                .parentId(0L)
                .sort(id.intValue())
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /auth/organization/tree - 获取机构树")
    class GetTree {

        @Test
        @DisplayName("返回机构树结构")
        void shouldReturnOrgTree() throws Exception {
            OrganizationVO child = mockOrgVO(2L);
            OrganizationVO root = OrganizationVO.builder()
                    .id(1L)
                    .orgName("总公司")
                    .orgCode("ROOT")
                    .status(1)
                    .children(Arrays.asList(child))
                    .build();
            when(organizationService.getTree()).thenReturn(Arrays.asList(root));

            mockMvc.perform(get("/auth/organization/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].orgName").value("总公司"))
                    .andExpect(jsonPath("$.data[0].children[0].orgName").value("机构2"));

            verify(organizationService, times(1)).getTree();
        }

        @Test
        @DisplayName("机构树为空时返回空列表")
        void shouldReturnEmptyTree() throws Exception {
            when(organizationService.getTree()).thenReturn(Arrays.asList());

            mockMvc.perform(get("/auth/organization/tree"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /auth/organization - 查询所有机构")
    class ListAll {

        @Test
        @DisplayName("返回所有机构列表")
        void shouldListAll() throws Exception {
            List<OrganizationVO> orgs = Arrays.asList(mockOrgVO(1L), mockOrgVO(2L));
            when(organizationService.listAll()).thenReturn(orgs);

            mockMvc.perform(get("/auth/organization"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].orgCode").value("ORG_1"));
        }
    }

    @Nested
    @DisplayName("GET /auth/organization/page - 分页查询机构")
    class Page {

        @Test
        @DisplayName("默认分页参数")
        void shouldPageWithDefaults() throws Exception {
            List<OrganizationVO> records = Arrays.asList(mockOrgVO(1L), mockOrgVO(2L));
            PageResult<OrganizationVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(organizationService.page(1, 10, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/organization/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2));
        }

        @Test
        @DisplayName("按机构名称筛选")
        void shouldPageWithOrgName() throws Exception {
            List<OrganizationVO> records = Arrays.asList(mockOrgVO(1L));
            PageResult<OrganizationVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(organizationService.page(1, 10, "技术")).thenReturn(pageResult);

            mockMvc.perform(get("/auth/organization/page")
                            .param("orgName", "技术"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));

            verify(organizationService).page(1, 10, "技术");
        }
    }

    @Nested
    @DisplayName("GET /auth/organization/{id} - 根据ID查询机构")
    class GetById {

        @Test
        @DisplayName("查询存在的机构")
        void shouldGetById() throws Exception {
            when(organizationService.getById(1L)).thenReturn(mockOrgVO(1L));

            mockMvc.perform(get("/auth/organization/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.orgCode").value("ORG_1"))
                    .andExpect(jsonPath("$.data.orgName").value("机构1"));
        }

        @Test
        @DisplayName("查询不存在的机构返回null")
        void shouldReturnNullWhenNotFound() throws Exception {
            when(organizationService.getById(999L)).thenReturn(null);

            mockMvc.perform(get("/auth/organization/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /auth/organization - 创建机构")
    class Create {

        @Test
        @DisplayName("成功创建机构")
        void shouldCreateOrg() throws Exception {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setOrgName("新机构");
            dto.setOrgCode("NEW_ORG");
            dto.setParentId(0L);
            dto.setSort(1);
            dto.setStatus(1);

            when(organizationService.create(any(OrganizationDTO.class))).thenReturn(100L);

            mockMvc.perform(post("/auth/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));

            verify(organizationService, times(1)).create(any(OrganizationDTO.class));
        }

        @Test
        @DisplayName("机构名称为空时创建失败")
        void shouldFailWhenOrgNameBlank() throws Exception {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setOrgCode("VALID_CODE");
            dto.setStatus(1);
            // orgName 为空

            mockMvc.perform(post("/auth/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("机构编码为空时创建失败")
        void shouldFailWhenOrgCodeBlank() throws Exception {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setOrgName("有效机构名");
            dto.setStatus(1);
            // orgCode 为空

            mockMvc.perform(post("/auth/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("状态为空时创建失败")
        void shouldFailWhenStatusNull() throws Exception {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setOrgName("有效机构名");
            dto.setOrgCode("VALID_CODE");
            // status 为空

            mockMvc.perform(post("/auth/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /auth/organization - 更新机构")
    class Update {

        @Test
        @DisplayName("成功更新机构")
        void shouldUpdateOrg() throws Exception {
            OrganizationDTO dto = new OrganizationDTO();
            dto.setId(1L);
            dto.setOrgName("更新后的机构");
            dto.setOrgCode("UPDATED_ORG");
            dto.setStatus(1);

            doNothing().when(organizationService).update(any(OrganizationDTO.class));

            mockMvc.perform(put("/auth/organization")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(organizationService, times(1)).update(any(OrganizationDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/organization/{id} - 删除机构")
    class Delete {

        @Test
        @DisplayName("成功删除机构")
        void shouldDeleteOrg() throws Exception {
            doNothing().when(organizationService).delete(1L);

            mockMvc.perform(delete("/auth/organization/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(organizationService, times(1)).delete(1L);
        }
    }
}
