package seedu.recruitdex.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.recruitdex.testutil.TypicalApplicants.ALICE;
import static seedu.recruitdex.testutil.TypicalApplicants.BENSON;
import static seedu.recruitdex.testutil.TypicalApplicants.getTypicalRecruitDexData;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.recruitdex.logic.commands.FindCommand;
import seedu.recruitdex.logic.parser.exceptions.ParseException;
import seedu.recruitdex.model.Model;
import seedu.recruitdex.model.ModelManager;
import seedu.recruitdex.model.applicant.Applicant;
import seedu.recruitdex.model.userprefs.UserPrefs;

public class FindCommandParserTest {
    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertInvalidFormat("     ");
    }

    @Test
    public void parse_noAttribute_throwsParseException() {
        assertInvalidFormat("");
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertInvalidFormat("Alice -n Alice");
    }

    @Test
    public void parse_duplicatePrefix_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("-n Alice -n Bob"));
    }

    @Test
    public void parse_emptyTagSearch_throwsParseException() {
        assertInvalidFormat("-t     ");
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("-p not-a-phone"));
    }

    @Test
    public void parse_invalidYearsOfExperience_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("-y many"));
    }

    @Test
    public void parse_nameSearch_isCaseInsensitiveAndPartial() throws Exception {
        assertMatchingApplicants("-n aLiCe", ALICE);
    }

    @Test
    public void parse_phoneSearch_matchesPartialNumber() throws Exception {
        assertMatchingApplicants("-p 9435", ALICE);
    }

    @Test
    public void parse_emailSearch_isCaseInsensitiveAndPartial() throws Exception {
        assertMatchingApplicants("-e ALICE@EXAMPLE", ALICE);
    }

    @Test
    public void parse_addressSearch_isCaseInsensitiveAndPartial() throws Exception {
        assertMatchingApplicants("-a JURONG WEST", ALICE);
    }

    @Test
    public void parse_interviewNotesSearch_isCaseInsensitiveAndPartial() throws Exception {
        assertMatchingApplicants("-i STRONG IN JAVA", ALICE);
    }

    @Test
    public void parse_yearsOfExperienceSearch_matchesExactValue() throws Exception {
        assertMatchingApplicants("-y 5", ALICE);
    }

    @Test
    public void parse_sourceSearch_isCaseInsensitiveAndPartial() throws Exception {
        assertMatchingApplicants("-s LINK", ALICE);
    }

    @Test
    public void parse_tagSearch_matchesEveryTermAcrossTags() throws Exception {
        assertMatchingApplicants("-t fri end", ALICE, BENSON);
    }

    @Test
    public void parse_multipleAttributes_requiresAllToMatch() throws Exception {
        assertMatchingApplicants("-n ALICE PAULINE -p 9435 -e alice@example.com "
                        + "-a jurong -i java -y 5 -s linkedin -t fri",
                ALICE);
    }

    @Test
    public void parse_attributeValuesMayContainSpacesAndPrefixesMayBeReordered() throws Exception {
        assertMatchingApplicants("-t fri -a 123, Jurong West Ave 6, #08-111 -n Alice Pauline",
                ALICE);
    }

    private void assertInvalidFormat(String args) {
        assertThrows(ParseException.class, () -> parser.parse(args));
    }

    private void assertMatchingApplicants(String args, Applicant... expectedApplicants) throws Exception {
        FindCommand command = parser.parse(args);
        Model model = new ModelManager(getTypicalRecruitDexData(), new UserPrefs());

        command.execute(model);

        assertEquals(List.of(expectedApplicants), model.getFilteredApplicantList());
    }
}
