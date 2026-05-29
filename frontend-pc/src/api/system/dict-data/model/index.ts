import type { PageParam } from '@/api';

/**
 * 字典数据 (RuoYi SysDictData 格式)
 */
export interface DictData {
  /** 字典数据id */
  dictCode?: number;
  /** 字典排序 */
  dictSort?: number;
  /** 字典标签 */
  dictLabel?: string;
  /** 字典键值 */
  dictValue?: string;
  /** 字典类型 */
  dictType?: string;
  /** 样式属性 */
  cssClass?: string;
  /** 表格回显样式 */
  listClass?: string;
  /** 是否默认 */
  isDefault?: string;
  /** 状态 */
  status?: string;
  /** 备注 */
  remark?: string;
  /** 创建时间 */
  createTime?: string;
}

/**
 * 字典数据搜索参数
 */
export interface DictDataParam extends PageParam {
  dictType?: string;
  dictLabel?: string;
  status?: string;
}
