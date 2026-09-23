import { expect, test } from "../../support/fixtures";
import { clearMessages, waitForMessageTo } from "../../support/mailhog";

// THE GRADED TASK - see TASK.md. The cooks area ( /cooks and /cooks/:id ) ships
// UNTESTED. Replace each `throw new Error("TODO")` below with a real test.
// Keep the six test names exactly as they are; add more tests if you like.
//
// You have everything the written suites use: `loggedInPage`, `runId`, the
// MailHog helpers, and the app's seed ( three cooks: Maria Rossi with three
// recipes, Ken Tanaka with two, Priya Nair with one ).
test.describe("the cooks area", () => {
  test("the list shows the seeded cooks", async ({ loggedInPage: page }) => {
    void page;
    throw new Error("TODO");
  });

  test("the add-cook form validates, then a new cook appears and survives a reload", async ({
    loggedInPage: page,
    runId,
  }) => {
    void page;
    void runId;
    throw new Error("TODO");
  });

  test("a new cook receives a welcome email", async ({ loggedInPage: page, runId }) => {
    void page;
    void runId;
    void clearMessages;
    void waitForMessageTo;
    throw new Error("TODO");
  });

  test("removing a cook goes through the confirm modal: cancel keeps, confirm removes", async ({
    loggedInPage: page,
    runId,
  }) => {
    void page;
    void runId;
    throw new Error("TODO");
  });

  test("the detail page lists the cook's recipes", async ({ loggedInPage: page }) => {
    void page;
    throw new Error("TODO");
  });

  test("/cooks redirects to /login without a session", async ({ page }) => {
    void page;
    void expect;
    throw new Error("TODO");
  });
});
