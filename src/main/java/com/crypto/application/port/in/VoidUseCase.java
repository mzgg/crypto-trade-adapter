package com.crypto.application.port.in;

import com.crypto.application.domain.model.CurrencyPairInfo;

public interface VoidUseCase<REQUEST> {
    CurrencyPairInfo execute(REQUEST request);
}
