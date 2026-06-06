import type { DashboardActivity } from "@/api/dashboard";
import { Avatar, Button } from "@/shared/ui";
import { Panel } from "./dashboard-components";
import type { OverlayId } from "./dashboard-constants";

export function ActivityPanel({
  activities,
  overlay,
  onOverlayChange,
}: {
  activities: DashboardActivity[];
  overlay: OverlayId;
  onOverlayChange: (id: OverlayId) => void;
}) {
  return (
    <Panel id="activities" title="活动动态" className="min-h-[282px]" overlay={overlay} onOverlayChange={onOverlayChange}>
      <div className="space-y-3 px-5 pb-4">
        {activities.map((activity) => (
          <ActivityLine key={activity.id} activity={activity} />
        ))}
        <Button className="pt-1 text-[13px] font-medium text-[#0C66E4] hover:underline">查看全部动态</Button>
      </div>
    </Panel>
  );
}

function ActivityLine({ activity }: { activity: DashboardActivity }) {
  return (
    <div className="grid grid-cols-[28px_1fr_72px] items-center gap-3 text-[13px] text-[#172B4D]">
      <Avatar name={activity.actor.name} imageSrc={activity.actor.avatar} className="h-6 w-6" />
      <p className="min-w-0 truncate">
        <span className="font-bold">{activity.actor.name}</span>
        <span> {activity.verb} </span>
        {activity.issueKey ? <span className="font-semibold text-[#0C66E4]">{activity.issueKey}</span> : null}
        {activity.targetText ? <span> {activity.targetText}</span> : null}
      </p>
      <span className="text-right text-[#44546F]">{activity.createdAtText}</span>
    </div>
  );
}
