package seedu.recruitdex.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.recruitdex.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.recruitdex.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.EMPTY_ADDRESS_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INTERVIEW_NOTES_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INTERVIEW_NOTES_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.INVALID_YEARS_OF_EXPERIENCE_DESC;
import static seedu.recruitdex.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.recruitdex.logic.commands.CommandTestUtil.SOURCE_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.SOURCE_DESC_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.recruitdex.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_INTERVIEW_NOTES_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_SOURCE_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.recruitdex.logic.commands.CommandTestUtil.VALID_YEARS_OF_EXPERIENCE_BOB;
import static seedu.recruitdex.logic.commands.CommandTestUtil.YEARS_OF_EXPERIENCE_DESC_AMY;
import static seedu.recruitdex.logic.commands.CommandTestUtil.YEARS_OF_EXPERIENCE_DESC_BOB;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_INTERVIEW_NOTES;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_SOURCE;
import static seedu.recruitdex.logic.parser.CliSyntax.PREFIX_YEARS_OF_EXPERIENCE;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.recruitdex.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.recruitdex.testutil.TypicalApplicants.AMY;
import static seedu.recruitdex.testutil.TypicalApplicants.BOB;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.Messages;
import seedu.recruitdex.logic.commands.AddCommand;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.applicant.Email;
import seedu.recruitdex.model.applicant.Name;
import seedu.recruitdex.model.applicant.Phone;
import seedu.recruitdex.model.applicant.YearsOfExperience;
import seedu.recruitdex.model.tag.Tag;
import seedu.recruitdex.testutil.ApplicantBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedApplicant));


        // multiple tags - all accepted
        Applicant expectedApplicantMultipleTags = new ApplicantBuilder(BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedApplicantMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedApplicantString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedApplicantString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // invalid or empty value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // empty address
        assertParseFailure(parser, EMPTY_ADDRESS_DESC + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid or empty value

        // invalid name
        assertParseFailure(parser, validExpectedApplicantString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedApplicantString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedApplicantString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // empty address
        assertParseFailure(parser, validExpectedApplicantString + EMPTY_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Applicant expectedApplicant = new ApplicantBuilder(AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY,
                new AddCommand(expectedApplicant));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                expectedMessage);

        // all optional fields without a name
        assertParseFailure(parser, PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INTERVIEW_NOTES_DESC_BOB + YEARS_OF_EXPERIENCE_DESC_BOB + SOURCE_DESC_BOB + TAG_DESC_FRIEND,
                expectedMessage);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, " -n   ", Name.MESSAGE_CONSTRAINTS);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB + VALID_ADDRESS_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Email.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + INVALID_PHONE_DESC,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_allOptionalFieldsPresent_success() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withInterviewNotes(VALID_INTERVIEW_NOTES_BOB)
                .withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB).withSource(VALID_SOURCE_BOB)
                .withTags(VALID_TAG_FRIEND).build();
        String requiredFields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB;
        String optionalFields = INTERVIEW_NOTES_DESC_BOB + YEARS_OF_EXPERIENCE_DESC_BOB + SOURCE_DESC_BOB
                + TAG_DESC_FRIEND;

        assertParseSuccess(parser, requiredFields + optionalFields, new AddCommand(expectedApplicant));

        // optional fields before the required fields
        assertParseSuccess(parser, optionalFields + requiredFields, new AddCommand(expectedApplicant));

        // optional fields in a different order
        assertParseSuccess(parser, requiredFields + SOURCE_DESC_BOB + TAG_DESC_FRIEND + YEARS_OF_EXPERIENCE_DESC_BOB
                + INTERVIEW_NOTES_DESC_BOB, new AddCommand(expectedApplicant));
    }

    @Test
    public void parse_eachOptionalFieldOnly_success() {
        String requiredFields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB;

        assertParseSuccess(parser, requiredFields + INTERVIEW_NOTES_DESC_BOB,
                new AddCommand(new ApplicantBuilder(BOB).withTags().withInterviewNotes(VALID_INTERVIEW_NOTES_BOB)
                        .build()));
        assertParseSuccess(parser, requiredFields + YEARS_OF_EXPERIENCE_DESC_BOB,
                new AddCommand(new ApplicantBuilder(BOB).withTags()
                        .withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB).build()));
        assertParseSuccess(parser, requiredFields + SOURCE_DESC_BOB,
                new AddCommand(new ApplicantBuilder(BOB).withTags().withSource(VALID_SOURCE_BOB).build()));
    }

    @Test
    public void parse_optionalFieldsAbsent_usesEmptyDefaults() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withPhone("").withEmail("").withAddress("")
                .withTags().build();
        assertParseSuccess(parser, NAME_DESC_BOB, new AddCommand(expectedApplicant));
        assertEquals("", expectedApplicant.getPhone().value);
        assertEquals("", expectedApplicant.getEmail().value);
        assertEquals("", expectedApplicant.getAddress().value);
        assertEquals("", expectedApplicant.getInterviewNotes().value);
        assertEquals("", expectedApplicant.getYearsOfExperience().value);
        assertEquals("", expectedApplicant.getSource().value);
    }

    @Test
    public void parse_optionalFieldsEmpty_success() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withPhone("").withEmail("").withAddress("")
                .withTags().build();
        String emptyOptionalFields = " -p   -e   -a   -i   -y   -s   ";
        assertParseSuccess(parser, NAME_DESC_BOB + emptyOptionalFields, new AddCommand(expectedApplicant));
    }

    @Test
    public void parse_optionalFieldsWithExtraWhitespace_trimmed() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withTags().withInterviewNotes("Strong in Java")
                .withYearsOfExperience("7").withSource("LinkedIn").build();
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + " -i   Strong in Java    -y  7   -s    LinkedIn   ", new AddCommand(expectedApplicant));
    }

    @Test
    public void parse_contactFieldsOmittedWithOtherDetails_success() {
        Applicant expectedApplicant = new ApplicantBuilder(BOB).withPhone("").withEmail("").withAddress("")
                .withInterviewNotes(VALID_INTERVIEW_NOTES_BOB).withYearsOfExperience(VALID_YEARS_OF_EXPERIENCE_BOB)
                .withSource(VALID_SOURCE_BOB).withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, NAME_DESC_BOB + INTERVIEW_NOTES_DESC_BOB + YEARS_OF_EXPERIENCE_DESC_BOB
                + SOURCE_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedApplicant));
    }

    @Test
    public void parse_contactFieldsIndependentlyOptional_success() {
        // All combinations of omitted and supplied contact fields.
        for (int fields = 0; fields < 8; fields++) {
            boolean hasPhone = (fields & 1) != 0;
            boolean hasEmail = (fields & 2) != 0;
            boolean hasAddress = (fields & 4) != 0;
            Applicant expectedApplicant = new ApplicantBuilder(BOB)
                    .withPhone(hasPhone ? VALID_PHONE_BOB : "")
                    .withEmail(hasEmail ? VALID_EMAIL_BOB : "")
                    .withAddress(hasAddress ? VALID_ADDRESS_BOB : "").withTags().build();
            assertParseSuccess(parser, NAME_DESC_BOB + (hasPhone ? PHONE_DESC_BOB : "")
                    + (hasEmail ? EMAIL_DESC_BOB : "") + (hasAddress ? ADDRESS_DESC_BOB : ""),
                    new AddCommand(expectedApplicant));
        }
    }

    @Test
    public void parse_invalidOptionalValueWithOtherFieldsOmitted_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_YEARS_OF_EXPERIENCE_DESC,
                YearsOfExperience.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedContactFieldWithOtherFieldsOmitted_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + PHONE_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + EMAIL_DESC_AMY,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
        assertParseFailure(parser, NAME_DESC_BOB + ADDRESS_DESC_BOB + EMPTY_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_repeatedOptionalValue_failure() {
        String validExpectedApplicantString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // multiple interview notes
        assertParseFailure(parser, INTERVIEW_NOTES_DESC_AMY + INTERVIEW_NOTES_DESC_BOB
                + validExpectedApplicantString, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INTERVIEW_NOTES));

        // multiple years of experience
        assertParseFailure(parser, YEARS_OF_EXPERIENCE_DESC_AMY + YEARS_OF_EXPERIENCE_DESC_BOB
                + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_YEARS_OF_EXPERIENCE));

        // multiple sources
        assertParseFailure(parser, SOURCE_DESC_AMY + SOURCE_DESC_BOB + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SOURCE));

        // multiple of every optional field
        assertParseFailure(parser, INTERVIEW_NOTES_DESC_AMY + YEARS_OF_EXPERIENCE_DESC_AMY + SOURCE_DESC_AMY
                + INTERVIEW_NOTES_DESC_BOB + YEARS_OF_EXPERIENCE_DESC_BOB + SOURCE_DESC_BOB
                + validExpectedApplicantString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INTERVIEW_NOTES, PREFIX_YEARS_OF_EXPERIENCE,
                        PREFIX_SOURCE));
    }

    @Test
    public void parse_invalidYearsOfExperience_failure() {
        String requiredFields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB;

        assertParseFailure(parser, requiredFields + INVALID_YEARS_OF_EXPERIENCE_DESC,
                YearsOfExperience.MESSAGE_CONSTRAINTS);

        String[] invalidYears = {"-1", "1.5", "abc", "5 5", "007", "100"};
        for (String invalidYear : invalidYears) {
            assertParseFailure(parser, requiredFields + " " + PREFIX_YEARS_OF_EXPERIENCE + invalidYear,
                    YearsOfExperience.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_validYearsOfExperienceBoundaries_success() {
        String requiredFields = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB;
        String[] validYears = {"0", "1", "9", "10", "59", "60"};
        for (String validYear : validYears) {
            Applicant expectedApplicant = new ApplicantBuilder(BOB).withTags().withYearsOfExperience(validYear)
                    .build();
            assertParseSuccess(parser, requiredFields + " " + PREFIX_YEARS_OF_EXPERIENCE + validYear,
                    new AddCommand(expectedApplicant));
        }
    }

    @Test
    public void parse_invalidRequiredFieldWithValidOptionalFields_failure() {
        // the invalid required field is reported even when the optional fields are fine
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INTERVIEW_NOTES_DESC_BOB + YEARS_OF_EXPERIENCE_DESC_BOB + SOURCE_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);
    }
}
