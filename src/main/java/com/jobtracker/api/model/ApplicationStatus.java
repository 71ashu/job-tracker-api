package com.jobtracker.api.model;

import java.util.Set;

public enum ApplicationStatus {
    APPLIED {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of(SCREENING, INTERVIEW, OFFER, REJECTED, WITHDRAWN);
        }
    },
    SCREENING {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of(INTERVIEW, REJECTED, WITHDRAWN);
        }
    },
    INTERVIEW {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of(OFFER, REJECTED, WITHDRAWN);
        }
    },
    OFFER {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of(WITHDRAWN);
        }
    },
    REJECTED {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of();
        }
    },
    WITHDRAWN {
        @Override
        public Set<ApplicationStatus> validTransitions() {
            return Set.of();
        }
    };

    public abstract Set<ApplicationStatus> validTransitions();

    public boolean canTransitionTo(ApplicationStatus next) {
        return validTransitions().contains(next);
    }
}
