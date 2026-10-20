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

The table separates inputs expected to be accepted from those expected to be rejected.
Validity depends on the operation: empty notes are valid when clearing an edit but invalid when
supplied during creation. A valid find query may return no matches; that is not invalid input.

| Area | Valid partitions and boundary values (accepted) | Invalid partitions and boundary values (rejected) | Evidence |
| --- | --- | --- | --- |
| Country code and subscriber length | Country-code lengths 1, 2 and 3; subscriber lengths 3 and 4. Examples: `+1 123`, `+12 123`, `+123 123`, `+123 1234`. | Country-code lengths 0 and 4; subscriber length 2. Examples: `+ 123`, `+1234 123`, `+123 12`. | `PhoneTest`, `EditCommandParserTest` |
| Distance | Positive numbers within the precision and digit limits: 0.01/0.02 and 999999.98/999999.99. | Non-numeric, negative, zero, excessive precision and excessive whole digits: `NaN`, `1e2`, -0.01, 0, 0.001 and 1000000. | `RideBoundaryIntegrationTest`, existing `DistanceTest` |
| Minimum power | Whole numbers within 1–600: 1/2/599/600. | Below/above the range, fractions and integer overflow: 0/601, 1.5 and 2147483648. | `RideBoundaryIntegrationTest`, existing minimum-power tests |
| Maximum group size | Positive whole numbers up to nine digits: 1/2/999999998/999999999. | Zero, more than nine digits and fractions: 0/1000000000 and 1.5. | `RideBoundaryIntegrationTest`, existing `GroupSizeTest` |
| Notes | Missing optional notes; empty edit to clear notes; 1/9/10 words. | 11 words; blank notes explicitly supplied during creation. | `RideBoundaryIntegrationTest`, existing `NotesTest` |
| Calendar and clock | Real dates, including 29-02-2000 and 29-02-2028; valid month ends; times 0000/2359; four-digit year endpoints 0001/9999. | Non-leap-century dates 29-02-1900 and 29-02-2100; 31 April; day 32; months 00/13; times 2360/2400 and five-digit time; five-digit year 10000. | `RideDateTimeTest` |
| Ride periods | End equal to or one minute after start; edits that remain consistent with the preserved endpoint. | End one minute before start, including edits whose supplied endpoint conflicts with the preserved endpoint. | `RideBoundaryIntegrationTest` |
| Find date ranges | Ordered or equal bounds; date-only defaults of 0000/2359; `all` to reset. For valid queries, rides exactly on the bounds match; those one minute outside do not. Empty results are valid. | Inverted ranges, with `to/` earlier than `from/`. Malformed dates/times are rejected as listed above. | `RideBoundaryIntegrationTest`, existing predicate/parser tests |
| Criteria and identifiers | At least one valid criterion; positive existing ride IDs. The value 2147483647 is syntactically valid but is tested as an unknown ID at execution. | Missing criteria, required blank fields, duplicate prefixes, ID 0, negative IDs and integer overflow. Syntactically valid but unknown IDs are rejected at execution. | `RideBoundaryIntegrationTest`, existing ride parser tests |
| Roster state | Add an eligible cyclist to a roster with space; remove a joined cyclist; saved roster exactly at capacity. FTP 180/181 meets minimum 180; empty and full rosters are valid states. | Duplicate additions, removal of a cyclist who has not joined, unknown cyclist/ride, addition to a full roster and FTP 179 against minimum 180. Saved duplicate members, over-capacity rosters and missing cyclist references are rejected. | `RideBoundaryIntegrationTest`, `RideDataIntegrityTest`, existing roster integration tests |

Two further groups check state transitions rather than classifying individual values:

* **Filters and ID lifecycle:** deleting the selected roster's ride empties its cyclist display;
  a later ride receives a new ID; selected roster changes update the display live; independent
  ride filters are preserved. Evidence: `RideBoundaryIntegrationTest` and existing
  `RideListIntegrationTest`.
* **Rejected-operation state:** invalid syntax and invalid command-state combinations must leave
  the complete book, current cyclist list, current ride list and saved bytes unchanged. Evidence:
  the shared helper in `RideBoundaryIntegrationTest`, used for 63 field-rejection combinations
  plus identifier and command-state cases.

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
