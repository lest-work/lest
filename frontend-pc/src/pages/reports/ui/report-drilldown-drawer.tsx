import { ArrowLeft, Download, X } from "lucide-react";
import { useEffect, useState } from "react";

import { issuePriorityLabel, issueStatusLabel, issueTypeLabel, type IssuePriority, type IssueStatus, type IssueType } from "@/entities/issue";
import type { ReportDrilldownRequest, ReportDrilldownResponse } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { cn } from "@/shared/lib/utils";
import { Button, DataTable, Drawer, EmptyState } from "@/shared/ui";

interface ReportDrilldownDrawerProps {
  request?: ReportDrilldownRequest;
  onClose: () => void;
}

const summaryToneClassName: Record<ReportDrilldownResponse["summary"][number]["tone"], string> = {
  blue: "border-[#CCE0FF] bg-[#F0F6FF] text-[#0C66E4]",
  green: "border-[#ABF5D1] bg-[#F3FCF7] text-[#216E4E]",
  orange: "border-[#FFE2BD] bg-[#FFF7ED] text-[#974F0C]",
  red: "border-[#FFD5CC] bg-[#FFF4F2] text-[#AE2E24]",
  neutral: "border-[#DFE1E6] bg-[#FAFBFC] text-[#44546F]",
};

export function ReportDrilldownDrawer({ request, onClose }: ReportDrilldownDrawerProps) {
  const [data, setData] = useState<ReportDrilldownResponse>();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!request) {
      return;
    }
    setLoading(true);
    void projectWorkspaceService
      .getReportDrilldown(request)
      .then(setData)
      .finally(() => setLoading(false));
  }, [request]);

  if (!request) {
    return null;
  }

  return (
    <Drawer
      open
      closable={false}
      width="min(100vw, 1080px)"
      zIndex={50}
      className="lest-frameless-drawer"
      styles={{ body: { height: "100%" }, mask: { background: "rgba(9, 30, 66, 0.25)" } }}
      onClose={onClose}
    >
      <section className="flex h-full w-full flex-col border-l border-[#DFE1E6] bg-white shadow-[0_18px_48px_rgba(9,30,66,0.28)]">
        <header className="flex items-start justify-between gap-4 border-b border-[#EBECF0] px-6 py-4">
          <div>
            <Button className="mb-2 inline-flex items-center gap-1 text-[12px] font-semibold text-[#0C66E4]" onClick={onClose}>
              <ArrowLeft className="h-3.5 w-3.5" />
              返回报表
            </Button>
            <h2 className="text-[18px] font-bold text-[#172B4D]">{data?.title ?? "图表下钻明细"}</h2>
            <div className="mt-2 flex flex-wrap items-center gap-1 text-[12px] text-[#626F86]">
              {(data?.breadcrumb ?? ["报表", "加载中"]).map((item, index) => (
                <span key={`${item}-${index}`} className="inline-flex items-center gap-1">
                  {index > 0 ? <span>/</span> : null}
                  {item}
                </span>
              ))}
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button variant="outline" className="h-8 rounded-[4px] bg-white text-[13px]">
              <Download className="h-4 w-4" />
              导出
            </Button>
            <Button variant="ghost" size="icon" className="h-8 w-8 rounded-[4px]" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </header>

        <div className="workspace-scrollbar flex-1 overflow-y-auto bg-[#F4F5F7] p-6">
          {loading ? (
            <div className="rounded-[6px] border border-[#DFE1E6] bg-white p-6">
              <div className="mb-4 h-5 w-40 rounded bg-[#F1F2F4]" />
              <div className="space-y-3">
                {Array.from({ length: 8 }).map((_, index) => (
                  <div key={index} className="h-9 rounded bg-[#F7F8F9]" />
                ))}
              </div>
            </div>
          ) : data && data.rows.length > 0 ? (
            <>
              <div className="mb-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
                {data.summary.map((item) => (
                  <section key={item.id} className={cn("rounded-[6px] border p-4", summaryToneClassName[item.tone])}>
                    <div className="text-[12px] font-bold opacity-80">{item.label}</div>
                    <div className="mt-2 text-[26px] font-bold">{item.value}</div>
                  </section>
                ))}
              </div>

              <section className="rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_1px_2px_rgba(9,30,66,0.08)]">
                <div className="flex flex-wrap items-center gap-2 border-b border-[#EBECF0] px-4 py-3">
                  {["筛选", "分组", "排序", "列设置"].map((item) => (
                    <Button key={item} className="h-8 rounded-[4px] border border-[#DFE1E6] bg-white px-3 text-[12px] font-semibold text-[#44546F] hover:bg-[#F4F5F7]">
                      {item}
                    </Button>
                  ))}
                </div>
                <div className="workspace-scrollbar overflow-x-auto">
                  <DataTable<ReportDrilldownResponse["rows"][number]>
                    className="min-w-[900px]"
                    columns={[
                      {
                        title: "问题",
                        dataIndex: "key",
                        render: (_, row) => (
                          <div>
                            <div className="font-semibold text-[#0C66E4]">{row.key}</div>
                            <div className="mt-1 max-w-[320px] truncate text-[#172B4D]">{row.title}</div>
                          </div>
                        ),
                      },
                      { title: "类型", dataIndex: "type", render: (type: IssueType) => <span className="text-[#44546F]">{issueTypeLabel[type]}</span> },
                      { title: "状态", dataIndex: "status", render: (status: IssueStatus) => <span className="text-[#44546F]">{issueStatusLabel[status]}</span> },
                      { title: "优先级", dataIndex: "priority", render: (priority: IssuePriority) => <span className="font-semibold text-[#172B4D]">{issuePriorityLabel[priority]}</span> },
                      { title: "负责人", dataIndex: "assignee", render: (assignee) => <span className="text-[#44546F]">{assignee}</span> },
                      { title: "项目", dataIndex: "projectName", render: (projectName) => <span className="text-[#44546F]">{projectName}</span> },
                      { title: "创建时间", dataIndex: "createdAt", render: (createdAt) => <span className="text-[#626F86]">{createdAt}</span> },
                    ]}
                    dataSource={data.rows}
                    pagination={false}
                    rowKey="id"
                    rowClassName="border-t border-[#EBECF0] hover:bg-[#F4F5F7]"
                  />
                </div>
                <footer className="flex items-center justify-between border-t border-[#EBECF0] px-4 py-3 text-[12px] text-[#626F86]">
                  <span>共 {data.total} 条</span>
                  <div className="flex items-center gap-2">
                    {[1, 2, 3, 4, 5].map((page) => (
                      <Button key={page} className={cn("flex h-7 w-7 items-center justify-center rounded-[4px] font-semibold", page === data.page ? "bg-[#0C66E4] text-white" : "hover:bg-[#F4F5F7]")}>
                        {page}
                      </Button>
                    ))}
                  </div>
                </footer>
              </section>
            </>
          ) : (
            <EmptyState title="暂无下钻数据" description="当前图表节点没有明细记录，试试返回上一级或调整筛选条件。" actionLabel="返回报表" onAction={onClose} />
          )}
        </div>
      </section>
    </Drawer>
  );
}
