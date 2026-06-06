import { Loading } from "@/shared/ui";

export function RouteLoading() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-[#F7F8F9] text-[#172B4D]">
      <div className="rounded-[6px] border border-[#DFE1E6] bg-white px-5 py-4 shadow-[0_8px_24px_rgba(9,30,66,0.12)]">
        <Loading label="正在加载页面" />
      </div>
    </div>
  );
}
