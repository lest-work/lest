import request from '@/utils/request';
import { download, toFormData, checkDownloadRes } from '@/utils/common';
import type { AjaxResult, TableDataInfo } from '@/api';
import type { Post, PostParam } from './model';

export async function pagePosts(params?: PostParam): Promise<TableDataInfo<Post>> {
  const res = await request.get<TableDataInfo<Post>>('/system/post/list', { params });
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function addPost(data: Post): Promise<string> {
  const res = await request.post<AjaxResult<unknown>>('/system/post', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function updatePost(data: Post): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>('/system/post', data);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removePost(id: number): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/post/' + id);
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function removePosts(ids: number[]): Promise<string> {
  const res = await request.delete<AjaxResult<unknown>>('/system/post/' + ids.join());
  if (res.data.code === 200) return res.data.msg || '操作成功';
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}

export async function exportPosts(params?: PostParam) {
  const res = await request({ url: '/system/post/export', method: 'POST', data: toFormData(params), responseType: 'blob' });
  await checkDownloadRes(res);
  download(res.data, `post_${Date.now()}.xlsx`);
}

export async function listPosts(): Promise<Post[]> {
  const res = await request.get<AjaxResult<Post[]>>('/system/post/optionselect');
  if (res.data.code === 200 && res.data.data) return res.data.data;
  return Promise.reject(new Error(res.data.msg || '操作失败'));
}
