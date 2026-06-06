# Ant Design Adapter Migration

## Goal

Ant Design is the low-level component foundation. Product code keeps importing from `@/shared/ui`.

## Install

```bash
npm install antd@^6.4.3 @ant-design/icons@^6.2.5
```

The baseline is Ant Design 6.x. Do not add compatibility code for Ant Design 5.x unless a future product decision explicitly requires it.

## First Wrappers

1. `button/Button`: wraps `antd/Button`, preserving `ButtonProps.loading`, `variant`, `size`, and existing className behavior.
2. `input/Input`: wraps `antd/Input`, keeping existing controlled input call sites stable.
3. `overlay/Modal`, `overlay/Drawer`, `overlay/Dropdown`: wrappers exist before replacing page-level custom overlays.
4. `form/Select`, `form/DatePicker`, `form/Textarea`, `form/Checkbox`, `form/Switch`: replace raw controls gradually after wrapper APIs are stable.
5. `data-display/DataTable`: wraps `antd/Table`; add TanStack table helpers only where domain tables need advanced state.
6. `data-display/PieChart`, `data-display/BarChart`: use a chart library behind wrappers; pages must not import chart libraries directly.

## Compatibility Rule

Existing page layout should not change during migration. Replace internals first, then migrate call sites only when the wrapper API is stable.
