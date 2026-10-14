package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;

import seedu.address.logic.commands.ComparisonCommand;
import seedu.address.logic.commands.ComparisonCommand.Side;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a single comparison side, accepting lowercase and uppercase labels. */
public class ComparisonCommandParser implements Parser<ComparisonCommand> {
    @Override
    public ComparisonCommand parse(String args) throws ParseException {
        requireNonNull(args);
        return switch (args.trim().toLowerCase(Locale.ROOT)) {
            case "a" -> new ComparisonCommand(Side.A);
            case "b" -> new ComparisonCommand(Side.B);
            default -> throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ComparisonCommand.MESSAGE_USAGE));
        };
    }
}
