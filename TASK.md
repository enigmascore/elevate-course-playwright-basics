# The graded task: test the cooks area

Recipe Box ships with two Playwright suites written and green ( `auth`,
`recipes` ) and ONE area deliberately untested: **cooks** - the list at
`/cooks`, the "Add cook" form, removal through a confirm modal, and the
detail page at `/cooks/:id`. Your job is to write those tests.

## Where

`e2e/suites/answers/cooks.spec.ts` holds six named tests whose bodies are
`throw new Error("TODO")`. Replace each body with a real test. **Keep the
six names exactly as they are** - the marker runs them by name. Add more
tests if you want to.

## The six behaviours

1. **The list shows the seeded cooks** - `seed.sql` creates three: Maria
   Rossi ( Pro, three recipes ), Ken Tanaka ( Home, two ), Priya Nair
   ( Home, one ). Assert on WHO is there, not only how many rows.
2. **The add-cook form validates, then a new cook appears and survives a
   reload** - submit empty and check the messages ( "Name is required",
   "Email is required", "Choose a level" ); enter a badly formed email and
   check "Email must look like name@example.com"; then add a cook with a
   `runId`-unique name and email, see the row, `page.reload()`, still there.
3. **A new cook receives a welcome email** - `clearMessages()` first, add the
   cook, then `waitForMessageTo( email, { subjectContains: "Welcome" } )`.
   Polled, never a fixed sleep.
4. **Removing a cook goes through the confirm modal: cancel keeps, confirm
   removes** - a cook who still has RECIPES cannot be removed ( the backend
   refuses and the modal shows "Remove the cook's recipes first" ), so add a
   fresh cook in this test and remove that one. Cancel keeps the row;
   Remove takes it away.
5. **The detail page lists the cook's recipes** - click a seeded cook's name,
   check the url matches `/cooks/<id>`, and assert WHICH recipe titles are
   listed ( Ken Tanaka: "Chicken Katsu Curry" and "Miso Soup" ), not just a
   count.
6. **`/cooks` redirects to `/login` without a session** - use the bare
   `page` fixture, not `loggedInPage`.

## The rules

- Everything OUTSIDE `e2e/suites/answers/` stays untouched: the written
  suites, `e2e/support/`, the config, the app, its backend tests. The
  marker diffs your clone against the template.
- Dependencies and the lockfile do not change. `@playwright/test` stays at
  the shipped version.
- Locators follow the taught hierarchy: `getByRole`, `getByLabel`, then
  `getByTestId` ( the app promises `cook-row` ); no CSS paths where a role
  locator exists.
- No `page.waitForTimeout()`. Assertions are web-first and about OUTCOMES.
- Your tests share the `answers` suite's fresh backend with each other:
  tolerate each other's data and put `runId` in every name and email you
  create.
- Reuse what the written suites use: `loggedInPage`, `runId`, the MailHog
  helpers.
- Write the tests yourself. The mark is for tests that meet the rubric, and
  the point of the course is that you can.

## Checking your work

```
make docker-up
make test-answers    # your suite, with its own fresh backend
make test            # written + answers - all green when you are done
```

## Submitting

The course's "Submitting your work" page has the full loop: an issue, a
branch, commits, a pull request in YOUR repository, and its URL pasted into
the course.
