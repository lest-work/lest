# Shared UI

`shared/ui` 是 Lest PC 前端唯一的通用组件出口。页面、widgets、features 必须从这里引入 UI 能力，不直接引入 Ant Design。

当前底座为 `antd@^6.4.3` 和 `@ant-design/icons@^6.2.5`。Ant Design 只能作为底层实现细节存在，业务页面永远使用 Lest 自己的组件 API。

## Boundary

- Allowed in pages/features/widgets: `@/shared/ui`
- Allowed in app providers, shared config, shared/ui only: `antd`, `@ant-design/icons`
- Domain-specific components stay in `entities/*/ui`, `features/*/ui`, or `widgets/*/ui`

## Directory Map

- `form`: `Input`, `Textarea`, `Select`, `Checkbox`, `Switch`, `DatePicker`
- `data-display`: `Avatar`, `Badge`, `Card`, `DataTable`, `Pagination`
- `feedback`: `Alert`, `EmptyState`, `Loading`, `ResultState`, `Skeleton`, `ToastViewport`
- `overlay`: `Modal`, `Drawer`, `Dropdown`, `DangerDialog`
- `antd-adapter`: Ant Design integration boundary

## API Rule

- API request/response contracts and services live in `src/api/<domain>/contracts.ts`.
- Mock services implement the same request shape that the real backend should expose.
- Shared response conventions live in `src/api/models`: `ApiResult`, `ApiPageRequest`, `ApiPageResponse`, `ApiErrorBody`.
- All pages import API types from `@/api/<domain>` instead of entities.
- React Query hooks referencing API calls live in `src/api/<domain>/queries.ts` or at the consuming widget/feature layer.

## Navigation Rule

Global navigation, search, notifications, help, settings, and user menu belong to `ProjectWorkspaceShell`.
Menu definitions live in `entities/project-workspace/model/navigation.ts`, so top navigation, side navigation, and shell action menus do not drift into separate page-level copies.

## Migration Rule

When Ant Design is installed, replace internals inside `shared/ui/*` or add new wrappers under these folders. Do not update page code to import from `antd`.
