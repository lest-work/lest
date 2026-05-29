/**
 * 接口统一返回结果 (RuoYi 格式)
 */
export interface AjaxResult<T = unknown> {
  /** 状态码 */
  code: number;
  /** 状态信息 */
  msg: string;
  /** 返回数据 */
  data?: T;
}

/**
 * 分页查询统一结果 (RuoYi TableDataInfo 格式)
 */
export interface TableDataInfo<T> {
  /** 状态码 */
  code: number;
  /** 状态信息 */
  msg: string;
  /** 数据列表 */
  rows: T[];
  /** 总数量 */
  total: number;
}

/**
 * 分页查询基本参数 (RuoYi 格式)
 */
export interface PageParam {
  /** 第几页 */
  pageNum?: number;
  /** 每页多少条 */
  pageSize?: number;
}
