package com.h1b.lottery.web.dto;

public record ExportResponse(
        String filePath,
        long rowCount
) {
}
