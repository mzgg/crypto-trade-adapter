package com.crypto.application.domain.usecase;

import com.crypto.application.domain.model.enums.OrderType;
import com.crypto.application.port.command.CreateOrderCommand;
import com.crypto.application.port.in.VoidUseCase;
import com.crypto.application.domain.model.*;
import com.crypto.application.port.out.ExchangeMarketOperationPort;
import com.crypto.application.port.out.UserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateOrderUseCase implements VoidUseCase<CreateOrderCommand> {

    private final UserPort userPort;

    private final ExchangeMarketOperationPort exchangeMarketOperationPort;


    @Override
    public CurrencyPairInfo execute(CreateOrderCommand command) {

        var user = userPort.findByUserById(command.getUserId());

        var order = CreateOrder.of(command);

        order.calculateFinalOrderBalance(command.getCurrentGrade());

        exchangeMarketOperationPort.createBuyOrder(user, order.getSymbol(), order.getBalance(), OrderType.MARKET);
        log.info("create order is completed successfully user:{}, symbol:{}, grade:{}, price:{}",
                user.getId(), order.getSymbol(), command.getCurrentGrade(), order.getBalance());

        var currencyPairInfo = exchangeMarketOperationPort.getCurrencyPrecision(user, order.getSymbol());
        var amount = exchangeMarketOperationPort.getFreeBalance(user, order.getSymbol());

        order.calculateProfitPrice(command.getProfitRatio(), currencyPairInfo.getPricePrecision());
        order.calculateStopLossPrice(command.getStoplossRatio(), currencyPairInfo.getPricePrecision());
        order.roundAmount(amount, currencyPairInfo.getAmountPrecision());

        exchangeMarketOperationPort.createSellOcoOrder(user, order);
        return currencyPairInfo;

    }
}
