import { CalendarRange, Flag, Play, SquareCheckBig } from "lucide-react";

import type { Issue } from "@/entities/issue";
import type { Sprint } from "@/entities/sprint";
import { Button, Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/shared/ui";

export interface SprintPanelProps {
  sprint: Sprint;
  issues: Issue[];
}

export function SprintPanel({ sprint, issues }: SprintPanelProps) {
  const sprintIssues = issues.filter((issue) => sprint.issueIds.includes(issue.id));
  const doneEstimate = sprintIssues
    .filter((issue) => issue.status === "done" || issue.status === "closed")
    .reduce((total, issue) => total + issue.estimate, 0);
  const usedEstimate = sprintIssues.reduce((total, issue) => total + issue.estimate, 0);

  return (
    <Card className="overflow-hidden">
      <CardHeader className="border-b bg-white">
        <div className="flex items-start justify-between gap-4">
          <div>
            <CardTitle>{sprint.name}</CardTitle>
            <CardDescription className="mt-2">{sprint.goal}</CardDescription>
          </div>
          <Button size="sm">
            <Play className="h-4 w-4" />
            启动 Sprint
          </Button>
        </div>
      </CardHeader>
      <CardContent className="grid gap-4 pt-5 md:grid-cols-3">
        <div className="rounded-lg bg-blue-50 p-4 text-blue-700">
          <CalendarRange className="mb-3 h-5 w-5" />
          <div className="text-sm font-semibold">迭代周期</div>
          <div className="mt-1 text-xs">{sprint.startDate} 至 {sprint.endDate}</div>
        </div>
        <div className="rounded-lg bg-violet-50 p-4 text-violet-700">
          <Flag className="mb-3 h-5 w-5" />
          <div className="text-sm font-semibold">容量使用</div>
          <div className="mt-1 text-xs">{usedEstimate}/{sprint.capacity} 故事点</div>
        </div>
        <div className="rounded-lg bg-emerald-50 p-4 text-emerald-700">
          <SquareCheckBig className="mb-3 h-5 w-5" />
          <div className="text-sm font-semibold">完成进度</div>
          <div className="mt-1 text-xs">{doneEstimate}/{usedEstimate || 1} 故事点</div>
        </div>
      </CardContent>
    </Card>
  );
}
