import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { DictData, DictDataParam } from './model';

/**
 * 查询字典数据列表
 */
export async function listDictDatas(type: string): Promise<DictData[]> {
  const res = await request.get<AjaxResult<DictData[]>>(
    '/system/dict/data/type/' + type
  );
  if (res.data.code === 200 && res.data.data) {
    return res.data.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 分页查询字典数据
 */
export async function pageDictDatas(
  params?: DictDataParam
): Promise<TableDataInfo<DictData>> {
  const res = await request.get<TableDataInfo<DictData>>(
    '/system/dict/data/list',
    { params }
  );
  if (res.data.code === 200) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 添加字典数据
 */
export async function addDictData(data: DictData): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>(
    '/system/dict/data',
    data
  );
  if (res.data.code === 200) {
    return res.data.msg;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改字典数据
 */
export async function updateDictData(data: DictData): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/dict/data', data);
  if (res.data.code === 200) {
    return res.data.msg;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 删除字典数据
 */
export async function removeDictData(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(
    '/system/dict/data/' + id
  );
  if (res.data.code === 200) {
    return res.data.msg;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 批量删除字典数据
 */
export async function removeDictDataBatch(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>(
    '/system/dict/data/' + ids.join()
  );
  if (res.data.code === 200) {
    return res.data.msg;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 导出字典数据列表
 */
export async function exportDictDatas(params?: DictDataParam) {
  const res = await request({
    url: '/system/dict/data/export',
    method: 'POST',
    data: toFormData(params),
    responseType: 'blob'
  });
  await checkDownloadRes(res);
  download(res.data, `dict_data_${Date.now()}.xlsx`);
}
