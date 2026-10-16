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
Run the Developer Guide's **Alphabetical display regression** manual scenario to verify
visible card indexes after reordering and filtering, and startup ordering after a restart.

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

`CommandTemplatesTest` checks roster dropdown labels, aliases, menu order and exact
starter text, including the trailing space. Run the Developer Guide's **Roster Help
dropdown regression** for menu selection, focus and caret behaviour. Roster command
execution is delivered separately from these menu templates.
