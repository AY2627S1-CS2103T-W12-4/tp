package seedu.recruitdex.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.commands.SwitchTabCommand.Tab;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

/** Parses the tab name for {@code switch}, ignoring case and surrounding whitespace. */
public class SwitchTabCommandParser implements Parser<SwitchTabCommand> {
    @Override
    public SwitchTabCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return switch (args.trim().toLowerCase(Locale.ROOT)) {
            case "compare" -> new SwitchTabCommand(Tab.COMPARE);
            case "applicants" -> new SwitchTabCommand(Tab.APPLICANTS);
            default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SwitchTabCommand.MESSAGE_USAGE));
        };
    }
}
