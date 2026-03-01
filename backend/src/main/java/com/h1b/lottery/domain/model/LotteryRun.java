package com.h1b.lottery.domain.model;

import com.h1b.lottery.domain.model.enums.LotteryRunStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "lottery_runs")
public class LotteryRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    @Column(name = "regular_cap", nullable = false)
    private Integer regularCap;

    @Column(name = "masters_cap", nullable = false)
    private Integer mastersCap;

    @Column(name = "seed", nullable = false)
    private Long seed;

    @Column(name = "total_submitted", nullable = false)
    private Integer totalSubmitted;

    @Column(name = "selected_regular", nullable = false)
    private Integer selectedRegular;

    @Column(name = "selected_masters", nullable = false)
    private Integer selectedMasters;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LotteryRunStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(Integer fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public Integer getRegularCap() {
        return regularCap;
    }

    public void setRegularCap(Integer regularCap) {
        this.regularCap = regularCap;
    }

    public Integer getMastersCap() {
        return mastersCap;
    }

    public void setMastersCap(Integer mastersCap) {
        this.mastersCap = mastersCap;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public Integer getTotalSubmitted() {
        return totalSubmitted;
    }

    public void setTotalSubmitted(Integer totalSubmitted) {
        this.totalSubmitted = totalSubmitted;
    }

    public Integer getSelectedRegular() {
        return selectedRegular;
    }

    public void setSelectedRegular(Integer selectedRegular) {
        this.selectedRegular = selectedRegular;
    }

    public Integer getSelectedMasters() {
        return selectedMasters;
    }

    public void setSelectedMasters(Integer selectedMasters) {
        this.selectedMasters = selectedMasters;
    }

    public LotteryRunStatus getStatus() {
        return status;
    }

    public void setStatus(LotteryRunStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
