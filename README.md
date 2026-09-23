# Recipe Box - the Basic Playwright course's template

A small full-stack web application whose Playwright suite is PARTLY written.
The **Basic Playwright** course's graded task lives here: two suites ship
green, and the cooks area ships untested for you to test ( see
[TASK.md](TASK.md) ). Recipe Box is the twin of Bookshelf, the app the
course teaches from - same shape, different domain - so everything you
learned there applies here.

## Create your own repository first

1. Click **Use this template** -> **Create a new repository**.
2. Name it `firstname-lastname-basic-playwright-answers` and make it
   **private**.
3. Invite your markers as collaborators ( the course names them ).
4. Clone YOUR repository, not this one.

## What Recipe Box does

Log in, a recipes list with server-side search and an "Add recipe" form, a
delete-confirm modal, a logout confirm, a shopping list that loads slowly
on purpose, and the cooks area: a list, an "Add cook" form that sends the
new cook a welcome email, removal through a confirm modal, and a detail
page listing each cook's recipes.

| Part          | Where       | How it runs                                   |
| ------------- | ----------- | --------------------------------------------- |
| Front end     | `frontend/` | Vite + React + TypeScript, on your machine     |
| Backend       | `backend/`  | Java Spring Boot, in a docker container         |
| Database      | docker      | Postgres 17                                    |
| Email         | docker      | MailHog - every email the app sends lands here |
| The tests     | `e2e/`      | Playwright, on your machine                    |

You never need Java on your machine, and you never need to read
`frontend/` or `backend/` - you run the app and you test it.

## Prerequisites

- **Docker Desktop**, running.
- **nvm** and **Node 24**: `nvm install` in this folder reads `.nvmrc`.
- **pnpm**: `npm install -g pnpm`.

## Run it

```
nvm use
make docker-up      # Postgres + MailHog, and BUILDS the backend image ( a few minutes the first time )
make install        # pnpm install in e2e/ and frontend/, then Playwright's Chromium
make test-written   # the two shipped suites: GREEN on a fresh copy
make test           # everything - RED until you have written the cooks tests
make test-answers   # only your suite
make report         # the HTML report of the last run
```

Each suite starts and stops its own backend container; you never start the
backend by hand. MailHog's inbox is at http://localhost:8027.

### Exploring the app yourself

```
make dev            # backend on the dev profile ( data survives restarts ) + the front end
```

Open http://localhost:5173 and log in. When you are done, run
**`make dev-stop`** before the next `make test`: the tests refuse to start
while a backend is already running on port 8086.

## Seeded users and data

`backend/src/main/resources/seed.sql` is loaded into EVERY fresh backend
the tests start:

| Email               | Password     |
| ------------------- | ------------ |
| alice@example.com   | `bookworm`   |
| bob@example.com     | `pageturner` |

Three cooks ( Maria Rossi: 3 recipes, Ken Tanaka: 2, Priya Nair: 1 ), six
recipes, five shopping-list items.

## The make targets

| Target                    | What it does                                                        |
| ------------------------- | ------------------------------------------------------------------- |
| `make docker-up`          | start Postgres + MailHog; build the backend image ( not started )   |
| `make docker-down`        | stop and remove the containers ( the database volume is kept )      |
| `make install`            | dependencies for `e2e/` and `frontend/`, plus Chromium              |
| `make test`               | the whole Playwright suite, headless                                |
| `make test-written`       | only the shipped suites ( `auth`, `recipes` )                       |
| `make test-answers`       | only `e2e/suites/answers/`                                          |
| `make test-headed`        | the whole suite with the browser visible                            |
| `make test-ui`            | Playwright's UI mode                                                |
| `make report`             | open the last HTML report                                           |
| `make dev` / `dev-stop`   | run / stop the app for manual exploration                           |
| `make mark STUDENT_REPO=` | markers only: run a student's answers folder from a clean clone     |

Course-author targets ( `backend-test-unit`, `backend-test-integration`,
`backend-verify` ) run the backend's own JUnit tests and need a JDK 25 on
the host. Students never run them.

## The map

```
docker-compose.yml   postgres ( 5435 ), mailhog ( 1027 / 8027 ), backend ( 8086 )
backend/             Spring Boot app + its unit tests ( *Test ) and integration tests ( *IT )
frontend/            Vite + React + TypeScript app on 5173; /api is proxied to the backend
e2e/                 the Playwright suite - see e2e/README.md; your work is e2e/suites/answers/
TASK.md              the graded task
```

## Bookshelf and Recipe Box

Both apps use the same ports, so run one at a time: `make docker-down` in
one repository before `make docker-up` in the other.
