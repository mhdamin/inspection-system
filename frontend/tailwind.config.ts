import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./index.html",
    "./App.tsx",
    "./index.tsx",
    "./components/**/*.{ts,tsx}",
    "./layout/**/*.{ts,tsx}",
    "./services/**/*.{ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: "#eef7ff",
          100: "#d8ecff",
          200: "#badfff",
          300: "#8bcbff",
          400: "#54aeff",
          500: "#2d8dff",
          600: "#176ee8",
          700: "#1458d1",
          800: "#1647a9",
          900: "#173f85",
        },
      },
      boxShadow: {
        panel: "0 2px 16px rgba(0, 18, 46, 0.08)",
      },
      borderRadius: {
        panel: "0.875rem",
      },
      fontFamily: {
        sans: ["Segoe UI", "Roboto", "Helvetica Neue", "Arial", "sans-serif"],
      },
    },
  },
  plugins: [],
};

export default config;
