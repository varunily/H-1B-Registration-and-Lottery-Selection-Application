package com.h1b.lottery.repository;

import com.h1b.lottery.domain.model.LotteryRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotteryRunRepository extends JpaRepository<LotteryRun, Long> {
    List<LotteryRun> findByFiscalYearOrderByCreatedAtDesc(Integer fiscalYear);

    long countByFiscalYear(Integer fiscalYear);
}
