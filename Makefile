# Recipe Box - the Basic Playwright course's TEMPLATE app. One entry point for
# everything; students and markers use exactly these targets.

.PHONY: help install docker-up docker-down dev dev-stop test test-written test-answers test-headed test-ui report mark \
        backend-test-unit backend-test-integration backend-verify

.DEFAULT_GOAL := help

# Test targets run HEADLESS; test-headed shows the browser.
# Override with HEADLESS= ( empty ) to watch a run; SLOWMO=<ms> slows every action.
HEADLESS ?= 1

help: ## List all targets with their descriptions
	@grep -E '^[a-zA-Z0-9_-]+:.*?## ' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-24s\033[0m %s\n", $$1, $$2}'

install: ## pnpm install in e2e/ and frontend/, then the Chromium browser Playwright drives
	cd e2e && pnpm install
	cd frontend && pnpm install
	cd e2e && pnpm exec playwright install chromium

docker-up: ## Start Postgres + MailHog and BUILD the backend image ( started later by the tests )
	docker compose up -d --build postgres mailhog
	docker compose build backend

docker-down: ## Stop and remove all three containers ( the database volume is kept )
	docker compose down

dev: docker-up ## Backend on the dev profile ( persistent data ) + the front end, for manual exploration
	SPRING_PROFILES_ACTIVE=dev docker compose up -d backend
	@echo "Backend on http://localhost:8086 ( dev profile ) - run 'make dev-stop' before 'make test'"
	cd frontend && pnpm dev

dev-stop: ## Stop the backend container ( the tests refuse to start over a running one )
	docker compose stop backend

test: docker-up ## Run the whole Playwright suite headless - RED on a fresh copy until the answers are written
	cd e2e && HEADLESS=$(HEADLESS) pnpm exec playwright test

test-written: docker-up ## Only the suites that ship WRITTEN ( auth, recipes ) - green on a fresh copy
	cd e2e && HEADLESS=$(HEADLESS) pnpm exec playwright test --project=auth --project=recipes

test-answers: docker-up ## Only the answers project ( e2e/suites/answers/ ) - the graded work
	cd e2e && HEADLESS=$(HEADLESS) pnpm exec playwright test --project=answers

test-headed: docker-up ## The same suite with the browser visible ( SLOWMO=500 make test-headed slows it down )
	cd e2e && HEADLESS= pnpm exec playwright test

test-ui: docker-up ## Playwright's UI mode
	cd e2e && pnpm exec playwright test --ui

report: ## Open the HTML report of the last run
	cd e2e && pnpm exec playwright show-report reports

# The MARKING procedure: the marker never runs a student's clone as-is. From a
# clean clone of THIS template, copy in ONLY the student's answers folder and run
# the answers project - so the marker's machine only ever runs the pinned versions.
mark: docker-up ## Mark a submission: make mark STUDENT_REPO=<path to the student's clone>
	@test -n "$(STUDENT_REPO)" || { echo "usage: make mark STUDENT_REPO=<path to the student's clone>"; exit 1; }
	@test -d "$(STUDENT_REPO)/e2e/suites/answers" || { echo "no e2e/suites/answers/ under $(STUDENT_REPO)"; exit 1; }
	rm -rf e2e/suites/answers
	cp -R "$(STUDENT_REPO)/e2e/suites/answers" e2e/suites/answers
	cd e2e && HEADLESS=$(HEADLESS) pnpm exec playwright test --project=answers

# ---- course-author targets: these need a JDK 25 on the host, students never run them ----

backend-test-unit: ## Backend unit tests only ( *Test, no Spring context )
	cd backend && ./mvnw -B test

backend-test-integration: docker-up ## Backend integration tests only ( *IT, against the dockerised Postgres )
	cd backend && ./mvnw -B -Dskip.ut=true verify

backend-verify: docker-up ## Backend unit + integration tests - the backend's green bar
	cd backend && ./mvnw -B clean verify
