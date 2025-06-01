package com.crypto.adapter.in.rest.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SellOcoOrderRequest {

    @NotNull(message = "userId cannot be empty")
    private Long userId;

    @NotBlank(message = "Symbol cannot be empty.")
    private String symbol;

    @NotBlank(message = "Amount cannot be empty.")
    private String amount;

    @NotNull(message = "Profit price cannot be empty.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Profit price must be greater than zero.")
    private Double profitPrice;

    @NotNull(message = "Stop price cannot be empty.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Stop price must be greater than zero.")
    private Double stopPrice;

}
