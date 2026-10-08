# Personal rules

If `AGENTS.personal.md` exists in the same directory as this file, read and follow it for additional personal rules.

# Project context

This project is migrating from the existing AddressBook Level 3 (AB3) app to RecruitDex. Much of the existing code is still in a transition state and may reflect the original AB3 app rather than the intended RecruitDex product.

The **Overview** section of [the Developer Guide](docs/DeveloperGuide.md#overview) is up to date and represents the vision for the final product. Use it as the reference for intended RecruitDex behavior.

Other sections of the Developer Guide, including **Design**, are outdated. Verify their descriptions against the current code before relying on them, and do not treat them as authoritative descriptions of the final product.

The project mainly lives in **Model**, with **UI** serving as the user-facing outlet, **Storage** serving as the persistent file storage outlet, and **Logic** serving as the command interpretation/execution layer between UI and Model. Logic is a misleading name because it actually only deals with commands; recruitment rules and behavior belong in Model.

The intended model structure can be found in [Models.md](Models.md), while the old AB3 model structure can be found in the [Developer Guide's Model component section](docs/DeveloperGuide.md#model-component).
