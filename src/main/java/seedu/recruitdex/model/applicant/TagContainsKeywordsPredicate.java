package seedu.recruitdex.model.applicant;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.recruitdex.commons.util.ToStringBuilder;
import seedu.recruitdex.model.tag.Tag;

/**
 * Tests that every keyword is contained in at least one of an applicant's tags.
 * Matching is case-insensitive and allows partial tag-name matches.
 */
public class TagContainsKeywordsPredicate implements Predicate<Applicant> {
    private final List<String> keywords;

    /**
     * Creates a predicate that checks whether an applicant has a matching tag for every keyword.
     *
     * @param keywords The non-empty tag search terms.
     */
    public TagContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = List.copyOf(keywords);
    }

    @Override
    public boolean test(Applicant applicant) {
        return keywords.stream().allMatch(keyword -> applicant.getTags().stream()
                .map(Tag::toString)
                .map(String::toLowerCase)
                .anyMatch(tagName -> tagName.contains(keyword.toLowerCase())));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof TagContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return keywords.equals(otherPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}
