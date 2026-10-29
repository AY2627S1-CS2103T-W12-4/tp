---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# RecruitDex Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Overview**

RecruitDex helps recruiters rank applicants through a series of side-by-side comparisons. Instead of placing every applicant in a complete ranking at once, a recruiter chooses which of two applicants is stronger, and RecruitDex updates the ranking as decisions are made. Recruiters can review the full ranking of applicants in the active comparison list and use it to inform hiring decisions.

The workflow has three distinct parts:

1. **Manage applicant records.** Add applicants and maintain details such as names, contact information, and interview notes. Records can be edited or deleted.
2. **Manage the active comparison list.** Use `compare-add` or `compare-remove` to change the set to be ranked. Creating an applicant record with `add` is a separate operation.
3. **Compare applicants.** Open the comparison view, review the two applicants RecruitDex presents, and choose the stronger one. The ranking updates immediately so the recruiter can see the effect of each decision.

These parts can be revisited as recruitment progresses. An applicant must have a record before joining the comparison list and must join the list before being compared, but recruiters can compare existing applicants before adding more. RecruitDex is designed for command-line input, with a graphical interface primarily for displaying applicants, comparisons, and ranking changes.

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

RecruitDex is adapted from [AddressBook Level 3](https://se-education.org/addressbook-level3/). It uses JavaFX for the desktop interface, Jackson for JSON persistence, and JUnit for tests. Dependency versions are declared in `build.gradle`.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

This section describes the current implementation. Recruitment rules belong in Model; Logic parses commands and calls model operations. The diagrams show selected classes and interactions rather than every implementation detail.

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

The following provides a quick overview of the main components and their interactions.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/Main.java) and [`MainApp`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows a successful `delete 1`. Logic resolves the displayed index and asks the applicant registry to delete the record; Model also cleans up comparison references and refreshes the ranking. Logic then saves the application data before returning success.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` and its parts, including `CommandBox`, `ResultDisplay`, `ApplicantListPanel` (with `ApplicantCard`s), `ComparisonPanel` (with two `ComparisonCard`s), `RankingPanel`, `HelpWindow` and `StatusBarFooter`. These parts inherit from `UiPart`, which captures common behavior among visible GUI parts.

The main window has two tabs. The **Compare** tab shows the `ComparisonPanel` (applicants A and B) beside the `RankingPanel`, and the **Applicants** tab shows the `ApplicantListPanel`, whose index numbers are the `INDEX` that commands such as `edit` and `delete` use. `HelpWindow` is a separate window that lists every command in a table. It opens when the user types `help`, presses `F1` or clicks **Help** in the menu bar. See [Help window command table](#help-window-command-table) for how its rows are produced.

The `sw` command switches between the two tabs. `SwitchTabCommand` does not change the model; it returns a `CommandResult` whose `isSwitchTab()` is true. `MainWindow` checks this flag after each command, in the same way as `isShowHelp()` and `isExit()`, selects the other tab and shows its name in the result display.

The `UI` component uses JavaFX, with layouts in `.fxml` files under `src/main/resources/view`. For example, [`MainWindow.fxml`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/resources/view/MainWindow.fxml) specifies the layout of [`MainWindow`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/ui/MainWindow.java). Some controls, such as help-table rows and comparison detail rows, are built in Java.

The `UI` component,

* executes user commands using the `Logic` component.
* observes model data exposed through `Logic`, including the filtered applicant list, full applicant list, ranking and current-pair property.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* displays model objects such as `Applicant` and `RankingEntry`. The leaderboard observes `Logic.getRanking()` and resolves current applicant names from `Logic.getApplicantList()`.

The leaderboard always includes the full active comparison list. It resolves command indices from the filtered applicant list, separately from ranking positions. `ComparisonPanel` observes the pending pair, applicant records and ranking, resolving the A/B cards by stable applicant ID. Missing optional details are hidden. Card ranks count assessed active members, including those hidden by a search; applicants with no eligible comparisons show Unassessed instead of a rank. The leaderboard uses separate assessed and unassessed lists.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates command parsing and deletion for a successful `execute("delete 1")` call. It shows how the displayed index is resolved to an applicant ID before calling `ApplicantRegistry.delete`. Snapshotting, saving, and model-internal cleanup are omitted; see [Atomic saving](#atomic-saving) for the persistence behavior.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

`LogicManager.execute` snapshots application data and the current filter before running a parsed command. It saves afterwards only if the application data differs from the snapshot. A save failure restores the saved-data snapshot and filter before reporting an error. Commands that leave saved data unchanged, including `find`, `list`, `help`, `sw` and `exit`, skip saving. See [Atomic saving](#atomic-saving).

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to a `RecruitDexParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete an applicant).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `RecruitDexParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `RecruitDexParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

Commands using an `INDEX` resolve it against `Model.getFilteredApplicantList()` and then operate on the applicant's stable ID. Comparison commands preserve the filter; successful `add` and `edit` reset it to show all applicants. `find` combines supplied attribute predicates with AND. It uses the same field parsers as `add` and `edit`, so, for example, non-empty email search values must be valid email addresses. Skill search terms are case-insensitive substrings and all terms must match some skill.

For `add`, only `-nNAME` is required. `AddCommandParser` supplies empty defaults for omitted phone, email, address, interview notes, years of experience, and source, and an empty set for omitted skills. `Phone`, `Email`, and `Address` accept empty strings to represent missing details while retaining validation for non-empty values. Explicitly empty prefixes also produce empty values, except that `add` requires each supplied `-t` to contain a valid skill. Duplicate single-valued prefixes remain invalid.

These empty values are stored in the existing JSON fields and restored on load. Applicant and comparison cards hide missing details. The shared value parsers also allow `edit` to clear contact details by supplying an empty prefix.

### Model component
**API** : [`Model.java`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="650" />

This structural overview omits value objects, applicant-ID references, read-only data interfaces, the deletion callback and filtered-list relations. `ComparisonList` uses the same `RankingEngine` owned by `ModelManager`. The callback and filtering behavior are described below.

The `Model` component,

* groups saved data in `RecruitDexData`: an `ApplicantRegistry` and a `ComparisonList`. Commands access these live domain objects through `Model` getters instead of forwarding every operation through `ModelManager`.
* holds active member IDs, ordered decisions and `ComparisonState` (an optional pending pair) in `ComparisonList`. That object coordinates ranking recalculation and pair selection.
* owns a runtime `RankingEngine` in `ModelManager`, shared with the data object's `ComparisonList`. Rankings and filters are derived runtime state, not persisted data.
* maintains a `FilteredList<Applicant>` backed by the registry's observable list for search results. It exposes an unmodifiable observable view so the UI updates with changes to records or the filter.
* stores GUI preferences in `UserPrefs`, accessed through `Model.getUserPrefs()` and saved separately.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

`Applicant` is immutable and has an `ApplicantId` backed by a UUID. An edit replaces the record while retaining its ID and registration position. Membership, decisions and pending pairs reference IDs, so changing details does not break comparisons. `Applicant.equals` compares details without the ID; use `getId()` for identity. The registry rejects duplicate IDs and records with exactly equal, case-sensitive names.

`RecruitDexData` connects registry deletion to `ComparisonList.deleteApplicantReferences`, so deleting through the registry also removes membership and decisions involving the applicant and replaces an affected pair. Removing membership alone retains the record and history; decisions count again if both participants become active later.

`RecruitDexData.resetData` validates replacement records and comparison references before restoring the existing components in place. Observable views and the current-pair property are retained, so UI bindings and the filtered list stay connected after clear or rollback. Collections exposed for observation are unmodifiable. The pending state is exposed as `ReadOnlyComparisonState`; its mutators are package-private.

### Storage component

**API** : [`Storage.java`](https://github.com/AY2627S1-CS2103T-W12-4/tp/tree/master/src/main/java/seedu/recruitdex/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* saves applicant records, comparison membership, decision history, the pending pair and user preferences in JSON format. Rankings are derived from the decisions, so they are rebuilt after loading and not saved.
* writes JSON through a temporary file and requests atomic replacement, falling back to ordinary replacement if atomic moves are unsupported. See [Atomic saving](#atomic-saving).
* is implemented by `StorageManager`, which delegates JSON file access to `JsonRecruitDexDataStorage` and `JsonUserPrefsStorage` (one class per data file).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

`MainApp` uses `data/recruitdex.json` for application data and `preferences.json` for GUI settings, relative to the process working directory. Preferences are written during initialization and shutdown. A missing data file supplies sample applicant records with no comparison members; an unreadable or invalid data file supplies empty application data and logs a warning. Startup attempts to save successfully loaded or sample data, including a newly selected pending pair. If loading fails, it leaves the invalid file untouched, attempts a timestamped backup and displays a recovery message. See [Atomic saving](#atomic-saving).

JSON adapters validate field values, unique IDs, decision structure and references to existing records. A pending pair must contain two distinct active members. A valid saved pair retains its A/B order; if the pair is absent and at least two members are active, restoration selects one without recording a decision. Files without comparison data load with an empty comparison list. Legacy records without IDs receive new IDs; missing interview notes, experience and source default to empty strings. Name, phone, email and address JSON fields must still be present, with empty strings allowed for the optional contact fields.

### Common classes

Classes used by multiple components are in the `seedu.recruitdex.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Applicant ranking

`RankingEntry` is an immutable result containing an applicant ID, a positive position, an optional finite score, and a non-negative comparison count. Equality and hashing use all four fields.

`RankingEngine.recalculate(activeMembers, decisions)` uses a regularized Bradley-Terry model with Gamma priors of shape 2 and rate 1.

* All active applicants appear. Unassessed applicants have strength 1 and score 0.
* Decisions contribute when both participants are active. Repeated decisions with distinct IDs have equal weight.
* Scores are natural logarithms of strengths, rounded to six decimal places. Assessed applicants precede unassessed applicants. Within each group, equal scores share positions such as `1, 1, 3`; applicant IDs order tied rows. Unassessed positions and initial scores are internal values and are not displayed as assessments; use `RankingEntry.isAssessed()` before displaying a rank.
* The engine publishes completed results through a live, unmodifiable observable list. Invalid input or fitting failure preserves the previous ranking.

`ModelManager` owns the shared engine. `Logic.getRanking()` exposes its view to `RankingPanel`, which refreshes names from the full applicant list independently of search filtering. UI-bound observable updates must run on the JavaFX application thread.

`ComparisonList` recalculates rankings after membership or decision changes and asks `PairSelector` for a new pair when needed. Adding a member preserves a valid pending pair. Removing or deleting a participant replaces the affected pair, or clears it if fewer than two members remain. `recordDecision` validates the winner, recalculates the ranking with a new UUID-identified, timestamped decision, appends it to history, and selects the next pair.

`PairSelector` uses weighted randomness, favouring applicants with fewer eligible comparisons and partners with close scores. It penalizes repeated pairings and excludes the most recently answered eligible pair when more than two applicants are active. With two members, it reuses that pair. A/B ordering balances previous side appearances, breaking ties randomly. Only decisions with both participants active affect selection statistics.

Members, decisions and the unanswered pair are saved in `data/recruitdex.json` through `JsonAdaptedComparisonList`, and restored at launch, so the ranking is rebuilt from the saved decisions. `RecruitDexData#resetData` checks the incoming comparison data against the new applicant records before replacing the current data.

### Atomic saving

To avoid truncating existing data while writing JSON, saving serializes to a temporary file before replacing the destination. Storage creates the destination and parent directories first if they are missing.

`FileUtil.writeToFileAtomically(file, content)` does the following:

1. It writes `content` to a temporary file next to `file`, named after it with a `.tmp` ending.
1. It requests `Files.move` with `ATOMIC_MOVE`. If unsupported, it retries with `REPLACE_EXISTING`; this fallback does not guarantee atomicity.
1. On a write or move failure it attempts to delete the temporary file and rethrows the error. Cleanup is best-effort, so a temporary file can remain.

`JsonUtil.saveJsonFile` uses it, so both the data file and the preferences file are saved this way.

A save can also fail after the command has already changed the model, for example when the disk is full. `LogicManager.execute` copies the application data before the command runs (`new RecruitDexData(model.getRecruitDexData())`) and separately retains the filter predicate. If saving throws an `IOException`, it restores both and throws a `CommandException`. `AccessDeniedException` gets a specific permissions message. Commands that leave application data unchanged bypass saving, so viewing, searching, help, tab switching and exit remain available. This rollback is automatic error recovery, not a user-facing undo feature.

`LogicManager.execute` saves only when the command changed the data, that is, when `model.getRecruitDexData()` no longer equals the copy. Commands such as `list`, `find`, `help`, `sw` and `exit` therefore never save. They keep working while saving fails, and they cannot overwrite a data file that failed to load. `RecruitDexData` equality covers every saved field except applicant IDs, which `Applicant#equals` ignores; no command changes only an ID.

At startup, `MainApp` treats the data file as follows:

* If the file loads, or is missing and the sample data is used, `MainApp` saves the loaded data once. This creates the file for new users, and records any pending pair chosen while loading.
* If the file cannot be loaded, `MainApp` starts with empty data and does not save. It calls `Storage#backUpRecruitDexDataFile()`, which copies the file to `recruitdex.invalid-yyyyMMdd-HHmmss.json` in the same folder without replacing an existing file. `UiManager` then shows a message in the result display naming the backup, or asking the user to copy the file if the backup failed. The user's first change replaces the invalid file.

**Design considerations:**

**Aspect: How to write the file.**

* **Alternative 1 (current choice):** Write a temporary file, then replace the data file with it.
  * Pros: Avoids writing JSON directly over the data file and provides atomic replacement on supported file systems. There is still only one human-editable data file.
  * Cons: The data folder must be writable, not just the file. A stale `.tmp` file can be left behind after a crash, but the next save overwrites it.
* **Alternative 2:** Overwrite the data file in place.
  * Pros: Simplest to write.
  * Cons: A crash during the write leaves a truncated file, and the app then starts with no data.
* **Alternative 3:** Write a new file for every command and keep them all.
  * Pros: Gives a history of the data.
  * Cons: Files pile up, the app must work out which is current, and the project requires a single human-editable data file.

**Aspect: What to do when a save fails.**

* **Alternative 1 (current choice):** Restore the data from before the command.
  * Pros: A failed save restores the previous application data and filter in memory.
  * Cons: Every command pays for one copy of the data, which is small for this application.
* **Alternative 2:** Keep the change in memory and only show an error.
  * Pros: No copy needed.
  * Cons: The user sees a change that the file does not have, and loses it when the app closes.

**Aspect: Which commands save.**

* **Alternative 1 (current choice):** Save when the data after the command differs from the copy taken before it.
  * Pros: New commands are handled correctly without extra work. Commands that end up changing nothing, such as an `edit` with the same values, also skip saving.
  * Cons: Every command compares the data once, which is cheap for this application. The comparison must cover every saved field.
* **Alternative 2:** Mark each command, or its result, as changing data or not.
  * Pros: Explicit, and no comparison is needed.
  * Cons: A developer who forgets the mark on a new read-only command makes it fail during save failures and overwrite an unloadable file.
* **Alternative 3:** Save after every command.
  * Pros: Simplest.
  * Cons: `list` or `find` can replace a data file that failed to load with an empty one, and fail whenever saving fails.

### Help window command table

The help window shows every command with its format and an example, and must not drift out of date when a command changes. The rows are therefore built from the usage text that each command already has.

* Each command class has a `MESSAGE_USAGE` starting with `word: description`, followed by `Parameters: ...` where applicable and `Example: ...`. These sections can span lines. Commands that take no parameters leave out `Parameters:`.
* `CommandInfo.fromUsage(usage)` splits that text into four parts: the command word, the description, the parameters and the example.
* `CommandSummary` lists the usage text of all commands in three groups (Applicants, Comparison and General) and exposes them as `CommandInfo` rows.
* `HelpWindow` creates a header row, then a heading and one row of four labels for each command, in a `GridPane` inside a `ScrollPane`.

`CommandSummaryTest` scans the `seedu.recruitdex.logic.commands` package for every concrete `Command` and fails if one has no entry in the summary. A developer who adds a command must therefore also add it to `CommandSummary`, and give it a `MESSAGE_USAGE` in the format above.

**Design considerations:**

**Aspect: Where the help text comes from.**

* **Alternative 1 (current choice):** Parse each command's existing `MESSAGE_USAGE`.
  * Pros: No second copy of the help text, so the formats shown cannot differ from the ones used in error messages. A test catches a forgotten command.
  * Cons: The usage text must keep its format, otherwise `CommandInfo.fromUsage` rejects it.
* **Alternative 2:** Write the help text by hand in the window.
  * Pros: Free-form layout.
  * Cons: It goes out of date every time a command changes.

### Command history and current scope

`CommandBox` uses `CommandHistoryList` to keep submitted commands, including failed ones, for the current session. Up and Down browse the history and restore the draft when moving past the newest entry. Successful commands clear the input; failed commands remain editable. This history is not persisted and does not undo data changes.

There are no undo, redo, import, export, or archive commands. The requirements backlog below includes future ideas; see the [User Guide command summary](UserGuide.md#command-summary) for the supported commands.

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

* recruits for software engineering roles and manages many applicants
* needs to compare applicants and explain how a shortlist was reached
* prefers desktop apps over other types of applications
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**:

RecruitDex helps recruiters make more systematic, reviewable hiring decisions by building a ranking from individual applicant comparisons.

* **Easier decisions:** Choosing between two applicants at a time is simpler than ordering the entire applicant pool in one sitting. Recruiters can make progress over multiple sessions and see the ranking evolve.
* **Less impact from a mistaken judgment:** One comparison need not permanently fix an applicant's position; further comparisons can provide more evidence and change the ranking.
* **Visible assessment progress:** Recruiters can see each active applicant's score and comparison count. Inspecting individual decisions, enforcing minimum counts and comparing different recruiters' judgments remain backlog ideas.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`.

These stories describe the requirements backlog, including ideas considered for future development; they do not indicate which features are currently implemented. High-priority stories support the core applicant-management and comparison workflow. Medium-priority stories improve convenience or reviewability, while low-priority stories are optional extensions.

An applicant record stores an applicant's details. The active comparison list is the set of applicants currently being ranked; removing an applicant from this list does not delete their record.

| Priority | As a …                                          | I can …                                                                                      | So that I can …                                                                     |
|----------|-------------------------------------------------|----------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|
| `* * *`  | new recruiter using RecruitDex                  | view command usage instructions                                                              | learn the commands and refer to them when I forget the syntax                       |
| `* * *`  | recruiter                                       | add an applicant record with their name and contact information                              | keep the applicant's details available throughout recruitment                       |
| `* * *`  | recruiter                                       | list all applicant records                                                                   | review the applicants stored in RecruitDex                                          |
| `* * *`  | recruiter                                       | view an applicant's details                                                                  | review the information I need to assess or contact them                             |
| `* * *`  | recruiter                                       | edit an applicant's details                                                                  | keep their record accurate as new information becomes available                     |
| `* * *`  | recruiter                                       | record and update interview notes for an applicant                                           | retain evidence to inform later comparisons                                         |
| `* * *`  | recruiter                                       | delete an applicant record                                                                   | remove records that should no longer be retained                                    |
| `* * *`  | recruiter with many applicants                  | find an applicant by name                                                                    | retrieve their record without scanning the entire list                              |
| `* * *`  | recruiter                                       | add an applicant with an existing record to the active comparison list                       | include them in the ranking when I am ready to assess them                          |
| `* * *`  | recruiter                                       | view the active comparison list                                                              | check which applicants are included in the ranking                                  |
| `* * *`  | recruiter                                       | remove an applicant from the active comparison list while retaining their record             | exclude them from the current ranking and keep their details for future reference   |
| `* * *`  | recruiter                                       | view the two applicants presented for comparison side by side                                | assess their relative strengths without switching between records                   |
| `* * *`  | recruiter                                       | choose the stronger applicant in a presented pair                                            | contribute a decision to the applicant ranking                                      |
| `* * *`  | recruiter                                       | see the updated ranking immediately after each comparison                                    | understand how my latest decision affects the applicants' positions                 |
| `* * *`  | recruiter                                       | view the full ranking of applicants in the active comparison list                            | review their relative positions to inform hiring decisions                          |
| `* * *`  | recruiter                                       | retain applicant records, comparison-list membership, and ranking progress between sessions  | continue recruitment without re-entering data or repeating completed work           |
| `* *`    | new recruiter using RecruitDex                  | view sample applicant records                                                                | understand how applicant information is organised before entering real data         |
| `* *`    | new recruiter using RecruitDex                  | clear the sample data                                                                        | begin recruitment with my own applicant records                                     |
| `* *`    | recruiter                                       | be warned when adding a duplicate applicant record                                           | avoid accidentally recording the same applicant more than once                      |
| `* *`    | recruiter                                       | record an applicant's skills                                                                 | keep relevant qualifications available when assessing them                          |
| `* *`    | recruiter with many applicants                  | filter applicants by their recorded skills                                                   | focus on applicants with skills relevant to the role                                |
| `* *`    | recruiter                                       | undo an accidental deletion of an applicant record                                           | recover information that I still need                                               |
| `* *`    | recruiter                                       | export the ranked applicant list                                                             | share the ranking with hiring managers for review                                   |
| `* *`    | recruiter                                       | record each applicant's recruitment source, such as LinkedIn or the company website          | track which channels supplied the applicants                                        |
| `* *`    | recruiter                                       | record and sort applicants by years of experience                                            | identify applicants whose experience matches the role's seniority                   |
| `* *`    | recruiter                                       | copy an applicant's contact information                                                      | use it quickly in another communication tool                                        |
| `* *`    | recruiter                                       | hide applicants' private contact details from the display                                    | reduce accidental exposure when showing the ranking to others                       |
| `* *`    | recruitment manager                             | review the recorded applicant comparisons behind a ranking                                   | understand the decisions that led to a shortlist                                    |
| `* *`    | recruitment manager                             | view how many comparisons each applicant has participated in                                 | identify applicants whose positions need more supporting evidence                   |
| `* *`    | recruitment manager                             | specify a minimum number of comparisons per applicant and see which applicants fall below it | request further assessment before relying on the shortlist                          |
| `*`      | recruiter hiring through other platforms        | import applicant records from a file                                                         | reduce manual entry when transferring existing applicant information                |
| `*`      | recruiter with many applicants                  | sort applicant records alphabetically by name                                                | locate a record while browsing the applicant list                                   |
| `*`      | recruiter                                       | save my preferred skills as reusable filter criteria                                         | focus on relevant applicants without re-entering the same criteria each session     |
| `*`      | recruiter hiring for software engineering roles | view a summary of an applicant's GitHub activity                                             | consider their coding activity alongside interview notes without leaving RecruitDex |
| `*`      | recruitment manager                             | compare decisions made by different recruiters about the same applicants                     | identify disagreements or possible anomalies that need further review               |

### Use cases

For all use cases below, the **System** is RecruitDex and the **Actor** is a recruiter. **MSS** means Main Success Scenario. These use cases describe the implemented workflow. Indices always refer to the displayed applicant list, including after a search.

<puml src="diagrams/UseCaseDiagram.puml" alt="UseCaseDiagram" />

The diagram above summarizes the six recruiter use cases described below. Their extensions are detailed in the individual use case descriptions.

**UC01: Add an applicant record**

**MSS**

1. Recruiter enters `add`, providing a name and any optional details.
2. RecruitDex validates the supplied details.
3. RecruitDex stores the applicant record and displays a success message with the added details.

   Use case ends. The applicant is available for inclusion in the active comparison list through UC02.

**Extensions**

* 2a. Required details are missing or a supplied value is invalid.
    * 2a1. RecruitDex explains the error without adding a record.

      Use case resumes at step 1.

* 2b. The supplied name exactly matches an existing applicant record's name.
    * 2b1. RecruitDex warns the recruiter about the duplicate without adding another record.

      Use case ends.

**UC02: Add an existing applicant to the active comparison list**

**MSS**

1. Recruiter requests to list applicant records.
2. RecruitDex displays the applicant records.
3. Recruiter requests to add a selected applicant to the active comparison list.
4. RecruitDex adds the applicant and displays the updated comparison list.

   Use case ends.

**Extensions**

* 2a. No applicant records exist.
    * 2a1. RecruitDex indicates that there are no applicant records to display.

      Use case ends. The recruiter can create a record through UC01.

* 3a. The selection does not identify an existing applicant record.
    * 3a1. RecruitDex explains the error without changing the comparison list.

      Use case resumes at step 3.

* 3b. The selected applicant is already in the active comparison list.
    * 3b1. RecruitDex informs the recruiter that the applicant is already included, leaving the list unchanged.

      Use case resumes at step 3.

**UC03: Remove an applicant from the active comparison list**

**MSS**

1. Recruiter enters `list` to obtain applicant indices and reviews the ranking in the Compare tab.
2. RecruitDex displays the applicant records and the active ranking.
3. Recruiter enters `compare-remove INDEX` using the applicant index, not their rank.
4. RecruitDex removes the applicant from the active comparison list and displays the ranking of the remaining applicants.

   Use case ends. The applicant's record and comparison history remain available. Decisions involving an inactive applicant are excluded from the current ranking.

**Extensions**

* 2a. The active comparison list is empty.
    * 2a1. RecruitDex indicates that there are no applicants in the comparison list.

      Use case ends.

* 3a. The selection does not identify an applicant in the active comparison list.
    * 3a1. RecruitDex explains the error without changing the list or applicant records.

      Use case resumes at step 3.

**UC04: Compare two applicants and update the ranking**

**MSS**

1. Recruiter requests to open the comparison view.
2. RecruitDex presents two applicants from the active comparison list side by side, including their details and interview notes.
3. Recruiter reviews the applicants and submits a choice of the stronger applicant.
4. RecruitDex records the decision, updates the ranking, and immediately displays the updated ranking.

   Use case ends. The recruiter can repeat this use case to make further comparisons.

**Extensions**

* 2a. Fewer than two applicants are in the active comparison list.
    * 2a1. RecruitDex explains that at least two applicants are required for a comparison.

      Use case ends. The recruiter can add applicants to the list through UC02.

* 3a. The submitted choice does not identify either applicant in the presented pair.
    * 3a1. RecruitDex explains the error without recording a decision or changing the ranking.

      Use case resumes at step 3 with the same pair.

**UC05: Review the full applicant ranking**

**MSS**

1. Recruiter views the Compare tab, using `sw` to switch tabs if needed.
2. RecruitDex displays all applicants in the active comparison list in ranking order, starting with the highest-ranked applicant.
3. Recruiter reviews positions, scores and comparison counts. To inspect a particular record, the recruiter uses `find` and views the Applicants tab.

   Use case ends.

**Extensions**

* 2a. The active comparison list is empty.
    * 2a1. RecruitDex indicates that there are no applicants to rank.

      Use case ends.

**UC06: Delete an applicant record**

**MSS**

1. Recruiter requests to list applicant records.
2. RecruitDex displays the applicant records.
3. Recruiter requests to delete a selected applicant record.
4. RecruitDex deletes the record and displays a success message identifying the deleted applicant.

   Use case ends.

**Extensions**

* 2a. No applicant records exist.
    * 2a1. RecruitDex indicates that there are no applicant records to display.

      Use case ends.

* 3a. The selection does not identify an existing applicant record.
    * 3a1. RecruitDex explains the error without deleting any records.

      Use case resumes at step 3.

* 4a. The applicant is in the active comparison list.
    * 4a1. RecruitDex also removes the applicant from the comparison list and updates the ranking to include only the remaining applicants.

      Use case ends. Unlike UC03, the applicant's record is deleted.

### Non-Functional Requirements

These are quality targets, not a record of completed performance, compatibility or usability testing. In particular, the reference machine and benchmark results have not been specified here.

**Performance**

1. On the project’s reference test machine, adding, editing, deleting, or searching applicant records must complete within 2 seconds when the data file contains up to 1,000 applicant records.
2. On the reference test machine, recalculating the ranking must complete within 10 seconds for up to 200 active applicants and 19,900 decisions.
3. After a comparison is submitted, the updated ranking must be available to the user within 2 seconds for a comparison list of up to 200 applicants.

**Reliability and data integrity**

1. Invalid commands or rejected operations must display an error message without terminating the application.
2. A rejected operation must leave applicant records, comparison-list membership, decision history, the pending comparison pair, and ranking unchanged.
3. If saving fails, RecruitDex must report the error and preserve the last successfully saved data file.
4. After a successful save and subsequent restart, RecruitDex must restore applicant records, comparison-list membership, ordered decision history, the pending comparison pair, and user preferences.
5. After loading saved data, RecruitDex must recalculate the ranking from the restored membership and decision history. The resulting ranking must match the ranking produced from the same data before saving.

**Usability**

1. The `help` command must show the syntax and a usage example for every supported command.
2. When a command is rejected, RecruitDex must identify the reason and, where applicable, show the expected command format.
3. In a usability test with five people unfamiliar with RecruitDex, at least four must be able to add an applicant, add the applicant to the comparison list, submit a comparison, and view the ranking within 10 minutes using the in-app help and without assistance.

**Compatibility and installation**

1. RecruitDex must run on Windows, macOS, and Linux with Java 25 installed.
2. Users must be able to launch RecruitDex without running an installer.

**Operational constraints**

1. Applicant management, comparison, and ranking features must work without an internet connection.
2. RecruitDex is a single-user desktop application. Concurrent use of the same data file by multiple running instances is not supported.

### Glossary

- **Active Comparison List:** The set of applicant records currently included in the ranking process. Removing an applicant from this list does not delete their applicant record.

- **Applicant Record:** The stored information associated with an applicant, including their name, contact information, and interview notes.

- **API (Application Programming Interface):** An interface that defines how other components can interact with a component without needing to know its internal implementation.

- **Command:** An operation requested by the user through the command-line interface, such as adding, deleting, or comparing applicants.

- **Command Parser:** A component that interprets a user's command input and converts it into the corresponding Command object.

- **Command-Line Interface (CLI):** The text-based interface through which users enter commands to interact with RecruitDex.

- **Command Result:** An object representing the outcome of executing a command. It is returned by the Logic component after a command has been processed.

- **Comparison:** A decision made by a recruiter between two applicants presented by RecruitDex. The selected applicant is considered stronger for that comparison.

- **Comparison View:** The part of the user interface that presents two applicants side by side for the recruiter to compare.

- **Component:** A major architectural part of RecruitDex. The main components are UI, Logic, Model, and Storage, with Commons containing classes shared across components.

- **Filtered List:** A list maintained by the Model containing applicants that satisfy the current filtering criteria. The UI observes this list to update the displayed applicants.

- **JavaFX:** The Java framework used by RecruitDex to implement its graphical user interface.

- **JSON (JavaScript Object Notation):** A text-based data format used by RecruitDex to store applicant data and user preferences.

- **Logic:** The component responsible for interpreting and executing user commands.

- **Mainstream OS:** Windows, Linux, Unix, or macOS

- **Main Success Scenario (MSS):** The normal sequence of steps in a use case where the user achieves the intended outcome without encountering an error or exception.

- **Model:** The component responsible for maintaining RecruitDex's application data in memory, including applicant records and user preferences.

- **Ranking:** The ordered list of applicants in the active comparison list, based on the outcomes of comparisons.

- **RecruitDex:** The application that helps recruiters manage applicants and produce a ranking through pairwise comparisons.

- **Storage:** The component responsible for saving RecruitDex's data to persistent storage and loading it when the application starts.

- **Use Case (UC):** A description of how a user interacts with RecruitDex to accomplish a particular goal, including the normal sequence of actions and possible extensions or errors.

- **User Interface (UI):** The component responsible for displaying RecruitDex's graphical interface and receiving user commands.

- **User Preferences:** Settings associated with the user, such as GUI-related settings, that are stored separately from applicant data.

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Use a separate writable folder and a copy of the JAR so that testing does not affect real applicant data. Run commands from that folder using `java -jar recruitdex.jar` with Java 25. These checks are starting points for exploratory testing.

### Launch and shutdown

1. Launch in a folder without `data/recruitdex.json`. Expect sample applicants, the Compare tab, and `0 applicants in comparison list`. Startup creates the data file with sample records when saving succeeds.
2. Resize and move the window, close it, and relaunch from the same folder. Expect the saved window size and position.
3. Enter `help` or press `F1`. Expect all commands grouped under Applicants, Comparison and General, with formats and examples. Copy an example and check the clipboard. Press `Esc` to close Help.
4. Enter `sw` twice. Expect Applicants then Compare, with feedback naming the visible tab. Enter `sw 1`; expect an invalid-format error and no tab change.

### Applicant records and search

1. Enter `add -n Test One`. Expect a record with only a name, all applicants displayed and comparison membership unchanged. Repeat the command; expect a duplicate error and no extra record.
2. Enter `find -n Test One`. Expect the matching applicant as index 1. Enter `edit 1 -p 91234567 -y 5 -t Java -t Kafka`. Expect updated details and all applicants displayed.
3. Enter `find -t java kaf -y 5`. Expect Test One among the results. Enter `find -e example.com`; expect email validation to reject it. Enter `find -e t1@example.com`; expect only records whose email contains that valid address.
4. Find Test One again and enter `edit 1 -p -y -t`. Expect the phone, experience and skills cleared. Name remains required: `edit 1 -n` is rejected.
5. Enter `delete 0`, `delete`, and an index larger than the displayed list. Expect errors with no deletion. Failed commands remain editable in the command box. Up and Down should recall submitted commands; history should reset on restart.

### Comparisons, ranking and persistence

1. Start with at least three records and enter `list`. Enter `compare-add 1` and `compare-add 2`. Expect two applicants in the Unassessed group and an A/B pair. Enter `compare-add 1` again; expect a duplicate-membership error.
2. Enter `compare-add 3`. Expect a third unassessed applicant and the same pending pair. Enter `choose a`. Expect the displayed A applicant to win, both participants to move into the assessed ranking with updated comparison counts, and a new pair. The remaining applicant stays unassessed without a displayed rank or score. With three active applicants the immediately preceding pair should not repeat.
3. Enter `choose c` and `choose a extra`. Expect errors without a new decision or pair change. Enter `find -n Test One`; expect the ranking and pending pair to remain unchanged while command indices reflect the filtered list.
4. Close and reopen the app. Expect the same membership, decisions, ranking and pending A/B sides; the search filter resets. Further choices may change the A/B sides.
5. Enter `list`, identify a member's applicant index and enter `compare-remove INDEX`. Expect the record to remain, its ranking entry to disappear, and an affected pair to be replaced or cleared. Remove it again; expect an error. Add it back; expect retained decisions to count again when both participants are active.
6. Delete a member using its displayed applicant index. Expect its record, membership and decisions involving it to be removed. An affected pair is replaced or cleared. Enter `clear`; expect no records, members, decisions, pair or ranking, including after restart.

### Saving and recovery

1. Close the app and move the test data file aside. Relaunch; expect sample applicants, empty membership and a new `data/recruitdex.json` created at startup.
2. Close the app, back up the test file, remove its last `}` and relaunch. Expect an empty applicant list and a recovery message naming a timestamped `recruitdex.invalid-DATE-TIME.json` backup (or requesting a manual copy if backup fails). Verify that a successful backup matches the invalid file. Enter `list`, `find -n Alex` and `help`; expect the invalid file to remain unchanged. A successful `add` replaces it while retaining the backup. Close the app and restore the valid test backup before continuing.
3. Enter `add -n Test Saved -e saved@example.com`, then inspect the data folder. Expect valid JSON containing the applicant and no `.tmp` file after a successful save.
4. To test save failure, first save valid test data, close the app and deny write access to the test data folder using the operating system's permission controls. On macOS/Linux, `chmod a-w data` can be used when running as a non-privileged user; the Windows folder read-only checkbox does not reliably deny writes.
5. Relaunch, note the displayed records and pending pair, then enter `add -n Test Unsaved`. Expect a save error, no added record and restoration of the prior data and filter. `list`, `find -n Test`, `help`, `sw` and `exit` should still work without a save error.
6. Restore write permissions (for example `chmod u+w data` on macOS/Linux) and verify that the file lacks Test Unsaved. Repeat with `choose a` to check that a failed save restores the decision history, ranking and pending pair. Restore permissions after testing.
