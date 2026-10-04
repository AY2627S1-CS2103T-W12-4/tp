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

RecruitDex helps recruiters rank applicants through a series of side-by-side comparisons. Instead of placing every applicant in a complete ranking at once, a recruiter chooses which of two applicants is stronger, and RecruitDex updates the ranking as decisions are made. Recruiters can review a chosen number of top-ranked applicants and use the ranking to inform hiring decisions.

The workflow has three distinct parts:

1. **Manage applicant records.** Add applicants and maintain details such as names, contact information, and interview notes. Records can be edited or deleted.
2. **Manage the active comparison list.** Add or remove applicants from the set to be ranked. Adding an applicant record and adding that applicant to the comparison list are separate operations in the system, even if the interface offers them together.
3. **Compare applicants.** Open the comparison view, review the two applicants RecruitDex presents, and choose the stronger one. The ranking updates immediately so the recruiter can see the effect of each decision.

These parts can be revisited as recruitment progresses. An applicant must have a record before joining the comparison list and must join the list before being compared, but recruiters can compare existing applicants before adding more. RecruitDex is designed for command-line input, with a graphical interface primarily for displaying applicants, comparisons, and ranking changes.

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* _{List the sources of reused or adapted ideas, code, documentation, and third-party libraries here, with links to the originals.}_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

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

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* provides its functionality through a concrete `{Component Name}Manager` class that implements the corresponding API interface.

For example, the `Logic` component defines its API in `Logic.java` and implements it in `LogicManager.java`. Other components interact with a component through its interface rather than its concrete class, preventing them from coupling to that component's implementation, as illustrated in the following partial class diagram.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

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

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X), but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>


How the `Logic` component works:

1. When `Logic` is called upon to execute a command, the command is passed to an `AddressBookParser` object, which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above for simplicity, the code can require several interactions between the command object and the `Model` to complete the operation.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name, e.g., `AddCommandParser`). The parser uses the other classes shown above to parse the user command and create an `XYZCommand` object (e.g., `AddCommand`). The `AddressBookParser` returns that object as a `Command` object.
* All `XYZCommandParser` classes, such as `AddCommandParser` and `DeleteCommandParser`, implement the `Parser` interface so they can be treated similarly where appropriate, for example during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the `Person` objects selected by the current filter, such as search results, in a separate _filtered_ list. It exposes this list as an unmodifiable `ObservableList<Person>` that the UI can observe and bind to, so the UI updates when the list changes.
* stores a `UserPrefs` object that represents the user’s preferences (currently, just the GUI settings). This is exposed to the outside as a `ReadOnlyUserPrefs` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


<box type="info" seamless>

**Note:** The alternative, arguably more object-oriented, design below keeps a unique list of tags in `AddressBook`, and each `Person` references tags from that list. This lets `AddressBook` maintain one `Tag` object per unique tag instead of each `Person` holding its own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />
</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

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

* `VersionedAddressBook#commit()` -- Saves the current address book state in its history.
* `VersionedAddressBook#undo()` -- Restores the previous address book state from its history.
* `VersionedAddressBook#redo()` -- Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitAddressBook()`, `Model#undoAddressBook()` and `Model#redoAddressBook()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedAddressBook` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete 5` command to delete the 5th person in the address book. The `delete` command calls `Model#commitAddressBook()`, causing the modified state of the address book after the `delete 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitAddressBook()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitAddressBook()`, so the address book state will not be saved into the `addressBookStateList`.
</box>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoAddressBook()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial AddressBook state, then there are no previous AddressBook states to restore. The `undo` command uses `Model#canUndoAddressBook()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.
</box>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

<puml src="diagrams/UndoSequenceDiagram-Logic.puml" alt="UndoSequenceDiagram-Logic" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X), but due to a limitation of PlantUML, it continues to the end of the diagram.
</box>

Similarly, how an undo operation goes through the `Model` component is shown below:

<puml src="diagrams/UndoSequenceDiagram-Model.puml" alt="UndoSequenceDiagram-Model" />

The `redo` command does the opposite — it calls `Model#redoAddressBook()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone AddressBook states to restore. The `redo` command uses `Model#canRedoAddressBook()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.
</box>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the address book, such as `list`, will usually not call `Model#commitAddressBook()`, `Model#undoAddressBook()` or `Model#redoAddressBook()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitAddressBook()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

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
* **Clearer oversight:** Recruitment managers can inspect the comparisons behind a ranking, require a minimum number of comparisons, and compare recruiters' decisions to identify possible anomalies.


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
| `* * *`  | recruiter                                       | view a chosen number of top-ranked applicants                                                | review a shortlist of the size needed for the next hiring stage                     |
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

(For all use cases below, the **System** is the `AddressBook` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1.  User requests to list persons
2.  AddressBook shows a list of persons
3.  User requests to delete a specific person in the list
4.  AddressBook deletes the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. AddressBook shows an error message.

      Use case resumes at step 2.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `25` or above installed.
2.  Should be able to hold up to 1000 persons without noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, or macOS
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.
</box>

### Launch and shutdown

1. Initial launch

   1. Download the JAR file and copy it into an empty folder.

   1. Double-click the JAR file.<br>
      Expected: The GUI opens with a set of sample contacts. The window size may not be optimal.

1. Saving window preferences

   1. Resize the window to an optimal size. Move the window to a different location. Close the window.

   1. Relaunch the app by double-clicking the JAR file.<br>
       Expected: The most recent window size and location are retained.

1. _{ more test cases … }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command, with multiple persons in the list.

   1. Test case: `delete 1`<br>
      Expected: The first contact is deleted from the list. The status message shows the deleted contact's details.

   1. Test case: `delete 0`<br>
      Expected: No person is deleted. The status message shows error details.

   1. Other incorrect delete commands to try: `delete`, `delete x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases … }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{Explain how to simulate missing or corrupted data files and state the expected behavior.}_

1. _{ more test cases … }_
