# Cyclique

[![CI Status](https://github.com/AY2627S1-CS2103T-W11-1/tp/actions/workflows/gradle.yml/badge.svg)](https://ay2627s1-cs2103t-w11-1.github.io/tp/DevOps.html)
[![codecov](https://codecov.io/gh/AY2627S1-CS2103T-W11-1/tp/branch/master/graph/badge.svg)](https://codecov.io/gh/AY2627S1-CS2103T-W11-1/tp)

**Cyclique is a desktop application for organisers of recreational cycling groups in Singapore.** Manage cyclist profiles and group rides through typed commands, with cyclist cards and a separate Rides panel showing your data at a glance.

![Cyclique showing cyclist profiles and the Rides panel](docs/images/release-v1.4/cyclists-and-rides.png)

## What you can do

* Store cyclists' contact and emergency numbers, Functional Threshold Power (FTP), experience levels and preferred cycling locations.
* Find cyclists by supported profile fields and view matching cyclist cards alphabetically.
* Create, edit, find and delete group rides with dates, locations, distances, minimum power and maximum group sizes.
* Add eligible cyclists to a ride, remove them from its roster and display its registered cyclists. Duplicate membership, capacity and minimum-power checks help you manage registrations.
* Keep cyclist and ride data between sessions through automatic saving to a local JSON file.

## Start using Cyclique

You will need Java 25. Follow the [Quick start](https://ay2627s1-cs2103t-w11-1.github.io/tp/UserGuide.html#quick-start) instructions to download and launch the app. The [User Guide](https://ay2627s1-cs2103t-w11-1.github.io/tp/UserGuide.html) explains the commands and their validation rules.

## Explore the project

* [Product website](https://ay2627s1-cs2103t-w11-1.github.io/tp/) — browse the published documentation.
* [User Guide](https://ay2627s1-cs2103t-w11-1.github.io/tp/UserGuide.html) — commands, examples and troubleshooting.
* [Developer Guide](https://ay2627s1-cs2103t-w11-1.github.io/tp/DeveloperGuide.html) — architecture, implementation and manual testing.
* [Setting up](https://ay2627s1-cs2103t-w11-1.github.io/tp/SettingUp.html) — prepare a development environment.
* [Testing guide](https://ay2627s1-cs2103t-w11-1.github.io/tp/Testing.html) — run automated checks.
* [About Us](https://ay2627s1-cs2103t-w11-1.github.io/tp/AboutUs.html) — meet the CS2103T project team.

## Acknowledgements

Cyclique is based on [AddressBook-Level3](https://se-education.org/addressbook-level3/), created by the [SE-EDU initiative](https://se-education.org/).

Libraries used include [JavaFX](https://openjfx.io/), [Jackson](https://javadoc.io/doc/com.fasterxml.jackson.core/jackson-databind) and [JUnit 5](https://junit.org/junit5/).
