# Project context

This repository contains Cyclique, a team project for CS2103T based on AddressBook Level 3.
Cyclique is a desktop application for organisers of recreational cycling groups in Singapore,
combining command-line input with a graphical user interface.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on this
repository. Adapt to the user's role if they identify themselves as an instructor or
another project stakeholder.

## Student profile

- Prior knowledge: Basic Java and OOP concepts.
- Programming experience: Intermediate.
- IDE and expertise: IntelliJ IDEA, intermediate.

# Guidance for interacting with users

- Briefly explain significant actions and their rationale, supporting the student's learning.
- When suggesting a Git command, explain what it does.
- Make generated code self-explanatory. Add Javadoc to classes and to nontrivial methods
  and fields whose purpose or behaviour is not obvious, following the Java coding standard.
- Add comments where they improve understanding.
- Choose the simplest design sufficient for the requirements, briefly explaining relevant
  alternatives when they help the user understand the trade-off.

# Project-specific requirements

## Java coding standards

Before creating, modifying, formatting, or reviewing Java code or tests, read and follow
`.agents/skills/seedu-java-coding-standard/SKILL.md`. The full
[SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/)
is authoritative for style; use `config/checkstyle/checkstyle.xml` for automated checks.
If another repository rule is stricter, follow the stricter rule.

## Java version and build checks

Use JDK 25 when running the application, builds, tests, and checks. Select an installed
JDK 25 distribution and verify the active version before running Java tasks.
The GitHub Actions workflow in
`.github/workflows/gradle.yml` already runs on pushes and pull requests.
Run `./gradlew check coverage` for Java changes, using JDK 25.

## Test coverage

Maintain JUnit test coverage of at least 50%, prioritising complex, core, and critical
business logic. Review the relevant tests after code changes and update them when needed
to protect changed behaviour against regressions. Use `./gradlew coverage` to generate
the JaCoCo coverage report; do not assume that generating a report enforces the target.

## UI regression testing

After code changes, review the testing guidance in `docs/Testing.md` and the
`Appendix: Instructions for manual testing` in `docs/DeveloperGuide.md`.
Update affected scenarios, inputs, expected results, or test configuration when needed.
For changes affecting user-visible behaviour, run the relevant manual UI checks. If the
affected scenarios cannot be identified confidently, run the full documented manual test
plan. Report what was tested and any checks that could not be completed.

## Language

Use British English in all documentation and user-facing text. Java comments
and Javadoc are an exception: use American English in accordance with the
SE-EDU Java coding standard. Preserve the original spelling of code identifiers,
commands, APIs, proper nouns, and quoted external text.

## Directory structure

Preserve these directory paths exactly; grading scripts depend on them:

- `src/main/java`
- `src/test/java`
- `docs`

Do not rename, move, or remove these directories. Changes to files within them
are allowed when needed for the project.

## Git

Before proposing or creating any commit, commit message, or branch, follow the
[SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
and the rules below. These rules are recorded here so they do not depend on skills
installed only in the iP repository.

### Commit messages

- Review the actual diff before writing a message so it describes only the intended commit.
- Use an imperative subject, capitalise its first letter, and omit a trailing full stop.
- Aim for at most 50 characters in the subject; 72 is the hard limit.
- Use an optional scope or category prefix only when it clarifies the change.
- Give nontrivial commits a body separated from the subject by a blank line.
- Wrap the body at 72 characters and separate paragraphs with blank lines.
- Explain what changed and why, leaving implementation mechanics to the diff.
- Describe the existing situation in the present tense, explain why it needs to change,
  describe the change in the imperative mood, and give the rationale or relevant context.
- Avoid repeating code comments. Split unrelated changes into separate commits.

### Branches, tags, and authorisation

- Use meaningful kebab-case branch names. For issue-related work, include the issue
  number and relevant title keywords, for example `1234-ui-freeze-error`.
- In Codex, use the `codex/` branch prefix unless the user requests otherwise, for example
  `codex/1234-ui-freeze-error`.
- Use lightweight tags unless the user requests annotated tags.
- Do not create branches or tags, commit, or push unless the user authorises the action.
- Do not commit or push unless explicitly asked.
