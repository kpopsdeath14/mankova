# Repository Guidelines

## Project Structure & Module Organization
- `markova/` is the customer-facing ClojureScript app (Shadow-CLJS + React).
- `mankova_admin/` is the admin ClojureScript app.
- `markova_backend/` and `mankova_admin_backend/` are Clojure backend services with Leiningen projects.
- Backend configs live in `markova_backend/db_parameters.edn` and `mankova_admin_backend/db_parameters.edn`.

## Build, Test, and Development Commands
- `cd markova && npm install` installs frontend dependencies.
- `cd markova && npx shadow-cljs watch app` runs the dev build; `npx shadow-cljs browser-repl` opens a REPL.
- `cd markova && npx shadow-cljs release app` creates a production build.
- `cd mankova_admin && npm install` and `npx shadow-cljs watch app` run the admin UI.
- `cd markova_backend && lein run` starts the backend service; `lein uberjar` builds `backend.jar`.
- `cd mankova_admin_backend && lein test` runs backend tests; `lein run` starts the service.

## Coding Style & Naming Conventions
- Clojure/ClojureScript: 2-space indentation; keep namespaces aligned with folder paths.
- Filenames under `src/.../events/` and `src/.../pages/` follow `snake_case`; keep new files consistent.
- Prefer small, focused namespaces; place shared logic under `src/<app>/` rather than duplicating.

## Testing Guidelines
- Automated tests currently exist in `mankova_admin_backend/test/`; expand them when adding backend behavior.
- No frontend test harness is configured; validate UI changes with `shadow-cljs watch app` locally.

## Commit & Pull Request Guidelines
- Current history is minimal and informal; use concise messages that name the component (e.g., `markova_backend: add cdek handler`).
- PRs should summarize UI/backend changes and mention any config updates in `db_parameters.edn`.

## Configuration & Secrets
- Keep credentials out of source control; use local `db_parameters.edn` values and environment overrides as needed.
