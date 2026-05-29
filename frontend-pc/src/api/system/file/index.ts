import request from '@/utils/request';
import type { AjaxResult } from '@/api';

export interface SysFile {
  name?: string;
  url?: string;
}

export interface UploadResult {
  code: number;
  msg: string;
  data?: SysFile;
}

export interface UserFile {
  id?: number;
  name?: string;
  parentId?: number;
  isDirectory?: number;
  length?: number;
  contentType?: string;
  updateTime?: string;
  url?: string;
  thumbnail?: string | null;
  downloadUrl?: string;
}

/**
 * 上传文件
 * POST /file/upload → lest-file SysFileController POST /upload
 * 返回 R<SysFile>: { code, msg, data: { name, url } }
 */
export async function uploadFile(
  file: File,
  config?: Record<string, any>,
  fileName?: string
): Promise<UploadResult> {
  const formData = new FormData();
  formData.append('file', file, fileName);
  const res = await request.post<UploadResult>('/file/upload', formData, config);
  if (res.data.code === 200) return res.data;
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 分页查询用户文件（暂用 mock 数据）
 */
export async function pageUserFiles(_params?: any): Promise<{ list: UserFile[]; count: number }> {
  return { list: [], count: 0 };
}

/**
 * 查询用户文件列表（暂用 mock 数据）
 */
export async function listUserFiles(_params?: any): Promise<UserFile[]> {
  return [];
}

export async function addUserFile(_data: UserFile): Promise<never> {
  return Promise.reject(new Error('没有访问权限'));
}

export async function updateUserFile(_data: UserFile): Promise<never> {
  return Promise.reject(new Error('没有访问权限'));
}

export async function removeUserFile(_id: number): Promise<never> {
  return Promise.reject(new Error('没有访问权限'));
}
