import request from './axios';

/**
 * Webhook API - external CI/CD and Git platform webhooks.
 * These endpoints are called BY external systems (GitLab/GitHub), not by the frontend.
 * Frontend uses this only for configuration/testing purposes.
 */
export const webhookApi = {
  /** 模拟触发 CI 构建 webhook（仅用于测试） */
  testCiBuild(payload?: Record<string, any>) {
    return request.post<any, { code: number }>('/webhook/ci/build', payload || {});
  },
  /** 模拟触发 Git 提交 webhook（仅用于测试） */
  testGitCommit(payload?: Record<string, any>) {
    return request.post<any, { code: number }>('/webhook/git/commit', payload || {});
  },
};
