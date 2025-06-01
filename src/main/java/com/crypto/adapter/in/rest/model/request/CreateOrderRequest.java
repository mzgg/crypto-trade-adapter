package com.crypto.adapter.in.rest.model.request;

import com.crypto.application.port.command.CreateOrderCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequest {

    @NotNull(message = "userId cannot be empty")
    private Long userId;

    @NotBlank(message = "symbol cannot be empty")
    private String symbol;

    @NotNull(message = "currentSymbolPrice cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "currentSymbolPrice must be greater than zero")
    private double currentSymbolPrice;

    @NotNull(message = "price cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than zero")
    private double initialBalance;

    @Min(value = 1, message = "currentGrade must be at least 1")
    private int currentGrade;

    @Min(value = 1, message = "maxGrade must be at least 1")
    private int maxGrade;

    @NotNull(message = "profitRatio cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "profitRatio must be greater than zero")
    private double profitRatio;

    @NotNull(message = "stoplossRatio cannot be empty")
    @DecimalMin(value = "0.0", inclusive = false, message = "stoplossRatio must be greater than zero")
    private double stoplossRatio;

    public CreateOrderCommand toCommand() {
        CreateOrderCommand order = new CreateOrderCommand();
        order.setUserId(this.getUserId());
        order.setSymbol(this.getSymbol());
        order.setInitialBaseBalance(this.getInitialBalance());
        order.setCurrentGrade(this.getCurrentGrade());
        order.setMaxGrade(this.getMaxGrade());
        order.setProfitRatio(this.getProfitRatio());
        order.setStoplossRatio(this.getStoplossRatio());
        order.setCurrentSymbolPrice(this.getCurrentSymbolPrice());
        return order;
    }
}
