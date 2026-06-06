export function DashboardSkeleton() {
  return (
    <div className="grid grid-cols-4 gap-4">
      {Array.from({ length: 4 }).map((_, index) => (
        <div key={index} className="h-[216px] animate-pulse rounded-[8px] border border-[#DFE1E6] bg-white p-5">
          <div className="h-4 w-24 rounded bg-[#EBECF0]" />
          <div className="mt-7 h-8 w-20 rounded bg-[#EBECF0]" />
          <div className="mt-8 h-16 rounded bg-[#F1F2F4]" />
        </div>
      ))}
    </div>
  );
}
