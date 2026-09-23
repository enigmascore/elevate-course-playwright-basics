import { expect, test } from "../../support/fixtures";

// WRITTEN and green: logging in, being refused, being redirected, logging out.
// The style to copy for the cooks suite.
test("the login page is up", async ({ page }) => {
  await page.goto("/login");

  await expect(page).toHaveTitle("Recipe Box");
  await expect(page.getByRole("heading", { name: "Log in to Recipe Box" })).toBeVisible();
});

test("Alice logs in and lands on the recipes", async ({ page }) => {
  await page.goto("/login");
  await page.getByLabel("Email").fill("alice@example.com");
  await page.getByLabel("Password").fill("bookworm");
  await page.getByRole("button", { name: "Log in" }).click();

  await expect(page).toHaveURL(/\/recipes/);
  await expect(page.getByRole("heading", { name: "Recipes" })).toBeVisible();
  await expect(page.getByText("Alice")).toBeVisible();
});

test("a wrong password is refused with a clear message", async ({ page }) => {
  await page.goto("/login");
  await page.getByLabel("Email").fill("alice@example.com");
  await page.getByLabel("Password").fill("not-her-password");
  await page.getByRole("button", { name: "Log in" }).click();

  await expect(page.getByRole("alert")).toHaveText("Wrong email or password");
  await expect(page).toHaveURL(/\/login/);
});

test("a protected page redirects to /login without a session", async ({ page }) => {
  await page.goto("/recipes");

  await expect(page).toHaveURL(/\/login/);
});

test("logging out asks first, with a native confirm", async ({ loggedInPage: page }) => {
  // dismiss: still logged in
  page.once("dialog", (dialog) => void dialog.dismiss());
  await page.getByRole("button", { name: "Log out" }).click();
  await expect(page).toHaveURL(/\/recipes/);

  // accept: out we go
  page.once("dialog", (dialog) => {
    expect(dialog.message()).toBe("Log out of Recipe Box?");
    void dialog.accept();
  });
  await page.getByRole("button", { name: "Log out" }).click();
  await expect(page).toHaveURL(/\/login/);
});
