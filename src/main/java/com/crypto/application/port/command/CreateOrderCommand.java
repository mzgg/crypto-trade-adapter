package com.crypto.application.port.command;

import lombok.Data;

@Data
public class CreateOrderCommand {

    private Long userId;

    private String symbol;

    private Double initialBaseBalance;

    private Double currentSymbolPrice;

    private int currentGrade;

    private int maxGrade;

    private double profitRatio;

    private double stoplossRatio;
}
