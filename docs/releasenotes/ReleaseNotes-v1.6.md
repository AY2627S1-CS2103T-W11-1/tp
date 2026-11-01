# Cyclique v1.6 Release Notes

Cyclique helps organisers of recreational cycling groups in Singapore manage
cyclists and plan group rides. Version 1.6 is a bug-fix release based on
beta testing of v1.5. It accepts more real-world names, makes `find` and
`ridefind` behave consistently, prevents duplicate phone numbers from slipping
in through leading zeros, and improves error feedback for ride commands. The
following changes are new compared with v1.5.

## What's New

### Names and phone numbers

* **Hyphens and apostrophes in names:** Cyclist names and ride names can now
  contain internal hyphens (`-`) and straight apostrophes (`'`), so names such
  as `Tan Wei-Ling`, `John O'Connor` or `Wei-Ling's Ride` can be saved as
  written. Punctuation at the start or end of a name, repeated punctuation, and
  punctuation next to a space are still rejected. Existing Unicode name support
  is unchanged.
  ([#243](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/243),
  [#244](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/244))
* **Phone numbers with leading zeros in the country code:** Cyclique now
  ignores leading zeros in the country code, so `+065 91234561` is stored and
  compared as `+65 91234561`. This stops the same number from being added for
  two cyclists by accident, and keeps `edit`, `delete`, `find p/` and ride
  rosters pointing at the right cyclist. The country code must still contain a
  non-zero digit, so a country code made up only of zeros, such as `+0 000`, is
  rejected, and the invalid-phone message now explains the rule. Leading zeros
  in the number itself are kept as typed, because they can be significant, so
  `+65 091234561` is a different number from `+65 91234561`.
  ([#247](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/247))

### Search improvements

* **Consistent `ridefind` name and location matching:** `ridefind n/` and
  `ridefind l/` now match complete words in order, ignoring case, in the same
  way as cyclist `find`. Intervening words are allowed, so `ridefind n/Sunday
  Ride` finds `Sunday Sunrise Ride` and `ridefind l/East Park` finds `East Coast
  Park`. Reversed word order and partial words still do not match, and exact
  searches continue to work.
  ([#242](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/242))
* **Ordered-word matching for ride notes:** `ridefind notes/` follows the same
  rule, so `ridefind notes/"camera aperture"` finds a ride whose note reads
  `Bring camera with aperture f/2`. Previously the searched words had to appear
  as one consecutive phrase.
  ([#254](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/254))
* **Searching inside hyphenated and apostrophised words:** `find` and
  `ridefind` treat spaces, hyphens and apostrophes as word separators, so
  `find n/Ling` finds `Tan Wei-Ling` and `ridefind n/Wei-Ling` finds
  `Wei-Ling's Ride`. Matching is still case-insensitive and whole-word, so
  fragments such as `Lin` do not match. Name searches must still be valid
  names, so a search such as `n/-` shows a validation error.
  ([#246](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/246))
* **Quoted notes in `ridefind`:** Notes searches can be quoted to protect text
  that looks like a command prefix. For example, `ridefind notes/"aperture f/2"`
  and `rf notes/"aperture f/2"` search for the literal text `f/2` instead of
  treating it as a command parameter. Criteria outside the quotes are still
  combined with AND, and malformed quotes or repeated prefixes outside the quotes
  are rejected.
  ([#252](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/252))

### Ride commands

* **Curly quotes in ride notes:** `rideadd`, `rideedit` and `ridefind` recognise
  matching curly quotation marks as well as straight ones around notes, so text
  pasted from a word processor or messaging app no longer breaks commands such
  as `rideedit 1 notes/“Bring camera with aperture f/2”`. Malformed quote
  delimiters now produce clearer error messages.
  ([#248](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/248))
* **Clearer ride ID errors:** `ridelist`, `rideedit`, `ridedelete`,
  `rideaddcyclist` and `ridedeletecyclist` now report `Ride ID should be a
  positive whole number no larger than 2147483647.` when a digit-only ride ID is
  too large, instead of a generic invalid-format message. Malformed IDs still
  show the usage message, and unknown IDs within range still report that the
  ride cannot be found.
  ([#252](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/252))

### Documentation and testing

* **Developer Guide corrections:** The Help menu description and its
  manual-testing expectations were corrected.
  ([#250](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/250))
* **Wider regression coverage:** Each fix above ships with regression tests, and
  the User Guide, Developer Guide and manual test scenarios were updated to
  match the new behaviour.
  ([#244](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/244))
