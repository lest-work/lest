import { Suspense, useCallback, useEffect, useState, useTransition } from "react";

import { defaultWorkspacePage, RouteLoading, workspaceRoutes } from "@/app/routes";
import { pageFromPath, pathFromPage } from "@/app/routes/route-paths";
import type { WorkspacePageKey } from "@/entities/project-workspace/model";

export default function App() {
  const [page, setPage] = useState<WorkspacePageKey>(
    () => pageFromPath(window.location.pathname) ?? defaultWorkspacePage,
  );
  const [, startTransition] = useTransition();
  const Page = workspaceRoutes[page];

  const navigateTo = useCallback((nextPage: WorkspacePageKey) => {
    startTransition(() => {
      setPage(nextPage);
      window.history.pushState({ page: nextPage }, "", pathFromPage(nextPage));
    });
  }, []);

  // Sync page state on browser back/forward
  useEffect(() => {
    const handler = () => {
      const resolved = pageFromPath(window.location.pathname);
      if (resolved) {
        setPage(resolved);
      }
    };
    window.addEventListener("popstate", handler);
    return () => window.removeEventListener("popstate", handler);
  }, []);

  return (
    <Suspense fallback={<RouteLoading />}>
      <Page onNavigate={navigateTo} />
    </Suspense>
  );
}
