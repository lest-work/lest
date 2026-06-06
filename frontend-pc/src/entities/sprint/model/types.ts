export interface Sprint {
  id: string;
  name: string;
  goal: string;
  status: "planning" | "active" | "completed";
  startDate: string;
  endDate: string;
  capacity: number;
  issueIds: string[];
}
