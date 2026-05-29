package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.DictionaryDataDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.service.DictionaryService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class DictionaryDataControllerTest {

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
    private DictionaryService dictionaryService;

    private DictionaryDataVO mockDataVO(Long id, Long dictId) {
        return DictionaryDataVO.builder()
                .id(id)
                .dictId(dictId)
                .dataKey("key_" + id)
                .dataValue("value_" + id)
                .label("标签" + id)
                .sort(id.intValue())
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /auth/dictionary-data/page - 分页查询字典数据")
    class Page {

        @Test
        @DisplayName("按dictId分页查询")
        void shouldPageByDictId() throws Exception {
            List<DictionaryDataVO> records = List.of(mockDataVO(1L, 1L), mockDataVO(2L, 1L));
            PageResult<DictionaryDataVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(dictionaryService.pageData(1, 10, 1L, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/dictionary-data/page")
                            .param("dictId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].dataKey").value("key_1"));
        }

        @Test
        @DisplayName("按dictCode分页查询")
        void shouldPageByDictCode() throws Exception {
            List<DictionaryDataVO> records = List.of(mockDataVO(1L, 1L));
            PageResult<DictionaryDataVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(dictionaryService.pageData(1, 10, null, "USER_STATUS")).thenReturn(pageResult);

            mockMvc.perform(get("/auth/dictionary-data/page")
                            .param("dictCode", "USER_STATUS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));

            verify(dictionaryService).pageData(1, 10, null, "USER_STATUS");
        }

        @Test
        @DisplayName("默认分页参数（不传过滤条件）")
        void shouldPageWithDefaults() throws Exception {
            List<DictionaryDataVO> records = List.of(mockDataVO(1L, 1L));
            PageResult<DictionaryDataVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(dictionaryService.pageData(1, 10, null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/dictionary-data/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary-data - 查询字典数据列表")
    class ListData {

        @Test
        @DisplayName("按dictId查询")
        void shouldListByDictId() throws Exception {
            List<DictionaryDataVO> data = List.of(mockDataVO(1L, 1L), mockDataVO(2L, 1L));
            when(dictionaryService.getDataByDictId(1L)).thenReturn(data);

            mockMvc.perform(get("/auth/dictionary-data")
                            .param("dictId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2));
        }

        @Test
        @DisplayName("按dictCode查询")
        void shouldListByDictCode() throws Exception {
            List<DictionaryDataVO> data = List.of(mockDataVO(1L, 1L));
            when(dictionaryService.getDataByDictCode("USER_STATUS")).thenReturn(data);

            mockMvc.perform(get("/auth/dictionary-data")
                            .param("dictCode", "USER_STATUS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));
        }

        @Test
        @DisplayName("不传任何参数时返回空列表")
        void shouldReturnEmptyWhenNoParams() throws Exception {
            mockMvc.perform(get("/auth/dictionary-data"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(0))
                    .andExpect(jsonPath("$.data.records").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary-data/{id} - 根据ID查询字典数据")
    class GetById {

        @Test
        @DisplayName("查询存在的字典数据")
        void shouldGetById() throws Exception {
            when(dictionaryService.getDataById(1L)).thenReturn(mockDataVO(1L, 1L));

            mockMvc.perform(get("/auth/dictionary-data/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dataKey").value("key_1"))
                    .andExpect(jsonPath("$.data.label").value("标签1"));
        }
    }

    @Nested
    @DisplayName("POST /auth/dictionary-data - 创建字典数据")
    class Create {

        @Test
        @DisplayName("成功创建字典数据（dataValue与dataKey相同）")
        void shouldCreateWithSameKeyAndValue() throws Exception {
            DictionaryDataDTO dto = new DictionaryDataDTO();
            dto.setDictId(1L);
            dto.setDataKey("enable");
            dto.setDataValue(""); // 空时使用dataKey
            dto.setLabel("启用");
            dto.setSort(1);
            dto.setStatus(1);

            when(dictionaryService.createData(1L, "enable", "enable", "启用", 1, 1)).thenReturn(100L);

            mockMvc.perform(post("/auth/dictionary-data")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));
        }

        @Test
        @DisplayName("成功创建字典数据（dataValue不为空）")
        void shouldCreateWithExplicitValue() throws Exception {
            DictionaryDataDTO dto = new DictionaryDataDTO();
            dto.setDictId(1L);
            dto.setDataKey("enable");
            dto.setDataValue("1");
            dto.setLabel("启用");
            dto.setSort(1);
            dto.setStatus(1);

            when(dictionaryService.createData(1L, "enable", "1", "启用", 1, 1)).thenReturn(101L);

            mockMvc.perform(post("/auth/dictionary-data")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(101));
        }
    }

    @Nested
    @DisplayName("PUT /auth/dictionary-data - 修改字典数据")
    class Update {

        @Test
        @DisplayName("成功修改字典数据")
        void shouldUpdateDictionaryData() throws Exception {
            DictionaryDataDTO dto = new DictionaryDataDTO();
            dto.setId(1L);
            dto.setDataKey("disable");
            dto.setDataValue("0");
            dto.setLabel("禁用");
            dto.setSort(2);
            dto.setStatus(1);

            doNothing().when(dictionaryService).updateData(1L, "disable", "0", "禁用", 2, 1);

            mockMvc.perform(put("/auth/dictionary-data")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).updateData(1L, "disable", "0", "禁用", 2, 1);
        }

        @Test
        @DisplayName("dataValue为空时使用dataKey")
        void shouldUseDataKeyWhenValueEmpty() throws Exception {
            DictionaryDataDTO dto = new DictionaryDataDTO();
            dto.setId(1L);
            dto.setDataKey("active");
            dto.setDataValue(null);
            dto.setLabel("激活");

            doNothing().when(dictionaryService).updateData(1L, "active", "active", "激活", null, null);

            mockMvc.perform(put("/auth/dictionary-data")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/dictionary-data/{id} - 删除字典数据")
    class Delete {

        @Test
        @DisplayName("成功删除字典数据")
        void shouldDeleteDictionaryData() throws Exception {
            doNothing().when(dictionaryService).deleteData(1L);

            mockMvc.perform(delete("/auth/dictionary-data/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).deleteData(1L);
        }
    }

    @Nested
    @DisplayName("DELETE /auth/dictionary-data/batch - 批量删除字典数据")
    class BatchDelete {

        @Test
        @DisplayName("批量删除多条字典数据")
        void shouldBatchDelete() throws Exception {
            Long[] ids = {1L, 2L, 3L};
            doNothing().when(dictionaryService).deleteData(anyLong());

            mockMvc.perform(delete("/auth/dictionary-data/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            // 每个id都应该被单独删除
            verify(dictionaryService, times(3)).deleteData(anyLong());
        }

        @Test
        @DisplayName("传入空数组时不执行删除")
        void shouldNotDeleteWhenIdsEmpty() throws Exception {
            Long[] ids = {};

            mockMvc.perform(delete("/auth/dictionary-data/batch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(ids)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, never()).deleteData(anyLong());
        }
    }
}
