# Personal rules

If `AGENTS.personal.md` exists in the same directory as this file, read and follow it for additional personal rules.

# Project context

This project is migrating from the existing AddressBook Level 3 (AB3) app to RecruitDex. Much of the existing code is still in a transition state and may reflect the original AB3 app rather than the intended RecruitDex product.

The [Developer Guide](docs/DeveloperGuide.md) is up to date with the current code base. Use its **Overview**, **Design**, and **Implementation** sections as references for RecruitDex behavior and architecture. The requirements backlog includes future ideas and is labelled separately from implemented behavior.

The [User Guide](docs/UserGuide.md) describes the commands and behavior available to users. Keep both guides in sync when changing the application.

The project mainly lives in **Model**, with **UI** serving as the user-facing outlet, **Storage** serving as the persistent file storage outlet, and **Logic** serving as the command interpretation/execution layer between UI and Model. Logic is a misleading name because it actually only deals with commands; recruitment rules and behavior belong in Model.

The current model structure is described in the [Developer Guide's Model component section](docs/DeveloperGuide.md#model-component). [Models.md](Models.md) records the earlier model design proposal; verify it against the current guide and code before relying on it.
