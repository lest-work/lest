import { AlertTriangle, Ban, CheckCircle2, Info, KeyRound, Loader2, LockKeyhole, Plus, Search, ShieldAlert, SlidersHorizontal, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";

import { issuePriorityLabel, issueStatusLabel, issueTypeLabel } from "@/entities/issue";
import type { IssuePriority, IssueStatus, IssueType } from "@/entities/issue";
import type { ComponentGalleryResponse } from "@/api/project";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Badge, Button, DangerDialog, Input, Pagination, ResultState, SkeletonBlock } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

export function ComponentGalleryPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [data, setData] = useState<ComponentGalleryResponse>();

  useEffect(() => {
    void projectWorkspaceService
      .getComponentGallery({
        projectKey: "alpha-platform",
        groups: ["buttons", "forms", "tables", "badges", "loading", "empty", "error", "permission", "danger"],
        includeApiSpec: true,
        locale: "zh-CN",
      })
      .then(setData);
  }, []);

  return (
    <ProjectWorkspaceShell
      activePage="components"
      title="组件规范"
      subtitle="沉淀按钮、表单、表格、标签、状态和优先级组件规范，供后续功能复用。"
      onNavigate={onNavigate}
    >
      <div className="space-y-4">
        <SpecPanel title="按钮组件状态" description="主按钮、次按钮、危险按钮、图标按钮和 Loading/Disabled 状态。">
          <div className="flex flex-wrap items-center gap-3">
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white">
              <Plus className="h-4 w-4" />
              主按钮
            </Button>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white">
              次按钮
            </Button>
            <Button variant="outline" className="h-9 rounded-[4px] border-dashed bg-white">
              虚线按钮
            </Button>
            <Button variant="ghost" className="h-9 rounded-[4px] text-[#0C66E4]">
              文字按钮
            </Button>
            <Button className="h-9 rounded-[4px] bg-[#DE350B] text-white hover:bg-[#BF2600]">
              <Trash2 className="h-4 w-4" />
              危险按钮
            </Button>
            <Button variant="outline" size="icon" className="h-9 w-9 rounded-[4px] bg-white">
              <SlidersHorizontal className="h-4 w-4" />
            </Button>
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" loading>
              创建中
            </Button>
            <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white" disabled>
              禁用
            </Button>
          </div>
        </SpecPanel>

        <SpecPanel title="表单组件状态" description="输入、下拉、日期、校验、开关、复选和禁用状态。">
          <div className="grid gap-4 xl:grid-cols-4">
            <FormDemo label="默认输入">
              <Input className="h-9 rounded-[4px] border-[#DFE1E6]" placeholder="请输入标题" />
            </FormDemo>
            <FormDemo label="聚焦/填写">
              <Input className="h-9 rounded-[4px] border-[#0C66E4] ring-1 ring-[#0C66E4]" value="添加用户登录认证功能" readOnly />
            </FormDemo>
            <FormDemo label="错误状态">
              <Input className="h-9 rounded-[4px] border-[#DE350B]" value="标题过短" readOnly />
              <div className="mt-1 text-[12px] text-[#DE350B]">标题至少需要 5 个字符</div>
            </FormDemo>
            <FormDemo label="成功状态">
              <div className="relative">
                <Input className="h-9 rounded-[4px] border-[#22A06B] pr-8" value="字段可用" readOnly />
                <CheckCircle2 className="absolute right-2 top-2.5 h-4 w-4 text-[#22A06B]" />
              </div>
            </FormDemo>
            <FormDemo label="下拉选择">
              <select className="h-9 w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]">
                <option>高优先级</option>
                <option>中优先级</option>
              </select>
            </FormDemo>
            <FormDemo label="日期选择">
              <Input className="h-9 rounded-[4px] border-[#DFE1E6]" type="date" value="2026-06-20" readOnly />
            </FormDemo>
            <FormDemo label="成员选择">
              <div className="flex h-9 items-center rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]">张晓明、李华</div>
            </FormDemo>
            <FormDemo label="禁用状态">
              <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value="不可编辑字段" disabled />
            </FormDemo>
          </div>
        </SpecPanel>

        <SpecPanel title="表格组件状态" description="筛选、排序、行选择、加载、空数据、分页和批量操作。">
          <div className="mb-3 flex flex-wrap items-center gap-3">
            <div className="relative w-[260px]">
              <Search className="absolute left-3 top-2.5 h-4 w-4 text-[#6B778C]" />
              <Input className="h-9 rounded-[4px] border-[#DFE1E6] pl-9" placeholder="搜索问题" />
            </div>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white">
              <SlidersHorizontal className="h-4 w-4" />
              列设置
            </Button>
            <Button variant="outline" className="h-9 rounded-[4px] bg-white">
              批量操作
            </Button>
          </div>
          <div className="overflow-hidden rounded-[6px] border border-[#DFE1E6]">
            <table className="w-full table-fixed border-collapse bg-white text-left text-[13px]">
              <thead className="bg-[#FAFBFC] text-[12px] font-bold text-[#6B778C]">
                <tr>
                  <th className="w-10 px-3 py-3"><input type="checkbox" /></th>
                  <th className="w-[100px] px-3 py-3">编号</th>
                  <th className="px-3 py-3">标题</th>
                  <th className="w-[100px] px-3 py-3">类型</th>
                  <th className="w-[100px] px-3 py-3">优先级</th>
                  <th className="w-[100px] px-3 py-3">状态</th>
                </tr>
              </thead>
              <tbody>
                {data?.tableRows.map((row, index) => (
                  <tr key={row.id} className={cn("border-t border-[#EBECF0] hover:bg-[#F7F8F9]", index === 1 && "bg-[#E9F2FF]/60")}>
                    <td className="px-3 py-3"><input type="checkbox" checked={index === 1} readOnly /></td>
                    <td className="px-3 py-3 font-semibold text-[#0C66E4]">{row.key}</td>
                    <td className="truncate px-3 py-3 font-medium text-[#172B4D]">{row.title}</td>
                    <td className="px-3 py-3">{issueTypeLabel[row.type as IssueType]}</td>
                    <td className="px-3 py-3">{issuePriorityLabel[row.priority as IssuePriority]}</td>
                    <td className="px-3 py-3">{issueStatusLabel[row.status as IssueStatus]}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="flex items-center justify-between border-t border-[#EBECF0] px-4 py-3">
              <span className="text-[13px] text-[#626F86]">已选择 1 项，共 178 条</span>
              <Pagination page={1} pageSize={20} total={178} onPageChange={() => undefined} />
            </div>
          </div>
        </SpecPanel>

        <SpecPanel title="标签 / 状态 / 优先级组件" description="状态、优先级、类型和业务标签的浅色背景、深色文字和尺寸规范。">
          <div className="flex flex-wrap gap-3">
            {data?.badgeSamples.map((sample) => (
              <Badge key={sample.id} variant={sample.tone === "red" ? "danger" : sample.tone === "green" ? "success" : sample.tone === "orange" ? "warning" : sample.tone === "blue" ? "default" : "muted"} className="rounded-[4px] px-2.5 py-1 text-[12px] font-semibold">
                {sample.kind === "priority" ? <AlertTriangle className="mr-1 h-3.5 w-3.5" /> : null}
                {sample.label}
              </Badge>
            ))}
            <span className="rounded-[4px] bg-[#091E42] px-2.5 py-1 text-[12px] font-semibold text-white">深色标签</span>
            <span className="rounded-[4px] border border-[#C1C7D0] bg-white px-2.5 py-1 text-[12px] font-semibold text-[#44546F]">描边标签</span>
          </div>
        </SpecPanel>

        <SpecPanel title="Toast / Message 提示组件" description="成功、错误、警告、信息、加载、Undo 和多条堆叠反馈规范。">
          <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_300px]">
            <div className="space-y-3">
              <ToastSample tone="success" icon={<CheckCircle2 className="h-4 w-4 text-[#22A06B]" />} title="创建成功" description="问题 DEMO-129 已创建" />
              <ToastSample tone="error" icon={<AlertTriangle className="h-4 w-4 text-[#E34935]" />} title="创建失败" description="网络错误，请稍后重试" />
              <ToastSample tone="warning" icon={<AlertTriangle className="h-4 w-4 text-[#F59E0B]" />} title="即将超期" description="截止日期已接近，请确认负责人" action="查看" />
              <ToastSample tone="info" icon={<Info className="h-4 w-4 text-[#0C66E4]" />} title="已取消关联" description="已取消与 DEMO-128 的关联" action="撤销" />
              <ToastSample tone="loading" icon={<Loader2 className="h-4 w-4 animate-spin text-[#0C66E4]" />} title="正在创建问题..." description="请勿关闭当前弹窗" />
            </div>
            <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
              <h3 className="text-[14px] font-bold text-[#172B4D]">位置与行为</h3>
              <div className="mt-3 grid gap-2 text-[12px] text-[#44546F]">
                {["顶部居中", "顶部右侧", "中间居中", "底部左侧", "底部右侧"].map((item) => (
                  <div key={item} className="flex items-center justify-between rounded-[4px] border border-[#DFE1E6] bg-white px-3 py-2">
                    <span>{item}</span>
                    <span className="font-semibold text-[#0C66E4]">支持</span>
                  </div>
                ))}
              </div>
              <p className="mt-3 text-[12px] leading-5 text-[#626F86]">默认 3.2 秒自动关闭；Loading 不自动关闭；支持手动关闭、Undo 操作和多条堆叠。</p>
            </div>
          </div>
        </SpecPanel>

        <SpecPanel title="加载骨架屏" description="对应 50.png：列表、详情、看板、报表、弹窗和表单加载态，尺寸稳定，避免真实内容回来时跳动。">
          <div className="grid gap-4 xl:grid-cols-[1.2fr_0.8fr]">
            <SkeletonBlock shape="table" rows={6} />
            <div className="grid gap-3">
              {data?.feedbackStates.loading.slice(1, 5).map((item) => (
                <div key={item.id} className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-3">
                  <div className="mb-2 flex items-center justify-between gap-3">
                    <span className="text-[13px] font-bold text-[#172B4D]">{item.title}</span>
                    <Badge variant="default" className="rounded-[4px] bg-[#E9F2FF] text-[#0C66E4]">{item.scope}</Badge>
                  </div>
                  <SkeletonBlock shape={item.shape as any} rows={Math.min(item.rowCount ?? 0, 4)} className="border-0 p-0 shadow-none" />
                  <p className="mt-2 text-[12px] leading-5 text-[#626F86]">{item.description}</p>
                </div>
              ))}
            </div>
          </div>
        </SpecPanel>

        <SpecPanel title="空数据状态" description="对应 49.png/50.png 延伸：筛选为空、项目未初始化、报表无数据时给出下一步动作。">
          <div className="grid gap-4 xl:grid-cols-3">
            {data?.feedbackStates.empty.map((item, index) => (
              <ResultState
                key={item.id}
                tone="empty"
                size="sm"
                title={item.title ?? ""}
                description={item.description ?? ""}
                actionLabel={item.actionLabel ?? ""}
                secondaryActionLabel={item.secondaryActionLabel}
                icon={index === 1 ? Plus : Search}
              />
            ))}
          </div>
        </SpecPanel>

        <SpecPanel title="错误状态" description="对应 51.png：按错误优先级区分页面、模块、表单和 Toast，保留错误码与 traceId 便于排查。">
          <div className="grid gap-4 xl:grid-cols-[1fr_1fr_300px]">
            {data?.feedbackStates.errors.slice(0, 2).map((item) => (
              <ResultState
                key={item.id}
                tone="error"
                title={item.title ?? ""}
                description={item.description ?? ""}
                actionLabel={item.actionLabel ?? ""}
                secondaryActionLabel={item.secondaryActionLabel}
                icon={item.severity === "high" ? Ban : AlertTriangle}
                meta={<FeedbackMeta code={item.code ?? ""} traceId={item.traceId ?? ""} severity={item.severity as any} />}
              />
            ))}
            <div className="space-y-3">
              {data?.feedbackStates.errors.slice(2).map((item) => (
                <ToastSample
                  key={item.id}
                  tone={item.severity === "low" ? "warning" : "error"}
                  icon={<AlertTriangle className="h-4 w-4 text-[#E34935]" />}
                  title={item.title ?? ""}
                  description={`${item.code} · ${item.traceId}`}
                  action={item.actionLabel}
                />
              ))}
            </div>
          </div>
        </SpecPanel>

        <SpecPanel title="权限不足状态" description="对应 52.png：页面级禁止访问、模块级部分受限、操作级不可用，说明权限键与推荐角色。">
          <div className="grid gap-4 xl:grid-cols-3">
            {data?.feedbackStates.permissions.map((item, index) => (
              <ResultState
                key={item.id}
                tone="permission"
                size={index === 0 ? "md" : "sm"}
                title={item.title ?? ""}
                description={item.description ?? ""}
                actionLabel={item.actionLabel ?? ""}
                secondaryActionLabel={item.secondaryActionLabel}
                icon={index === 0 ? LockKeyhole : index === 1 ? KeyRound : ShieldAlert}
                meta={<PermissionMeta permissionKey={item.permissionKey ?? ""} roleSuggestion={item.roleSuggestion ?? ""} />}
              />
            ))}
          </div>
          <div className="mt-4 grid gap-3 md:grid-cols-3">
            {["按钮置灰并显示原因", "菜单项隐藏敏感动作", "字段脱敏或只读展示"].map((item) => (
              <div key={item} className="flex items-center gap-2 rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] px-3 py-2 text-[12px] font-semibold text-[#44546F]">
                <LockKeyhole className="h-4 w-4 text-[#0C66E4]" />
                {item}
              </div>
            ))}
          </div>
        </SpecPanel>

        <SpecPanel title="删除 / 危险操作确认弹窗" description="对应 53.png：删除、批量删除、永久删除、关闭 Sprint、移动和归档都通过统一危险操作模型渲染。">
          <div className="grid gap-4 xl:grid-cols-3">
            {data?.dangerActions.map((item) => (
              <DangerDialog
                key={item.id}
                title={item.title ?? ""}
                description={item.description ?? ""}
                actionLabel={item.actionLabel ?? ""}
                cancelLabel={item.cancelLabel}
                severity={item.severity as any}
                confirmationMode={item.confirmationMode}
                confirmationText={item.confirmationText ?? ""}
                affectedSummary={item.affectedSummary}
              />
            ))}
          </div>
        </SpecPanel>

        {data?.apiSpec ? (
          <SpecPanel title="API Mock 契约" description="前端按正式接口建模，当前由 mock service 返回；后端接管时保持请求和响应结构即可。">
            <div className="grid gap-4 xl:grid-cols-[360px_minmax(0,1fr)]">
              <div className="rounded-[6px] border border-[#DFE1E6] bg-[#FAFBFC] p-4">
                <div className="text-[12px] font-bold uppercase text-[#0C66E4]">{data.apiSpec.method}</div>
                <div className="mt-2 break-all font-mono text-[13px] font-semibold text-[#172B4D]">{data.apiSpec.endpoint}</div>
                <div className="mt-3 text-[12px] leading-5 text-[#626F86]">Mock 延迟：{data.apiSpec.mockLatencyMs}ms · 响应：{data.apiSpec.responseShape}</div>
              </div>
              <pre className="max-h-[240px] overflow-auto rounded-[6px] border border-[#DFE1E6] bg-[#172B4D] p-4 text-[12px] leading-5 text-[#E6FCFF]">
                {JSON.stringify(data.apiSpec.requestParams, null, 2)}
              </pre>
            </div>
          </SpecPanel>
        ) : null}
      </div>
    </ProjectWorkspaceShell>
  );
}

function SpecPanel({ title, description, children }: { title: string; description: string; children: React.ReactNode }) {
  return (
    <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
      <div className="mb-4">
        <h2 className="text-[16px] font-bold text-[#172B4D]">{title}</h2>
        <p className="mt-1 text-[13px] text-[#626F86]">{description}</p>
      </div>
      {children}
    </section>
  );
}

function FormDemo({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <div className="mb-1.5 text-[13px] font-semibold text-[#44546F]">{label}</div>
      {children}
    </label>
  );
}

function FeedbackMeta({ code, traceId, severity }: { code: string; traceId: string; severity: string }) {
  return (
    <div className="grid gap-1 rounded-[4px] bg-[#FFF4F2] px-3 py-2 text-left text-[12px] text-[#5D1F1A]">
      <div>错误码：<span className="font-semibold">{code}</span></div>
      <div>Trace：<span className="font-mono">{traceId}</span></div>
      <div>级别：<span className="font-semibold">{severity}</span></div>
    </div>
  );
}

function PermissionMeta({ permissionKey, roleSuggestion }: { permissionKey: string; roleSuggestion: string }) {
  return (
    <div className="rounded-[4px] bg-[#F4F5F7] px-3 py-2 text-left text-[12px] leading-5 text-[#44546F]">
      <div>权限键：<span className="font-mono font-semibold text-[#172B4D]">{permissionKey}</span></div>
      <div>建议角色：<span className="font-semibold text-[#172B4D]">{roleSuggestion}</span></div>
    </div>
  );
}

function ToastSample({ tone, icon, title, description, action }: { tone: "success" | "error" | "warning" | "info" | "loading"; icon: React.ReactNode; title: string; description: string; action?: string }) {
  const className = {
    success: "border-[#ABF5D1] bg-[#F3FCF7]",
    error: "border-[#FFD5CC] bg-[#FFF4F2]",
    warning: "border-[#FFE2BD] bg-[#FFF7ED]",
    info: "border-[#CCE0FF] bg-[#F0F6FF]",
    loading: "border-[#DFE1E6] bg-white",
  }[tone];

  return (
    <div className={`flex items-start gap-3 rounded-[6px] border px-4 py-3 shadow-[0_4px_12px_rgba(9,30,66,0.10)] ${className}`}>
      {icon}
      <div className="min-w-0 flex-1">
        <div className="text-[13px] font-bold text-[#172B4D]">{title}</div>
        <div className="mt-1 text-[12px] text-[#44546F]">{description}</div>
        {action ? <button className="mt-2 text-[12px] font-semibold text-[#0C66E4]">{action}</button> : null}
      </div>
      <span className="text-[14px] text-[#626F86]">×</span>
    </div>
  );
}
