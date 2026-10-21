package seedu.recruitdex.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.recruitdex.logic.commands.SwitchTabCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;

/** Parses input for {@code sw}, which takes no arguments. */
public class SwitchTabCommandParser implements Parser<SwitchTabCommand> {
    @Override
    public SwitchTabCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SwitchTabCommand.MESSAGE_USAGE));
        }
        return new SwitchTabCommand();
    }
}
