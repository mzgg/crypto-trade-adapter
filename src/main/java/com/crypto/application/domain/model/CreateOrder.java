package com.crypto.application.domain.model;

import com.crypto.application.port.command.CreateOrderCommand;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CreateOrder {

    private String symbol;

    private BigDecimal amount;

    private BigDecimal symbolPrice;

    private Double balance;

    private BigDecimal takeProfitPrice;

    private BigDecimal stopLossPrice;

    public static CreateOrder of(CreateOrderCommand command) {
        CreateOrder createOrder = new CreateOrder();
        createOrder.setSymbol(command.getSymbol());
        createOrder.setBalance(command.getInitialBaseBalance());
        createOrder.setSymbolPrice(BigDecimal.valueOf(command.getCurrentSymbolPrice()));
        return createOrder;
    }



    /**
     * According to the given price, grade-1 times multiplies the price by two.
     * For example price=10, grade=5 → 10 * 2⁴ = 160
     */
    public void calculateFinalOrderBalance(int grade) {
        double currentPrice = this.balance;
        for (int i = 0; i < grade - 1; i++) {
            currentPrice *= 2;
        }
        this.balance = currentPrice;
    }




    public void calculateProfitPrice(double profitRatio, int pricePrecision) {
        BigDecimal profitMultiplier = BigDecimal.valueOf(profitRatio)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
        this.takeProfitPrice = symbolPrice.multiply(profitMultiplier).setScale(pricePrecision, RoundingMode.HALF_UP);
    }

    public void calculateStopLossPrice(double stopLossRatio, int pricePrecision) {
        BigDecimal ratio = BigDecimal.valueOf(stopLossRatio)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.FLOOR);
        BigDecimal multiplier = BigDecimal.ONE.subtract(ratio);
        this.stopLossPrice = symbolPrice.multiply(multiplier).setScale(pricePrecision, RoundingMode.HALF_UP);
    }

    public void roundAmount(String amount, int amountPrecision) {
        if (amountPrecision < 0) {
            throw new IllegalArgumentException("Precision must be non-negative");
        }
        this.amount = new BigDecimal(amount)
                .setScale(amountPrecision, RoundingMode.FLOOR);
    }
}
