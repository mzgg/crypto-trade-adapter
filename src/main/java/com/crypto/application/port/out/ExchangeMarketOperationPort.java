package com.crypto.application.port.out;

import com.crypto.application.domain.model.CreateOrder;
import com.crypto.application.domain.model.CurrencyPairInfo;
import com.crypto.application.domain.model.User;
import com.crypto.application.domain.model.enums.OrderType;

public interface ExchangeMarketOperationPort {

    void createBuyOrder(User user, String symbol, double price, OrderType orderType);

    CurrencyPairInfo getCurrencyPrecision(User user, String symbol);

    String getFreeBalance(User user, String symbol);

    void createSellOcoOrder(User user, CreateOrder order);


}
