package com.booksphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for report information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {

    /**
     * The title of the report.
     */
    private String title;

    /**
     * The subtitle of the report (often includes date range).
     */
    private String subtitle;

    /**
     * The data rows for the report.
     * Each map represents a row with column name to value mapping.
     */
    private List<Map<String, String>> data = new ArrayList<>();

    /**
     * Summary statistics for the report.
     * Map of statistic name to value.
     */
    private Map<String, String> summary = new HashMap<>();

    /**
     * Add a data row to the report.
     * 
     * @param row The data row to add
     */
    public void addDataRow(Map<String, String> row) {
        this.data.add(row);
    }

    /**
     * Add a summary statistic to the report.
     * 
     * @param name The name of the statistic
     * @param value The value of the statistic
     */
    public void addSummary(String name, String value) {
        this.summary.put(name, value);
    }
}
