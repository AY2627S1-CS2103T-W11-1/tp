# Cyclique

[![CI Status](https://github.com/AY2627S1-CS2103T-W11-1/tp/actions/workflows/gradle.yml/badge.svg)](https://github.com/AY2627S1-CS2103T-W11-1/tp/actions/workflows/gradle.yml)

**Cyclique is a desktop application for organisers of recreational cycling groups in Singapore.** It combines typed commands with a graphical interface, keeping cyclist profiles, emergency contact numbers, group rides and ride rosters in one place.

![Cyclique showing cyclist profiles and the Rides panel](docs/images/release-v1.4/cyclists-and-rides.png)

## Features

* **Manage cyclist profiles:** Keep contact and emergency numbers, Functional Threshold Power (FTP), experience levels and preferred cycling locations. Find cyclists by supported profile fields and view results alphabetically.
* **Plan group rides:** Add, edit, find and delete rides with dates, locations, distances, minimum power and maximum group sizes. Each ride has a persistent ID.
* **Manage ride rosters:** Add eligible cyclists, remove memberships and list a ride's registered cyclists. Cyclique checks duplicate membership, capacity and minimum power before adding a cyclist.
* **Keep data between sessions:** Cyclist records and rides are saved automatically in the same local JSON file. Cyclist deletion asks for confirmation; ride deletion is immediate.

## Get started

Install Java 25, download the JAR from [Cyclique releases](https://github.com/AY2627S1-CS2103T-W11-1/tp/releases), and follow the [User Guide](https://ay2627s1-cs2103t-w11-1.github.io/tp/UserGuide.html#quick-start) for launch instructions and command examples.

## Project documentation

* [Product website](https://ay2627s1-cs2103t-w11-1.github.io/tp/)
* [User Guide](docs/UserGuide.md) — commands, examples and troubleshooting.
* [Developer Guide](docs/DeveloperGuide.md) — architecture and implementation.
* [Setting up](docs/SettingUp.md) and [Testing guide](docs/Testing.md) — contributing to the project.
* [About Us](docs/AboutUs.md) — the CS2103T project team.

## Acknowledgements

Cyclique is based on [AddressBook-Level3](https://github.com/se-edu/addressbook-level3), created by the [SE-EDU initiative](https://se-education.org/).
