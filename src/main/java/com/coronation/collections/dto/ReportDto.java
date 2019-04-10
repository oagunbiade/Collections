package com.coronation.collections.dto;

/**
 * Created by Toyin on 4/10/19.
 */
public class ReportDto {
    private AmountReport amountReport;
    private CountReport countReport;

    public AmountReport getAmountReport() {
        return amountReport;
    }

    public void setAmountReport(AmountReport amountReport) {
        this.amountReport = amountReport;
    }

    public CountReport getCountReport() {
        return countReport;
    }

    public void setCountReport(CountReport countReport) {
        this.countReport = countReport;
    }
}
