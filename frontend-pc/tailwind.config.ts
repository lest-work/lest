import type { Config } from "tailwindcss";

export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        /* CSS variable-based colors (shadcn/ui pattern, used by @apply in CSS) */
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        success: {
          DEFAULT: "hsl(var(--success))",
          foreground: "hsl(var(--success-foreground))",
        },
        /* Semantic design tokens aligned with AntD theme */
        "lest": {
          primary: "#0C66E4",
          "primary-hover": "#0052CC",
          "primary-light": "#E9F2FF",
          "text-primary": "#172B4D",
          "text-secondary": "#626F86",
          "text-tertiary": "#6B778C",
          border: "#DFE1E6",
          "border-light": "#EBECF0",
          "bg-page": "#F7F8F9",
          "bg-card": "#FFFFFF",
          "bg-hover": "#F4F5F7",
          "bg-selected": "#E9F2FF",
        },
        status: {
          todo: "#0C66E4",
          progress: "#B76E00",
          review: "#5E4DB2",
          done: "#006644",
          closed: "#44546F",
        },
        priority: {
          highest: "#DE350B",
          high: "#FFAB00",
          medium: "#0C66E4",
          low: "#1F9D83",
          lowest: "#C1C7D0",
        },
        issue: {
          bug: "#DE350B",
          task: "#0C66E4",
          story: "#36B37E",
          subtask: "#42526E",
        },
      },
      borderRadius: {
        sm: "4px",
        md: "8px",
      },
      fontSize: {
        xs: ["12px", "16px"],
        sm: ["13px", "18px"],
        base: ["14px", "20px"],
        lg: ["20px", "28px"],
        xl: ["28px", "36px"],
      },
      fontFamily: {
        sans: [
          "Inter", "ui-sans-serif", "system-ui", "-apple-system",
          "BlinkMacSystemFont", '"Segoe UI"', '"PingFang SC"',
          '"Microsoft YaHei"', "sans-serif",
        ],
      },
      boxShadow: {
        card: "0 1px 2px rgba(9, 30, 66, 0.08)",
        popover: "0 4px 12px rgba(9, 30, 66, 0.15)",
      },
      keyframes: {
        "slide-in-right": {
          "0%": { transform: "translateX(100%)" },
          "100%": { transform: "translateX(0)" },
        },
      },
      animation: {
        "slide-in-right": "slide-in-right 0.2s ease-out",
      },
    },
  },
  plugins: [],
} satisfies Config;
