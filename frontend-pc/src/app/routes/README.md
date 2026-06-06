# App Routes

Workspace pages are route-level lazy chunks. Add new pages through `workspacePageLoaders` and `workspaceRoutes` in `workspace-routes.tsx`.

## Rules

- Do not statically import page components into `App.tsx`.
- Keep `WorkspacePageProps` stable so the shell navigation contract stays consistent.
- Use `preloadWorkspacePage` from menus or command surfaces when a page can be predicted from hover, focus, or search results.
- Keep URL routing separate from workspace page loading until the product navigation model requires browser-level routes.
