import type { Route } from "./+types/home";
import { Welcome } from "../pages/welcome/welcome";

export function meta(_: Route.MetaArgs) {
  return [
    { title: "SecureBank" },
    { name: "description", content: "Welcome to SecureBank!" },
  ];
}

export default function Home() {
  return <Welcome />;
}
