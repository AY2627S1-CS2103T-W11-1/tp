# Cyclique v1.6 Release Notes

Cyclique helps organisers of recreational cycling groups in Singapore manage
cyclists and plan group rides. Version 1.6 is mainly a bug-fix release based
on beta testing of v1.5. It accepts more real-world names, makes `find` and
`ridefind` behave consistently, adds searching cyclists by email address,
prevents duplicate phone numbers from slipping in through leading zeroes, and
improves error feedback for search and ride commands. The following changes
are new compared with v1.5.

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
* **Phone numbers with leading zeroes in the country code:** Cyclique now
  ignores leading zeroes in the country code, so `+065 91234561` is stored and
  compared as `+65 91234561`. This stops the same number from being added for
  two cyclists by accident, and keeps `edit`, `delete`, `find p/` and ride
  rosters pointing at the right cyclist. The country code must still contain a
  non-zero digit, so a country code made up only of zeroes, such as `+0 000`, is
  rejected, and the invalid-phone message now explains the rule. Leading zeroes
  in the number itself are kept as typed, because they can be significant, so
  `+65 091234561` is a different number from `+65 91234561`.
  ([#247](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/247))

### Search improvements

* **Find cyclists by email address:** `find` accepts a new `e/EMAIL`
  criterion. Matching is exact but ignores letter case, so
  `find e/ALICE@EXAMPLE.COM` finds a cyclist saved as `alice@example.com`.
  Repeat `e/` to search for several addresses at once; a cyclist matches if
  their email matches any of them. Invalid email addresses are rejected, and
  the Help menu template and full help list include the new field.
  ([#258](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/258))
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
  as one consecutive phrase. Punctuation next to a word still does not prevent
  a match, so `ridefind notes/lights` finds `Bring lights!`.
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

### Error messages

* **Clearer `find` validation messages:** When a `p/` or `sos/` search value
  starts with `+` but is invalid, for example `find p/+65 9123abc`, the error
  now lists the phone number rules: at least 3 digits with no other
  characters, a country code with a non-zero digit, and exactly one space
  between them. An invalid `f/` value now says whether it contains non-digits
  or is outside the range of 1 to 600 W.
  ([#262](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/262))
* **Clearer `ridefind` validation messages:** `ridefind` now reports
  `Distance must contain only digits and up to 2 decimal places.`,
  `Minimum power must contain digits only.` or
  `Group size must contain digits only.` when `d/`, `f/` or `s/` is not in the
  expected form, for example `ridefind f/20a` or `ridefind f/-5`. The distance
  message also appears for a distance with more than 6 digits before the
  decimal point or more than 2 after it.
  ([#262](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/262))

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

* **User Guide updates:** New sections explain which characters names and
  locations accept, how leading zeroes in phone numbers are treated, and how to
  list every ride with `ridefind all` (as opposed to `ridelist ID`, which lists
  the cyclists on one ride). Search sections now cover repeated spaces and
  combined criteria, and the Help screenshots were refreshed.
  ([#260](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/260))
* **Developer Guide corrections:** The Help menu description and its
  manual-testing expectations were corrected, and the guide was audited against
  the v1.6 behaviour. The requirements, user stories and use cases now cover
  email search and ordered-word ride search, the `ridelist` sequence diagram
  follows textbook UML notation, the UI class diagram shows its dependency
  arrows more clearly, and the team's use of AI tools is declared.
  ([#250](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/250),
  [#260](https://github.com/AY2627S1-CS2103T-W11-1/tp/pull/260))
* **Wider regression coverage:** Each fix above ships with regression tests, and
  the User Guide, Developer Guide and manual test scenarios were updated to
  match the new behaviour.
  (see the pull requests linked in each entry above)
