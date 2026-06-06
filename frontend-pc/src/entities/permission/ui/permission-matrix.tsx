import { Check, Minus } from "lucide-react";

import type { PermissionItem, PermissionRole } from "@/entities/permission";
import { Card, CardContent, CardHeader, CardTitle } from "@/shared/ui";

export interface PermissionMatrixProps {
  roles: PermissionRole[];
  items: PermissionItem[];
}

export function PermissionMatrix({ roles, items }: PermissionMatrixProps) {
  return (
    <Card>
      <CardHeader className="border-b bg-white">
        <CardTitle>权限矩阵</CardTitle>
        <p className="text-sm text-slate-500">对应项目设置与权限不足状态：角色、模块、动作、可见性与禁用态。</p>
      </CardHeader>
      <CardContent className="overflow-x-auto pt-5">
        <table className="w-full min-w-[680px] border-separate border-spacing-0 text-sm">
          <thead>
            <tr>
              <th className="rounded-l-lg bg-slate-50 px-4 py-3 text-left font-semibold text-slate-600">模块</th>
              <th className="bg-slate-50 px-4 py-3 text-left font-semibold text-slate-600">操作</th>
              {roles.map((role, index) => (
                <th
                  key={role.id}
                  className={`bg-slate-50 px-4 py-3 text-center font-semibold text-slate-600 ${index === roles.length - 1 ? "rounded-r-lg" : ""}`}
                >
                  {role.name}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {items.map((item) => (
              <tr key={item.id}>
                <td className="border-b px-4 py-3 text-slate-600">{item.module}</td>
                <td className="border-b px-4 py-3 font-medium text-slate-900">{item.action}</td>
                {roles.map((role) => (
                  <td key={role.id} className="border-b px-4 py-3 text-center">
                    {item.roles[role.id] ? (
                      <span className="inline-flex h-7 w-7 items-center justify-center rounded-full bg-emerald-50 text-emerald-700">
                        <Check className="h-4 w-4" />
                      </span>
                    ) : (
                      <span className="inline-flex h-7 w-7 items-center justify-center rounded-full bg-slate-100 text-slate-400">
                        <Minus className="h-4 w-4" />
                      </span>
                    )}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </CardContent>
    </Card>
  );
}
