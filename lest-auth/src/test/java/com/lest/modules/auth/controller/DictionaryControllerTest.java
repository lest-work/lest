package com.lest.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lest.common.core.PageResult;
import com.lest.modules.auth.entity.dto.DictionaryDTO;
import com.lest.modules.auth.entity.vo.DictionaryDataVO;
import com.lest.modules.auth.entity.vo.DictionaryVO;
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
class DictionaryControllerTest {

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

    private DictionaryVO mockDictVO(Long id) {
        return DictionaryVO.builder()
                .id(id)
                .dictCode("DICT_" + id)
                .dictName("字典" + id)
                .description("字典" + id + "描述")
                .status(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private DictionaryDataVO mockDictDataVO(Long id, Long dictId) {
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
    @DisplayName("GET /auth/dictionary - 查询所有字典")
    class ListAll {

        @Test
        @DisplayName("返回所有字典列表")
        void shouldListAll() throws Exception {
            List<DictionaryVO> dicts = List.of(mockDictVO(1L), mockDictVO(2L));
            when(dictionaryService.listAll()).thenReturn(dicts);

            mockMvc.perform(get("/auth/dictionary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2))
                    .andExpect(jsonPath("$.data.records[0].dictCode").value("DICT_1"));
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary/page - 分页查询字典")
    class Page {

        @Test
        @DisplayName("默认分页参数")
        void shouldPageWithDefaults() throws Exception {
            List<DictionaryVO> records = List.of(mockDictVO(1L), mockDictVO(2L));
            PageResult<DictionaryVO> pageResult = PageResult.of(records, 2, 1, 10);
            when(dictionaryService.page(1, 10, null, null, null)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/dictionary/page"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(2));
        }

        @Test
        @DisplayName("按字典编码和状态筛选")
        void shouldPageWithFilters() throws Exception {
            List<DictionaryVO> records = List.of(mockDictVO(1L));
            PageResult<DictionaryVO> pageResult = PageResult.of(records, 1, 1, 10);
            when(dictionaryService.page(1, 10, "USER", null, 1)).thenReturn(pageResult);

            mockMvc.perform(get("/auth/dictionary/page")
                            .param("dictCode", "USER")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1));

            verify(dictionaryService).page(1, 10, "USER", null, 1);
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary/data/{dictCode} - 根据字典编码获取字典数据")
    class GetDataByDictCode {

        @Test
        @DisplayName("返回指定字典编码的数据列表")
        void shouldGetDataByDictCode() throws Exception {
            List<DictionaryDataVO> dataList = List.of(
                    mockDictDataVO(1L, 1L),
                    mockDictDataVO(2L, 1L)
            );
            when(dictionaryService.getDataByDictCode("USER_STATUS")).thenReturn(dataList);

            mockMvc.perform(get("/auth/dictionary/data/USER_STATUS"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].dataKey").value("key_1"))
                    .andExpect(jsonPath("$.data[1].dataKey").value("key_2"));

            verify(dictionaryService, times(1)).getDataByDictCode("USER_STATUS");
        }

        @Test
        @DisplayName("字典编码不存在时返回空列表")
        void shouldReturnEmptyWhenDictCodeNotFound() throws Exception {
            when(dictionaryService.getDataByDictCode("NOT_EXIST")).thenReturn(List.of());

            mockMvc.perform(get("/auth/dictionary/data/NOT_EXIST"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary/{id} - 根据ID查询字典")
    class GetById {

        @Test
        @DisplayName("查询存在的字典")
        void shouldGetById() throws Exception {
            when(dictionaryService.getById(1L)).thenReturn(mockDictVO(1L));

            mockMvc.perform(get("/auth/dictionary/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dictCode").value("DICT_1"))
                    .andExpect(jsonPath("$.data.dictName").value("字典1"));
        }
    }

    @Nested
    @DisplayName("GET /auth/dictionary/code/{dictCode} - 根据字典编码查询字典")
    class GetByDictCode {

        @Test
        @DisplayName("查询存在的字典编码")
        void shouldGetByDictCode() throws Exception {
            when(dictionaryService.getByDictCode("DICT_1")).thenReturn(mockDictVO(1L));

            mockMvc.perform(get("/auth/dictionary/code/DICT_1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.dictCode").value("DICT_1"));
        }
    }

    @Nested
    @DisplayName("POST /auth/dictionary - 创建字典")
    class Create {

        @Test
        @DisplayName("成功创建字典")
        void shouldCreateDictionary() throws Exception {
            DictionaryDTO dto = new DictionaryDTO();
            dto.setDictCode("NEW_DICT");
            dto.setDictName("新字典");
            dto.setDescription("新字典描述");
            dto.setStatus(1);

            when(dictionaryService.create(any(DictionaryDTO.class))).thenReturn(100L);

            mockMvc.perform(post("/auth/dictionary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(100));

            verify(dictionaryService, times(1)).create(any(DictionaryDTO.class));
        }

        @Test
        @DisplayName("字典编码为空时创建失败")
        void shouldFailWhenDictCodeBlank() throws Exception {
            DictionaryDTO dto = new DictionaryDTO();
            dto.setDictName("有效字典名");
            dto.setStatus(1);

            mockMvc.perform(post("/auth/dictionary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("字典名称为空时创建失败")
        void shouldFailWhenDictNameBlank() throws Exception {
            DictionaryDTO dto = new DictionaryDTO();
            dto.setDictCode("VALID_CODE");
            dto.setStatus(1);

            mockMvc.perform(post("/auth/dictionary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("状态为空时创建失败")
        void shouldFailWhenStatusNull() throws Exception {
            DictionaryDTO dto = new DictionaryDTO();
            dto.setDictCode("VALID_CODE");
            dto.setDictName("有效字典名");

            mockMvc.perform(post("/auth/dictionary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /auth/dictionary - 更新字典")
    class Update {

        @Test
        @DisplayName("成功更新字典")
        void shouldUpdateDictionary() throws Exception {
            DictionaryDTO dto = new DictionaryDTO();
            dto.setId(1L);
            dto.setDictCode("UPDATED_DICT");
            dto.setDictName("更新后的字典");
            dto.setStatus(1);

            doNothing().when(dictionaryService).update(any(DictionaryDTO.class));

            mockMvc.perform(put("/auth/dictionary")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).update(any(DictionaryDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/dictionary/{id} - 删除字典")
    class Delete {

        @Test
        @DisplayName("成功删除字典")
        void shouldDeleteDictionary() throws Exception {
            doNothing().when(dictionaryService).delete(1L);

            mockMvc.perform(delete("/auth/dictionary/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).delete(1L);
        }
    }

    @Nested
    @DisplayName("POST /auth/dictionary/data - 创建字典数据")
    class CreateData {

        @Test
        @DisplayName("成功创建字典数据")
        void shouldCreateDictionaryData() throws Exception {
            when(dictionaryService.createData(1L, "enable", "1", "启用", 1, 1)).thenReturn(200L);

            mockMvc.perform(post("/auth/dictionary/data")
                            .param("dictId", "1")
                            .param("dataKey", "enable")
                            .param("dataValue", "1")
                            .param("label", "启用")
                            .param("sort", "1")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value(200));

            verify(dictionaryService, times(1)).createData(1L, "enable", "1", "启用", 1, 1);
        }

        @Test
        @DisplayName("不传可选参数时也能创建")
        void shouldCreateWithoutOptionalParams() throws Exception {
            when(dictionaryService.createData(1L, "disable", "0", "禁用", null, null)).thenReturn(201L);

            mockMvc.perform(post("/auth/dictionary/data")
                            .param("dictId", "1")
                            .param("dataKey", "disable")
                            .param("dataValue", "0")
                            .param("label", "禁用"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(201));
        }
    }

    @Nested
    @DisplayName("PUT /auth/dictionary/data/{id} - 更新字典数据")
    class UpdateData {

        @Test
        @DisplayName("成功更新字典数据")
        void shouldUpdateDictionaryData() throws Exception {
            doNothing().when(dictionaryService).updateData(1L, "enable", "1", "启用", 1, 1);

            mockMvc.perform(put("/auth/dictionary/data/1")
                            .param("dataKey", "enable")
                            .param("dataValue", "1")
                            .param("label", "启用")
                            .param("sort", "1")
                            .param("status", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).updateData(1L, "enable", "1", "启用", 1, 1);
        }

        @Test
        @DisplayName("只更新部分字段")
        void shouldUpdatePartialFields() throws Exception {
            doNothing().when(dictionaryService).updateData(1L, null, null, "新标签", null, null);

            mockMvc.perform(put("/auth/dictionary/data/1")
                            .param("label", "新标签"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("DELETE /auth/dictionary/data/{id} - 删除字典数据")
    class DeleteData {

        @Test
        @DisplayName("成功删除字典数据")
        void shouldDeleteDictionaryData() throws Exception {
            doNothing().when(dictionaryService).deleteData(1L);

            mockMvc.perform(delete("/auth/dictionary/data/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(dictionaryService, times(1)).deleteData(1L);
        }
    }
}
