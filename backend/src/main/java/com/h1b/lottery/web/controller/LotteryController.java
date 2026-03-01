package com.h1b.lottery.web.controller;

import com.h1b.lottery.service.LotteryService;
import com.h1b.lottery.web.dto.LotteryRunRequest;
import com.h1b.lottery.web.dto.LotteryRunResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/runs")
    @ResponseStatus(HttpStatus.CREATED)
    public LotteryRunResponse run(@Valid @RequestBody LotteryRunRequest request) {
        return lotteryService.runLottery(request);
    }

    @GetMapping("/runs")
    public List<LotteryRunResponse> listRuns(@RequestParam Integer fiscalYear) {
        return lotteryService.listRuns(fiscalYear);
    }
}
