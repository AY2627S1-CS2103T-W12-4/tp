# RecruitDex

[![CI Status](https://github.com/AY2627S1-CS2103T-W12-4/tp/workflows/Java%20CI/badge.svg)](https://github.com/AY2627S1-CS2103T-W12-4/tp/actions)

![RecruitDex UI mockup](docs/images/Ui.png)

RecruitDex is a desktop application for software engineering recruiters to manage applicant information and build rankings through side-by-side comparisons. It is designed for recruiters who prefer typing commands, with a graphical interface for reviewing applicant details, comparisons, and ranking changes.

Choosing between two applicants at a time makes it easier to assess a large applicant pool. RecruitDex aims to help recruiters build a ranking over multiple sessions and use those decisions to inform a shortlist.

## Recruitment workflow

The intended workflow has three parts:

1. **Manage applicant records.** Add, find, edit, and delete applicants, and maintain contact information and interview notes.
2. **Manage the active comparison list.** Choose which applicants to include in the current ranking. Removing an applicant from this list retains their record for future reference.
3. **Compare and rank applicants.** Review two applicants side by side, choose the stronger one, and see the ranking update after each decision. Review the full ranking to inform hiring decisions.

An applicant must have a record before joining the comparison list and must be in that list before being compared. Recruiters can revisit each part of the workflow as recruitment progresses.

## Documentation

Visit the [RecruitDex documentation website](https://AY2627S1-CS2103T-W12-4.github.io/tp/) or go directly to a guide below.

| Guide | Contents |
| --- | --- |
| [User Guide](https://AY2627S1-CS2103T-W12-4.github.io/tp/UserGuide.html) | Commands and usage instructions |
| [Developer Guide](https://AY2627S1-CS2103T-W12-4.github.io/tp/DeveloperGuide.html) | Product scope, recruitment workflow, architecture, and requirements |
| [Setting Up](https://AY2627S1-CS2103T-W12-4.github.io/tp/SettingUp.html) | Development environment and coding conventions |
| [Testing](https://AY2627S1-CS2103T-W12-4.github.io/tp/Testing.html) | Running tests and understanding test coverage |
| [About Us](https://AY2627S1-CS2103T-W12-4.github.io/tp/AboutUs.html) | Project team |

## Acknowledgements

RecruitDex is based on [AddressBook Level 3](https://github.com/se-edu/addressbook-level3), developed by the [se-education.org](https://se-education.org) initiative. It uses JavaFX for the interface, Jackson for JSON storage, and JUnit 5 for testing.

## License

This project is licensed under the [MIT License](LICENSE).
