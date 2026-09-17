---
name: seedu-java-coding-standard
description: Apply the SE-EDU Java coding standard when creating, modifying, formatting, or reviewing Java code in this repository.
---

# SE-EDU Java Coding Standard

Use the [full SE-EDU Java coding standard](https://se-education.org/guides/conventions/java/)
for every Java file in this project, including test code. For topics it does not cover, use the
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
If another project rule is stricter, follow the stricter rule.
The summary below is a quick reference; consult the linked standard for rules it omits.

## Naming

- Use lowercase package names rooted in the project name and group related classes into logical packages.
- Use PascalCase nouns for classes and enums, camelCase verbs for methods, and camelCase for variables.
- Use SCREAMING_SNAKE_CASE for constants and a common prefix for associated constants.
- Write names in English. Treat acronyms as words when embedded in names, such as `exportHtmlSource`.
- Make boolean names read as booleans, normally with prefixes such as `is`, `has`, `was`, `can`, or `should`.
  Boolean setters take a correspondingly named parameter, such as `setFound(boolean isFound)`.
- Use plural names for collections. Give large-scope variables descriptive names; reserve short scratch names for
  small scopes. Use `i` for an outer loop and `j`, `k`, and later letters only for nested loops.
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`, omitting later parts when appropriate.

## Layout

- Indent with four spaces, never tabs. Indent wrapped lines eight spaces beyond their parent line.
- Keep lines below 110 characters where practical and never exceed 120 characters.
- Wrap after commas and before operators, including `.`, `&` in type bounds, and `|` in multi-catch clauses.
  Keep a method name attached to its opening parenthesis and prefer higher-level line breaks.
- Use K&R braces. Always use braces for loops and conditionals, including single-statement bodies, and put the
  conditional body on a separate line.
- Indent `case` and `default` labels inside a `switch`. Add `// Fallthrough` whenever a colon-style case
  intentionally continues into the next case.
- Put spaces around operators, after Java keywords and commas, and after semicolons in `for` headers. Surround a
  ternary colon with spaces.
- Separate logical units within a block with one blank line.

## Declarations and statements

- Put every class in a package and use explicit imports. Keep imports minimal and consistently ordered.
- Attach array brackets to the type, such as `int[] values`.
- Initialize variables at declaration when a valid value is available and declare them in the smallest useful scope.
- Keep fields non-public unless they are constants or members of a behavior-free data class.

## Comments and Javadoc

- Write comments in English using American spelling and avoid local slang.
- Add descriptive Javadoc to every class and public method, except getters/setters, overrides whose inherited
  documentation applies exactly, and test code.
- Start Javadoc with a concise summary sentence using forms such as “Returns”, “Adds”, or “Sends”. Keep `/**` on
  its own line for class and method comments, align subsequent `*` characters, leave one blank line before tags,
  punctuate tag descriptions, and place no blank line between the comment and declaration. A field Javadoc may be
  written on one line.
- Include either every `@param` tag or none. Omit them only when every parameter is already self-explanatory or
  explained in the description. Omit `@return` when the return value is already obvious.
- Indent block comments with the code they describe. Trailing comments are allowed when they remain clear.

## Completion check

Before finishing a Java change, inspect all changed Java lines for these rules, run the Gradle
`checkstyleMain` and `checkstyleTest` tasks and the relevant JUnit tests with Java 25, and update tests when
behavior changes.
Do not introduce unrelated formatting changes.
