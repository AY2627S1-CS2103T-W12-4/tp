package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * Stores applicants under their existing IDs, preserving registration order.
 * Applicants must have distinct IDs and must not be duplicates according to
 * {@link Applicant#isSameApplicant(Applicant)}.
 * IDs belong to the applicants and are not generated or changed by the registry.
 * This registry stores data in memory; persisting the registry requires storage integration.
 */
public class ApplicantRegistry {

    private final Map<ApplicantId, Applicant> applicants = new LinkedHashMap<>();

    /**
     * Creates an empty registry.
     */
    public ApplicantRegistry() {}

    /**
     * Registers {@code applicant} under its existing ID and returns that ID.
     * Validation failures leave existing records unchanged.
     *
     * @param applicant The applicant to register.
     * @return The existing ID of the registered applicant.
     * @throws NullPointerException if {@code applicant} is null.
     * @throws DuplicateApplicantException if an applicant with the same ID or identity is already registered.
     */
    public ApplicantId add(Applicant applicant) {
        requireNonNull(applicant);

        ApplicantId applicantId = applicant.getId();
        if (applicants.containsKey(applicantId)
                || applicants.values().stream().anyMatch(applicant::isSameApplicant)) {
            throw new DuplicateApplicantException();
        }

        applicants.put(applicantId, applicant);
        return applicantId;
    }

    /**
     * Replaces the applicant registered under {@code applicantId} with {@code editedApplicant}.
     * The edited applicant must retain the same ID and must not duplicate another registered applicant.
     * Validation failures leave existing records unchanged.
     *
     * @param applicantId The ID of the applicant to edit.
     * @param editedApplicant The replacement applicant, with the same ID.
     * @throws NullPointerException if either argument is null.
     * @throws ApplicantNotFoundException if {@code applicantId} is not registered.
     * @throws IllegalArgumentException if the edited applicant has a different ID.
     * @throws DuplicateApplicantException if another registered applicant has the same identity.
     */
    public void edit(ApplicantId applicantId, Applicant editedApplicant) {
        requireAllNonNull(applicantId, editedApplicant);

        Applicant target = this.get(applicantId);
        checkArgument(applicantId.equals(editedApplicant.getId()), "Editing an applicant must not change its ID.");

        if (!target.isSameApplicant(editedApplicant)
                && applicants.values().stream().anyMatch(editedApplicant::isSameApplicant)) {
            throw new DuplicateApplicantException();
        }

        applicants.put(applicantId, editedApplicant);
    }

    /**
     * Returns the applicant registered under {@code applicantId}, without changing the registry.
     *
     * @param applicantId The ID of the applicant to retrieve.
     * @return The applicant registered under the given ID.
     * @throws NullPointerException if {@code applicantId} is null.
     * @throws ApplicantNotFoundException if {@code applicantId} is not registered.
     */
    public Applicant get(ApplicantId applicantId) {
        requireNonNull(applicantId);

        Applicant applicant = applicants.get(applicantId);
        if (applicant == null) {
            throw new ApplicantNotFoundException();
        }

        return applicant;
    }
}
