import { Bell, CircleHelp, Code2, Grip, LayoutDashboard, LogOut, Plus, Search, Settings, UserRound } from "lucide-react";
import type { ReactNode } from "react";
import { useEffect, useState } from "react";

import type { WorkspaceNotification, WorkspaceSearchResponse, WorkspaceShellResponse } from "@/api/project";
import { projectWorkspaceService } from "@/api/project";
import { workspaceShellActionMenus, workspaceTopNavigationItems, workspaceUserMenuGroups, type WorkspacePageKey } from "@/entities/project-workspace/model";
import { preloadWorkspacePage } from "@/app/routes";
import { ProjectSidebar } from "@/widgets/project-sidebar";
import { DropdownMenu, DropdownMenuItem, TopNavItem } from "@/shared/ui/menu";
import { cn } from "@/shared/lib/utils";
import { Avatar, Button, Input } from "@/shared/ui";

type ShellOverlayId = "search" | "notifications" | "user" | "help" | "settings" | null;

export interface ProjectWorkspaceShellProps {
  activePage: WorkspacePageKey;
  title: string;
  subtitle?: string;
  actions?: ReactNode;
  showPageHeader?: boolean;
  contentClassName?: string;
  projectKey?: string;
  onCreateIssue?: () => void;
  onOpenIssue?: (issueId: string) => void;
  onNavigate: (page: WorkspacePageKey) => void;
  children: ReactNode;
}

export function ProjectWorkspaceShell({
  activePage,
  title,
  subtitle,
  actions,
  showPageHeader = true,
  contentClassName,
  projectKey = "alpha-platform",
  onCreateIssue,
  onOpenIssue,
  onNavigate,
  children,
}: ProjectWorkspaceShellProps) {
  const [shellData, setShellData] = useState<WorkspaceShellResponse>();
  const [overlay, setOverlay] = useState<ShellOverlayId>(null);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [searchResponse, setSearchResponse] = useState<WorkspaceSearchResponse>();

  useEffect(() => {
    void projectWorkspaceService
      .getWorkspaceShell({ projectKey, includeNotifications: true, notificationPageSize: 8 })
      .then(setShellData);
  }, [projectKey]);

  useEffect(() => {
    if (overlay !== "search") {
      return;
    }

    const timer = window.setTimeout(async () => {
      const response = await projectWorkspaceService.searchWorkspace({ projectKey, keyword: searchKeyword, limit: 8 });
      setSearchResponse(response);
    }, 120);

    return () => window.clearTimeout(timer);
  }, [overlay, projectKey, searchKeyword]);

  async function markAllNotificationsRead() {
    const response = await projectWorkspaceService.markAllWorkspaceNotificationsRead({ projectKey });
    setShellData((current) =>
      current
        ? {
            ...current,
            unreadNotificationCount: response.unreadCount,
            notifications: current.notifications.map((item) => ({ ...item, unread: false })),
          }
        : current,
    );
  }

  return (
    <div className="flex min-h-screen flex-col bg-[#F7F8F9] text-[#172B4D]">
      <header className="relative flex h-[68px] shrink-0 items-center justify-between border-b border-[#DFE1E6] bg-white px-6">
        <div className="flex items-center gap-6">
          <button className="flex h-8 w-8 items-center justify-center rounded-[4px] text-[#172B4D] hover:bg-[#F4F5F7]" aria-label="应用菜单">
            <Grip className="h-5 w-5" />
          </button>
          <div className="flex items-center gap-3">
            <div className="flex h-7 w-7 items-center justify-center rounded-[5px] bg-[#0C66E4] text-white">
              <LayoutDashboard className="h-[17px] w-[17px]" />
            </div>
            <span className="text-[15px] font-bold text-[#172B4D]">项目管理</span>
          </div>
          <nav className="hidden items-center gap-3 lg:flex">
            {workspaceTopNavigationItems.map((item) => (
              <TopNavItem
                key={item.key}
                label={item.label}
                active={item.pageKeys.includes(activePage)}
                hasSubMenu={item.hasMenu}
                onClick={() => {
                  if (item.defaultPageKey) {
                    onNavigate(item.defaultPageKey);
                  }
                }}
                onFocus={() => preloadWorkspacePage(item.defaultPageKey)}
                onMouseEnter={() => preloadWorkspacePage(item.defaultPageKey)}
              />
            ))}
            <Button className="ml-2 h-9 rounded-[4px] bg-[#0C66E4] px-4 text-[14px] font-semibold text-white hover:bg-[#0052CC]" onClick={onCreateIssue}>
              <Plus className="h-4 w-4" />
              新建
            </Button>
          </nav>
        </div>

        <div className="flex items-center gap-4">
          <div className="relative hidden w-[260px] xl:block">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-[#6B778C]" />
            <Input
              className="h-9 rounded-[4px] border-[#C1C7D0] bg-white pl-9 pr-14 text-[13px] placeholder:text-[#6B778C] focus:border-[#0C66E4] focus:ring-1 focus:ring-[#0C66E4]"
              placeholder="搜索项目、问题、人员..."
              value={searchKeyword}
              onChange={(event) => setSearchKeyword(event.target.value)}
              onFocus={() => setOverlay("search")}
            />
            <div className="absolute right-2 top-1/2 flex -translate-y-1/2 items-center gap-1 text-[#6B778C]">
              <kbd className="flex h-5 items-center rounded-[4px] bg-[#F4F5F7] px-1.5 text-[11px] leading-none">⌘</kbd>
              <kbd className="flex h-5 items-center rounded-[4px] bg-[#F4F5F7] px-1.5 text-[11px] leading-none">K</kbd>
            </div>
            {overlay === "search" ? (
              <ShellSearchDropdown
                response={searchResponse}
                keyword={searchKeyword}
                onNavigate={(pageKey) => {
                  onNavigate(pageKey);
                  setOverlay(null);
                }}
                onOpenIssue={(issueId) => {
                  onOpenIssue?.(issueId);
                  setOverlay(null);
                }}
              />
            ) : null}
          </div>
          <ShellIconButton className="relative" onClick={() => setOverlay(overlay === "notifications" ? null : "notifications")}>
            <Bell className="h-[21px] w-[21px]" />
            {shellData?.unreadNotificationCount ? (
              <span className="absolute -right-1 -top-1 flex h-[18px] min-w-[18px] items-center justify-center rounded-full bg-[#FF2D20] px-1 text-[10px] font-bold leading-none text-white">
                {shellData.unreadNotificationCount}
              </span>
            ) : null}
          </ShellIconButton>
          {overlay === "notifications" && shellData ? (
            <ShellNotificationPanel
              notifications={shellData.notifications}
              unreadCount={shellData.unreadNotificationCount}
              onMarkAllRead={markAllNotificationsRead}
            />
          ) : null}
          <ShellIconButton onClick={() => setOverlay(overlay === "help" ? null : "help")}>
            <CircleHelp className="h-[21px] w-[21px]" />
          </ShellIconButton>
          {overlay === "help" ? <ShellSimplePanel menu={workspaceShellActionMenus.help} className="right-[88px]" /> : null}
          <ShellIconButton onClick={() => setOverlay(overlay === "settings" ? null : "settings")}>
            <Settings className="h-[21px] w-[21px]" />
          </ShellIconButton>
          {overlay === "settings" ? <ShellSimplePanel menu={workspaceShellActionMenus.settings} className="right-[48px]" /> : null}
          <button className="flex items-center gap-2 rounded-[4px] px-1 py-1 text-[#172B4D] hover:bg-[#F4F5F7]" onClick={() => setOverlay(overlay === "user" ? null : "user")}>
            <Avatar name={shellData?.user.name ?? "张晓明"} imageSrc={shellData?.user.avatar} className="h-8 w-8" />
            <span className="hidden text-[13px] font-semibold xl:inline">{shellData?.user.name ?? "张晓明"}</span>
            <ChevronDownIcon />
          </button>
          {overlay === "user" && shellData ? <ShellUserMenuPanel data={shellData} /> : null}
        </div>
      </header>

      <div className="flex min-h-0 flex-1">
        <ProjectSidebar activePage={activePage} collapsed={sidebarCollapsed} onNavigate={onNavigate} onToggleCollapse={() => setSidebarCollapsed((v) => !v)} onPreloadPage={preloadWorkspacePage} />
        <main className={cn("workspace-scrollbar min-w-0 flex-1 overflow-y-auto bg-[#F7F8F9] px-6 pb-6 pt-5", contentClassName)}>
          {showPageHeader ? (
            <div className="mb-5 flex items-start justify-between gap-4">
              <div>
                <h1 className="text-[24px] font-semibold tracking-[-0.01em] text-[#172B4D]">{title}</h1>
                {subtitle ? <p className="mt-1 text-[13px] text-[#626F86]">{subtitle}</p> : null}
              </div>
              {actions ? <div className="flex items-center gap-3">{actions}</div> : null}
            </div>
          ) : null}
          {children}
        </main>
      </div>
    </div>
  );
}

function ChevronDownIcon() {
  return (
    <svg className="hidden h-4 w-4 xl:block" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="6 9 12 15 18 9" />
    </svg>
  );
}

function ShellIconButton({ className, children, onClick }: { className?: string; children: ReactNode; onClick?: () => void }) {
  return (
    <button className={cn("flex h-8 w-8 items-center justify-center rounded-full text-[#172B4D] hover:bg-[#F4F5F7]", className)} onClick={onClick}>
      {children}
    </button>
  );
}

function ShellSearchDropdown({
  response,
  keyword,
  onNavigate,
  onOpenIssue,
}: {
  response?: WorkspaceSearchResponse;
  keyword: string;
  onNavigate: (pageKey: WorkspacePageKey) => void;
  onOpenIssue: (issueId: string) => void;
}) {
  return (
    <div className="absolute right-0 top-[44px] z-40 w-[420px] rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_12px_32px_rgba(9,30,66,0.2)]">
      <div className="border-b border-[#EBECF0] px-4 py-3 text-[12px] text-[#6B778C]">{keyword ? `搜索 "${keyword}"` : "最近搜索"}</div>
      {!keyword ? (
        <div className="flex flex-wrap gap-2 px-4 py-3">
          {(response?.recentKeywords ?? ["登录认证", "仪表盘", "导航栏"]).map((item) => (
            <span key={item} className="rounded-[12px] bg-[#F4F5F7] px-2.5 py-1 text-[12px] text-[#44546F]">
              {item}
            </span>
          ))}
        </div>
      ) : null}
      <div className="max-h-[360px] overflow-y-auto py-2">
        {(response?.results ?? []).map((item) => (
          <button
            key={`${item.type}-${item.id}`}
            className="flex w-full items-start gap-3 px-4 py-2 text-left hover:bg-[#F4F5F7]"
            onClick={() => {
              if (item.type === "issue") {
                onOpenIssue(item.id);
                return;
              }
              if (item.targetPageKey) {
                onNavigate(item.targetPageKey as WorkspacePageKey);
              }
            }}
          >
            {item.avatar ? (
              <Avatar name={item.title} imageSrc={item.avatar} className="h-6 w-6" />
            ) : (
              <span className="mt-1 flex h-5 w-5 items-center justify-center rounded-[3px] bg-[#DEEBFF] text-[#0C66E4]">
                {item.type === "member" ? <UserRound className="h-3.5 w-3.5" /> : <Code2 className="h-3.5 w-3.5" />}
              </span>
            )}
            <span className="min-w-0">
              <span className="block truncate text-[13px] font-semibold text-[#172B4D]">{item.title}</span>
              <span className="block truncate text-[12px] text-[#6B778C]">{item.description}</span>
            </span>
          </button>
        ))}
      </div>
      <button className="w-full border-t border-[#EBECF0] px-4 py-3 text-left text-[13px] font-medium text-[#0C66E4] hover:bg-[#F4F5F7]">查看全部结果</button>
    </div>
  );
}

function ShellNotificationPanel({
  notifications,
  unreadCount,
  onMarkAllRead,
}: {
  notifications: WorkspaceNotification[];
  unreadCount: number;
  onMarkAllRead: () => void;
}) {
  return (
    <div className="absolute right-[116px] top-[58px] z-40 w-[390px] rounded-[6px] border border-[#DFE1E6] bg-white shadow-[0_12px_32px_rgba(9,30,66,0.2)]">
      <div className="flex items-center justify-between border-b border-[#EBECF0] px-4 py-3">
        <div className="text-[14px] font-bold text-[#172B4D]">通知</div>
        <button className="text-[12px] font-medium text-[#0C66E4]" onClick={onMarkAllRead}>
          全部已读
        </button>
      </div>
      <div className="px-4 py-2 text-[12px] font-semibold text-[#6B778C]">未读 {unreadCount}</div>
      <div className="max-h-[420px] overflow-y-auto">
        {notifications.map((item) => (
          <div key={item.id} className="flex gap-3 border-b border-[#F1F2F4] px-4 py-3 hover:bg-[#F7F8F9]">
            <Avatar name={item.actor?.name ?? "系统"} imageSrc={item.actor?.avatar} className="h-8 w-8" />
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                {item.unread ? <span className="h-2 w-2 rounded-full bg-[#0C66E4]" /> : null}
                <div className="truncate text-[13px] font-semibold text-[#172B4D]">{item.title}</div>
              </div>
              <div className="mt-1 text-[12px] leading-5 text-[#44546F]">{item.description}</div>
              <div className="mt-1 text-[12px] text-[#6B778C]">{item.createdAtText}</div>
            </div>
          </div>
        ))}
      </div>
      <button className="w-full px-4 py-3 text-left text-[13px] font-medium text-[#0C66E4] hover:bg-[#F4F5F7]">查看全部通知</button>
    </div>
  );
}

function ShellUserMenuPanel({ data }: { data: WorkspaceShellResponse }) {
  const groups = workspaceUserMenuGroups.map((group) => ({
    ...group,
    items: group.items.map((item) => {
      if (item === "主题") {
        return `主题：${data.theme === "light" ? "浅色" : "系统"}`;
      }
      if (item === "语言") {
        return `语言：${data.locale}`;
      }
      return item;
    }),
  }));

  return (
    <DropdownMenu className="right-6 top-[58px] w-[280px]">
      <div className="flex gap-3 border-b border-[#EBECF0] px-4 py-4">
        <Avatar name={data.user.name} imageSrc={data.user.avatar} className="h-10 w-10" />
        <div className="min-w-0">
          <div className="truncate text-[14px] font-bold text-[#172B4D]">{data.user.name}</div>
          <div className="truncate text-[12px] text-[#6B778C]">{data.user.email}</div>
          <div className="mt-1 text-[12px] text-[#44546F]">{data.workspace.name}</div>
        </div>
      </div>
      {groups.map((group) => (
        <div key={group.key} className="py-1">
          {group.items.map((item) => (
            <DropdownMenuItem key={item} label={item} />
          ))}
        </div>
      ))}
      <div className="border-t border-[#EBECF0] py-1">
        <button className="flex w-full items-center gap-2 px-4 py-2 text-left text-[13px] text-[#DE350B] hover:bg-[#FFEBE6]">
          <LogOut className="h-4 w-4" />
          退出登录
        </button>
      </div>
    </DropdownMenu>
  );
}

function ShellSimplePanel({ menu, className }: { menu: { title?: string; items: string[] }; className?: string }) {
  return (
    <DropdownMenu title={menu.title} className={cn("top-[58px] w-[220px]", className)}>
      {menu.items.map((item) => (
        <DropdownMenuItem key={item} label={item} />
      ))}
    </DropdownMenu>
  );
}
