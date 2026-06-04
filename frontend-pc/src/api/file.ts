import request from './axios';

export interface UploadedFile {
  name: string;
  url: string;
}

export const fileApi = {
  upload(file: File | Blob) {
    const formData = new FormData();
    formData.append('file', file);
    return request.post<any, { code: number; data: UploadedFile }>('/upload/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  delete(fileUrl: string) {
    return request.delete<any, { code: number }>('/upload/upload', {
      params: { fileUrl },
    });
  },
};
