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

Select JDK 25 and verify it with `java -version` before running `./gradlew check coverage shadowJar`.
This runs JUnit, checks production and test code with Checkstyle, generates coverage, and builds the release JAR.
Review `build/reports/tests/test/index.html` and `build/reports/jacoco/coverage/html/index.html`.
The coverage report does not enforce the 50% target automatically; check the measured coverage explicitly.

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

`CommandBoxTest` exercises real FXML command entry: empty input, exact input forwarding,
successful clearing, retained invalid input, error styling and recovery. Its overlap scenario
connects the command box and result display to real logic and JSON storage, checking warning,
invalid reply, cancellation and confirmed persistence. `ListPanelTest` checks live list bindings,
the empty-ride placeholder and recycled ride/cyclist cards. UI component tests share one toolkit
through `JavaFxTestUtil`; they run on the JavaFX application thread with bounded waits and do not
open desktop windows. Linux test runs require an X display, as for `PersonCardTest`.

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
