# Cyclique v1.5 Release Notes

Cyclique helps organisers of recreational cycling groups in Singapore manage
cyclists and plan group rides. Version 1.5 expands the command workflow,
improves ride management, strengthens validation and makes the interface and
release package more reliable. The following changes are new compared with
v1.4.

## What's New

### Cyclist commands

* **More robust `add`, `edit` and `delete` input:** Names now support Unicode
  characters. Valid email local parts may contain consecutive punctuation, and
  `edit` and `delete` preserve more specific phone-validation errors instead of
  replacing them with a generic message. ([#199](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/199),
  [#200](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/200))
* **Editing filtered cyclists:** Editing a cyclist no longer unexpectedly loses
  the active cyclist filter. The updated cyclist remains displayed when it
  still matches the filter, while the filter behaviour is handled consistently
  when the edit changes searchable details. ([#207](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/207))
* **Stricter `clear` input:** `clear` rejects extra text after the command,
  preventing an unintended command from being treated as a valid clear.
  ([#151](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/151))

### Ride commands and rosters

* **Ride command aliases:** All ride commands have short aliases for quicker
  entry: `rideadd` (`ra`), `rideedit` (`re`), `ridedelete` (`rd`),
  `rideaddcyclist` (`rac`), `ridedeletecyclist` (`rdc`), `ridelist` (`rl`),
  and `ridefind` (`rf`). The aliases are shown beside the full command names in
  Help.
  ([#193](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/193))
* **Ride-overlap warnings:** `rideaddcyclist` warns when a new cyclist would
  join a ride that overlaps their existing ride, while `rideedit` warns when a
  time change creates overlaps for registered cyclists. Organisers can confirm
  intentional overlaps; cancelling leaves the original data unchanged.
  ([#190](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/190))
* **Safer ride editing:** Ride edits preserve the existing ride and roster when
  confirmation is cancelled or validation fails. The command also keeps the
  ride ID and other unspecified fields unchanged.
* **Consistent ride IDs:** Numeric ride IDs accept leading zeroes, so inputs
  such as `rideedit 001` can refer to ride 1. Roster commands provide clearer,
  command-specific usage messages and examples. ([#172](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/172),
  [#173](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/173))
* **Clearer ride-list feedback:** The Rides panel distinguishes an address book
  with no rides from rides that are temporarily hidden by the active filter.
  Ride roster phone numbers are displayed one per line for easier reading.
  ([#174](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/174))

### Search improvements

* **More flexible cyclist `find`:** `find` supports ordered partial name
  searches and multi-word queries. Multi-word locations are handled correctly,
  and `any` location matching is exact instead of matching unrelated text.
  ([#203](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/203))
* **Improved ride-note matching:** Ride notes support partial whole-word
  matching without depending on the computer's locale. Literal note text is
  preserved when it resembles a command prefix when double-quoted. For example,
  `rideedit 1 notes/"Bring camera with aperture f/2"` preserves `f/2` as note
  text instead of treating it as a power parameter. ([#179](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/179),
  [#201](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/201))
* **Ride-filter behaviour after changes:** When a ride is added or edited while
  a `ridefind` filter is active, Cyclique checks whether the changed ride would
  be hidden. If so, the ride filter resets and the changed ride becomes visible;
  if the ride still matches, the existing filter is preserved. Ride filters are
  also cleared when the address book is replaced. ([#143](https://github.com/AY2627S1-CS2103T-W11-1/tp/issues/143))
* **Clearer `ridefind` formats:** `ridefind all` and criteria-based searches now
  have separate formats in the command usage and Help menu. Selecting either
  Help entry inserts the appropriate command starter into the command box.

### Interface and distribution

* **Improved cyclist and ride display:** Locations are displayed with
  consistent capitalisation, cyclist-card location rows are aligned more
  clearly, and long card text wraps more reliably in narrow windows.
  ([#206](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/206),
  [#205](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/205),
  [#152](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/152))
* **More usable Help menu:** The Help menu fits its available width, displays
  ride aliases and uses clearer command terminology such as `MIN_POWER`.
  The Full Help link and command examples have also been updated.
  ([#160](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/160),
  [#166](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/166))
* **Application identity:** The main window title now identifies the app as
  Cyclique. ([#202](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/202))
* **Cross-platform packaging:** The release build restores the JavaFX setup
  needed for the application to run across supported platforms.
  ([#198](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/198))

## Cyclique in Action

### Confirming an overlapping ride

When a new or edited ride overlaps an existing ride, Cyclique explains the
conflict before saving and lets the organiser confirm or cancel the change.

### Managing rides with aliases

The Help menu shows aliases such as `rf` beside the full ride commands, and the
command box can be populated directly from the selected Help entry.

### Searching across multiple fields

Organisers can combine ride name, location and note criteria; a ride must satisfy
every supplied criterion. A changed ride remains visible if an active filter
would otherwise hide it.

