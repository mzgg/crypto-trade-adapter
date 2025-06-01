package com.crypto.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@ToString
public class CurrencyPairInfo {

    private int amountPrecision;

    private int pricePrecision;

}
