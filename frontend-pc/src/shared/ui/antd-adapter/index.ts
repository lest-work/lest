export type AntdFoundationStatus = "pending-install" | "installed";

export const antdFoundation = {
  status: "installed" as AntdFoundationStatus,
  rule: "Pages must import from @/shared/ui. Ant Design is allowed only behind shared/ui adapters.",
  packages: ["antd@^6.4.3", "@ant-design/icons@^6.2.5"],
};
