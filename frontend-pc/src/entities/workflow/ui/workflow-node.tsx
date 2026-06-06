import { ArrowRight } from "lucide-react";

import type { WorkflowNodeData } from "@/entities/workflow";
import { cn } from "@/shared/lib/utils";

export interface WorkflowNodeProps {
  node: WorkflowNodeData;
  nodes: WorkflowNodeData[];
}

export function WorkflowNode({ node, nodes }: WorkflowNodeProps) {
  return (
    <div className="rounded-xl border bg-white p-4 shadow-workspace-card">
      <div className="flex items-start gap-3">
        <span className={cn("mt-1 h-3 w-3 rounded-full", node.color)} />
        <div>
          <h3 className="font-semibold text-slate-900">{node.title}</h3>
          <p className="mt-1 text-sm text-slate-500">{node.description}</p>
        </div>
      </div>
      <div className="mt-4 flex flex-wrap gap-2">
        {node.transitions.map((transition) => {
          const targetNode = nodes.find((item) => item.id === transition);

          return (
            <span key={transition} className="inline-flex items-center gap-1 rounded-full bg-slate-100 px-2.5 py-1 text-xs text-slate-600">
              <ArrowRight className="h-3 w-3" />
              {targetNode?.title ?? transition}
            </span>
          );
        })}
      </div>
    </div>
  );
}
