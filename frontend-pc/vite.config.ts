import react from "@vitejs/plugin-react";
import path from "node:path";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [react()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          const normalizedId = id.split(path.sep).join("/");

          if (normalizedId.includes("/src/pages/dashboard/")) {
            return "page-dashboard";
          }

          if (normalizedId.includes("/src/pages/roadmap/")) {
            return "page-roadmap";
          }

          if (normalizedId.includes("/src/pages/backlog/")) {
            return "page-backlog";
          }

          if (normalizedId.includes("/src/pages/kanban/")) {
            return "page-kanban";
          }

          if (normalizedId.includes("/src/pages/reports/")) {
            return "page-reports";
          }

          if (normalizedId.includes("/src/pages/issues/")) {
            return "page-issues";
          }

          if (normalizedId.includes("/src/pages/project-settings/")) {
            return "page-project-settings";
          }

          if (normalizedId.includes("/src/pages/component-gallery/")) {
            return "page-component-gallery";
          }

          if (normalizedId.includes("/src/widgets/project-shell/")) {
            return "workspace-shell";
          }

          if (!normalizedId.includes("node_modules")) {
            return undefined;
          }

          if (normalizedId.includes("/@ant-design/") || normalizedId.includes("/@rc-component/") || normalizedId.includes("/rc-")) {
            return "vendor-antd-support";
          }

          if (normalizedId.includes("/antd/")) {
            return "vendor-antd";
          }

          if (normalizedId.includes("/react/") || normalizedId.includes("/react-dom/") || normalizedId.includes("/scheduler/")) {
            return "vendor-react";
          }

          if (normalizedId.includes("/@dnd-kit/")) {
            return "vendor-dnd";
          }

          if (normalizedId.includes("/@tanstack/")) {
            return "vendor-tanstack";
          }

          return "vendor";
        },
      },
    },
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
