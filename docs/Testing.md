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
It checks omitted addresses, repeated tags and locations, edit replacement and preservation of fields,
find matching rules, filtered edit/delete behaviour, rejected commands without saved-data changes,
clear persistence, and help/exit flags. It also verifies duplicate-phone rejection across filters,
unchanged unique numbers, legacy collision loading, ambiguous-command rejection, and filtered repairs
that preserve contacts across reload. `FtpTest` covers the FTP boundaries and invalid numeric values.
These automated checks complement the full manual test plan in the Developer Guide; they do not verify
window layout, keyboard focus, artwork, or operating-system integration.

Record release results, unresolved defects and incomplete manual checks in [Release testing](ReleaseTesting.md).
