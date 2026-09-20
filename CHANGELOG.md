# Changelog

## 0.1.0 - v1

- Fishing bobber bite marker: a screen-space marker floats above your bobber, switching from
  yellow to red the instant vanilla's real bite flag flips - readable through walls and in the
  dark. Toggleable in `config/foreman.json`.
- Terraform material calculator: two-corner WorldEdit-style selection (`[` / `]`), scans the
  real block data in the selected region, and reports an exact material list (raw counts plus
  stacks/shulkers) to chat with `\`. Configurable filters for air, water, and bedrock.
- Terraform selections persist per world/server, so re-joining doesn't force a re-select.
- A translucent wireframe box marks the active terraform selection in the world.
