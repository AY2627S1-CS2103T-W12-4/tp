package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.CompareCommand;
import seedu.address.logic.commands.CompareCommand.Side;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a single comparison side, accepting lowercase and uppercase labels. */
public class CompareCommandParser implements Parser<CompareCommand> {
    @Override
    public CompareCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return switch (args.trim().toLowerCase(Locale.ROOT)) {
            case "a" -> new CompareCommand(Side.A);
            case "b" -> new CompareCommand(Side.B);
            default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    CompareCommand.MESSAGE_USAGE));
        };
    }
}
