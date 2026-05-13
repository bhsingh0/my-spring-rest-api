################################################################################
#  DEVELOPER AUTOMATION GUIDE — Spring Boot REST API
#  Reusable reference for every feature, bug fix, and release cycle.
################################################################################

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 1 · COMMIT AND PUSH WORKFLOW
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

# 1a. Create a feature branch (always branch from main)
git checkout main
git pull origin main
git checkout -b feature/<short-description>
# Examples:
#   git checkout -b feature/add-user-auth
#   git checkout -b fix/null-pointer-user-create
#   git checkout -b chore/improve-test-coverage

# 1b. Stage changes
git add <specific-file>           # preferred — avoids accidental secrets
git add src/                      # stage entire src tree
git add -p                        # interactive staging (review hunks)

# 1c. Commit with a structured message
git commit -m "$(cat <<'EOF'
<type>(<scope>): <short summary under 72 chars>

<optional body: what changed and why, not how>

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
# Type values: feat | fix | test | refactor | docs | chore | perf
# Examples:
#   feat(user): add email uniqueness validation
#   fix(user): handle null body in POST /api/users
#   test(user): add missing 404 and 400 edge cases

# 1d. Push the branch
git push -u origin feature/<short-description>

# 1e. Keep branch up-to-date with main (do this regularly)
git fetch origin
git rebase origin/main

# 1f. Build and test BEFORE pushing (required by CLAUDE.md)
mvn clean test
mvn clean install


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 2 · CLAUDE CODE CLI COMMANDS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

# Open Claude Code in the project root
claude

# ── Review code ──────────────────────────────────────────────────────────────
# Review a specific file
> Review src/main/java/com/example/api/UserController.java for Spring Boot
  best practices, error handling, and security issues.

# Review all changes on the current branch vs main
> Review all changes on this branch compared to main. Focus on:
  - Spring Boot conventions
  - Input validation and error handling
  - Test coverage gaps
  - Security issues (no SQL injection, no exposed secrets)

# Run the built-in review skill
/review

# ── Generate tests ───────────────────────────────────────────────────────────
# Generate tests for a controller
> Generate comprehensive JUnit 5 tests for UserController.java following the
  naming convention in CLAUDE.md. Cover: happy path, invalid input (400),
  not found (404), duplicate data, and all HTTP status codes.

# Generate tests for a specific method
> Generate tests for the createUser endpoint. Follow CLAUDE.md test standards.
  Use @WebMvcTest and @MockBean for the repository.

# ── Create a PR ──────────────────────────────────────────────────────────────
> Create a pull request from the current branch to main. Use the conventional
  commit messages on this branch for the PR title and description. Include a
  test plan checklist.

# Via gh CLI (Claude will run this for you, or run it yourself):
gh pr create \
  --title "<type>(<scope>): <summary>" \
  --body "$(cat <<'EOF'
## Summary
- <bullet 1>
- <bullet 2>

## Test plan
- [ ] `mvn clean test` passes locally
- [ ] Happy path tested manually
- [ ] Edge cases covered in unit tests
- [ ] No secrets or credentials committed

## Related issues
Closes #<issue-number>

🤖 Generated with [Claude Code](https://claude.ai/claude-code)
EOF
)"

# ── Fix failing tests ────────────────────────────────────────────────────────
> The following tests are failing. Identify the root cause, fix the production
  code (not the tests unless the tests are wrong), then verify all tests pass:
  <paste mvn test output here>

# Run a specific failing test to get the stack trace
mvn test -Dtest=UserControllerTest#testCreateUser_WithMissingEmail_ReturnsBadRequest

# ── Add documentation ────────────────────────────────────────────────────────
> Add Javadoc to all public methods in UserController.java. Keep comments
  concise — document the WHY and any non-obvious constraints, not the what.

> Update README.md to document the new <endpoint name> endpoint, including
  request/response examples and error codes.


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 3 · GITHUB PR CREATION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

# ── PR title templates ───────────────────────────────────────────────────────
feat(user): add email uniqueness validation
fix(user): handle null request body in POST /api/users
test(user): add comprehensive edge-case coverage for UserController
refactor(user): extract validation logic into UserValidator service
docs(api): document all UserController endpoints with examples
chore(deps): upgrade Spring Boot to 3.2.1

# ── PR body template (copy-paste and fill in) ────────────────────────────────
gh pr create \
  --title "feat(user): <summary>" \
  --base main \
  --body "$(cat <<'EOF'
## Summary
-
-
-

## Changes
| File | Change |
|------|--------|
| `UserController.java` | |
| `UserControllerTest.java` | |

## Test plan
- [ ] `mvn clean test` — all tests pass
- [ ] `mvn clean install` — build succeeds
- [ ] Happy path verified
- [ ] Error cases verified (400, 404, 409)
- [ ] No secrets committed

## Related issues
Closes #

🤖 Generated with [Claude Code](https://claude.ai/claude-code)
EOF
)"

# ── Link issues ──────────────────────────────────────────────────────────────
# In PR body, use any of these GitHub keywords to auto-close issues on merge:
#   Closes #<n>   Fixes #<n>   Resolves #<n>

# List open issues
gh issue list

# View a specific issue
gh issue view <number>

# ── Useful gh commands ───────────────────────────────────────────────────────
gh pr list                          # list open PRs
gh pr view <number>                 # view a PR
gh pr checks <number>               # check CI status
gh pr merge <number> --squash       # merge (squash)
gh pr close <number>                # close without merging
gh pr diff <number>                 # view diff in terminal


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 4 · SLACK NOTIFICATIONS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

# Send a Slack message via the Incoming Webhooks API
# (replace WEBHOOK_URL with your team's actual webhook URL stored in env)

# ── New PR notification ───────────────────────────────────────────────────────
curl -s -X POST "$SLACK_WEBHOOK_URL" \
  -H 'Content-type: application/json' \
  --data '{
    "text": "*PR Ready for Review*",
    "attachments": [{
      "color": "#36a64f",
      "fields": [
        {"title": "PR", "value": "<'"$PR_URL"'|'"$PR_TITLE"'>", "short": false},
        {"title": "Branch", "value": "'"$BRANCH"'", "short": true},
        {"title": "Author", "value": "'"$AUTHOR"'", "short": true}
      ]
    }]
  }'

# ── Tests failed notification ────────────────────────────────────────────────
curl -s -X POST "$SLACK_WEBHOOK_URL" \
  -H 'Content-type: application/json' \
  --data '{
    "text": ":red_circle: *Tests Failed* on `'"$BRANCH"'`\nRun: `mvn test` locally to reproduce."
  }'

# ── PR merged notification ────────────────────────────────────────────────────
curl -s -X POST "$SLACK_WEBHOOK_URL" \
  -H 'Content-type: application/json' \
  --data '{
    "text": ":white_check_mark: *Merged* `'"$PR_TITLE"'` into main\n<'"$PR_URL"'|View PR>"
  }'

# ── Environment variables to set (do NOT hard-code these) ────────────────────
# export SLACK_WEBHOOK_URL="https://hooks.slack.com/services/..."
# Store in .env (git-ignored) or your CI/CD secrets vault.


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 5 · COMPLETE WORKFLOW EXAMPLE (start to finish)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Scenario: Add email uniqueness validation to POST /api/users

─── Step 1: Branch ──────────────────────────────────────────────────────────
git checkout main && git pull origin main
git checkout -b feature/user-email-uniqueness

─── Step 2: Open Claude Code and implement ──────────────────────────────────
claude
> Add email uniqueness validation to UserController.java.
  When a duplicate email is POSTed, return HTTP 409 Conflict with a
  descriptive error message. Follow the error-handling patterns already in
  the codebase.

─── Step 3: Generate tests ──────────────────────────────────────────────────
> Generate tests for the email uniqueness validation. Follow CLAUDE.md naming:
  testCreateUser_WithDuplicateEmail_Returns409Conflict
  Use @WebMvcTest + @MockBean for UserRepository.

─── Step 4: Run tests locally ───────────────────────────────────────────────
mvn clean test
# If failures → see Section 7 (Troubleshooting) or ask Claude to fix them.

─── Step 5: Build ───────────────────────────────────────────────────────────
mvn clean install

─── Step 6: Stage and commit ────────────────────────────────────────────────
git add src/main/java/com/example/api/UserController.java
git add src/main/java/com/example/api/GlobalExceptionHandler.java
git add src/test/java/com/example/api/UserControllerTest.java

git commit -m "$(cat <<'EOF'
feat(user): return 409 Conflict on duplicate email

Adds a check in createUser that queries the repository for an existing
email before persisting. GlobalExceptionHandler maps the resulting
DataIntegrityViolationException to a 409 response.

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"

─── Step 7: Push ────────────────────────────────────────────────────────────
git push -u origin feature/user-email-uniqueness

─── Step 8: Create PR ───────────────────────────────────────────────────────
gh pr create \
  --title "feat(user): return 409 Conflict on duplicate email" \
  --base main \
  --body "$(cat <<'EOF'
## Summary
- POST /api/users now returns 409 when email already exists
- GlobalExceptionHandler maps DataIntegrityViolationException → 409
- Tests added: duplicate email returns 409 with error body

## Test plan
- [ ] `mvn clean test` passes
- [ ] `mvn clean install` succeeds
- [ ] Manual: POST duplicate email → 409 response verified

## Related issues
Closes #<n>

🤖 Generated with [Claude Code](https://claude.ai/claude-code)
EOF
)"

─── Step 9: Notify team (optional) ──────────────────────────────────────────
PR_URL=$(gh pr view --json url -q .url)
PR_TITLE="feat(user): return 409 Conflict on duplicate email"
BRANCH="feature/user-email-uniqueness"
AUTHOR="bhsingh0"

curl -s -X POST "$SLACK_WEBHOOK_URL" \
  -H 'Content-type: application/json' \
  --data '{
    "text": "*PR Ready for Review*",
    "attachments": [{
      "color": "#36a64f",
      "fields": [
        {"title": "PR", "value": "<'"$PR_URL"'|'"$PR_TITLE"'>"},
        {"title": "Branch", "value": "'"$BRANCH"'", "short": true},
        {"title": "Author", "value": "'"$AUTHOR"'", "short": true}
      ]
    }]
  }'

─── Step 10: Address review feedback ────────────────────────────────────────
# Make changes, then:
git add <files>
git commit -m "fix(user): address PR review feedback"
git push
# (PR updates automatically)


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 6 · TEMPLATE COMMANDS BY SCENARIO
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

─── A. Adding a new feature ─────────────────────────────────────────────────
git checkout -b feature/<name>

# In Claude Code:
> Implement <feature description> in <file(s)>. Follow Spring Boot patterns
  in CLAUDE.md. Return HTTP <201|200|etc.> on success.

> Generate comprehensive tests for <feature>. Cover happy path, 400, 404,
  and any domain-specific edge cases. Use @WebMvcTest + @MockBean.

mvn clean test && mvn clean install

git add src/
git commit -m "feat(<scope>): <summary>"
git push -u origin feature/<name>
gh pr create --title "feat(<scope>): <summary>" --base main

─── B. Fixing a bug ─────────────────────────────────────────────────────────
git checkout -b fix/<description>

# In Claude Code:
> Bug: <describe exact behavior>. Expected: <expected behavior>.
  Affected file(s): <list files>. Fix the root cause; do not patch symptoms.

> Add a regression test named test<Method>_<Scenario>_<ExpectedResult>
  that would have caught this bug.

mvn clean test && mvn clean install

git add src/
git commit -m "fix(<scope>): <one-line description of root cause fix>"
git push -u origin fix/<description>
gh pr create --title "fix(<scope>): <summary>" --base main

─── C. Improving code quality / refactor ────────────────────────────────────
git checkout -b refactor/<area>

# In Claude Code:
> Refactor <class/method> to <goal: extract method / reduce duplication /
  improve readability>. Do not change observable behavior; all existing
  tests must still pass after the refactor.

mvn clean test

git add src/
git commit -m "refactor(<scope>): <summary>"
git push -u origin refactor/<area>
gh pr create --title "refactor(<scope>): <summary>" --base main

─── D. Generating tests ─────────────────────────────────────────────────────
git checkout -b test/<class-name>

# In Claude Code:
> Generate a complete test class for <ClassName>. Follow CLAUDE.md test
  standards. Name tests as test<Method>_<Scenario>_<ExpectedResult>.
  Use @WebMvcTest + @MockBean. Cover:
  1. Happy path (200/201)
  2. Invalid input (400)
  3. Not found (404)
  4. Duplicate/conflict (409)
  5. Edge cases (null, empty, boundary values)
  6. Correct HTTP status code for every case

mvn test -Dtest=<ClassNameTest>

git add src/test/
git commit -m "test(<scope>): add comprehensive coverage for <ClassName>"
git push -u origin test/<class-name>
gh pr create --title "test(<scope>): <summary>" --base main

─── E. Adding documentation ─────────────────────────────────────────────────
git checkout -b docs/<topic>

# In Claude Code:
> Add/update documentation for <class or endpoint>. Include:
  - Javadoc on all public methods (WHY and constraints, not what)
  - README section: endpoint description, request/response examples,
    error codes, and curl example

git add src/ README.md
git commit -m "docs(<scope>): <summary>"
git push -u origin docs/<topic>
gh pr create --title "docs(<scope>): <summary>" --base main


━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SECTION 7 · TROUBLESHOOTING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

PROBLEM: Tests fail locally after pulling main
  CAUSE:  Someone merged a breaking change or your branch is behind.
  FIX:    git fetch origin && git rebase origin/main
          mvn clean test

PROBLEM: mvn clean install fails with compilation error
  FIX:    Check Java version: java -version  (must be 17+)
          Check JAVA_HOME: echo $JAVA_HOME
          Run: mvn clean compile -e  to see full error

PROBLEM: Port 8080 already in use when running spring-boot:run
  FIX:    lsof -ti:8080 | xargs kill -9
          or: mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

PROBLEM: H2 console not accessible
  FIX:    Confirm application.properties has:
            spring.h2.console.enabled=true
            spring.h2.console.path=/h2-console
          URL: http://localhost:8080/h2-console
          JDBC URL: jdbc:h2:mem:testdb

PROBLEM: @MockBean not injecting properly in tests
  FIX:    Ensure @WebMvcTest(UserController.class) is on the test class.
          Only @MockBean the repository/service used by that controller.
          Don't mix @SpringBootTest and @WebMvcTest in the same class.

PROBLEM: gh pr create fails (not authenticated)
  FIX:    gh auth login
          Follow prompts to authenticate with GitHub.

PROBLEM: git push rejected (non-fast-forward)
  FIX:    git fetch origin && git rebase origin/<branch>
          Resolve conflicts if any, then: git push

PROBLEM: Claude Code review suggests changes that conflict with CLAUDE.md
  FIX:    CLAUDE.md takes precedence. Tell Claude:
          > Please re-review following the constraints in CLAUDE.md.

PROBLEM: Tests pass locally but fail in CI
  CAUSE:  Environment differences (env vars, DB state, file paths).
  FIX:    Check CI logs: gh run view --log
          Ensure tests use @SpringBootTest(webEnvironment = RANDOM_PORT)
          or @WebMvcTest and don't depend on external state.

PROBLEM: Merge conflicts after rebase
  FIX:    git status                   # see conflicting files
          # Edit files to resolve conflicts (remove <<<<< >>>>> markers)
          git add <resolved-files>
          git rebase --continue

PROBLEM: Accidentally committed to main
  FIX:    # Create a branch at the current position first
          git checkout -b fix/<name>
          git push -u origin fix/<name>
          # Then reset main (only if not yet pushed to remote)
          git checkout main
          git reset --hard origin/main

PROBLEM: `mvn test` hangs indefinitely
  FIX:    mvn test -Dsurefire.timeout=60
          Check for @BeforeEach that opens connections without closing them.

PROBLEM: Spring context fails to load in tests
  FIX:    Check application.properties in src/test/resources exists.
          Look for missing @Bean definitions or circular dependencies in logs.
          Run: mvn test -e 2>&1 | grep "Caused by"

─── Quick diagnostic sequence ───────────────────────────────────────────────
java -version                         # Java 17+?
mvn -version                          # Maven present?
git status                            # Clean working tree?
mvn clean test -e 2>&1 | tail -50    # Full error output
gh auth status                        # GitHub auth OK?


################################################################################
#  END OF AUTOMATION GUIDE
#  Keep this file in the repo root and update it when team conventions change.
################################################################################
