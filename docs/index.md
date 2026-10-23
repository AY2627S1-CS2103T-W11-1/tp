---
layout: page
title: Cyclique
---

[![CI Status](https://github.com/AY2627S1-CS2103T-W11-1/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-W11-1/tp/actions/workflows/gradle.yml)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-W11-1/tp/branch/master/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103T-W11-1/tp)

**Cyclique is a desktop application for organisers of recreational cycling groups in Singapore.** Manage cyclist profiles and group rides through typed commands, with cyclist cards and a separate Rides panel showing your data at a glance.

![Cyclique showing cyclist profiles and the Rides panel](images/release-v1.4/cyclists-and-rides.png)

## What you can do

* Store cyclists' contact and emergency numbers, Functional Threshold Power (FTP), experience levels and preferred cycling locations.
* Find cyclists by supported profile fields and view matching cyclist cards alphabetically.
* Create, edit, find and delete group rides with dates, locations, distances, minimum power and maximum group sizes.
* Add eligible cyclists to a ride, remove them from its roster and display its registered cyclists. Duplicate membership, capacity and minimum-power checks help you manage registrations.
* Keep cyclist and ride data between sessions through automatic saving to a local JSON file.

## Start using Cyclique

You will need Java 25. Follow the [Quick start](UserGuide.html#quick-start) instructions to download and launch the app. The [User Guide](UserGuide.html) explains the commands and their validation rules.

## Explore the project

* [User Guide](UserGuide.html) — commands, examples and troubleshooting.
* [Developer Guide](DeveloperGuide.html) — architecture, implementation and manual testing.
* [Setting up](SettingUp.html) — prepare a development environment.
* [Testing guide](Testing.html) — run automated checks.
* [About Us](AboutUs.html) — meet the CS2103T project team.
* [Source code and issue tracker](https://github.com/AY2627S1-CS2103T-W11-1/tp) — browse the repository or report a problem.

## Acknowledgements

Cyclique is based on [AddressBook-Level3](https://github.com/se-edu/addressbook-level3), created by the [SE-EDU initiative](https://se-education.org/).

Libraries used include [JavaFX](https://openjfx.io/), [Jackson](https://github.com/FasterXML/jackson) and [JUnit 5](https://github.com/junit-team/junit5).
