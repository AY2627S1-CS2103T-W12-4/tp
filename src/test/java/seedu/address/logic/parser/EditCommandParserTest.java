package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INTERVIEW_NOTES_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.INTERVIEW_NOTES_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_YEARS_OF_EXPERIENCE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.SOURCE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.SOURCE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_INTERVIEW_NOTES_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOURCE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SOURCE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.YEARS_OF_EXPERIENCE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.YEARS_OF_EXPERIENCE_DESC_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPLICANT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPLICANT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_APPLICANT;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditApplicantDescriptor;
import seedu.address.model.applicant.Address;
import seedu.address.model.applicant.Email;
import seedu.address.model.applicant.Name;
import seedu.address.model.applicant.Phone;
import seedu.address.model.applicant.YearsOfExperience;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditApplicantDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 x/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Applicant} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_APPLICANT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_APPLICANT;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_APPLICANT;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditApplicantDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditApplicantDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditApplicantDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditApplicantDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_APPLICANT;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_APPLICANT;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_optionalFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_APPLICANT;
        String userInput = targetIndex.getOneBased() + SOURCE_DESC_BOB + INTERVIEW_NOTES_DESC_AMY
                + YEARS_OF_EXPERIENCE_DESC_BOB;

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder()
                .withInterviewNotes(VALID_INTERVIEW_NOTES_AMY).withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB)
                .withSource(VALID_SOURCE_BOB).build();

        assertParseSuccess(parser, userInput, new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_oneOptionalFieldSpecified_success() {
        Index targetIndex = INDEX_THIRD_APPLICANT;

        // interview notes
        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder()
                .withInterviewNotes(VALID_INTERVIEW_NOTES_AMY).build();
        assertParseSuccess(parser, targetIndex.getOneBased() + INTERVIEW_NOTES_DESC_AMY,
                new EditCommand(targetIndex, descriptor));

        // years of experience
        descriptor = new EditApplicantDescriptorBuilder().withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_AMY)
                .build();
        assertParseSuccess(parser, targetIndex.getOneBased() + YEARS_OF_EXPERIENCE_DESC_AMY,
                new EditCommand(targetIndex, descriptor));

        // source
        descriptor = new EditApplicantDescriptorBuilder().withSource(VALID_SOURCE_AMY).build();
        assertParseSuccess(parser, targetIndex.getOneBased() + SOURCE_DESC_AMY,
                new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_clearOptionalFields_success() {
        // an empty value is allowed, and clears the field
        Index targetIndex = INDEX_FIRST_APPLICANT;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_INTERVIEW_NOTES + " "
                + PREFIX_YEARS_OF_EXPERIENCE + " " + PREFIX_SOURCE;

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withInterviewNotes("")
                .withYearsOfExperience("").withSource("").build();

        assertParseSuccess(parser, userInput, new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_optionalFieldsWithExtraWhitespace_trimmed() {
        Index targetIndex = INDEX_FIRST_APPLICANT;
        String userInput = targetIndex.getOneBased() + " i/   Strong in Java    y/  7   s/    LinkedIn   ";

        EditApplicantDescriptor descriptor = new EditApplicantDescriptorBuilder().withInterviewNotes("Strong in Java")
                .withYearsOfExperience("7").withSource("LinkedIn").build();

        assertParseSuccess(parser, userInput, new EditCommand(targetIndex, descriptor));
    }

    @Test
    public void parse_invalidYearsOfExperience_failure() {
        assertParseFailure(parser, "1" + INVALID_YEARS_OF_EXPERIENCE_DESC, YearsOfExperience.MESSAGE_CONSTRAINTS);

        String[] invalidYears = {"-1", "1.5", "abc", "5 5", "007", "100"};
        for (String invalidYear : invalidYears) {
            assertParseFailure(parser, "1 " + PREFIX_YEARS_OF_EXPERIENCE + invalidYear,
                    YearsOfExperience.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedOptionalFields_failure() {
        Index targetIndex = INDEX_FIRST_APPLICANT;

        assertParseFailure(parser, targetIndex.getOneBased() + INTERVIEW_NOTES_DESC_AMY + INTERVIEW_NOTES_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INTERVIEW_NOTES));

        assertParseFailure(parser, targetIndex.getOneBased() + YEARS_OF_EXPERIENCE_DESC_AMY
                + YEARS_OF_EXPERIENCE_DESC_BOB, Messages.getErrorMessageForDuplicatePrefixes(
                PREFIX_YEARS_OF_EXPERIENCE));

        assertParseFailure(parser, targetIndex.getOneBased() + SOURCE_DESC_AMY + SOURCE_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SOURCE));

        // repeating a field takes priority over its value being invalid
        assertParseFailure(parser, targetIndex.getOneBased() + INVALID_YEARS_OF_EXPERIENCE_DESC
                + YEARS_OF_EXPERIENCE_DESC_BOB, Messages.getErrorMessageForDuplicatePrefixes(
                PREFIX_YEARS_OF_EXPERIENCE));
    }
}
