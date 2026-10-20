---
layout: page
title: Ride feature bug testing report
---

## Scope and approach (20 October 2026)

Review ride creation, editing, deletion, roster addition/removal, roster listing, ride finding,
field validation, live filters and JSON loading. Run the entire automated suite, including existing
cyclist commands, emergency contacts, deletion confirmation and storage tests.
Use the [Week 10 test design guidance](https://nus-cs2103-ay2627-s1.github.io/website/schedule/week10/topics.html#w10-3-test-cases-intro)
to select equivalence partitions and adjacent boundary values. This is a targeted audit, not an
exhaustive proof of correctness or a completed cross-platform manual test campaign.

Existing staged defensive-programming changes remain in place; this audit does not stage,
commit or push changes. Production fixes are confined to JSON loading/validation and argument tokenisation.

## Reproduced defects fixed

Five reproduction tests failed before the production fixes; all pass afterwards. A sixth control
case, a valid roster exactly at capacity, passed before and after.

| Defect | Impact and reproduction | Fix and regression |
| --- | --- | --- |
| JSON null root escapes normal error handling | Put `null` in the saved book. `JsonUtil.readJsonFile` calls `Optional.of(null)`, throwing an uncaught `NullPointerException`. It bypasses the application's normal loading recovery. | Reject a deserialised null root inside the existing I/O error path so it becomes `DataLoadingException`. `RideDataIntegrityTest.read_nullJsonRoot_reportsLoadingFailure` covers the actual file boundary. |
| Duplicate roster members load | Save the same phone twice in a ride roster. It loads successfully, inflates occupancy and can trigger the roster-removal membership assertion when assertions are enabled. | Reject duplicate phones during ride conversion with a checked validation error. `RideDataIntegrityTest.read_duplicateRosterMember_reportsLoadingFailure`. |
| Over-capacity rosters load | Save two members on a ride with maximum group size 1. Loading accepts a state that CLI additions prohibit. | Compare saved roster size with the validated group size before constructing the ride. `RideDataIntegrityTest.read_overCapacityRoster_reportsLoadingFailure`. |
| Missing cyclist references load | Save a roster phone absent from the cyclist list. The book loads, but `ridelist` cannot display that roster and reports a missing-member error. | Validate all roster references against the complete cyclist list before exposing the loaded book. `RideDataIntegrityTest.read_missingCyclistReference_reportsLoadingFailure`. |
| Tabs can silently swallow an edit field | Run `rideedit 1 notes/Bring lights<TAB>s/2`, with a literal tab. The old tokenizer treats `s/2` as part of notes, leaving maximum group size unchanged while reporting success. | Recognise whitespace before prefixes, advance searches explicitly and ignore empty prefixes. Preserve embedded prefix-like text within values. `RideRosterIntegrationTest.execute_tabSeparatedRideEdit_updatesBothFields` checks both fields and persistence; `ArgumentTokenizerTest` covers separator classes. |

Loading rejects the whole invalid book through the existing recovery mechanism rather than dropping
members silently. The startup behaviour after a loading failure remains an empty book. Back up a
corrupted file before using that recovery session to save new data. Direct storage reads in the
regression tests verify that rejection itself leaves the original file unchanged.

Three pre-existing round-trip test fixtures contained roster phones with no cyclist records.
Update those fixtures to reference an existing cyclist or add the corresponding test cyclists;
retain their original assertions about ride details, notes, IDs and persistence.

## Equivalence partitions and boundary values

| Area | Selected partitions and boundaries | Evidence |
| --- | --- | --- |
| Country code and subscriber length | Country-code lengths 0, 1, 2, 3, 4; subscriber lengths 2, 3, 4. Accept `+12 123` and `+123 123`; reject `+1234 123` and `+123 12`. | `PhoneTest`, `EditCommandParserTest` |
| Distance | Non-numeric, negative, zero, positive, excessive precision and excessive whole digits. Valid 0.01/0.02 and 999999.98/999999.99; invalid 0, -0.01, 0.001 and 1000000. | `RideBoundaryIntegrationTest`, existing `DistanceTest` |
| Minimum power | Below, within and above 1–600; valid 1/2/599/600, invalid 0/601, fractions and integer overflow. | `RideBoundaryIntegrationTest`, existing minimum-power tests |
| Maximum group size | Valid 1/2/999999998/999999999; invalid 0/1000000000 and fractions. | `RideBoundaryIntegrationTest`, existing `GroupSizeTest` |
| Notes | Missing optional notes, empty edit to clear, 1/9/10 words, 11 words; blank supplied creation notes remain invalid. | `RideBoundaryIntegrationTest`, existing `NotesTest` |
| Calendar and clock | Century leap-year exceptions (1900/2000/2100), normal leap years, month ends, day/month overflow, 0000/2359/2360/2400 and year-width limits. | `RideDateTimeTest` |
| Ride periods | End one minute before, equal to and one minute after start; an edit must compare against the preserved endpoint. | `RideBoundaryIntegrationTest` |
| Find date ranges | One minute below/equal/above each boundary; date-only midnight/2359; both bounds inclusive; inverted ranges; no match and reset with `all`. | `RideBoundaryIntegrationTest`, existing predicate/parser tests |
| Criteria and identifiers | Missing criteria, blank fields, duplicate prefixes, valid-but-unknown IDs, 0, negative IDs and integer overflow. | `RideBoundaryIntegrationTest`, existing ride parser tests |
| Roster state | Empty/non-empty/full, duplicate/not joined, unknown cyclist/ride, FTP 179/180/181 against minimum 180, valid saved roster at capacity and invalid saved rosters. | `RideBoundaryIntegrationTest`, `RideDataIntegrityTest`, existing roster integration tests |
| Filters and ID lifecycle | Delete a selected roster's ride, add a later ride without reusing the ID, update selected roster live, preserve independent ride filters. | `RideBoundaryIntegrationTest`, existing `RideListIntegrationTest` |
| Rejected-operation state | Preserve complete book, current cyclist list, current ride list and saved bytes for rejected syntax and semantic partitions. | Shared helper in `RideBoundaryIntegrationTest`; 63 field-rejection combinations plus identifier and command-state cases |

Numeric valid-boundary edits are reloaded from JSON. Invalid field partitions run through creation,
editing and finding; invalid period edits correctly use command errors rather than parser errors
when validation needs the existing ride. The boundary suite complements existing unit tests instead
of assuming that parser acceptance alone proves command execution or persistence correctness.

## Deferred minor findings and policy questions

* **Ride-ID padding inconsistency:** `ridelist 01` parses as ride 1, while
  `rideaddcyclist 01 +65 91234567` is rejected. Edit/delete/list use a general positive-integer
  helper while roster parsing uses `[1-9][0-9]*`. Ordinary unpadded IDs work. Standardise this
  accepted format in a later increment.
* **Distance error-message omission:** 999999.99 is accepted and 1000000 is rejected, but the
  distance constraint message and User Guide do not explain the six whole-digit limit.
  The limit is protected by tests; clarify the message/docs or deliberately change the limit later.
* **Year zero policy:** `01-01-0000 0000` is accepted by Java's proleptic calendar. The product
  only specifies a real four-digit date. Decide whether the intended year range starts at 0001
  before treating this as a validation defect.
* **FTP changes after joining:** lowering a cyclist's FTP below an existing ride minimum is
  currently allowed. The guide describes a minimum needed to join, while ride edits protect
  existing participants. Decide whether eligibility must remain true after cyclist edits;
  this audit preserves the current policy rather than silently removing memberships or rejecting edits.

The ID, distance and year-zero observations were checked directly against the rebuilt classes
using JDK 25 JShell. No unrelated behaviour changes were made for these minor/policy items.

## Validation and limitations

Final test counts, coverage and command results are recorded in [Release testing](ReleaseTesting.md).
Review the [Testing guide](Testing.md) and the Developer Guide's **Ride boundary and corrupt-data
regression** scenario for repeatable checks.

Launch the rebuilt JAR with assertions enabled and a disposable `null` data file. Logs confirm a
loading warning followed by successful application/UI initialisation, rather than an uncaught null
exception. This is a startup smoke check, not visual verification. Stop the test process afterwards.

Interactive UI checks remain incomplete: the computer-use tool cannot bind the standalone Java
window, including an attempt using the installed JDK bundle identifier. Card layout, command-box
focus, interactive tab entry and cross-platform behaviour therefore remain unverified manually.
The relevant roster/list/find and corrupt-data manual scenarios are documented for follow-up.
