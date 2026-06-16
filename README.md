# mankova

Telegram-магазин для бренда Mankova. Интегрирован с CDEK (доставка) и RetailCRM.

## Компоненты

| Папка | Назначение |
|---|---|
| `markova` | Фронтенд каталога (ClojureScript, Reagent, shadow-cljs) |
| `markova_backend` | Бэкенд каталога (Clojure, http-kit, compojure) |
| `mankova_admin` | Фронтенд админ-панели (ClojureScript, Reagent) |
| `mankova_admin_backend` | Бэкенд админ-панели (Clojure, http-kit) |

## Запуск

**Бэкенд каталога:**
```bash
cd markova_backend
lein run
```

**Фронтенд каталога:**
```bash
cd markova
npm install
npx shadow-cljs watch app
```

Аналогично для `mankova_admin` / `mankova_admin_backend`.

## Конфигурация

Создай `api_keys.edn` и `db_parameters.edn` в папках бэкендов с токеном бота, ключами CDEK, RetailCRM и параметрами PostgreSQL.
