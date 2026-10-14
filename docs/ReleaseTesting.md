# Release testing results

## Residential address removal (12 October 2026)

Remove residential address from cyclist fields, add/edit commands, success messages,
JSON output, sample data and Help templates. Older JSON records containing `address`
remain loadable; the retired field is ignored and omitted on subsequent saves.
Update the user guide, model diagrams and affected manual scenarios accordingly.

`./gradlew check coverage shadowJar` passes with JDK 25: **350 tests**, no failures,
errors or skips; production and test Checkstyle pass. Line coverage is **74.85%**
(1,098 of 1,467 lines), above the 50% target. Regression tests verify address-free
feedback and persistence, legacy JSON with populated, blank or null addresses,
and rejection of commands containing the retired `a/` prefix without data changes.

The rebuilt JAR launched in `/private/tmp/cyclique-address-removal-ui`, but the
computer-use tool could not bind to its JavaFX window (`Invalid app: java`).
Manual add/edit, card display, Help and restart checks remain outstanding.
The temporary application was stopped after the attempt. Automated command and
storage checks pass; they do not establish visual correctness.

## Error-message consistency (12 October 2026)

Edit and delete now share the same missing-target and ambiguous-phone messages.
Both missing-target errors include the entered phone number and advise using `list`
to show all cyclists. Error wording uses cyclist terminology, and error sentences
end with full stops. This includes name, address, tag and experience validation,
email constraint bullets, duplicate-prefix errors, saving errors and storage errors.
The invalid-format heading is `Invalid command format.` Command syntax and usage
examples remain unchanged so their text can be copied directly.

`./gradlew check coverage shadowJar` passes with JDK 25: **356 tests**, no failures,
errors or skips; Checkstyle passes. Line coverage remains **75.58%**
(1,142 of 1,511 lines). Repository checks and `git diff --check` pass.
New command integration regressions verify matching errors for hidden and unknown
targets, punctuation in invalid-field and duplicate-prefix errors, and preservation
of saved JSON after rejected commands.

The rebuilt JAR was launched in `/private/tmp/cyclique-error-messages-ui` for manual
checks, but the computer-use tool again returned `Invalid app: java`. The command-box
checks in the Developer Guide's **Error message consistency** scenario therefore
remain outstanding; automated tests verify the error text through real parsing
and command execution. The temporary application was stopped after this attempt.

## Merged sorting and displayed-only deletion (12 October 2026)

After merging duplicate-identity prevention, three sorting fixtures reused phone
numbers or email addresses and were rejected by the new identity rules. The filtered
phone-edit integration test also expected insertion order and attempted a duplicate
phone edit. Correct the fixtures to use unique identities, expect alphabetical
display, and verify that the duplicate edit is rejected without changing saved data.

The existing live sorted view passes tests for startup, additions, filtering,
name edits, stable case-equivalent and identical names, book replacement and JSON
reload. An additional regression covers renaming a cyclist into and out of an
identical-name group without disturbing stored relative order.

Deletion now targets only displayed cyclists by phone number. Tests cover hidden
targets, an empty display, same-name cyclists with distinct identities, successful
filtered deletion and deletion after `list`. Rejections preserve the filter,
stored contacts and saved JSON. User instructions and manual scenarios reflect
this restriction and the merged duplicate-identity rules.

`./gradlew check coverage shadowJar` passes using Eclipse Temurin JDK 25.0.4.1
on macOS ARM64: **354 tests**, no failures, errors or skips; both Checkstyle
tasks pass. JaCoCo line coverage is **75.58%** (1,142 of 1,511 lines), instruction
coverage is 78.27% and branch coverage is 88.07%. The repository checks and
`git diff --check` pass. No merge-conflict markers remain in source or documentation.

The rebuilt JAR reaches UI startup in `/private/tmp/cyclique-merged-sorting-ui`.
The computer-use tool still does not expose the standalone JavaFX app, and
selecting `java` returns `Invalid app: java`. Therefore the updated alphabetical
display and deletion manual scenarios could not be completed visually. Card
indexes, command-box interactions and restart display require manual confirmation.
Windows and Linux UI checks remain outstanding. JavaFX native-access and existing
test deprecation warnings do not fail the build.

The following sections record earlier checks.

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

## Tag removal regression — 14 October 2026

Verified with Temurin JDK 25.0.4.1 on macOS:

- `./gradlew check coverage shadowJar`: passed, including production and test Checkstyle.
  Tag-removal validation: 350 tests passed; line coverage 74.91%, instruction coverage 77.70%,
  branch coverage 87.59% (above the 50% target).
- Unit and integration tests cover tag-free records and feedback, removal of Help parameters,
  old tag input rejection without changes to records or saved files, and legacy JSON tag values.
- The legacy-file workflow loads cyclists, edits a supported field, saves without tags and
  reloads while retaining the supported fields. Existing JSON fixtures containing tags remain
  as compatibility regression data.
- JavaFX tests load the real card FXML, verify supported controls and check that no tag container remains.
- Release JAR startup in a disposable directory reached UI startup. The UI tool cannot select
  the standalone Java process (`java` and `Cyclique` both return `Invalid app`), so command-box,
  Help menu, card layout and restart checks could not be completed manually. The process was stopped.

Run the Developer Guide's **Tag removal compatibility regression** manually before release.
Windows and Linux manual checks remain outstanding. Developer Guide UML diagrams are unchanged.

## Cyclist deletion confirmation — 14 October 2026

`delete PHONE` now shows the target cyclist in the existing result panel and
waits for a reply in the command box. Case-insensitive `y`/`yes` confirms;
`n`/`no` cancels without changing the records, active filter or saved data.
Invalid replies retain the pending request. Another supported command cancels
it and displays `Deletion cancelled.` followed by that command's output or error.
Another delete starts a new confirmation for its own target. Phone-based targeting
and the existing missing/hidden/ambiguous-target and storage errors are preserved.

Verified with Eclipse Temurin JDK 25.0.4.1 on macOS ARM64:

- `./gradlew check coverage` passes: **372 tests**, no failures, errors or skips;
  production and test Checkstyle pass. `shadowJar` builds the release JAR.
- JaCoCo line coverage is **75.87%** (1,094 of 1,442 lines), instruction coverage
  is 78.44% and branch coverage is 88.54%, exceeding the 50% target.
- Tests exercise requests without mutation or storage writes, every accepted
  reply capitalisation, invalid replies, cancellation, subsequent outputs/errors,
  replacement requests, Help/Exit flags, filtered and same-name phone targets,
  stale cyclist details, pending-request loss on restart and storage failures.
  Successful confirmations are checked against the saved JSON and reloaded book.
- Repository checks and `git diff --check` pass. UG/DG instructions and affected
  manual scenarios are updated; UML diagrams remain unchanged.
- The release JAR reached UI startup using disposable data in
  `/private/tmp/cyclique-deletion-confirmation-ui`. The computer-use tool does not
  expose the JavaFX process and selecting `Cyclique` returns `Invalid app`.
  Manual command-box, result-panel, Help, resize and restart checks could not be
  completed. The temporary application was stopped. Windows and Linux manual
  checks remain outstanding.

Run the Developer Guide's **Deletion confirmation regression** before release.
Automated command/storage tests do not establish visual correctness or keyboard
focus behaviour.
