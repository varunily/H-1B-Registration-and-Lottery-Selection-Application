package com.h1b.lottery.service;

import com.h1b.lottery.domain.model.Registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LotterySelectionEngine {

    public SelectionResult run(List<Registration> submitted,
                               int mastersCap,
                               int regularCap,
                               Random random) {
        List<Registration> mastersPool = submitted.stream()
                .filter(registration -> registration.getBeneficiary().isMastersCapEligible())
                .toList();

        List<Registration> mastersSelected = pickRandom(mastersPool, mastersCap, random);
        Set<Long> mastersSelectedIds = mastersSelected.stream()
                .map(Registration::getId)
                .collect(java.util.stream.Collectors.toSet());

        List<Registration> regularPool = submitted.stream()
                .filter(registration -> !mastersSelectedIds.contains(registration.getId()))
                .toList();

        List<Registration> regularSelected = pickRandom(regularPool, regularCap, random);
        return new SelectionResult(mastersSelected, regularSelected);
    }

    private List<Registration> pickRandom(List<Registration> pool, int count, Random random) {
        if (count <= 0 || pool.isEmpty()) {
            return List.of();
        }

        if (pool.size() <= count) {
            return pool;
        }

        List<Registration> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, count);
    }

    public record SelectionResult(
            List<Registration> mastersSelected,
            List<Registration> regularSelected
    ) {
        public Set<Long> allSelectedIds() {
            Set<Long> ids = new HashSet<>();
            ids.addAll(mastersSelected.stream().map(Registration::getId).toList());
            ids.addAll(regularSelected.stream().map(Registration::getId).toList());
            return ids;
        }
    }
}
