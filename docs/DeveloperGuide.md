---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams are in `docs/diagrams`. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` and its parts, such as `CommandBox`, `ResultDisplay`, `PersonListPanel`, and `StatusBarFooter`. All of these, including `MainWindow`, inherit from the abstract `UiPart` class, which captures common behavior among classes that represent visible GUI parts.

The `UI` component uses the JavaFX UI framework. The layouts of these UI parts are defined in matching `.fxml` files in `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java).

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component because it displays `Person` objects from the model.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* is implemented by `StorageManager`, which delegates the actual JSON file access to `JsonAddressBookStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedAddressBook`. It extends `AddressBook` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedAddressBook#commit()` — Saves the current address book state in its history.
* `VersionedAddressBook#undo()` — Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo execute:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command is correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

Cyclique is designed for organisers of recreational cycling groups in Singapore who:
* have a need to manage a significant number of cyclists
* have a need to quickly access cyclists’ contact and emergency information
* organise group rides and needs to identify available cyclists based on factors such as preferred cycling location, FTP and experience level
* are comfortable with typing and using CLI apps

**Value proposition**:
Organisers of cycling groups often have rider contact and information scattered across chats, spreadsheets, and memory, making it hard to identify suitable participants. Cyclique keeps this information in one place and helps organisers find which riders suit a given ride, based on experience, location, availability, and preferences.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                 | I want to …​                                               | So that I can…​                                                                |
| -------- | ------------------------------------------ | ------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| `* * *`  | proactive cycling organiser                | add a new cyclist with their name, phone number, and email    | have their basic contact records stored in one centralised directory              |
| `* * *`  | community-focused organiser                | view a complete list of all registered cyclists               | see an overview of my entire cycling community at a glance                        |
| `* * *`  | attentive organiser                        | edit an existing cyclist's contact details                    | keep contact information up to date without re-creating profiles                  |
| `* * *`  | meticulous organiser,                      | delete a cyclist’s profile                                    | keep my directory free of members who have permanently left the group             |
| `* *`    | safety-conscious cycling organiser         | store an emergency contact for each cyclist                   | reach the appropriate person quickly when an emergency occurs                     |
| `* *`    | logistics-minded cycling organiser         | record a cyclist's preferred cycling location                 | identify cyclists who prefer rides in different parts of Singapore                |
| `* *`    | attentive cycling organiser                | record a cyclist's experience level                           | identify riding groups that are appropriate for the cyclist                       |
| `* *`    | pace-conscious cycling organiser           | record a cyclist's FTP                                        | identify cyclists with a suitable fitness level for a planned ride                |
| `*`      | cycling organiser planning a group ride    | create a record for a planned group ride                      | keep track of the important details of an upcoming ride                           |
| `*`      | cycling organiser with existing records    | import cyclist records from a structured file                 | avoid entering every existing cyclist record manually                             |

### Use cases

(For all use cases below, the **System** is `Cyclique` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add cyclist**

**MSS**

1.  User adds a new cyclist by providing the cyclist's name, phone number, email address, FTP, experience level, and preferred cycling location(s).
2.  Cyclique adds the cyclist to the directory.
3.  Cyclique displays the newly-added cyclist.

    Use case ends.

**Extensions**

* 1a. User provides invalid cyclist details.
    * 1a1. Cyclique informs the user that the provided details are invalid.
    * 1a2. User provides valid cyclist details.

      Steps 1a1-1a2 are repeated until the details provided are valid.

      Use case resumes from step 2.

**Use case: List cyclists**

**MSS**

1.  User requests to view all cyclists.
2.  Cyclique displays all cyclists currently registered in the directory together with their details.

    Use case ends.

**Extensions**

* 2a. The cyclist directory is empty.
    * 2a1. Cyclique informs the user that there are no cyclists in the directory.

      Use case ends.

**Use case: Edit cyclist**

**MSS**

1.  User requests to view all cyclists.
2.  Cyclique displays all cyclists currently registered in the directory.
3.  User identifies a cyclist whose details are to be edited.
4.  User requests to edit one or more details of the cyclist.
5.  Cyclique updates the specified details of the cyclist.
6.  Cyclique displays the updated cyclist information.

    Use case ends.

**Extensions**

* 3a. The specified cyclist does not exist.
    * 3a1. Cyclique informs the user that the cyclist cannot be found.

      Use case ends.

* 4a. User provides invalid cyclist details.
    * 4a1. Cyclique informs the user that the provided details are invalid.
    * 4a2. User provides valid cyclist details.

      Steps 4a1-4a2 are repeated until the details provided are valid.

      Use case resumes from step 5.

**Use case: Delete cyclist**

**MSS**

1.  User requests to view all cyclists.
2.  Cyclique displays all cyclists currently registered in the directory.
3.  User identifies a cyclist to be deleted.
4.  User requests to delete the cyclist.
5.  Cyclique requests confirmation from the user to delete the cyclist.
6.  User confirms the deletion.
7.  Cyclique permanently removes the cyclist from the directory.
8.  Cyclique displays the updated cyclist directory.

    Use case ends.

**Extensions**

* 3a. The specified cyclist does not exist.
    * 3a1. Cyclique informs the user that the cyclist cannot be found.

      Use case ends.

* 6a. User declines the deletion.
    * 6a1. Cyclique cancels the deletion.

      Use case ends.

### Non-Functional Requirements

**Performance**

* Cyclique should display search or shortlist results within 2 seconds when storing up to 500 cyclists.
* Cyclique should display the cyclist list within 2 seconds when storing up to 500 cyclists.

**Usability**

* Adding or editing a cyclist should be completable using a single command entered from the main screen.
* Deleting a cyclist should require no more than one command and one confirmation entered from the main screen.
* Error messages should identify the invalid field, explain the violated constraint, and state the expected input format.

**Data requirements**

* Cyclist contact and emergency information must persist reliably across sessions (no data loss on app close/reopen).
* Cyclique should support storing records for at least 500 cyclists while meeting the stated performance requirements.
* Emergency contact information should be accessible within 2 user actions and displayed within 2 seconds.

**Environment/Compatibility**

* Cyclique should run on mainstream operating systems with Java 25 installed, without requiring additional hardware.

**Security/Privacy**

* Cyclique should store cyclist data only on the user's local computer.
* Cyclique should not transmit cyclist data to third parties.

**Reliability/Availability**

* Data saved by the last successfully executed command should remain available after Cyclique is unexpectedly terminated and restarted.

### Glossary

* **Auto-save**: Automatic saving of changes to persistent storage without requiring a separate save command.
* **CLI**: *Command-Line Interface*. An interface through which the user operates Cyclique primarily by typing text commands.
* **Cyclist**: A person whose contact and cycling-related information is stored and managed in Cyclique. The terms *rider* and *member* refer to the same concept; this documentation uses *cyclist* consistently.
* **Cyclist directory**: The collection of cyclist records stored in Cyclique.
* **Cycling organiser**: The primary user of Cyclique who manages cyclist records and organises recreational group rides.
* **Duplicate cyclist**: A cyclist entry considered to represent an existing cyclist according to Cyclique's duplicate-detection rules, currently based on matching phone number or email address.
* **Experience level**: A categorical indication of a cyclist's riding experience. Cyclique uses three experience levels: **Beginner**, **Intermediate**, and **Advanced**.
* **Functional Threshold Power (FTP)**: A cyclist's cycling performance metric, measured in watts (W), representing the highest power they can approximately sustain for an extended period. In Cyclique, FTP is recorded as a whole number from 1 to 600 W.
* **Group ride**: An organised cycling session involving one or more cyclists. The term *ride meetup* refers to the same concept; this documentation uses *group ride* unless a feature specifically uses the term *ride meetup*.
* **GUI**: *Graphical User Interface*. The visual interface through which Cyclique displays cyclist information and command results.
* **JAR file**: A Java Archive file used to package and distribute the Cyclique application.
* **Java 25**: The minimum Java platform version required to run Cyclique, based on the project constraints.
* **Local storage**: Data stored on the user's own computer rather than on a remote server.
* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Parameter prefix**: A command prefix that identifies the type of value being supplied, such as `n/` for name, `p/` for phone number, or `f/` for FTP.
* **Persistent data**: Data that remains available after Cyclique is closed and reopened.
* **Preferred cycling location**: A location where a cyclist prefers to ride or participate in group rides. A cyclist may have more than one preferred cycling location.
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Ride roster**: The list of cyclists registered or confirmed to participate in a particular group ride.
* **Validation**: The process of checking whether user input satisfies Cyclique's specified input rules before accepting it.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases …​ }_
