---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
    For example: `seedu.address.logic.LogicManagerTest`

## Release checks

`CyclistFtpEditIntegrationTest` verifies registered-ride eligibility when editing FTP, including minimum-power
boundaries, multiple/hidden rides and combined phone edits. It checks rejection without changes to cyclist
records, rosters, both filters or saved JSON, and successful edits through JSON readback.
Run **Registered cyclist FTP edit regression** in the Developer Guide after changing FTP edit validation.

`FilteredEditIntegrationTest` checks that edits preserve find and live ride-roster filters, re-evaluate
search membership, retain alphabetical order and update roster phone references. It also checks rejected
edits without changes to records, filters or saved JSON, and successful edits through JSON readback.
Run **Filtered cyclist edit regression** in the Developer Guide after changing cyclist edit filtering.

`EditCommandParserTest`, `DeleteCommandParserTest` and `PhoneValidationIntegrationTest` verify specific
phone errors for malformed edit/delete targets, usage for missing targets, and consistency with add/find.
Integration checks preserve cyclist records, ride rosters, the active filter and saved JSON on rejection.
Run **Phone target error regression** in the Developer Guide after changing target-phone parsing.

`NameTest` and `UnicodeNameIntegrationTest` cover accented and non-Latin names, combining marks,
Unicode digits, cyclist/ride add and edit commands, JSON readback and invalid-name rejection without changes.
They also cover internal hyphens/apostrophes and reject misplaced punctuation.
`StringUtilTest`, `PersonMatchesFindCriteriaPredicateTest`, `RideMatchesFindCriteriaPredicateTest` and
`UnicodeNameIntegrationTest` check that `find` and `ridefind` treat hyphens and apostrophes as word separators in
names and locations, and that a criterion containing no word matches nothing.
Run **Unicode name regression** and **Hyphenated and apostrophised name regression** in the
Developer Guide after changing name validation or name and location matching.

`EmailTest` and `EmailValidationIntegrationTest` cover consecutive local-part special characters,
add/edit acceptance and JSON readback, and rejected emails without saved-data changes.
Run **Email local-part regression** in the Developer Guide after changing email validation.

Select JDK 25 and verify it with `java -version` before running `./gradlew check coverage shadowJar`.
This runs JUnit, checks production and test code with Checkstyle, generates coverage, and builds the release JAR.
Review `build/reports/tests/test/index.html` and `build/reports/jacoco/coverage/html/index.html`.
The coverage report does not enforce the 50% target automatically; check the measured coverage explicitly.

The build follows the original tP packaging: one Shadow JAR containing JavaFX dependencies for the
`win`, `mac` and `linux` classifiers. On macOS, use the prescribed Azul Zulu JDK 25 with JavaFX included,
following the [SE-EDU Mac Java installation guide](https://se-education.org/guides/tutorials/javaInstallationMac.html).
After changing native dependencies, run `./gradlew run` and launch the packaged JAR in a disposable folder
to check that the main window opens without native-library errors. The CI matrix checks Windows, macOS
and Linux builds; verify the same release JAR separately on each required platform, using the prescribed
JDK+FX on both Intel and ARM Macs.

For `./gradlew run` and `./gradlew test` on macOS, Gradle selects JavaFX libraries matching the
architecture of its JVM. This development classpath does not change the release JAR's dependencies.
The Mac build jobs use plain JDK 25 so bundled JavaFX cannot hide incorrect development dependencies.
Windows and Linux retain the original JDK+FX setup.
The `mac-release` CI jobs download the same Linux-built JAR, check its checksum, and verify that its
main window opens and the `exit` command shuts it down on Intel and ARM Macs with Zulu JDK+FX 25.0.3.
These smoke checks use a fresh native-library cache and do not rebuild the JAR on either Mac.
The Intel hosted VM uses OpenGL (`-Dprism.order=es2,sw`): default rendering with the prescribed
JDK+FX 25.0.3 aborts there, whereas OpenGL passes. This is consistent with the
[JavaFX Metal renderer bug on virtual machines](https://docs.oracle.com/en/java/java-components/javafx/25/release-notes/),
fixed in JavaFX 25.0.4. The ARM check uses default rendering. Default rendering on a physical Intel Mac
still requires a manual check; the CI OpenGL setting does not change application defaults.

`ReleaseCommandIntegrationTest` covers command sequences through parsing, the model, filtering and JSON storage.
It checks cyclist data without retired fields, repeated locations, edit replacement and preservation of fields,
find matching rules, filtered edit/delete behaviour, rejected commands without saved-data changes,
clear persistence, and help/exit flags. It treats cyclists as duplicates when their phone numbers
match or their email addresses match ignoring case. It verifies those conflicts across filters,
unchanged unique identities, same-name cyclists with unique phone numbers and emails, and successful
filtered edits that preserve contacts across reload.
`JsonSerializableAddressBookTest` verifies that duplicate records in JSON are rejected during loading
rather than silently discarded or loaded for interactive repair. `FtpTest` covers the FTP boundaries
and invalid numeric values.
These automated checks complement the full manual test plan in the Developer Guide; they do not verify
window layout, keyboard focus, artwork, or operating-system integration.

Record release results, unresolved defects and incomplete manual checks in [Release testing](ReleaseTesting.md).

`CommandBoxTest` covers command entry, error recovery and overlap confirmation with JSON persistence.
`ListPanelTest` covers live lists, empty-ride placeholders and recycled cards.
`MainWindowLayoutTest` checks the real FXML result pane at normal, constrained and large sizes,
including many conflicts, invalid replies and restoration of the normal height.
JavaFX tests require an X display on Linux; CI runs them using `xvfb-run`.

`ModelManagerTest` checks alphabetical ordering by full name, case-insensitive ties,
live updates after additions and name edits, filtered membership, deletion and book replacement.
`ReleaseCommandIntegrationTest` checks sorted add/find/list/edit workflows, stable equal-name
ordering across JSON reload and phone-number targeting when the display order changes.
Deletion tests reject hidden cyclists and targets in an empty display without changing
stored records, the active filter or saved JSON. A displayed cyclist can still be deleted
by their exact phone number after confirmation, including when another cyclist has an identical name.
Error-message regressions check identical edit/delete errors for hidden and unknown
phone targets, full stops in invalid-field and duplicate-prefix errors, and the
`Invalid command format.` heading. The Developer Guide's **Error message consistency**
scenario covers the corresponding command-box checks.
Run the Developer Guide’s **Alphabetical display regression** manual scenario to verify
visible card indexes after reordering and filtering, and startup ordering after a restart.

Run the Developer Guide’s **Find and ridefind regression** manual scenario after changing
search criteria, ride filtering, command validation or the ride display panel.

Run the Developer Guide's **Tag removal compatibility regression** after tag-related changes.
`JsonAdaptedPersonTest` checks legacy tags (including empty, null and obsolete invalid values)
are ignored and omitted on serialisation; parser tests reject removed tag input.

`DeletionConfirmationTest` verifies the two-step delete workflow through the real
logic and JSON storage. It checks accepted reply capitalisations, invalid replies,
explicit cancellation, unchanged records/filter/file and zero writes before
confirmation, cancellation notices with subsequent outputs/errors, replacement
requests, Help/Exit flags, stale targets, restart behaviour and storage failures.
Run the Developer Guide's **Deletion confirmation regression** for command-box,
result-panel, Help, resize and restart checks after changes to this flow.
Run the Developer Guide’s **Emergency contact regression** for `sos/` changes, including legacy loading, persistence, phone validation, find combinations, cyclist card layout and both Help surfaces.

`JsonAdaptedPersonTest.fromJson_constructorDiscoveryOrder_preservesCurrentAndLegacyRecords`
exercises both constructor discovery orders to reproduce JVM-dependent Jackson loading
failures. It verifies populated, null and missing emergency numbers. Only the full
`JsonAdaptedPerson` constructor carries JSON property annotations; the convenience
constructor is for direct Java callers.

`RideRosterIntegrationTest` covers roster commands, filtered cyclists,
capacity and FTP boundaries, invalid input, rejected commands without mutation,
phone edits, confirmed/cancelled contact deletion, JSON readback and removal after reload.
It also checks that leading-zero IDs identify the same ride across roster, list, edit and delete commands,
while signed, all-zero and overflowing IDs leave the roster and saved data unchanged.
It also checks command-specific usage for missing roster arguments and preserves the more specific
ID and phone validation errors for malformed arguments. `HelpWindowTest` checks that Full help includes
both roster command examples.
`RideRosterCommandParserTest` checks both `FINE` rejection diagnostics and verifies that their messages
contain only the operation, without the raw ID or phone number.
`HelpWindowTest` verifies both roster command formats and dropdown starters. Run the
Developer Guide's **Ride roster regression (manual testing)** after roster changes.

`AddressBookTest` checks that invalid stored ride IDs leave the ride list and counter unchanged,
and that ID exhaustion cannot overflow the counter. `RideAddCommandTest` checks the corresponding
command error without mutation. `JsonSerializableAddressBookTest` rejects missing/null cyclist lists,
null cyclist entries and stored ride IDs that leave no room for the counter, and preserves an
exhausted counter across JSON reload. `LogicManagerTest` rejects null dependencies;
`DeletionConfirmationTest` exercises the deletion-state assertions with assertions enabled.
Run the Developer Guide's **Defensive loading and ride ID boundaries** manual scenario after these changes.

`RideRosterIntegrationTest` exercises roster membership and size postconditions, and the
absence of old phone references after cyclist edits and confirmed deletion. `AddressBookTest`
exercises the assigned-ID postconditions, including the final available ID.
`DeletionConfirmationTest` covers request creation, both cancellation paths, confirmation
and changed-target rejection. These operations now emit `FINE` diagnostics using operation
names, ride IDs and counts without adding cyclist details to these log messages.


## Ride feature boundary and partition audit

`RideOverlapTest` covers partial overlap, containment, identical periods, adjacency, zero duration
and overnight boundaries. `RideOverlapConfirmationTest` checks registration and time-edit warnings,
all affected cyclists, hidden rides, cancellation, confirmation, stale data, restart and save failures
through real logic and JSON storage. Existing roster/list integration scenarios confirm intentional
overlapping registrations. Run **Ride overlap confirmation regression** in the Developer Guide
after changing overlap detection or confirmation handling.

See [Bug testing report](BugTestingReport.md) for reproduced defects, deferred minor findings,
coverage and manual-check limitations. The cases follow the Week 10 equivalence-partitioning
and boundary-value approach: test distinct valid/invalid classes and adjacent values at limits.

`RideBoundaryIntegrationTest` exercises creation, editing, deletion, roster membership, listing,
find filters and JSON readback through real logic and storage. Its rejected-command checks compare
the whole book, both visible lists and saved JSON. It covers numeric and date boundaries, blank
and duplicate criteria, invalid and overflowing IDs, notes limits, capacity, FTP eligibility,
non-reused IDs and live roster filters. `RideDataIntegrityTest` checks invalid saved roster
partitions and JSON null roots through actual file loading, plus the valid at-capacity boundary.
Existing ride command/model/parser tests cover the remaining individual field and command cases.

`PhoneTest` and `EditCommandParserTest` explicitly cover country-code lengths 0, 1, 2, 3 and 4,
and subscriber lengths 2, 3 and 4. Thus `+12 123` and `+123 123` are both tested as valid.
`RideDateTimeTest` adds century leap-year rules, month ends and clock boundaries.
`ArgumentTokenizerTest` covers spaces, tabs, line breaks and embedded prefix-like text;
`RideRosterIntegrationTest` verifies a tab-separated edit persists both supplied fields.

Run **Ride boundary and corrupt-data regression** in the Developer Guide after these changes.

Run **Ride command alias regression** in the Developer Guide after changing ride command dispatch.
`RideCommandAliasTest` checks parsing equivalence, validation, case sensitivity, saved workflows
and cancellation of pending cyclist deletions for all seven aliases.

Run **Literal prefixes in ride notes (Issue #155)** in the Developer Guide after changing notes tokenisation.
`ArgumentTokenizerTest` checks the explicit quoted-notes entry point and unchanged default tokenisation.
Ride Add/Edit parser tests check literal prefixes, ordering, malformed quotes, duplicate fields and the word limit.
`RideBoundaryIntegrationTest` checks quoted notes with a roster, intentional additional edits, JSON round trips,
unchanged records/filters/files after rejection, non-consumed IDs and legacy unquoted behaviour.
Ride Find keeps its existing unquoted whole-word and phrase semantics.
`MainWindowRidePanelTest` submits quoted notes through the JavaFX command box, checks displayed feedback
and card text, and verifies error styling and unchanged data after malformed input.
`RideOverlapConfirmationTest` also combines quoted notes with overlapping time edits: the entire edit is deferred,
confirmed exactly once, cancelled, rejected for stale data or rejected before overlap processing for invalid input.
`MainWindowRidePanelTest` checks the combined command through the real command box and confirmation display.

Run **Unicode whitespace in ride notes (Issue #169)** in the Developer Guide after notes changes.
Model/parser tests cover Unicode blank detection, normalisation and word boundaries; tokenizer tests preserve
quote and prefix rules. Command, overlap and JavaFX tests cover atomicity, IDs, rosters, filters, persistence,
feedback and Notes-row removal. `MainAppNotesLoadingTest` covers legacy data, explicit repair/restart, real
fatal dialogs and shutdown (including separate JVMs), original-file preservation and normal startup.
These automated checks complement the manual visual scenarios; they do not establish native focus or layout
on untested operating systems.

`RideOverlapConfirmationTest` checks hidden-target roster additions/removals (including aliases),
both display filters, confirmed overlaps and JSON readback. It also verifies that detail edits
still reveal a ride hidden by the current search. Run the hidden-ride steps in **Ride roster
regression (manual testing)** after changing shared ride replacement or edit filtering.
