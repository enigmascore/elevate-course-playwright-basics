import { expect, test } from "../../support/fixtures";

// WRITTEN and green: the recipes area, using the `loggedInPage` fixture, unique
// data per test ( runId ), role and label locators, and outcome assertions.
test("the seeded recipes are listed with their cooks", async ({ loggedInPage: page }) => {
  await expect(page.getByTestId("recipe-row")).toHaveCount(6);
  await expect(page.getByRole("row").filter({ hasText: "Spaghetti Carbonara" })).toContainText("Maria Rossi");
});

test("the search is server-side and matches title or cuisine", async ({ loggedInPage: page }) => {
  const response = page.waitForResponse((r) => r.url().includes("/api/recipes?q=japanese"));
  await page.getByLabel("Search").fill("japanese");
  expect((await response).status()).toBe(200);

  await expect(page.getByTestId("recipe-row")).toHaveCount(2);
  await expect(page.getByTestId("recipe-row").first()).toContainText("Chicken Katsu Curry");
});

test("the add form validates before it saves", async ({ loggedInPage: page }) => {
  await page.getByRole("button", { name: "Add recipe" }).click();

  await expect(page.getByText("Title is required")).toBeVisible();
  await expect(page.getByText("Choose a cuisine")).toBeVisible();
  await expect(page.getByText("Choose a cook")).toBeVisible();
});

test("a new recipe appears in the list and survives a reload", async ({ loggedInPage: page, runId }) => {
  const title = `Tacos ${runId}`;

  await page.getByLabel("Title").fill(title);
  await page.getByLabel("Cuisine").selectOption("Mexican");
  await page.getByLabel("Cook").selectOption({ label: "Priya Nair" });
  await page.getByLabel("Servings").fill("3");
  await page.getByLabel("Vegetarian").check();
  await page.getByRole("button", { name: "Add recipe" }).click();

  const row = page.getByRole("row").filter({ hasText: title });
  await expect(row).toContainText("Priya Nair");
  await expect(row).toContainText("Yes");

  await page.reload();
  await expect(page.getByRole("row").filter({ hasText: title })).toBeVisible();
});

test("deleting goes through the confirm modal", async ({ loggedInPage: page, runId }) => {
  const title = `Doomed dish ${runId}`;
  await page.getByLabel("Title").fill(title);
  await page.getByLabel("Cuisine").selectOption("French");
  await page.getByLabel("Cook").selectOption({ label: "Ken Tanaka" });
  await page.getByRole("button", { name: "Add recipe" }).click();
  await expect(page.getByRole("row").filter({ hasText: title })).toBeVisible();

  // Cancel keeps it
  await page.getByRole("button", { name: `Delete ${title}` }).click();
  const dialog = page.getByRole("dialog");
  await expect(dialog).toContainText(title);
  await dialog.getByRole("button", { name: "Cancel" }).click();
  await expect(dialog).toBeHidden();
  await expect(page.getByRole("row").filter({ hasText: title })).toBeVisible();

  // Delete removes it
  await page.getByRole("button", { name: `Delete ${title}` }).click();
  await page.getByRole("dialog").getByRole("button", { name: "Delete" }).click();
  await expect(page.getByRole("row").filter({ hasText: title })).toHaveCount(0);
});

test("the slow shopping list arrives without a sleep", async ({ loggedInPage: page }) => {
  await page.getByRole("link", { name: "Shopping list" }).click();

  await expect(page.getByText("Loading shopping list...")).toBeVisible();
  await expect(page.getByTestId("shopping-row")).toHaveCount(5);
  await expect(page.getByText("Loading shopping list...")).toBeHidden();
});
