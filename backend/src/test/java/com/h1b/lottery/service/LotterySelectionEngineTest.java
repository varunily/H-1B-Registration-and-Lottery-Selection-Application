package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Beneficiary;
import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.domain.model.enums.EducationLevel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class LotterySelectionEngineTest {

    private final LotterySelectionEngine engine = new LotterySelectionEngine();

    @Test
    void shouldSelectFromMastersPoolFirst() {
        List<Registration> submitted = List.of(
                registration(1L, EducationLevel.MASTERS),
                registration(2L, EducationLevel.PHD),
                registration(3L, EducationLevel.BACHELORS),
                registration(4L, EducationLevel.BACHELORS)
        );

        LotterySelectionEngine.SelectionResult result = engine.run(submitted, 1, 2, new Random(42));

        assertThat(result.mastersSelected()).hasSize(1);
        assertThat(result.regularSelected()).hasSize(2);
        assertThat(result.mastersSelected().get(0).getBeneficiary().isMastersCapEligible()).isTrue();
        assertThat(result.allSelectedIds()).hasSize(3);
    }

    @Test
    void shouldBeDeterministicWithSameSeed() {
        List<Registration> submitted = new ArrayList<>();
        for (long id = 1; id <= 30; id++) {
            EducationLevel level = id % 3 == 0 ? EducationLevel.MASTERS : EducationLevel.BACHELORS;
            submitted.add(registration(id, level));
        }

        LotterySelectionEngine.SelectionResult first = engine.run(submitted, 4, 10, new Random(7));
        LotterySelectionEngine.SelectionResult second = engine.run(submitted, 4, 10, new Random(7));

        assertThat(first.allSelectedIds()).containsExactlyInAnyOrderElementsOf(second.allSelectedIds());
    }

    private Registration registration(Long id, EducationLevel educationLevel) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setHighestEducation(educationLevel);

        Registration registration = new Registration();
        registration.setId(id);
        registration.setBeneficiary(beneficiary);
        return registration;
    }
}
