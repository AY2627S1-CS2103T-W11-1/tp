# Release testing results

## Alphabetical display checks (11 October 2026)

The alphabetical display change passes `./gradlew check coverage shadowJar` with
Eclipse Temurin JDK 25.0.4.1 on macOS ARM64: **308 tests**, no failures, errors or
skips, and both Checkstyle tasks pass. JaCoCo line coverage is **75.33%**
(1,139 of 1,512 lines), exceeding the 50% target. Instruction coverage is 77.60%
and branch coverage is 88.44%. `./.github/run-checks.sh` and `git diff --check` pass.

Nine new tests cover full-name and case-insensitive ordering, stable ties, additions,
name edits in both directions, filtering, deletion, book replacement, phone-number
targeting and JSON reload. Existing command expectations now reflect alphabetical
display while saved records retain their original order.

The rebuilt JAR was launched with JDK 25 in `/private/tmp/cyclique-alphabetical-ui`.
Logs confirm sample-data loading and entry into UI startup. The computer-use app
inventory does not expose the standalone JavaFX process, and selecting `java` returns
`Invalid app: java`. Consequently, the Developer Guide's **Alphabetical display
regression** scenario could not be performed visually. Consecutive card indexes,
command-box interactions and restart display still require manual confirmation;
the automated tests verify the underlying displayed order and reload behaviour.
Windows and Linux UI checks remain outstanding.

The results below describe the earlier duplicate-phone change.

Date: 11 October 2026 (Singapore). Tested locally on macOS ARM64 using
Eclipse Temurin JDK 25.0.4.1. Application version reported at startup: V0.5.1.

Duplicate cyclist identity conflicts are **resolved** with regression coverage.
Release recommendation: **complete the outstanding manual UI checks** before release.

## Automated checks

`./gradlew check coverage shadowJar` passes. JUnit reports **341 tests**,
**0 failures**, **0 errors** and **0 skipped tests**. Both production and test
Checkstyle tasks pass, and the release JAR builds successfully.
The repository-wide `./.github/run-checks.sh` passes. Its cached Git checks were
supplemented with checks of all working-tree text files, including new files;
there were no prohibited line endings, missing final newlines or trailing whitespace.
`git diff --check` also passes.

Sixteen regression tests were added: thirteen command workflow tests and three FTP tests.
The command tests exercise real parsing, model changes, filtering and JSON read-back.
They cover the help-guide add example, omitted address, repeated tags and locations,
edit replacement and preservation of unspecified fields, tag clearing, name/phone
find rules, filtered edit/delete behaviour, clear persistence, rejected commands
without data changes, and help/exit flags. Additional duplicate-handling tests cover phone
and case-insensitive email conflicts across filters, unchanged unique identities, same-name
cyclists with unique phone numbers and emails, and successful unique edits across reload.
FTP tests cover valid boundaries,
invalid input, equality and hashing.

| JaCoCo metric | Before | After |
| --- | ---: | ---: |
| Lines | 74.85% | 75.28% |
| Instructions | 77.00% | 77.53% |
| Branches | 86.92% | 88.44% |

Line coverage is 1,136 of 1,509 lines, above the required 50% target.
The report includes the UI and application entry points; no classes were excluded
to raise the result. Coverage generation does not automatically enforce the target.
Command classes have 100% line coverage, parsers 97.98%, storage 97.20%,
and person model classes 92.63%. UI line coverage remains 11.94%, so the aggregate
result cannot establish visual correctness.

Generated reports:

- JUnit: `build/reports/tests/test/index.html`
- JaCoCo: `build/reports/jacoco/coverage/html/index.html`
- Coverage XML: `build/reports/jacoco/coverage/coverage.xml`
- Release JAR: `build/libs/addressbook.jar`

## Resolved release issue: ambiguous phone identity

The original issue allowed different names to share a phone number, and edit/delete
selected the first matching phone number. The target depended on list order and filtering.
The following reproduction describes the behaviour before the fix.

Reproduced through `LogicManager` and real JSON storage using the release JAR
with JDK 25 and disposable temporary data:

```text
add n/John Doe p/+65 98765432 e/john@example.com f/300 x/Beginner l/any
add n/Jane Doe p/+65 98765432 e/jane@example.com f/300 x/Beginner l/any
find n/Jane
delete +65 98765432
```

Before the fix, both adds succeeded. Only Jane was displayed after find, but delete
reported Deleted person: John Doe and left Jane in the book. Adding John again with a
different number and editing his number to Jane's also succeeded, demonstrating how
duplicate phone numbers could be introduced.

The agreed fix treats two cyclists as duplicates if they share the same phone number or
email address, with email comparison ignoring letter case. Add and edit reject conflicts,
including those involving hidden contacts. Retaining a cyclist's own identity is accepted.
Cyclists may share a name when both their phone numbers and emails are unique. Checks run
before model mutation or saving.

Saved JSON containing duplicate identities is rejected during loading through the existing
invalid-data handling path. Conflicting records are neither silently discarded nor loaded
for interactive repair. Back up and correct the JSON data before attempting to load it again.

## Manual UI checks and limitations

Before the fix, the release JAR was launched in `/private/tmp/cyclique-release-smoke` with JDK 25.
Startup logs confirm initialisation with missing preferences and data files,
selection of sample data, and entry into UI startup. This is startup evidence,
not a visual pass. The available UI automation tool does not expose the standalone
Java process as an accessible application, so its window could not be inspected
or operated. The temporary process was stopped after the launch attempt.

Still required before release:

- Visual confirmation of opening/closing artwork and labelled contact cards.
- Commands entered through the command box, including duplicate phone or email errors,
  same-name cyclists with unique identities, filtered conflicts, and focus behaviour.
- Help window readability and F1, window close, resize/move and preference restoration.
- Application restart after add, edit, delete and clear.
- Missing/corrupt data and preferences recovery through application startup.
- The complete manual plan on the supported operating systems; Windows and Linux
  were not exercised locally.

For Issue #62 release testing, use country-code phone numbers and verify duplicate
phone or email rejection, same-name cyclists with unique identities, and duplicate
JSON load rejection. Do not use legacy interactive repair scenarios because such
JSON data is rejected during loading.

JavaFX emits native-access, unnamed-module and deprecated Unsafe warnings on JDK 25.
They did not fail the automated checks or prevent logged UI startup.
