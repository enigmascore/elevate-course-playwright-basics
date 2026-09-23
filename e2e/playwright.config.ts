import { defineConfig } from "@playwright/test";

// The Recipe Box Playwright suite. One SUITE = one directory under suites/ = one
// PROJECT below = one fresh backend ( support/fixtures.ts starts a backend
// container before the suite's first test and stops it after the last ).
//
// Two suites ship WRITTEN and green ( auth, recipes ); the third - answers - is
// the graded work and ships as a red skeleton.
//
// Knobs ( env vars, no code changes ):
//   HEADLESS=1   run headless ( the browser is VISIBLE by default; make test sets it )
//   SLOWMO=<ms>  slow every Playwright action down, e.g. SLOWMO=500 to watch
const headless = !!process.env.HEADLESS;
const slowMo = Number(process.env.SLOWMO ?? 0);

export default defineConfig({
  testDir: "./suites",
  outputDir: "./test-results",
  // Each suite's worker owns THE backend on port 8086, so suites must run one
  // after another - parallel workers would fight over it.
  workers: 1,
  fullyParallel: false,
  // A retry mid-suite would replay steps against data the failed attempt
  // already created. Fail honestly instead.
  retries: 0,
  reporter: [["list"], ["html", { outputFolder: "./reports", open: "never" }]],
  timeout: slowMo > 0 ? 600_000 : 120_000,
  expect: { timeout: 10_000 },
  use: {
    baseURL: "http://localhost:5173",
    headless,
    launchOptions: { slowMo },
    // a trace on EVERY failure ( on-first-retry would never fire with retries: 0 )
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
  },
  projects: [
    { name: "auth", testDir: "./suites/auth" },
    { name: "recipes", testDir: "./suites/recipes" },
    // the graded work: its own project, so it gets its own fresh backend
    { name: "answers", testDir: "./suites/answers" },
  ],
  // ONE stateless Vite dev server serves every suite; only the backend restarts
  // per suite ( a webServer starts once per run, a fixture once per worker )
  webServer: {
    command: "pnpm dev",
    cwd: "../frontend",
    url: "http://localhost:5173",
    reuseExistingServer: true,
    timeout: 60_000,
  },
});
