import { type RouteConfig, route } from "@react-router/dev/routes";

export default [
    route("/frontend/", "routes/home.tsx"),
    route("/frontend/dashboard", "routes/dashboard.tsx"),
    route("/frontend/admin", "routes/admin.tsx"),
] satisfies RouteConfig;
