export interface PermissionRole {
  id: string;
  name: string;
  description: string;
}

export interface PermissionItem {
  id: string;
  module: string;
  action: string;
  roles: Record<string, boolean>;
}
