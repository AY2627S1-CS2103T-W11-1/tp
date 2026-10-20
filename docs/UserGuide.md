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

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar cyclique.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com f/300 x/Beginner l/any` : Adds a contact named `John Doe` to the Address Book.

   * `delete +65 98765432` : Requests deletion of the contact with this phone number. Enter `yes` to confirm or `no` to cancel.

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
  For example, `edit PHONE [n/NAME] [f/FTP]` can be used as `edit +65 91234567 n/John Doe` or `edit +65 91234567 f/300`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[l/LOCATION]…` in `edit` may be omitted, or written as `l/Punggol` or `l/Punggol l/Marina Bay`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

The **Help** dropdown lists `rideedit` and `ridedelete` after `rideadd`, followed by
`rideaddcyclist ID PHONE` and `ridedeletecyclist ID PHONE`. Clicking either roster entry places
its full command name followed by a space in the command box for you to complete.
`ID` and `PHONE` are positional arguments; no `p/` prefix is inserted.

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to the address book.

Format: `add n/NAME p/PHONE sos/EMERGENCY_PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [l/LOCATION]…`

The emergency contact number (`sos/`) is required for new cyclists and follows the same validation as `p/`: a `+`, a country code of 1–3 digits, exactly one space, and a number of at least 3 digits. Blank values, parentheses and hyphens are rejected. Emergency numbers may be shared and do not determine duplicate cyclists.

The phone number must include a country code and one separating space, for example `+65 98765432`.
It must not belong to another contact, including contacts hidden by a filter.
FTP must be a whole number from 1 to 600.
At least one preferred location is required. Repeat `l/` for multiple locations, or use `l/any` alone.

`EXPERIENCE_LEVEL` must be one of `Beginner`, `Intermediate` or `Advanced` (case-insensitive).

Examples:
* `add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com f/300 x/Beginner l/any`
* `add n/Betsy Crowe sos/+65 99999999 e/betsycrowe@example.com p/+65 91234567 f/250 x/Advanced l/Punggol`

### Listing all persons: `list`

Shows a list of all persons in the address book.

Format: `list`

### Editing a person: `edit`

Edits an existing person in the address book.

Format: `edit PHONE [n/NAME] [p/PHONE] [sos/EMERGENCY_PHONE] [e/EMAIL] [f/FTP] [x/EXPERIENCE] [l/LOCATION]…`

* Edits the person with the specified phone number in the displayed list. Exactly one displayed contact must match.
* The resulting phone number must not belong to another contact anywhere in the book. An unchanged phone number is allowed when it is already unique.
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.

Examples:
* `edit +65 98765432 p/+65 91111111 e/johndoe@example.com` updates the identified contact's phone and email.
* `edit +65 91234567 n/Betsy Crower` updates Betsy's name.

* `edit +65 98765432 sos/+65 98888888` updates only the emergency number. Omitting `sos/` retains its current value; blank `sos/` is rejected.

Cyclist cards label emergency contact numbers as **SOS**. Older saved records without an emergency number load normally and show **Not provided**. Use `edit PHONE sos/EMERGENCY_PHONE` to supply one; other edits preserve its absence. Numbers are saved automatically and retained after restarting.

### Locating persons by name, phone or emergency number: `find`

Finds persons by name keywords, phone numbers, emergency contact numbers, FTP, experience level and preferred location.

Format: `find [n/NAME_KEYWORD]… [p/PHONE]… [sos/EMERGENCY_PHONE]… [f/MIN_FTP] [x/EXPERIENCE]… [l/LOCATION]…`
(at least one field is required)

Field | Meaning and rules
------|------------------
`n/NAME_KEYWORD` | A single name word or a complete full name. Single-word matching is case-insensitive and requires a full word; `n/hans` matches `Hans` but `n/Han` does not. A multi-word value such as `n/John Doe` matches the complete full name, ignoring case.
`p/PHONE` | A complete phone number using a `+`, a 1–3 digit country code, one space and at least 3 digits. Matching is exact; `p/+65 963` does not match `+65 96320842`.
`sos/EMERGENCY_PHONE` | A complete emergency contact number using the same validation and exact matching as `p/`. Cyclists without an emergency number do not match this criterion.
`f/MIN_FTP` | A whole number from 1 to 600. Matches cyclists whose FTP is greater than or equal to the supplied value in watts.
`x/EXPERIENCE` | `Beginner`, `Intermediate` or `Advanced`, ignoring case. Repeat `x/` to match any of the supplied experience levels.
`l/LOCATION` | A preferred location, ignoring case. Repeat `l/` to match any supplied location. A cyclist with `l/any` matches every location criterion.

At least one criterion is required. Each prefix may be repeated except `f/`, which accepts only one value. Values
within the same field use `OR`, while different fields use `AND`; for example, `find n/Hans n/Bo f/250` matches
cyclists named `Hans` or `Bo` whose FTP is at least 250 W. Prefix order does not matter. Text after a prefix belongs
to that field until the next recognised prefix, so full names and locations containing spaces can be entered normally.

The command filters only the displayed cyclist list and does not change saved records. Matching cyclists remain in
alphabetical order, ignoring case. The result message reports how many cyclists match; an empty result is valid and
leaves the cyclist panel empty. Use `list` to remove the current filter and display all cyclists again.

Invalid phone numbers, FTP values, experience levels, preferred locations, name values and duplicate `f/` prefixes
are rejected without changing the current displayed list. A name or phone criterion searches only the corresponding
field; emergency contact numbers are not treated as the cyclist's own phone number.

Examples:
* `find n/John` returns `john` and `John Doe`
* `find n/John Doe` returns only the cyclist with the complete full name `John Doe`, ignoring case.
* `find n/alex n/david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find n/alex n/david'](images/findAlexDavidResult.png)
* `find n/alex p/+65 96320842` returns only people whose name contains `alex` and whose phone number is `+65 96320842`.
* `find l/Punggol x/Intermediate f/250` returns cyclists whose preferred location is Punggol, experience level is Intermediate,
  and FTP is at least 250 W.
* `find sos/+65 99999999` returns cyclists with that emergency contact number.
* `find x/Beginner x/Intermediate` returns cyclists with either experience level.

Common failures include:

* `find` with no criteria is rejected with the command usage instructions.
* `find n/John Doe n/Jane Doe` is valid and matches either complete full name.
* `find John Doe` is rejected because name values must use the `n/` prefix.
* A phone number, FTP value, experience level or location that does not follow the constraints above is rejected
  without changing the current filter.

### Deleting a person: `delete`

Requests confirmation before deleting the specified cyclist from the address book.

Format: `delete PHONE`, followed by a confirmation reply in the command box.

* Shows the details of the cyclist with the specified `PHONE` number in the result panel. No cyclist is removed until you confirm.
* Enter `y` or `yes` to confirm, or `n` or `no` to cancel. Replies ignore letter case, so `Y`, `YES`, `yEs`, `N` and `NO` are accepted. Leading and trailing spaces are ignored.
* Cancellation displays `Deletion cancelled.` and leaves cyclist records, the current filter and saved data unchanged.
* An unrecognised reply, such as `maybe` or `yes please`, shows an error and keeps waiting for a valid reply.
* Entering another supported command cancels the pending deletion and executes that command. The result panel shows `Deletion cancelled.` followed by the command’s usual output or error. Another `delete PHONE` starts a fresh confirmation for its target. Commands retain their usual case-sensitive spelling.
* Closing or restarting Cyclique discards any pending confirmation. A confirmation reply has no effect when no deletion is pending.
* A cyclist hidden by a filter cannot be deleted. Use `list` or change the `find` criteria to display the intended cyclist first.
* If more than one displayed contact has that number, deletion is rejected.
* Use a complete number with country code and one space, for example `+65 96742165`;
  the number after the space must contain at least three digits.

Examples:
* Enter `delete +65 96742165`, check the displayed cyclist details, then enter `yes` to delete that cyclist.
* Enter `delete +65 96742165`, then `no` to keep the cyclist.
* Enter `delete +65 96742165`, then `list` to cancel the deletion and list all cyclists. Both messages appear in the result panel.

Both `edit` and `delete` report the same error when the target is absent from the displayed list:
`Cyclist with phone number PHONE cannot be found in the displayed list. Use list to show all cyclists.`
Here, `PHONE` is replaced with the number you entered. Error sentences end with full stops;
command syntax and usage examples retain their original format so you can copy them directly.

### Adding a group ride: `rideadd`

Adds a group ride that an organiser plans to lead.

Format: `rideadd n/NAME from/FROM to/TO l/LOCATION d/DISTANCE f/MINIMUM_POWER s/MAX_GROUP_SIZE [notes/NOTES]`

Field | Meaning and rules
------|------------------
`n/NAME` | The name of the ride. Letters, numbers and spaces only; it must not be blank.
`from/FROM` | When the ride starts, written as `DD-MM-YYYY TTTT`. `TTTT` is the time on a 24-hour clock from `0000` to `2359`, for example `18-10-2026 0630`. The date must be a real date.
`to/TO` | When the ride ends, in the same format as `from/`. It must not be earlier than `from/` (the same time is allowed).
`l/LOCATION` | One location. Letters, numbers, spaces, hyphens and apostrophes only.
`d/DISTANCE` | The distance in kilometres. A positive number with at most 2 decimal places, such as `45` or `45.5`.
`f/MINIMUM_POWER` | The minimum power a cyclist needs to join, in watts. A whole number from 1 to 600, the same range as a cyclist's FTP.
`s/MAX_GROUP_SIZE` | The most cyclists that can join. A positive whole number.
`notes/NOTES` | Optional. At most 10 words.

Every field except `notes/` is required, and each prefix can be used only once. You can give the fields in any order.

Each new ride gets an ID automatically: 1 for the first ride, 2 for the next and so on. IDs follow the order in which rides are created, are never reused, and carry on from where they stopped after you restart the app. A new ride has no cyclists on its roster yet. Two rides may have the same name, because rides are told apart by their IDs.

The ride is shown in the result box when it is added, and it is saved with your other data. The window also has a **Rides** panel in a column on the right (about 30% of the window width), next to the command box, result box and cyclist list. Each ride is shown as a card, like the cyclist cards. The card shows the ride's ID and name in a yellow-tinted heading, then its start and end, then labelled lines for the location, distance, minimum power and maximum group size, and a **Notes** line if the ride has notes. Long names, locations and notes wrap onto more lines, so the panel never needs a horizontal scrollbar. Click a ride to highlight its card. If there are no rides, the panel says `No rides yet. Use rideadd to create one.`

Examples:
* `rideadd n/Sunday Sunrise Ride from/18-10-2026 0630 to/18-10-2026 0930 l/East Coast d/45 f/180 s/12 notes/Bring lights and a spare tube`
* `rideadd n/Evening Spin from/20-10-2026 1800 to/20-10-2026 1930 l/Punggol d/30.5 f/150 s/8`

### Finding group rides: `ridefind`

Finds rides matching one or more supplied criteria and displays the matching rides in the Rides panel.

Format: `ridefind [n/NAME] [from/DATE [TIME]] [to/DATE [TIME]] [l/LOCATION] [d/DISTANCE] [f/MINIMUM_POWER] [s/MAX_GROUP_SIZE] [notes/NOTES]`

To list every ride, use `ridefind all`.

At least one criterion is required unless the special `all` argument is used. Different fields use AND matching, so a ride must satisfy every field supplied. `ridefind all` clears the current ride filter and displays every saved ride in the Rides panel.
The fields are matched as follows:

* `n/NAME` matches the complete ride name, ignoring case.
* `from/` matches rides starting at or after the supplied date and time.
* `to/` matches rides ending at or before the supplied date and time.
* `l/LOCATION` matches the ride location, ignoring case.
* `d/DISTANCE` and `s/MAX_GROUP_SIZE` use exact numeric matching.
* `f/MINIMUM_POWER` matches rides whose minimum power is greater than or equal to the supplied value.
* `notes/NOTES` matches rides with the supplied notes. Rides without notes do not match a notes criterion.

The date and time format is `DD-MM-YYYY TTTT`, using the 24-hour clock. A date-only `from/` value means `0000`,
while a date-only `to/` value means `2359`. When both bounds are supplied, `to/` cannot be earlier than `from/`.
Both bounds are inclusive. Each prefix can be used only once. Distances are positive numbers with at most two decimal places, minimum power is a whole number from 1 to 600, and maximum group size is a positive whole number. Dates must be real calendar dates and times must be between `0000` and `2359`.

The command updates only the displayed ride list; it does not change or delete saved rides. If no ride matches, the Rides panel is empty and the result message reports `0 ride(s) listed!`. Invalid criteria are rejected without changing the current ride filter.

Examples:
* `ridefind n/Sunday Sunrise Ride`
* `ridefind from/18-10-2026 to/18-10-2026`
* `ridefind l/Tampines f/200`
* `ridefind from/18-10-2026 0630 to/18-10-2026 2000 l/East Coast d/45 f/180 s/12`
* `ridefind all` displays all rides, including rides hidden by an earlier search.

Common failures include:

* `ridefind` with no criteria is rejected with the command usage instructions; use `ridefind all` to show all rides.
* A date-only `from/` or `to/` value is expanded to the start or end of that day respectively.
* A `to/` value earlier than `from/`, an invalid date or time, or a value outside the numeric constraints is rejected without changing the displayed rides.
* Supplying the same prefix twice is rejected. For example, use only one `f/` criterion.

### Editing a group ride: `rideedit`

Edits an existing group ride identified by its ride ID.

Format: `rideedit ID [n/NAME] [from/START] [to/END] [l/LOCATION] [d/DISTANCE] [f/MIN_FTP] [s/MAX_SIZE] [notes/NOTES]`

`ID` is the positive whole number displayed on the ride card and assigned when the ride is created. Supply at
least one editable field after it. All fields use the same rules as [`rideadd`](#adding-a-group-ride-rideadd), and
each prefix can be used only once. In particular, `from/` and `to/` use `DD-MM-YYYY TTTT` with 24-hour time. The
resulting end date and time cannot be earlier than the start date and time; equal times are allowed.

Only the supplied fields are replaced. The ride ID, cyclist roster and all unspecified fields remain unchanged.
Supplying `notes/NOTES` replaces the existing notes, while `notes/` with no value clears them. Omitting `notes/`
preserves the existing notes. Notes are optional and limited to 10 words.

The minimum FTP cannot be raised above the FTP of any cyclist currently on the roster, and the maximum group size
cannot be reduced below the current roster count. An edit that breaks either constraint is rejected without changing
the ride.

On success, Cyclique displays `Edited ride:` followed by the complete updated ride. The Rides panel updates in the
same position, and the change is saved automatically. An invalid edit is rejected without changing the ride or
its roster.

Examples:
* `rideedit 101 n/NightRide`
* `rideedit 101 f/250`
* `rideedit 101 from/20-10-2026 1400 to/20-10-2026 1600`
* `rideedit 101 notes/`

Common failures include:
* A missing, zero, negative, non-numeric or excessively large ride ID is rejected with the command usage instructions.
* An ID that does not identify an existing ride: `Ride with ID ID cannot be found.`, where the second `ID` is
  replaced with the number entered.
* No editable field after the ID: `At least one field to edit must be provided.`
* An invalid field value, which reports the same constraint as `rideadd`.
* A resulting end time before the start time, including after changing only one endpoint:
  `The end date and time (to/) cannot be earlier than the start date and time (from/).`

### Deleting a group ride: `ridedelete`

Deletes one group ride using the persistent ID shown on its ride card.

Format: `ridedelete ID`

Example: `ridedelete 101`

The ID must be a positive integer and identifies the ride rather than its position in the Rides panel. Deletion is
immediate and does not require confirmation. On success, Cyclique displays `Deleted ride:` followed by the deleted
ride's details, removes its card from the Rides panel and saves the change automatically.

The deleted ride's roster associations are removed with the ride. Cyclist records, other rides and their rosters
remain unchanged. Remaining ride IDs are not renumbered, and deleted IDs are never reused.

A missing, malformed, non-positive, overflowing or additional argument produces `Invalid command format.` followed
by the command usage. For example, an unknown ID of 101 reports `Ride with ID 101 cannot be found.` Failed
deletions do not change ride or cyclist data.

### Adding and removing cyclists from a ride

Use `rideaddcyclist ID PHONE` to add a saved cyclist to a ride.
Use `ridedeletecyclist ID PHONE` to remove them from that ride only.
For example: `rideaddcyclist 1 +65 91234567`, then `ridedeletecyclist 1 +65 91234567`.

* `ID` is the positive ride ID shown on the card, rather than a position in the list.
  Use a whole number from 1 to 2147483647. Command names are case-sensitive.
* `PHONE` includes the country code and one space, as in cyclist commands. Do not add `p/`.
* Cyclists can be added even when hidden by `find`. They must exist in the address book,
  meet the ride's minimum power and fit within its maximum group size.
* A cyclist cannot join the same ride twice. Removing a cyclist who is absent reports an error.
* Removing a cyclist from a ride keeps their contact record and other ride memberships.
* Ride cards show roster phone numbers, or `No cyclists` when empty. Changes are saved automatically.
* Editing a cyclist's phone updates all their ride memberships. Confirming deletion of their
  contact removes them from all ride rosters; cancelling deletion preserves those memberships.
* Both commands appear in Help. Entering either while a contact deletion is pending cancels
  that pending deletion and runs the roster command.

### Listing cyclists registered for a ride: `ridelist`

Shows the cyclists registered for one ride in the left-hand cyclist panel without changing any records.

Format: `ridelist ID`

Example: `ridelist 101`

A successful non-empty selection reports `Showing registered cyclists for ride 101 in the left panel.`

`ID` is the persistent positive ride ID shown on its card, not the ride's position in the Rides panel.
The selected cyclists use the existing cards and remain alphabetically sorted. The right-hand Rides panel
does not change. An empty roster succeeds with `Ride 101 has no registered cyclists.` and shows no cyclist cards.

Ride List replaces the current cyclist filter rather than combining with it. Successful `rideaddcyclist` and
`ridedeletecyclist` commands automatically refresh the displayed cyclists when they change the selected ride.
Changes to another ride do not change the selected ride. `list` restores every cyclist; `find` or another `ridelist`
replaces the Ride List filter, and later roster changes do not reactivate it.

If the selected ride is deleted, Ride List remains associated with that ride ID and the cyclist panel becomes empty.
It does not switch to another ride. Use `list`, `find` or another `ridelist` to replace the filter.

An unknown ride ID, such as `999999`, reports `Ride with ID 999999 cannot be found.` Malformed, missing, zero,
negative, overflowing or extra arguments produce the invalid-command-format message and usage guidance. If a roster
phone no longer resolves to a cyclist, the command reports `Cannot list ride: a registered cyclist cannot be found.`
The previous filter and all ride and cyclist data remain unchanged after either failure.

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

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Older data files may contain `address` or `tags` fields; Cyclique ignores them and omits them when saving, while retaining all supported cyclist details. Group rides are saved in the same file under `rides`, together with a `nextRideId` number that stops ride IDs from being reused; data files saved before rides existed still load, with no rides. Advanced users are welcome to update data directly by editing that data file.

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
**Add** | `add n/NAME p/PHONE sos/EMERGENCY_PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [l/LOCATION]…` <br> e.g., `add n/James Ho p/+65 22224444 sos/+65 99999999 e/jamesho@example.com f/300 x/Intermediate l/any`
**Clear** | `clear`
**Delete** | `delete PHONE`, then `y`/`yes` to confirm or `n`/`no` to cancel (case-insensitive)<br> e.g., `delete +65 22224444`, then `yes`
**Edit** | `edit PHONE [n/NAME] [p/PHONE] [sos/EMERGENCY_PHONE] [e/EMAIL] [f/FTP] [x/EXPERIENCE] [l/LOCATION]…`<br> e.g., `edit +65 22224444 n/James Lee e/jameslee@example.com`
**Find** | `find [n/NAME_KEYWORD]… [p/PHONE]… [sos/EMERGENCY_PHONE]… [f/MIN_FTP] [x/EXPERIENCE]… [l/LOCATION]…`<br> e.g., `find n/James n/Jake`, `find n/James p/+65 96320842`
**List** | `list`
**Help** | `help`
**Rideadd** | `rideadd n/NAME from/FROM to/TO l/LOCATION d/DISTANCE f/MINIMUM_POWER s/MAX_GROUP_SIZE [notes/NOTES]`<br> e.g., `rideadd n/Sunday Sunrise Ride from/18-10-2026 0630 to/18-10-2026 0930 l/East Coast d/45 f/180 s/12 notes/Bring lights`
**Ridefind** | `ridefind all` or `ridefind [n/NAME] [from/DATE [TIME]] [to/DATE [TIME]] [l/LOCATION] [d/DISTANCE] [f/MINIMUM_POWER] [s/MAX_GROUP_SIZE] [notes/NOTES]`<br> e.g., `ridefind all`, `ridefind from/18-10-2026 to/18-10-2026 f/180`
**Rideedit** | `rideedit ID [n/NAME] [from/START] [to/END] [l/LOCATION] [d/DISTANCE] [f/MIN_FTP] [s/MAX_SIZE] [notes/NOTES]`<br> e.g., `rideedit 101 n/NightRide`, `rideedit 101 notes/`
**Ridedelete** | `ridedelete ID`<br> e.g., `ridedelete 101`
**Add cyclist to ride** | `rideaddcyclist ID PHONE`
**Remove cyclist from ride** | `ridedeletecyclist ID PHONE`
**List cyclists in ride** | `ridelist ID`<br> e.g., `ridelist 101`
