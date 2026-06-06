import { Download, Plus, Save, Trash2 } from "lucide-react";
import { useEffect, useState } from "react";

import type { ProjectSettingsResponse } from "@/api/project";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";
import { projectWorkspaceService } from "@/api/project";
import { Button, Input, MenuItem } from "@/shared/ui";
import { ProjectWorkspaceShell } from "@/widgets/project-shell/ui";

const tabs = ["项目概览", "工作流程", "字段管理", "权限管理", "状态与优先级", "自动化规则", "通知设置", "集成管理"];

export function ProjectSettingsPage({ onNavigate }: { onNavigate: (page: WorkspacePageKey) => void }) {
  const [data, setData] = useState<ProjectSettingsResponse>();
  const [activeTab, setActiveTab] = useState("项目概览");

  useEffect(() => {
    void projectWorkspaceService.getProjectSettings({ projectKey: "alpha-platform", section: "overview" }).then(setData);
  }, []);

  return (
    <ProjectWorkspaceShell
      activePage="settings"
      title="项目设置"
      subtitle="配置项目基础信息、工作流、权限、字段、状态、自动化规则和集成。"
      onNavigate={onNavigate}
      actions={
        <>
          <Button variant="outline" className="h-9 rounded-[4px] bg-white">
            <Download className="h-4 w-4" />
            导出配置
          </Button>
          <Button className="h-9 rounded-[4px] bg-[#0C66E4] text-white">
            <Save className="h-4 w-4" />
            保存设置
          </Button>
        </>
      }
    >
      <div className="grid gap-4 xl:grid-cols-[230px_minmax(0,1fr)]">
        <aside className="rounded-[8px] border border-[#DFE1E6] bg-white p-3">
          {tabs.map((tab) => (
            <MenuItem
              key={tab}
              label={tab}
              active={activeTab === tab}
              className="mb-0.5 h-9 text-[13px]"
              onClick={() => setActiveTab(tab)}
            />
          ))}
        </aside>

        <div className="space-y-4">
          <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
            <h2 className="mb-4 text-[16px] font-bold text-[#172B4D]">项目信息</h2>
            <div className="grid gap-4 md:grid-cols-2">
              <Field label="项目名称">
                <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={data?.project.name ?? ""} readOnly />
              </Field>
              <Field label="项目 Key">
                <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={data?.project.key ?? ""} readOnly />
              </Field>
              <Field label="项目负责人">
                <Input className="h-9 rounded-[4px] border-[#DFE1E6]" value={data?.project.lead ?? ""} readOnly />
              </Field>
              <Field label="可见性">
                <select className="h-9 w-full rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[13px]" value={data?.project.visibility ?? "team"} onChange={() => undefined}>
                  <option value="team">团队可见</option>
                  <option value="private">私有</option>
                  <option value="public">公开</option>
                </select>
              </Field>
            </div>
          </section>

          <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-[16px] font-bold text-[#172B4D]">工作流程</h2>
              <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[12px]">
                <Plus className="h-4 w-4" />
                添加状态
              </Button>
            </div>
            <div className="grid gap-3 lg:grid-cols-5">
              {data?.workflow.map((status) => (
                <div key={status.id} className="rounded-[6px] border border-[#DFE1E6] p-4">
                  <div className="mb-3 flex items-center gap-2">
                    <span className="h-2.5 w-2.5 rounded-full" style={{ backgroundColor: status.color }} />
                    <span className="text-[14px] font-bold text-[#172B4D]">{status.label}</span>
                  </div>
                  <div className="text-[12px] text-[#626F86]">可流转至</div>
                  <div className="mt-2 min-h-[42px] text-[12px] text-[#44546F]">
                    {status.transitions.length ? status.transitions.join("、") : "无后续状态"}
                  </div>
                </div>
              ))}
            </div>
          </section>

          <div className="grid gap-4 xl:grid-cols-2">
            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h2 className="mb-4 text-[16px] font-bold text-[#172B4D]">权限角色</h2>
              <div className="space-y-3">
                {data?.roles.map((role) => (
                  <div key={role.id} className="rounded-[6px] border border-[#DFE1E6] p-4">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-[#172B4D]">{role.name}</span>
                      <span className="text-[12px] text-[#626F86]">{role.members} 人</span>
                    </div>
                    <div className="mt-3 flex flex-wrap gap-2">
                      {role.permissions.map((permission) => (
                        <span key={permission} className="rounded-[12px] bg-[#F4F5F7] px-2.5 py-1 text-[12px] text-[#44546F]">
                          {permission}
                        </span>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </section>

            <section className="rounded-[8px] border border-[#DFE1E6] bg-white p-5 shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
              <h2 className="mb-4 text-[16px] font-bold text-[#172B4D]">自动化规则</h2>
              <div className="space-y-3">
                {data?.automations.map((rule) => (
                  <div key={rule.id} className="flex items-center justify-between rounded-[6px] border border-[#DFE1E6] p-4">
                    <div>
                      <div className="font-bold text-[#172B4D]">{rule.name}</div>
                      <div className="mt-1 text-[12px] text-[#626F86]">{rule.trigger}</div>
                    </div>
                    <span className={`rounded-[12px] px-2.5 py-1 text-[12px] font-bold ${rule.enabled ? "bg-[#E3FCEF] text-[#006644]" : "bg-[#F4F5F7] text-[#626F86]"}`}>
                      {rule.enabled ? "已启用" : "已停用"}
                    </span>
                  </div>
                ))}
              </div>
            </section>
          </div>

          <section className="rounded-[8px] border border-[#FFBDAD] bg-white p-5">
            <h2 className="text-[16px] font-bold text-[#DE350B]">危险操作</h2>
            <p className="mt-2 text-[13px] text-[#626F86]">删除项目会移除项目配置、问题关系和报表数据。请谨慎操作。</p>
            <Button className="mt-4 h-9 rounded-[4px] bg-[#DE350B] text-white hover:bg-[#BF2600]">
              <Trash2 className="h-4 w-4" />
              删除项目
            </Button>
          </section>
        </div>
      </div>
    </ProjectWorkspaceShell>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label>
      <div className="mb-1.5 text-[13px] font-semibold text-[#44546F]">{label}</div>
      {children}
    </label>
  );
}
