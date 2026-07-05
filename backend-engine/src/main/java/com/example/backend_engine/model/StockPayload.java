package com.example.backend_engine.model;

public class StockPayload {
    private String ticker;
    private double currentPrice;
    private double trailingPE;
    private double returnOnEquity;
    private double debtToEquity;
    private double timestamp;

    public StockPayload() {
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getTrailingPE() {
        return trailingPE;
    }

    public void setTrailingPE(double trailingPE) {
        this.trailingPE = trailingPE;
    }

    public double getReturnOnEquity() {
        return returnOnEquity;
    }

    public void setReturnOnEquity(double returnOnEquity) {
        this.returnOnEquity = returnOnEquity;
    }

    public double getDebtToEquity() {
        return debtToEquity;
    }

    public void setDebtToEquity(double debtToEquity) {
        this.debtToEquity = debtToEquity;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}