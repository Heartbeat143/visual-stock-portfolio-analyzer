
package com.crio.warmup.stock.dto;

public class AnnualizedReturn {

  private final String symbol;
  private final Double annualized;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualized = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualized;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }
}
