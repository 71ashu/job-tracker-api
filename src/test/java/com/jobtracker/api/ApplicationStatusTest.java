package com.jobtracker.api;

import com.jobtracker.api.model.ApplicationStatus;
import org.junit.jupiter.api.Test;

import static com.jobtracker.api.model.ApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationStatusTest {

    @Test
    void appliedCanTransitionToScreening() {
        assertTrue(APPLIED.canTransitionTo(SCREENING));
    }

    @Test
    void appliedCanTransitionToInterview() {
        assertTrue(APPLIED.canTransitionTo(INTERVIEW));
    }

    @Test
    void appliedCanTransitionToOffer() {
        assertTrue(APPLIED.canTransitionTo(OFFER));
    }

    @Test
    void appliedCanTransitionToRejected() {
        assertTrue(APPLIED.canTransitionTo(REJECTED));
    }

    @Test
    void appliedCanTransitionToWithdrawn() {
        assertTrue(APPLIED.canTransitionTo(WITHDRAWN));
    }

    @Test
    void screeningCanTransitionToInterview() {
        assertTrue(SCREENING.canTransitionTo(INTERVIEW));
    }

    @Test
    void screeningCanTransitionToRejected() {
        assertTrue(SCREENING.canTransitionTo(REJECTED));
    }

    @Test
    void screeningCanTransitionToWithdrawn() {
        assertTrue(SCREENING.canTransitionTo(WITHDRAWN));
    }

    @Test
    void screeningCannotTransitionToApplied() {
        assertFalse(SCREENING.canTransitionTo(APPLIED));
    }

    @Test
    void interviewCanTransitionToOffer() {
        assertTrue(INTERVIEW.canTransitionTo(OFFER));
    }

    @Test
    void interviewCanTransitionToRejected() {
        assertTrue(INTERVIEW.canTransitionTo(REJECTED));
    }

    @Test
    void interviewCanTransitionToWithdrawn() {
        assertTrue(INTERVIEW.canTransitionTo(WITHDRAWN));
    }

    @Test
    void interviewCannotTransitionToApplied() {
        assertFalse(INTERVIEW.canTransitionTo(APPLIED));
    }

    @Test
    void interviewCannotTransitionToScreening() {
        assertFalse(INTERVIEW.canTransitionTo(SCREENING));
    }

    @Test
    void offerCanTransitionToWithdrawn() {
        assertTrue(OFFER.canTransitionTo(WITHDRAWN));
    }

    @Test
    void offerCannotTransitionToApplied() {
        assertFalse(OFFER.canTransitionTo(APPLIED));
    }

    @Test
    void offerCannotTransitionToRejected() {
        assertFalse(OFFER.canTransitionTo(REJECTED));
    }

    @Test
    void rejectedIsTerminal() {
        for (ApplicationStatus s : ApplicationStatus.values()) {
            assertFalse(REJECTED.canTransitionTo(s));
        }
    }

    @Test
    void withdrawnIsTerminal() {
        for (ApplicationStatus s : ApplicationStatus.values()) {
            assertFalse(WITHDRAWN.canTransitionTo(s));
        }
    }
}
