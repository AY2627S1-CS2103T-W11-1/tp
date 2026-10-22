---
layout: page
title: User Guide
---

Cyclique is a **desktop application for organisers of recreational cycling groups in Singapore**, optimised for use through a Command Line Interface (CLI) while retaining the benefits of a Graphical User Interface (GUI). Manage cyclists, their emergency contact details and cycling profiles, and group rides by typing commands.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that JDK `25` is installed on your computer.<br>
   Run `java -version` in a terminal to check that the active version is `25`.

1. Download `cyclique.jar` from the [Cyclique releases page](https://github.com/AY2627S1-CS2103T-W11-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for Cyclique.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar cyclique.jar`.<br>
   The main window should appear after the opening artwork. When no saved data file exists, Cyclique starts with Alex Tan, Bernice Lee and Chloe Lim, and two example group rides with empty rosters, as shown below. If you already have saved data, Cyclique loads it instead.<br>
   ![Cyclique main window showing three cyclists with SOS numbers and two group rides](images/ug-main-window.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Shows all cyclists.

   * `add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com f/300 x/Beginner l/any` : Adds a cyclist named `John Doe`, including an emergency contact number supplied using `sos/`.

   * `delete +65 98765432` : Requests deletion of the cyclist with this phone number. Enter `yes` to confirm or `no` to cancel.

   * `clear` : Deletes all cyclists and all rides.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

Cyclists are displayed alphabetically by their full name, ignoring letter case, on startup and after `list`, `find` or `ridelist`. Adding a cyclist places them in the correct alphabetical position; editing their name updates their position. Names that are identical ignoring case retain their relative stored order. The visible indexes run consecutively from 1 within the current display, including filtered results. Use phone numbers to target `edit` and `delete` as usual.

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `edit PHONE [n/NAME] [f/FTP_LEVEL]` can be used as `edit +65 91234567 n/John Doe` or `edit +65 91234567 f/300`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[l/LOCATION]…` in `edit` may be omitted, or written as `l/Punggol` or `l/Punggol l/Marina Bay`.

* Prefixed fields can be in any order. Put positional arguments, such as the target `PHONE` in `edit`, before the prefixed fields.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Cyclique gives you help in two places: the **Help** menu in the menu bar, and the **Full help** window.

**The Help menu**

Click **Help** in the menu bar to see every command with its format, followed by **Full help...** at the bottom. The commands are listed in this order: `add`, `edit`, `delete`, `find`, `rideadd`, `rideedit`, `ridedelete`, `rideaddcyclist`, `ridedeletecyclist`, `ridelist`, `ridefind`, `list`, `clear`, `help` and `exit`.

![Help menu](images/helpDropdown.png)

Choosing a command from the menu does **not** run it. It places the start of that command in the command box for you to complete, and then you press Enter as usual:

* For most commands, the command word followed by a space is inserted, for example `rideedit `.
* For `add` and `rideadd`, the command word is followed by every required prefix, for example `rideadd n/ from/ to/ l/ d/ f/ s/`. Type a value straight after each prefix. Optional parameters such as `notes/` are not inserted, so type them yourself if you need them.
* `ID` and `PHONE` in `rideaddcyclist ID PHONE` and `ridedeletecyclist ID PHONE` are typed after the command name without a prefix, so no `p/` is inserted.

**The Full help window**

Choose **Help → Full help...**, press `F1`, or enter `help` to open the Full help window. It lists the usage, parameters and an example for every command. At the bottom it shows a link to the user guide and a **Copy URL** button that copies the link to your clipboard. After you enter `help`, the result box shows `Opened help window.` If the window is already open, Cyclique focuses it instead of opening a second one.

![Full help window](images/helpMessage.png)

Format: `help`


### Adding a cyclist: `add`

Adds a cyclist to Cyclique.

Format: `add n/NAME p/PHONE sos/EMERGENCY_PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [l/LOCATION]…`

Field | Meaning and rules
------|------------------
`n/NAME` | The cyclist's name. It may contain letters, numbers and spaces, but must not be blank.
`p/PHONE` | A unique phone number using a `+`, a 1–3 digit country code, one space and at least 3 digits, such as `+65 98765432`. Parentheses, hyphens and blank values are rejected. The number must not belong to another cyclist, including cyclists hidden by a filter.
`sos/EMERGENCY_PHONE` | The cyclist's emergency contact phone number, displayed as **SOS** on their card. Required for new cyclists. It follows the same format as `p/` but may be shared by multiple cyclists and does not determine duplicate cyclists.
`e/EMAIL` | A valid email address, such as `johnd@example.com`. It must be unique across all cyclists, including those hidden by a filter; comparisons ignore letter case.
`f/FTP_LEVEL` | Functional Threshold Power (FTP): a whole number from 1 to 600, in watts.
`x/EXPERIENCE_LEVEL` | `Beginner`, `Intermediate` or `Advanced`, ignoring case.
`l/LOCATION` | A non-blank preferred cycling location containing only letters, numbers, spaces, hyphens or apostrophes. At least one is required. Repeat `l/` for multiple locations, or use `l/any` alone to indicate no location preference. `any` ignores case and cannot be combined with another location.

All fields are required. Only `l/` may be repeated; repeating any other prefix is rejected.
Prefixes can be entered in any order. Cyclists may share a name or emergency contact number,
but their own phone numbers and email addresses must be unique.

On success, the result box shows `New person added:` followed by the cyclist's details, including
`Emergency phone:`. The new card shows the number as **SOS**. Cyclique restores the full cyclist
list, places the new card in alphabetical order and saves the data automatically.

Examples (use phone numbers and email addresses that are not already stored):

* `add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com f/300 x/Beginner l/any` adds John with an FTP of 300 W, Beginner experience and no location preference.
* `add n/Betsy Crowe sos/+65 99999999 e/betsycrowe@example.com p/+65 91234567 f/250 x/Advanced l/Punggol l/Seletar` adds Betsy with two preferred locations. Sharing John's emergency contact number is allowed.

![Add success showing John Doe's SOS number and cycling profile alongside the existing cyclists and rides](images/ug-add-success.png)

*After the first example, John appears after Chloe in the alphabetical list. The two existing rides are unchanged.*

Missing required fields, blank values, invalid values and repeated single-valued prefixes are rejected.
A phone or email conflict reports `Another cyclist already has this phone number or email.`
These rejected commands leave existing records unchanged and do not add a cyclist.

### Listing all persons: `list`

Shows every cyclist currently stored in the address book.

Format: `list`

* Removes any active `find` or [`ridelist`](#listing-cyclists-registered-for-a-ride-ridelist) filter and restores all cyclists. The result box shows `Listed all persons.`
* Affects only the cyclist panel. It does not change the Rides panel; use `ridefind all` to show every ride again.
* Displays cyclist cards alphabetically by full name, ignoring case. Names that are identical ignoring case retain their stored order.
* Each card shows the cyclist's name, phone number, SOS number, email, FTP, experience level and preferred locations. A missing SOS number is shown as **Not provided**.
* If there are no cyclists, the cyclist panel is empty and the result still shows `Listed all persons.`

### Editing a cyclist: `edit`

Updates an existing cyclist's contact details or cycling profile.

Format: `edit PHONE [n/NAME] [p/PHONE] [sos/EMERGENCY_PHONE] [e/EMAIL] [f/FTP_LEVEL] [x/EXPERIENCE_LEVEL] [l/LOCATION]…`

* The unprefixed `PHONE` identifies the cyclist to edit. It must match exactly one cyclist in the **displayed list**. Use `list` first if a filter hides the cyclist. Card indexes cannot be used as the target.
* `p/PHONE`, if supplied, is the cyclist's **replacement** phone number. For example, `edit +65 98765432 p/+65 91111111` targets the old number and replaces it with the new number.
* Supply at least one optional field. Otherwise, Cyclique reports `At least one field to edit must be provided.`
* Supplied fields follow the [Add field rules](#adding-a-cyclist-add). Only `l/` may be repeated. Phone numbers and email addresses must remain unique across all cyclists, including hidden records; email comparisons ignore case. Keeping the cyclist's own current phone or email is allowed.
* Omitted fields remain unchanged. Supplying one or more `l/` fields replaces the **entire** preferred location set; it does not append locations. Blank `l/` is invalid. Use `l/any` alone for no location preference.
* Omitting `sos/` preserves the existing emergency contact number. Blank `sos/` is invalid and cannot remove the number.

On success, the result box shows `Edited person:` followed by the updated details. Cyclique restores
all cyclists in alphabetical order and saves the changes. A name edit may move a card to a different
position. Changing the cyclist's own phone number also updates that number in every ride roster
they belong to, preserving their memberships.

Examples, starting with John from the Add example:

* `edit +65 98765432 sos/+65 98888888 f/350 x/Intermediate l/East Coast Park l/Punggol` updates John's emergency number, FTP and experience, and replaces `any` with two preferred locations. His name, own phone number and email remain unchanged.
* `edit +65 98765432 sos/+65 98888888` updates only the emergency number; all other fields remain unchanged. This can also be used independently of the previous example.
* `edit +65 98765432 p/+65 91111111 e/johndoe@example.com` changes John's own phone and email. Subsequent edits or deletion must target `+65 91111111`.

![Edit success showing John Doe with SOS number +65 98888888, FTP 350 W, Intermediate experience and two preferred locations](images/ug-edit-success.png)

*After the first example, John's updated card remains after Chloe. The deletion examples below continue from this state, without changing John's own phone number.*

Older saved records without an emergency contact number load normally and show **Not provided**
in the SOS field. Use `edit PHONE sos/EMERGENCY_PHONE` to supply one; edits to other fields preserve
its absence. Emergency numbers are retained after restarting.

Invalid edits, duplicate phone numbers or emails, and targets hidden by a filter are rejected without
changing cyclist records, ride rosters or the current filter. Phone or email conflicts report
`Another cyclist already has this phone number or email.`

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

### Deleting a cyclist: `delete`

Deletes a cyclist only after you check their details and confirm in the command box.

Format: `delete PHONE`, then enter a separate confirmation or cancellation reply.

* Use the cyclist's own complete phone number, including country code and exactly one space, for example `+65 98765432`. The country code contains 1–3 digits; the number after the space contains at least 3 digits. Do not use a card index or the `p/` prefix.
* The number must identify exactly one cyclist in the **displayed list**. A cyclist hidden by a filter cannot be deleted; use `list` to show all cyclists first. Multiple displayed matches are rejected.
* The result panel shows `Delete this cyclist?` followed by the target's details, including their emergency number, and warns `This also removes them from all ride rosters.` No cyclist, roster or saved data changes at this stage.
* Enter `y` or `yes` to confirm, or `n` or `no` to cancel. Replies ignore letter case and surrounding spaces, so `YeS` and ` NO ` are accepted.
* Cancellation shows `Deletion cancelled.` and preserves cyclist records, ride rosters, the current filter and saved data.
* An invalid reply, such as `maybe`, `yes please` or an unknown command, reports `Invalid confirmation.` and keeps waiting for a valid reply. It does not cancel the request or change data.
* A recognised command cancels the pending deletion and then runs. The result shows `Deletion cancelled.` followed by that command's usual output or error, even if its arguments are invalid. Another valid `delete PHONE` starts a fresh request. Command names remain case-sensitive: `list` cancels, but `LIST` is an invalid reply.
* Closing or restarting Cyclique discards any pending request. Without a pending request, `yes` and `no` report `Unknown command.`

Confirmation shows `Deleted person:` followed by the deleted cyclist's details. Their card disappears,
and their phone number is removed from **all ride rosters**. The rides themselves, other cyclists and
their memberships remain. The change is saved automatically. There is no undo command.

**Example: check, cancel, then confirm John's deletion**

These screenshots continue from the first Edit example: John has SOS `+65 98888888`, FTP 350 W,
Intermediate experience and two preferred locations. Before the deletion request, Alex and John
have been registered for Sunday Sunrise Ride using `rideaddcyclist 1 +65 91234561` and
`rideaddcyclist 1 +65 98765432` (where `1` is that ride's ID).

1. Enter `delete +65 98765432`. Check that the result panel identifies John. His card and both roster numbers remain visible while confirmation is pending.

   ![Pending deletion identifying John Doe with confirmation instructions while John and Alex remain on the ride roster](images/ug-delete-confirmation.png)

   *The request shows John's identity, the roster-removal warning and the accepted replies. Scroll horizontally in the result box if needed to read the remaining details.*

1. Enter `no`. The result shows `Deletion cancelled.` John remains in the cyclist list and on the ride roster.

   ![Deletion cancelled with John Doe still present and both cyclists retained on Sunday Sunrise Ride's roster](images/ug-delete-cancelled.png)

   *Cancellation keeps John's record and ride membership.*

1. Enter `delete +65 98765432` again, then enter `yes`. The result starts `Deleted person: John Doe; Phone: +65 98765432`. John disappears, and only Alex's number remains on Sunday Sunrise Ride's roster. Evening Spin remains unchanged.

   ![Successful deletion of John Doe with his card removed and only Alex's phone number remaining on the ride roster](images/ug-delete-success.png)

   *Confirmation removes John and his roster memberships while keeping both rides.*

Alternatively, enter `delete +65 98765432`, then `list` before confirming. The result shows
`Deletion cancelled.` followed by `Listed all persons.` John is retained and all cyclists are displayed.

Both `edit` and `delete` report the same error when the target is absent from the displayed list:
`Cyclist with phone number PHONE cannot be found in the displayed list. Use list to show all cyclists.`
Here, `PHONE` is replaced with the number entered. Invalid or missing phone arguments show the
command usage without changing records.

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

Shows the cyclists registered for one ride in the left-hand cyclist panel, so that you can see who is on that ride. No ride or cyclist records are changed.

Format: `ridelist ID`

Example: `ridelist 101`

* `ID` is the persistent ride ID shown on the ride card, not the ride's position in the Rides panel. Use a whole number from 1 to 2147483647.
* The cyclists appear on the usual cyclist cards, sorted alphabetically. The Rides panel on the right does not change.
* On success, Cyclique reports `Showing registered cyclists for ride 101 in the left panel.`
* If nobody is registered for the ride, Cyclique reports `Ride 101 has no registered cyclists.` and the cyclist panel is empty.

**How the selection behaves**

* `ridelist` replaces the current cyclist filter instead of combining with it. For example, if `find` was hiding some cyclists, `ridelist` still shows everyone registered for the ride.
* The panel refreshes by itself when `rideaddcyclist` or `ridedeletecyclist` changes the selected ride. Changes to other rides do not affect it.
* The selection ends when you run `list`, `find`, `clear`, another `ridelist`, or a successful `edit`, because each of these replaces the cyclist filter. Later roster changes do not bring the old selection back.
* If you delete the selected ride with `ridedelete`, the cyclist panel becomes empty and does not switch to another ride. Use `list`, `find` or another `ridelist` to show cyclists again.

**If something goes wrong**

* An unknown ride ID, such as `999999`, reports `Ride with ID 999999 cannot be found.`
* A missing, malformed, zero, negative, overflowing or extra argument produces the invalid-command-format message with the command usage.
* If a phone number on the roster no longer matches any cyclist, Cyclique reports `Cannot list ride: a registered cyclist cannot be found.`

After any of these errors, the previous filter and all ride and cyclist data stay unchanged.

### Clearing all entries: `clear`

Deletes **every cyclist and every ride** from Cyclique.

Format: `clear`

* Cyclique does **not** ask you to confirm, and there is no undo. The change is saved automatically, so the data file is emptied as well. Keep a copy of your data file first if you might need the data again (see [Saving the data](#saving-the-data)).
* Ride IDs are never reused. After `clear`, the next ride you add receives the next unused ID instead of starting again from 1.
* Any cyclist filter, including a [`ridelist`](#listing-cyclists-registered-for-a-ride-ridelist) selection, is reset.
* On success, the result box shows `Address book has been cleared!`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

Cyclique saves successful additions, edits and confirmed deletions automatically; you do not need to save manually. It also saves after other successfully executed commands, including commands that only change the display. A deletion request, an explicit cancellation reply (`n`/`no`) or an invalid confirmation reply does not write the data file. When another recognised command cancels a pending deletion, that command follows its normal saving behaviour. Rejected commands do not save changes.

### Repairing duplicate phone numbers in an existing book

Saved books containing duplicate phone numbers or email addresses (ignoring email case)
are rejected during loading. Cyclique starts with an empty directory and logs the error.
Before entering commands that could overwrite the file, close the application and back up
`data/addressbook.json`. Correct conflicting phone numbers and email addresses in a copy
of the file, then relaunch. Identical cyclist names are allowed when phone numbers and
email addresses are unique.

### Editing the data file

Cyclique stores data in `data/addressbook.json`, relative to the folder from which you launch the application. Following the Quick start steps places it in your chosen home folder. Older cyclist records without an `emergencyPhone` field load with **SOS: Not provided**; adding an emergency number through `edit` saves it for future sessions. Group rides are saved in the same file under `rides`, together with a `nextRideId` number that stops ride IDs from being reused; data files saved before rides existed still load, with no rides. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
Close Cyclique and back up the file before editing it. If your changes make the file invalid, Cyclique starts with an empty directory at the next launch. A subsequent successful command that saves data can overwrite the invalid file with that empty directory. Restore or repair your backup before entering commands. Direct edits must follow the supported data format and field constraints.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install JDK 25 and Cyclique on the other computer. Close Cyclique on both computers, then copy `data/addressbook.json` from the original home folder into the new home folder's `data` directory, backing up any existing file first. Launch Cyclique from the new home folder to load the cyclists and rides.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimise the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimised, and no new Help Window will appear. The remedy is to manually restore the minimised Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE sos/EMERGENCY_PHONE e/EMAIL f/FTP_LEVEL x/EXPERIENCE_LEVEL l/LOCATION [l/LOCATION]…` <br> e.g., `add n/John Doe p/+65 98765432 sos/+65 99999999 e/johnd@example.com f/300 x/Beginner l/any`
**Clear** | `clear`
**Delete** | `delete PHONE`, then `y`/`yes` to confirm or `n`/`no` to cancel (case-insensitive)<br> e.g., `delete +65 98765432`, then `yes`
**Edit** | `edit PHONE [n/NAME] [p/PHONE] [sos/EMERGENCY_PHONE] [e/EMAIL] [f/FTP_LEVEL] [x/EXPERIENCE_LEVEL] [l/LOCATION]…`<br> e.g., `edit +65 98765432 sos/+65 98888888 f/350 x/Intermediate l/East Coast Park l/Punggol`
**Find** | `find [n/NAME_KEYWORD]… [p/PHONE]… [sos/EMERGENCY_PHONE]… [f/MIN_FTP] [x/EXPERIENCE]… [l/LOCATION]…`<br> e.g., `find n/James n/Jake`, `find n/James p/+65 96320842`
**List** | `list`
**Help** | `help`<br> or press `F1`, or choose **Help → Full help...**
**Rideadd** | `rideadd n/NAME from/FROM to/TO l/LOCATION d/DISTANCE f/MINIMUM_POWER s/MAX_GROUP_SIZE [notes/NOTES]`<br> e.g., `rideadd n/Sunday Sunrise Ride from/18-10-2026 0630 to/18-10-2026 0930 l/East Coast d/45 f/180 s/12 notes/Bring lights`
**Ridefind** | `ridefind all` or `ridefind [n/NAME] [from/DATE [TIME]] [to/DATE [TIME]] [l/LOCATION] [d/DISTANCE] [f/MINIMUM_POWER] [s/MAX_GROUP_SIZE] [notes/NOTES]`<br> e.g., `ridefind all`, `ridefind from/18-10-2026 to/18-10-2026 f/180`
**Rideedit** | `rideedit ID [n/NAME] [from/START] [to/END] [l/LOCATION] [d/DISTANCE] [f/MIN_FTP] [s/MAX_SIZE] [notes/NOTES]`<br> e.g., `rideedit 101 n/NightRide`, `rideedit 101 notes/`
**Ridedelete** | `ridedelete ID`<br> e.g., `ridedelete 101`
**Add cyclist to ride** | `rideaddcyclist ID PHONE`
**Remove cyclist from ride** | `ridedeletecyclist ID PHONE`
**List cyclists in ride** | `ridelist ID`<br> e.g., `ridelist 101`
