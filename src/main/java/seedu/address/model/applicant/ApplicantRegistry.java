package seedu.address.model.applicant;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import seedu.address.model.applicant.exceptions.ApplicantNotFoundException;
import seedu.address.model.applicant.exceptions.DuplicateApplicantException;

/**
 * Stores applicants under registry-generated IDs, preserving registration order.
 * Applicants are considered duplicates according to {@link Applicant#isSameApplicant(Applicant)}.
 * IDs belong to the registry and remain associated with their records for its lifetime.
 * This registry stores data in memory; persisting IDs and records requires storage integration.
 */
public class ApplicantRegistry {

    private final Map<UUID, Applicant> applicants = new LinkedHashMap<>();

    /**
     * Creates an empty registry.
     */
    public ApplicantRegistry() {}

    /**
     * Registers {@code applicant} and returns its newly generated ID.
     * Validation failures leave existing records unchanged.
     *
     * @param applicant The applicant to register.
     * @return The ID assigned to the registered applicant.
     * @throws NullPointerException if {@code applicant} is null.
     * @throws DuplicateApplicantException if an applicant with the same identity is already registered.
     */
    public UUID add(Applicant applicant) {
        requireNonNull(applicant);

        if (applicants.values().stream().anyMatch(applicant::isSameApplicant)) {
            throw new DuplicateApplicantException();
        }

        UUID applicantId = UUID.randomUUID();
        while (applicants.containsKey(applicantId)) {
            applicantId = UUID.randomUUID();
        }

        applicants.put(applicantId, applicant);
        return applicantId;
    }

    /**
     * Returns the applicant registered under {@code applicantId}, without changing the registry.
     *
     * @param applicantId The ID of the applicant to retrieve.
     * @return The applicant registered under the given ID.
     * @throws NullPointerException if {@code applicantId} is null.
     * @throws ApplicantNotFoundException if {@code applicantId} is not registered.
     */
    public Applicant get(UUID applicantId) {
        requireNonNull(applicantId);

        Applicant applicant = applicants.get(applicantId);
        if (applicant == null) {
            throw new ApplicantNotFoundException();
        }

        return applicant;
    }
}
