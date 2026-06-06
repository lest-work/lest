import {
  Activity,
  Box,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  Code2,
  Compass,
  FileText,
  FolderKanban,
  GitMerge,
  LayoutDashboard,
  PieChart,
  Rocket,
  Settings,
} from "lucide-react";
import type { LucideIcon } from "lucide-react";

import { workspaceNavigationSections, type WorkspaceNavigationIcon, type WorkspacePageKey } from "@/entities/project-workspace/model";
import { MenuGroup, MenuItem } from "@/shared/ui/menu";
import { cn } from "@/shared/lib/utils";

const navigationIcons: Record<WorkspaceNavigationIcon, LucideIcon> = {
  activity: Activity,
  box: Box,
  clipboard: ClipboardList,
  code: Code2,
  compass: Compass,
  file: FileText,
  folder: FolderKanban,
  git: GitMerge,
  layout: LayoutDashboard,
  pie: PieChart,
  settings: Settings,
};

export interface ProjectSidebarProps {
  activeItem?: string;
  activePage?: WorkspacePageKey;
  collapsed?: boolean;
  onNavigate?: (page: WorkspacePageKey) => void;
  onToggleCollapse?: () => void;
  onPreloadPage?: (page?: WorkspacePageKey) => void;
}

export function ProjectSidebar({ activeItem, activePage, collapsed = false, onNavigate, onToggleCollapse, onPreloadPage }: ProjectSidebarProps) {
  return (
    <aside className={cn("flex shrink-0 flex-col border-r border-[#EBECF0] bg-white transition-all duration-200", collapsed ? "w-[64px]" : "w-[250px]")}>
      {/* Project header */}
      {collapsed ? (
        <div className="flex h-16 items-center justify-center pt-2">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-[6px] bg-[#0052CC] text-white">
            <Rocket className="h-4 w-4" />
          </div>
        </div>
      ) : (
        <div className="flex h-16 items-center gap-3 px-6 pt-2">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-[6px] bg-[#0052CC] text-white">
            <Rocket className="h-4 w-4" />
          </div>
          <div className="flex-1 overflow-hidden">
            <div className="truncate text-[15px] font-bold text-[#172B4D]">敏捷研发平台</div>
            <div className="truncate text-[12px] text-[#626F86]">软件项目</div>
          </div>
        </div>
      )}

      {/* Navigation */}
      <div className="workspace-scrollbar mt-4 flex-1 overflow-y-auto px-4 pb-4">
        {collapsed ? (
          /* Collapsed mode: centered icon-only items */
          <div className="flex flex-col items-center space-y-1">
            {workspaceNavigationSections.flatMap((section) =>
              section.items.map((item) => {
                const Icon = navigationIcons[item.icon];
                const isActive = activePage ? activePage === item.pageKey : activeItem === item.label;
                return (
                  <MenuItem
                    key={item.label}
                    label={item.label}
                    icon={<Icon className="h-[18px] w-[18px]" />}
                    active={isActive}
                    collapsed
                    onClick={() => item.pageKey && onNavigate?.(item.pageKey)}
                    onFocus={() => onPreloadPage?.(item.pageKey)}
                    onMouseEnter={() => onPreloadPage?.(item.pageKey)}
                  />
                );
              }),
            )}
          </div>
        ) : (
          /* Expanded mode: grouped sections */
          <>
            {workspaceNavigationSections.map((section, idx) => (
              <div key={section.title} className={cn(idx > 0 && "mt-6")}>
                <MenuGroup title={section.title}>
                  {section.items.map((item) => {
                    const Icon = navigationIcons[item.icon];
                    const isActive = activePage ? activePage === item.pageKey : activeItem === item.label;
                    return (
                      <MenuItem
                        key={item.label}
                        label={item.label}
                        icon={<Icon className="h-[18px] w-[18px]" />}
                        active={isActive}
                        onClick={() => item.pageKey && onNavigate?.(item.pageKey)}
                        onFocus={() => onPreloadPage?.(item.pageKey)}
                        onMouseEnter={() => onPreloadPage?.(item.pageKey)}
                      />
                    );
                  })}
                </MenuGroup>
              </div>
            ))}
          </>
        )}
      </div>

      {/* Collapse toggle */}
      {collapsed ? (
        <div className="flex justify-center p-4">
          <button
            className="flex items-center justify-center text-[#626F86] hover:text-[#172B4D]"
            onClick={onToggleCollapse}
            title="展开菜单"
          >
            <ChevronRight className="h-4 w-4" />
          </button>
        </div>
      ) : (
        <div className="p-4">
          <button
            className="flex w-full items-center gap-2 text-[13px] text-[#626F86] hover:text-[#172B4D]"
            onClick={onToggleCollapse}
          >
            <ChevronLeft className="h-4 w-4" />
            <span>收起菜单</span>
          </button>
        </div>
      )}
    </aside>
  );
}
