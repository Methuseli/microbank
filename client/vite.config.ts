import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

export default defineConfig({
  base: '/frontend/',
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  server: {
    host: '0.0.0.0',
    port: 5173
  },
  allowedHosts: [
      'frontend',          // service name inside Docker
      'localhost',         // local dev
      '127.0.0.1',
      '13.59.11.243.sslip.io' // your public domain/IP
    ]
});
