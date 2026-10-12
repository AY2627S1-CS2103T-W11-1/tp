---
layout: page
title: User Guide
---

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/+65 98765432 e/johnd@example.com f/300 x/Beginner l/any` : Adds a contact named `John Doe` to the Address Book.

   * `delete +65 98765432` : Deletes the contact with this phone number.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

Cyclists are displayed alphabetically by their full name, ignoring letter case, on startup
and after `list` or `find`. Adding a cyclist places them in the correct alphabetical position;
editing their name updates their position. Names that are identical ignoring case retain
their relative stored order. The visible indexes run consecutively from 1 within the current
display, including filtered results. Use phone numbers to target `edit` and `delete` as usual.

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [t/TAG]…`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

The phone number must include a country code and one separating space, for example `+65 98765432`.
It must not belong to another contact, including contacts hidden by a filter.
FTP must be a whole number from 1 to 600.

`EXPERIENCE_LEVEL` must be one of `Beginner`, `Intermediate` or `Advanced` (case-insensitive).

Examples:
* `add n/John Doe p/+65 98765432 e/johnd@example.com f/300 x/Beginner l/any`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com p/+65 91234567 f/250 x/Advanced l/Punggol`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit PHONE [n/NAME] [p/PHONE] [e/EMAIL] [f/FTP] [x/EXPERIENCE] [l/LOCATION]… [t/TAG]…`

* Edits the person with the specified phone number in the displayed list. Exactly one displayed contact must match.
* The resulting phone number must not belong to another contact anywhere in the book. An unchanged phone number is allowed when it is already unique.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
* `edit +65 98765432 p/+65 91111111 e/johndoe@example.com` updates the identified contact's phone and email.
* `edit +65 91234567 n/Betsy Crower t/` updates Betsy's name and clears her tags.

### Locating persons by name or phone: `find`

Finds persons by name keywords and phone numbers.

Format: `find [n/NAME_KEYWORD]… [p/PHONE]…` (at least one field is required)

* Each `n/` takes one name keyword. Name matching is case-insensitive and requires a full word; for example, `n/hans` matches `Hans` but `n/Han` does not.
* Each `p/` takes one complete phone number. Phone matching is exact; `p/+65 963` does not match `+65 96320842`.
* Multiple values for the same field use `OR`: `find n/Hans n/Bo` returns people named `Hans Gruber` or `Bo Yang`.
* When both fields are supplied, a person must match a name keyword **and** a phone number. Prefix order does not matter.
* Name keywords and phone numbers search only their respective fields.

Examples:
* `find n/John` returns `john` and `John Doe`
* `find n/alex n/david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find n/alex n/david'](images/findAlexDavidResult.png)
* `find n/alex p/+65 96320842` returns only people whose name contains `alex` and whose phone number is `+65 96320842`.

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete PHONE`

* Deletes the person with the specified `PHONE` number in the displayed list.
* A cyclist hidden by a filter cannot be deleted. Use `list` or change the `find` criteria to display the intended cyclist first.
* If more than one displayed contact has that number, deletion is rejected.
* Use a complete number with country code and one space, for example `+65 96742165`;
  the number after the space must contain at least three digits.

Examples:
* `delete +65 96742165` deletes the contact with that phone number.

Both `edit` and `delete` report the same error when the target is absent from the displayed list:
`Cyclist with phone number PHONE cannot be found in the displayed list. Use list to show all cyclists.`
Here, `PHONE` is replaced with the number you entered. Error sentences end with full stops;
command syntax and usage examples retain their original format so you can copy them directly.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Repairing duplicate phone numbers in an existing book

Saved books containing duplicate phone numbers or email addresses (ignoring email case)
are rejected during loading. Cyclique starts with an empty directory and logs the error.
Before entering commands that could overwrite the file, close the application and back up
`data/addressbook.json`. Correct conflicting phone numbers and email addresses in a copy
of the file, then relaunch. Identical cyclist names are allowed when phone numbers and
email addresses are unique.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Older data files may contain an `address` field; Cyclique ignores it and omits it when saving. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [t/TAG]…` <br> e.g., `add n/James Ho p/+65 22224444 e/jamesho@example.com f/300 x/Intermediate l/any t/friend`
**Clear** | `clear`
**Delete** | `delete PHONE`<br> e.g., `delete +65 22224444`
**Edit** | `edit PHONE [n/NAME] [p/PHONE] [e/EMAIL] [f/FTP] [x/EXPERIENCE] [l/LOCATION]… [t/TAG]…`<br> e.g., `edit +65 22224444 n/James Lee e/jameslee@example.com`
**Find** | `find [n/NAME_KEYWORD]… [p/PHONE]…`<br> e.g., `find n/James n/Jake`, `find n/James p/+65 96320842`
**List** | `list`
**Help** | `help`
