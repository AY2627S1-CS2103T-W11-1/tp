# Release testing results

## Phone numbers with leading zeros (31 October 2026)

Treat phone numbers that differ only by leading zeros in the country code as the same number (issue #239).
`Phone` now accepts leading zeros in the country code, stores the country code without them, and requires it to
contain a non-zero digit. The number is stored as typed, because its leading zeros can be significant (for example
`+39 0612345678`). `+065 91234561` is therefore rejected as a duplicate of `+65 91234561`, and `+0 000` is rejected
as an invalid phone number. This follows the review of #247, which narrowed the first version of this fix that also
removed zeros from the number. The change adds **19 lines** in `src/main` (all in `Phone`), including
comments; deletions are not counted.
This is the contribution of this fix, not a measurement of cumulative dashboard attribution.

Gradle could not start in the sandbox used for this fix (`Unable to establish loopback connection`), so
`./gradlew check coverage` was **not** run. The full test suite was instead compiled with `javac` and run with the
JUnit Platform launcher on Eclipse Temurin JDK 25.0.4.1 on Windows: **918 tests**, no failures or skips. Seven new
tests fail against the previous `Phone` and pass with the fix. Checkstyle 14.1.0 was run directly with
`config/checkstyle/checkstyle.xml` on the changed Java files. The JaCoCo coverage report still needs a normal
`./gradlew check coverage` run, and the manual **Phone leading zero regression** and **Phone target error
regression** scenarios have not been run.

## Hyphenated and apostrophised cyclist names (31 October 2026)

Accept internal hyphens (`-`) and straight apostrophes (`'`) in the shared `Name` validator,
preserving Unicode letters, digits and attached combining marks. Reject leading, trailing,
consecutive and space-adjacent punctuation. This also applies to cyclist edits and ride names.
The change adds or edits **7 lines** in `src/main`, including the message and comments,
relative to the clean working tree at the start of this fix; deletions are not counted.
This is the contribution of this fix, not a measurement of cumulative dashboard attribution.

Using Eclipse Temurin JDK 25.0.4.1 on macOS ARM64, `./gradlew check coverage shadowJar` passes
**904 tests**, with no failures, errors or skips; both Checkstyle checks pass.
Line coverage is **92.40%** (2,504 of 2,710 lines), above the 50% target.
Regression tests cover both reported add commands, exact spelling, find, edit, JSON readback,
Unicode punctuation boundaries and invalid-name rejection without changes to records or saved data.

The JAR launched in a disposable folder with the existing ARM64 JavaFX libraries on its classpath.
The desktop-control tool could not access the JavaFX window (`Invalid app: java` and
`Invalid app: Cyclique`), so the **Unicode name regression** and **Hyphenated and apostrophised
name regression** manual visual/restart checks remain outstanding. The temporary application
was stopped. Other operating systems were not tested locally.

## Registered cyclist FTP edit eligibility (27 October 2026)

Reject supplied FTP edits below any registered ride's minimum power before replacing the cyclist.
Check the full ride list using the cyclist's original phone, including hidden rides and combined phone/FTP
edits. The error names the conflicting ride ID, name and minimum FTP. Equality is accepted; rejected edits
preserve all supplied fields, rosters, both filters and saved data. Unrelated edits preserve existing FTP.

Using Eclipse Temurin JDK 25.0.4.1 and a temporary external ARM64 JavaFX dependency override,
`check coverage shadowJar` passes: **859 tests**, no failures, errors or skips; both Checkstyle checks pass.
Line coverage is **89.36%** (2,292 of 2,565 lines), above the 50% target. `build.gradle` is unchanged.
Integration regressions cover below/equal/above-minimum FTP, multiple registrations, hidden rides,
combined phone edits, no registrations, unchanged data on rejection and JSON readback on success.

The ARM test JAR launched in a disposable folder, but the computer-use tool could not access its JavaFX
window (`Invalid app: java`). The **Registered cyclist FTP edit regression** manual checks remain outstanding.
The temporary application was stopped after this attempt.

## Preserve cyclist filters after edits (27 October 2026)

Remove the successful edit command's unconditional reset of the cyclist filter. Existing observable
lists re-evaluate find criteria and retain the live ride-roster selection, including after phone edits.
Update earlier tests and manual expectations that assumed successful edits show every cyclist.

Using Eclipse Temurin JDK 25.0.4.1 and a temporary external init script selecting ARM64 JavaFX dependencies,
`check coverage shadowJar` passes: **852 tests**, no failures, errors or skips; both Checkstyle checks pass.
Line coverage is **89.32%** (2,282 of 2,555 lines), above the 50% target. `build.gradle` is unchanged.
New integration tests cover multiple edits under one find filter, removal of non-matching names/phone
numbers, alphabetical roster ordering, updated roster phone references, subsequent live roster changes,
rejected edits without changes and JSON readback of successful edits.

The ARM test JAR launched in a disposable folder, but the computer-use tool could not access its JavaFX
window (`Invalid app: java`). The **Filtered cyclist edit regression** manual checks remain outstanding.
The temporary application was stopped after this attempt.

## PR #201 integration with latest master (27 October 2026)

Combine feature HEAD `aaa7e9d3` with master `e89f88ba`, retaining optional quoted ride notes and the new
specific phone errors for supplied Edit/Delete targets. Both branches' release-testing records are preserved.

On Windows with Oracle JDK 25.0.4, `./gradlew clean check coverage shadowJar` passes **826 tests**, with no
failures, errors or skips; both Checkstyle tasks pass. Line coverage is **89.26%** (2,270 of 2,543 lines), and
branch coverage is **93.08%** (1,009 of 1,084 branches). Existing quoted-notes, overlap/deletion confirmation,
roster, persistence, Unicode name, email and JavaFX regression tests remain present and pass.

The final master build configuration is retained, including the platform-selection revert in `e99c52ca`.
The JAR bundles Windows, Linux and Intel macOS native libraries. Native manual phone-error checks and
non-Windows execution were not repeated during this agent run. Subsequent user verification of Issue #155
is recorded below.

### Subsequent Issue #155 manual verification

The user tested a freshly built merged Cyclique JAR on Windows using Temurin OpenJDK 25.0.4, in a disposable
folder with sample data. **All 11 manual scenarios passed:**

1. Create a ride with ordinary unquoted notes.
1. Edit notes using `notes/"Bring camera with aperture f/2"`.
1. Preserve literal `f/2` without changing minimum power.
1. Edit quoted notes alongside another parameter, such as `s/6`.
1. Trigger an overlap warning for a cyclist registered for both rides.
1. Confirm with `yes`, preserving complete notes and applying the intended time changes.
1. Cancel with `no`, preserving the original ride.
1. Reject malformed quoted notes without changing ride data.
1. Retain the existing unquoted interpretation of `f/200`.
1. Preserve multiple literal prefixes, including `f/2` and `s/3`, inside quoted notes.
1. Retain updated ride details after restarting Cyclique.

This completes the Issue #155 command and restart verification that agents initially could not perform.
Window-resize testing was not reported. This run does not verify Linux, macOS, Apple Silicon or Windows ARM64
compatibility; the existing cross-platform limitations remain.

## JavaFX native platform selection (26 October 2026; reverted)

Historical verification of the platform-selection change: `e99c52ca` subsequently reverted this selection.
The current build bundles the `win`, `mac` and `linux` classifiers; ARM-specific selection is not active.

The change selected one JavaFX native dependency classifier matching the operating system and architecture
of the Gradle JVM, including `mac-aarch64` for Apple Silicon. Unsupported combinations failed during
configuration with an explicit message. Its generated release JAR targeted the build platform, requiring
separate builds for each supported platform.

Before that revert, `./gradlew check coverage shadowJar` passed on macOS ARM64 using Eclipse Temurin
JDK 25.0.4.1 without an init-script override: **804 tests**, no failures, errors or skips; both Checkstyle
checks pass. Line coverage is **89.15%** (2,243 of 2,516 lines).
At that revision, `./gradlew run` reached UI initialisation using ARM64 JavaFX libraries without the previous
incompatible-architecture or missing-toolkit errors. The smoke-test application was then stopped.
Visual inspection remains unavailable because the computer-use tool cannot bind to the JavaFX application.
Windows, Linux and Intel macOS were not tested locally.

## Specific phone errors for edit/delete (26 October 2026)

Preserve the detailed phone-validation error for supplied edit/delete targets. Missing targets still
report command usage. Regression tests verify missing country codes, missing/multiple separators,
other malformed numbers, valid delete targets with surrounding whitespace and missing-target usage.
Integration tests compare edit/delete with add/find and confirm unchanged cyclist records, ride rosters,
active filters and saved JSON after rejection, without starting a deletion confirmation.

Using Eclipse Temurin JDK 25.0.4.1 and the temporary ARM JavaFX override described below,
`check coverage shadowJar` passes: **804 tests**, no failures, errors or skips; both Checkstyle checks pass.
Line coverage is **89.15%** (2,243 of 2,516 lines), above the 50% target.
The repository build configuration is unchanged.

The rebuilt JAR launched in a disposable folder, but the computer-use tool could not access its
JavaFX window (`Invalid app: java`). The **Phone target error regression** manual checks remain
outstanding. The temporary application was stopped after this attempt.

## Issue #155 integration with master (26 October 2026)

Integrate `3732943a` (optional quoted ride notes) with master `d2c023f3` (including overlap confirmation).
Retain deferred execution and saving for overlapping edits, and preserve literal prefix text in notes.

On Windows with Oracle JDK 25.0.4, focused parser, integration and JavaFX checks pass **190 tests** and both
Checkstyle tasks. `./gradlew clean check coverage shadowJar` passes **821 tests**, with no failures, errors or
skips. Line coverage is **89.27%** (2,272 of 2,545 lines); branch coverage is **93.06%** (1,005 of 1,080 branches).
All existing test methods from both branches remain present. New integration tests cover quoted notes with
overlap confirmation, cancellation, invalid replies, stale data, malformed input, roster constraints and saving
exactly once. A JavaFX main-window test checks the combined command and confirmation display.

The Ride Edit PNG is regenerated from the combined overlap-aware PlantUML source, with its complete source
embedded and its native-resolution rendering inspected. The release JAR contains Windows, Linux and macOS
native libraries. Linux/macOS execution and ARM compatibility are not verified by this Windows run.

During this earlier agent run, the packaged JAR launched with assertions enabled in a disposable folder.
Native desktop focus could not be maintained reliably for safe command entry, leaving standalone command,
resize and restart scenarios unverified at that time. The disposable application was stopped. The subsequent
user verification recorded above covers Issue #155 commands and restart persistence; resize testing remains
unverified.

## Unicode names (26 October 2026)

Accept Unicode letters, decimal digits and attached combining marks in cyclist and ride names.
Preserve the entered spelling, reject blank names, leading spaces, punctuation and detached marks,
and update the validation message and User Guide to explain the supported characters.

The standard `./gradlew check coverage shadowJar` run encountered 14 JavaFX-related test failures:
the existing build declares Intel macOS libraries, which cannot initialise the toolkit on this ARM Mac.
Use a temporary init script outside the repository to select the `mac-aarch64` JavaFX 17.0.7 dependencies.
With that override, `check coverage shadowJar` passes using Eclipse Temurin JDK 25.0.4.1:
**799 tests**, no failures, errors or skips; both Checkstyle checks pass. Line coverage is
**89.16%** (2,245 of 2,518 lines), above the 50% target. The repository build configuration is unchanged.

New tests cover accented and non-Latin names, composed/decomposed accents, Unicode digits,
cyclist/ride add and edit commands, exact spelling after JSON readback and rejection without data changes.
The ARM-compatible JAR launched in a disposable folder, but the computer-use tool could not access its
JavaFX window (`Invalid app: java`). The **Unicode name regression** manual checks remain outstanding.
The temporary application was stopped after this attempt.

## Consecutive email special characters (26 October 2026)

Allow consecutive underscores, plus signs, hyphens and mixed special characters inside the email local part.
Retain the restrictions on leading/trailing special characters, consecutive periods and invalid domains,
and explicitly mention consecutive periods in the validation message.

`./gradlew check coverage shadowJar` passes using Eclipse Temurin JDK 25.0.4.1:
**794 tests**, no failures, errors or skips; production and test Checkstyle pass.
Line coverage is **89.16%** (2,245 of 2,518 lines), above the 50% target.
New regressions exercise add/edit acceptance, JSON readback and rejected commands without saved-data changes.

The rebuilt JAR launched in `/private/tmp/cyclique-email-ui.ytvzuN`, but the computer-use tool could not
access its JavaFX window (`Invalid app: java`). The Developer Guide's **Email local-part regression**
manual checks remain outstanding; automated command/storage tests do not establish visual correctness.

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
## Emergency contact regression — 14 October 2026

Implemented compulsory `sos/` for new cyclists, optional emergency-number edits,
and exact emergency-number find criteria using the existing phone validation.
Emergency numbers are saved, displayed on labelled SOS card rows and included in
command feedback. Missing or null legacy values load as absent and display
**Not provided**; other edits preserve that absence. Emergency contacts may be
shared without changing cyclist identity or edit/delete targeting.

Verified using Eclipse Temurin JDK 25.0.4.1 on macOS ARM64:

- `./gradlew check coverage` passes: **365 tests**, no failures, errors or skips;
  production and test Checkstyle pass. The release JAR builds with `shadowJar`.
- JaCoCo line coverage is **75.72%** (1,085 of 1,433 lines), instruction coverage
  is 78.72% and branch coverage is 88.49%, exceeding the 50% target.
- Regression tests cover required, blank, malformed and repeated `sos/` input;
  shared emergency contacts; field-specific and combined find criteria; edit
  replacement and preservation; legacy loading and JSON save/reload; Help formats
  and dropdown templates. JavaFX tests load the real card FXML and verify the SOS
  label, displayed number, wrapping and legacy placeholder.
- The release JAR launched with a disposable legacy book in
  `/private/tmp/cyclique-sos-ui` and reached UI startup. The computer-use tool
  cannot bind to the standalone JavaFX process: both `java` and `Cyclique` return
  `Invalid app`. Manual command-box, Help/F1/dropdown, card resize and restart
  checks therefore remain outstanding. The temporary application was stopped.
  Automated checks do not confirm visual
  layout or keyboard behaviour. Windows and Linux manual checks remain outstanding.

Run the Developer Guide's **Emergency contact regression** manually before release.
Developer Guide UML diagrams are unchanged.

## Emergency contact CI loading fix — 15 October 2026

The emergency-contact PR's Ubuntu CI run failed 23 JSON loading and round-trip
checks. The convenience constructor retained `@JsonProperty` annotations alongside
the full `@JsonCreator` constructor. Reversing their declarations locally reproduced
`Could not find creator property with name 'emergencyPhone'`, including the same
four failing `JsonAdaptedPersonTest` methods.

Remove JSON annotations from the convenience constructor so only the full
constructor supplies creator properties. Keep the existing saved-data format and
legacy missing/null emergency-number handling unchanged. No UML diagrams change.

A new regression test controls Jackson's constructor discovery order and checks
current records, null emergency numbers and older records without that property.
The test fails before the fix and passes afterwards on the same JDK.

`./gradlew check coverage` passes with Eclipse Temurin JDK 25.0.4.1 on macOS ARM64:
**366 tests**, no failures, errors or skips; both Checkstyle tasks pass. Line
coverage remains **75.72%** (1,085 of 1,433 lines), above the 50% target.
Linux CI still needs to run against the updated branch; local verification does
not claim that the remote run has passed. The existing emergency-contact manual
UI checks remain outstanding because the UI tool cannot access the JavaFX window.


## Roster Help dropdown extraction (16 October 2026)

Branch `93-roster-help-dropdown` starts from master and adds only the dropdown
formats for `rideaddcyclist ID PHONE` and `ridedeletecyclist ID PHONE`,
their tests and documentation. Command execution remains in the separate roster
implementation. UML diagrams and Full help content are unchanged.

JDK 25.0.1: `./gradlew check coverage` and `./gradlew shadowJar` pass. All **555 tests**
pass with no failures, errors or skips; both Checkstyle tasks pass. Line coverage
is **79.72%** (1,498 of 1,879 lines), above the 50% target. The same temporary JVM
option `-Djdk.net.unixdomain.tmpdir=C:/codex-java-tmp` avoids the local Windows
loopback-socket startup issue without changing project configuration.

Manual Windows checks used an isolated temporary data directory. The Help menu
shows both roster entries, with positional arguments, after `rideadd`.
Selecting addition inserts `rideaddcyclist ` and focuses the command box with the
caret at the end. Selecting removal replaces it with `ridedeletecyclist `.
F1 still opens Full help. Automated tests verify exact trailing spaces. No roster
commands were executed because their implementation is outside this branch.

## Roster alias removal (17 October 2026)

Remove the short-form roster aliases from Help dropdown labels and update the
User Guide, Developer Guide and template assertions to match the PR reviews.
Full command names and inserted starter text remain the same.

JDK 25.0.4.1: `./gradlew check coverage shadowJar` passes using the documented
`-Djdk.net.unixdomain.tmpdir=C:/codex-java-tmp` workaround. All 555 tests pass,
both Checkstyle tasks pass, and line coverage is 79.72% (1,498 of 1,879 lines).

Manual Windows UI verification passed on retry using JDK 25 and temporary data
outside the repository. The Help dropdown shows `rideaddcyclist ID PHONE` followed
by `ridedeletecyclist ID PHONE` immediately after `rideadd`, with no short-form
aliases. Selecting each roster entry replaces the command box contents with its
full command name and a trailing space. Typing `1 91234567` immediately after each
selection appends the arguments correctly, confirming command-box focus and the
caret at the end; no prefixes or placeholders are inserted. Existing `add` and
`list` entries still insert their expected starter text. Clicking **Full help...**
opens the Help window. No roster commands were executed.

## Defensive programming additions (20 October 2026)

Enable assertions for Gradle run and test tasks. Add deletion-state assertions,
null dependency checks, save-failure warning logs with exceptions, checked loading
errors for missing/null cyclist data, and ride ID validation before mutation.
Guard ride ID exhaustion in both the model and command layer.

Temurin JDK 25.0.4.1: `./gradlew check coverage shadowJar` passes. All 694 tests
pass, both Checkstyle tasks pass, and line coverage is 83.08%.

Launch the built JAR with `-ea` in `/private/tmp/cyclique-defensive-ui`, using an
empty book with nextRideId 2147483646. Startup logs confirm successful loading
and UI initialisation. Manual interaction remains incomplete: the computer-use
app inventory does not expose the standalone Java window, and selecting the
Java executable is rejected as an invalid app. Stop the test process afterwards.
The Developer Guide's **Defensive loading and ride ID boundaries** and
**Deletion confirmation regression** scenarios still require interactive checks.

### Additional approved assertions and diagnostics

Add roster membership/size and assigned-ID postconditions, plus assertions that
phone edits and deletions leave no old roster references. Add FINE logs for
roster operations, assigned ride IDs, affected roster counts and deletion
confirmation transitions. Confirmation logs distinguish a performed deletion
from its subsequent persistence, so a failed save is not reported as saved.

Temurin JDK 25.0.4.1: `./gradlew check coverage` passes with 694 tests and
both Checkstyle tasks. Line coverage is 83.19%. Existing roster, ID boundary
and deletion-confirmation tests exercise the added assertions. No new
user-visible behaviour is introduced by these additional diagnostics. The
previously recorded interactive UI checks remain incomplete.

## Ride feature bug audit (20 October 2026)

See [Bug testing report](BugTestingReport.md) for scope, five reproduced and fixed
defects, equivalence partitions, boundary cases and deferred minor findings.
The audit adds 20 test methods, including end-to-end rejection/persistence
matrices, and updates invalid old roster fixtures.

Temurin JDK 25.0.4.1: `./gradlew check coverage shadowJar` passes. All 714
tests pass with 0 failures/errors and 0 skips; both Checkstyle tasks pass.
Line coverage is 83.31% (1987/2385), exceeding the 50% target.
`git diff --check` passes.

Launch the rebuilt JAR with `-ea` in `/private/tmp/cyclique-bug-audit`, using
a saved file containing `null`. Startup logs confirm a controlled loading
warning and UI initialisation. Stop the disposable test process afterwards.
Interactive UI checks remain incomplete because the computer-use tool cannot
attach to the standalone Java window, including via the installed JDK bundle ID.
The Developer Guide documents the affected roster/list/find and corrupt-data
manual scenarios; visual layout, focus and cross-platform behaviour remain
unverified manually.

## Ride overlap warning (25 October 2026)

Branch `ride-overlap-warning` adds command-box confirmation before overlapping
registrations and ride time edits. Adjacent and zero-duration periods do not
overlap; organisers may confirm an overlapping commitment explicitly.

JDK 25 `./gradlew check coverage` passes with 742 tests and both Checkstyle
checks. JaCoCo reports 86.20% line coverage (2111 of 2449 lines), up from
83.67% before the additional UI tests. Command entry, result display and both
list panels, including their cells, have 100% line coverage. Automated checks
cover warnings, confirmation/cancellation, stale data, persistence, error
recovery, interval boundaries and recycled cards.

## Unicode whitespace in ride notes — Issue #169 (27 October 2026)

Historical Windows runs used Temurin **25.0.4+7-LTS** and `./gradlew.bat clean check coverage shadowJar`.
Every successful run below had zero failures, errors and skips, and passed both Checkstyle tasks.
Initial checkout: clean `169-fix-unicode-ride-notes`, HEAD/merge base `ad4a48526b1efa331812fbe1f8babed358a55bd8`.

| Earlier verification stage | Tests | Line coverage | Branch coverage | JAR bytes |
| --- | ---: | ---: | ---: | ---: |
| Unicode validation | 863 | 89.37% (2,296/2,569) | 93.21% (1,029/1,104) | 12,594,450 |
| Legacy-data protection | 868 | 90.54% (2,327/2,570) | 93.39% (1,031/1,104) | 12,594,553 |
| PR #209 dialog corrections | 874 | 91.10% (2,386/2,619) | 92.73% (1,046/1,128) | 12,596,629 |

Before the Unicode fix, actual U+2003, U+00A0, U+202F and U+3000 reproduced eleven-word acceptance,
blank Ride Add acceptance and invisible Ride Edit notes; ASCII spaces behaved correctly. Quoted blank
NBSP/NARROW NBSP were accepted, unlike EM/IDEOGRAPHIC SPACE. Four new regressions failed before the fix.
The initial full run also exposed an obsolete Help assertion, corrected before the successful rerun.
Eleven initial regression methods covered validation, commands, storage and UI; Issue #155 tests stayed enabled.

The legacy-data audit reproduced empty-model substitution for blank EM SPACE/NBSP and eleven-word
EM SPACE/NBSP notes: loading left JSON intact, then `list` overwrote it, losing the unrelated cyclist and
both rides. Valid ten-word Unicode and ordinary notes preserved all records. Five startup regression methods
were added. Four packaged-JAR launches verified protection at that stage: exit code 1 before UI startup,
notes constraint/recovery diagnostics and byte-for-byte unchanged JSON.

The review correction accepts supported Unicode whitespace after closing quotes without broadening prefix
recognition. The fatal dialog uses a short header and a bounded, wrapped, scrollable reason/recovery area.
Nine automated `javaw` launches using the corrected JAR covered blank EM SPACE/NBSP, eleven-word EM SPACE/NBSP,
malformed JSON, duplicate phones, invalid ride IDs, a missing cyclist list and a long path. A JavaFX observer
checked the dialog and dismissed it through OK or window-close events. Every process exited normally with
code 0, opened no command UI and preserved JSON byte for byte. This exercised real shutdown, not a toolkit stub.

Manual double-click visibility, native focus and visual layout remain unverified. Run **Unicode whitespace
in ride notes (Issue #169)** and **Literal prefixes in ride notes (Issue #155)** in the Developer Guide before
release. Automated JavaFX assertions do not replace these checks. Embedded command line breaks remain rejected;
direct parser/model tests cover their normalisation. Build configuration, JavaFX packaging and UML are unchanged.
Existing native-access/deprecated-API warnings did not fail the recorded builds. Reports:
`build/reports/tests/test/index.html`, `build/reports/jacoco/coverage/html/index.html` and
`build/reports/jacoco/coverage/coverage.xml`; JAR: `build/libs/cyclique.jar`.

Final scope/coverage pass: `clean check coverage shadowJar` passed on Windows (Temurin JDK **25.0.4**)
and Linux/Xvfb (Zulu **25.0.4+7-LTS**): **874 tests**, zero failures/errors/skips and both Checkstyle tasks passing.
JaCoCo: **92.09%** lines (2,411/2,618), **93.58%** branches (1,050/1,122); JAR: **12,596,400 bytes**.
Local patch coverage: **100% - 76/76 executable lines and 42/42 branches**.
Four further automated `javaw` launches checked EM SPACE/NBSP blank/eleven-word notes, on-screen dialog/OK,
normal exit, no main window and unchanged JSON. No manual visual check was performed.
An earlier macOS CI run was cancelled before any steps ran because no hosted runner was available.
Final PR head `b9c635a3`: macOS, Ubuntu and Windows CI passed; Codecov patch coverage was **100.00%**.
