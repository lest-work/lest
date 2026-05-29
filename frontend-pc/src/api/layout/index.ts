import request from '@/utils/request';
import { mapTree } from 'ele-admin-plus';
import { API_BASE_URL } from '@/config/setting';
import type { AjaxResult } from '@/api';
import type { UserInfoResult } from '@/api/system/user/model';

/**
 * 获取当前登录用户的个人信息/权限/角色
 */
export async function getUserInfo(): Promise<UserInfoResult> {
  const res = await request.get<AjaxResult<unknown>>('/system/user/getInfo');
  if (res.data.code === 200) {
    return res.data as UserInfoResult;
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 获取当前登录用户的菜单
 */
export async function getUserMenu(): Promise<any[]> {
  const res = await request.get<AjaxResult<any[]>>('/system/menu/getRouters');
  if (res.data.code === 200 && res.data.data) {
    const temp = res.data.data;
    // 一级菜单去掉父级
    temp.forEach((item, i) => {
      if (item.path === '/') {
        const child = item.children[0];
        temp[i] = {
          ...child,
          path: child.path?.startsWith?.('/') ? child.path : `/${child.path}`
        };
      }
    });
    // 增加首页
    temp.unshift({
      path: '/index',
      component: 'index',
      meta: { title: '首页', icon: 'IconProHomeOutlined' }
    });
    // 增加个人中心
    temp.push({
      path: '/profile',
      component: 'profile',
      meta: {
        title: '个人中心',
        icon: 'IconProUserOutlined',
        active: '/index',
        hide: true
      }
    });
    // 修改图标
    return mapTree(temp, (item: any) => {
      const meta =
        (item.meta && typeof item.meta === 'string'
          ? JSON.parse(item.meta)
          : item.meta) || {};
      const result = {
        ...item,
        meta: {
          ...meta,
          icon: meta.icon ? (ruoYiIcons[meta.icon as string] ?? meta.icon) : meta.icon
        }
      };
      // 修改内嵌格式
      if ('tool/swagger/index' === item.component) {
        result.component = `${API_BASE_URL}/swagger-ui/index.html`;
      } else if ('monitor/druid/index' === item.component) {
        result.component = 'https://ruoyi.eleadmin.com/prod-api/druid/login.html';
      }
      return result;
    });
  }
  return Promise.reject(new Error(res.data.msg));
}

/**
 * 修改当前登录用户的密码
 */
export async function updatePassword(data: {
  oldPassword?: string;
  newPassword?: string;
}): Promise<string> {
  const res = await request.put<AjaxResult<unknown>>(
    '/system/user/profile/updatePwd',
    data
  );
  if (res.data.code === 200) {
    return res.data.msg;
  }
  return Promise.reject(new Error(res.data.msg));
}

/** 若依默认菜单图标名称 */
export const ruoYiIcons: Record<string, string> = {
  system: 'IconProSettingOutlined',
  user: 'IconProUserOutlined',
  peoples: 'IconProIdcardOutlined',
  'tree-table': 'IconProAppstoreOutlined',
  tree: 'IconProCityOutlined',
  post: 'IconProSuitcaseOutlined',
  dict: 'IconProBookOutlined',
  edit: 'IconProControlOutlined',
  message: 'IconProMessageOutlined',
  log: 'IconProLogOutlined',
  form: 'IconProFileOutlined',
  logininfor: 'IconProCalendarOutlined',
  monitor: 'IconProDashboardOutlined',
  online: 'IconProConnectionOutlined',
  job: 'IconProTimerOutlined',
  druid: 'IconProFundOutlined',
  server: 'IconProAnalysisOutlined',
  redis: 'IconProClusterOutlined',
  'redis-list': 'IconProDatabaseOutlined',
  tool: 'IconProAppstoreAddOutlined',
  build: 'IconProFormOutlined',
  code: 'IconProCodeOutlined',
  swagger: 'IconProLinkOutlined',
  guide: 'IconProLinkOutlined',
  '#': 'IconProLinkOutlined'
};
