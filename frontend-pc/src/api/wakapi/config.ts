import request from '@/utils/request';
import type { ApiResult, PageResult } from '@/api';
import type {
  WakapiAlias,
  WakapiAliasParam,
  WakapiLabel,
  WakapiLabelParam,
  WakapiLanguageMapping,
  WakapiLanguageMappingParam
} from './model';

/* ==================== Alias 别名管理 ==================== */

/** 分页查询别名 */
export async function pageAliases(params: WakapiAliasParam & { page?: number; limit?: number }) {
  const res = await request.get<ApiResult<PageResult<WakapiAlias>>>(
    '/wakapi/config/alias/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 查询别名列表 */
export async function listAliases(params?: WakapiAliasParam) {
  const res = await request.get<ApiResult<WakapiAlias[]>>('/wakapi/config/alias', { params });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 根据id查询别名 */
export async function getAlias(id: number) {
  const res = await request.get<ApiResult<WakapiAlias>>('/wakapi/config/alias/' + id);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 添加别名 */
export async function addAlias(data: WakapiAlias) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/config/alias', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 修改别名 */
export async function updateAlias(data: WakapiAlias) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/config/alias', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 删除别名 */
export async function removeAlias(id: number) {
  const res = await request.delete<ApiResult<unknown>>('/wakapi/config/alias/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== Label 项目标签管理 ==================== */

/** 分页查询项目标签 */
export async function pageLabels(params: WakapiLabelParam & { page?: number; limit?: number }) {
  const res = await request.get<ApiResult<PageResult<WakapiLabel>>>(
    '/wakapi/config/label/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 查询项目标签列表 */
export async function listLabels(params?: WakapiLabelParam) {
  const res = await request.get<ApiResult<WakapiLabel[]>>('/wakapi/config/label', { params });
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 根据id查询项目标签 */
export async function getLabel(id: number) {
  const res = await request.get<ApiResult<WakapiLabel>>('/wakapi/config/label/' + id);
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 添加项目标签 */
export async function addLabel(data: WakapiLabel) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/config/label', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 修改项目标签 */
export async function updateLabel(data: WakapiLabel) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/config/label', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 删除项目标签 */
export async function removeLabel(id: number) {
  const res = await request.delete<ApiResult<unknown>>('/wakapi/config/label/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/* ==================== LanguageMapping 语言映射管理 ==================== */

/** 分页查询语言映射 */
export async function pageLanguageMappings(
  params: WakapiLanguageMappingParam & { page?: number; limit?: number }
) {
  const res = await request.get<ApiResult<PageResult<WakapiLanguageMapping>>>(
    '/wakapi/config/language-mapping/page',
    { params }
  );
  if (res.data.code === 0) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 查询语言映射列表 */
export async function listLanguageMappings(params?: WakapiLanguageMappingParam) {
  const res = await request.get<ApiResult<WakapiLanguageMapping[]>>(
    '/wakapi/config/language-mapping',
    { params }
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 根据id查询语言映射 */
export async function getLanguageMapping(id: number) {
  const res = await request.get<ApiResult<WakapiLanguageMapping>>(
    '/wakapi/config/language-mapping/' + id
  );
  if (res.data.code === 0 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 添加语言映射 */
export async function addLanguageMapping(data: WakapiLanguageMapping) {
  const res = await request.post<ApiResult<unknown>>('/wakapi/config/language-mapping', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 修改语言映射 */
export async function updateLanguageMapping(data: WakapiLanguageMapping) {
  const res = await request.put<ApiResult<unknown>>('/wakapi/config/language-mapping', data);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}

/** 删除语言映射 */
export async function removeLanguageMapping(id: number) {
  const res = await request.delete<ApiResult<unknown>>('/wakapi/config/language-mapping/' + id);
  if (res.data.code === 0) {
    return res.data.message;
  }
  return Promise.reject(new Error(res.data.message));
}
