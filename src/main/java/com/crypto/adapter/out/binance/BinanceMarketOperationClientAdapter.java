package com.crypto.adapter.out.binance;

import com.binance.connector.client.common.ApiResponse;
import com.binance.connector.client.spot.rest.model.*;
import com.crypto.application.domain.model.CreateOrder;
import com.crypto.application.domain.model.CurrencyPairInfo;
import com.crypto.application.domain.model.User;
import com.crypto.application.domain.model.enums.OrderType;
import com.crypto.application.port.out.ExchangeMarketOperationPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.crypto.adapter.out.binance.ApiUtil.getApi;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinanceMarketOperationClientAdapter implements ExchangeMarketOperationPort {

    @Override
    public void createBuyOrder(User user, String symbol, double price, OrderType orderType) {
        log.info("create order is started user:{}, symbol:{}, price:{}", user.getName(), symbol, price);
        NewOrderRequest newOrderRequest = new NewOrderRequest();
        newOrderRequest.symbol(BinanceConversionUtil.convertSymbolFormat(symbol));
        newOrderRequest.side(com.binance.connector.client.spot.rest.model.Side.BUY);
        newOrderRequest.type(com.binance.connector.client.spot.rest.model.OrderType.MARKET);
        newOrderRequest.quoteOrderQty(price);
        ApiResponse<NewOrderResponse> response = getApi(user).newOrder(newOrderRequest);
        log.info("create order response for user:{} :{}", user.getName(), response.getData().toString());

    }

    @Override
    public CurrencyPairInfo getCurrencyPrecision(User user, String symbol) {
        log.info("getting currency precision is started user:{}, symbol:{}", user.getId(), symbol);
        symbol = BinanceConversionUtil.convertSymbolFormat(symbol);
        ApiResponse<ExchangeInfoResponse> response = getApi(user).exchangeInfo(symbol, null, null, false, null);
        if (response.getData().getSymbols() == null) {
            throw new RuntimeException();
        }

        CurrencyPairInfo info = response.getData().getSymbols().stream()
                .filter(res -> "USDT".equals(res.getQuoteAsset()))
                .filter(input -> Objects.nonNull(input.getFilters()))
                .map(input -> {
                    @Valid List<@Valid ExchangeInfoResponseExchangeFiltersInner> filters = input.getFilters();
                    // PRICE_FILTER'dan tickSize al
                    String tickSize = filters.stream()
                            .filter(filter -> "PRICE_FILTER".equals(filter.getFilterType()))
                            .map(ExchangeInfoResponseExchangeFiltersInner::getTickSize)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                    // LOT_SIZE'dan stepSize al
                    String stepSize = filters.stream()
                            .filter(filter -> "LOT_SIZE".equals(filter.getFilterType()))
                            .map(ExchangeInfoResponseExchangeFiltersInner::getStepSize)
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);

                    return CurrencyPairInfo.builder()
                            .amountPrecision(BinancePrecisionCalculator.getDecimalPlaces(stepSize))
                            .pricePrecision(BinancePrecisionCalculator.getDecimalPlaces(tickSize))
                            .build();
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("USDT pair not found"));
        log.info("getting currency precision is completed user:{}, symbol:{}," +
                " getAmountPrecision:{}, getPricePrecision:{}", user.getId(), symbol, info.getAmountPrecision(), info.getPricePrecision());
        return info;
    }

    @Override
    public String getFreeBalance(User user, String symbol) {
        String formattedSymbol = BinanceConversionUtil.removeCurrency(symbol);

        Boolean omitZeroBalances = false;
        Long recvWindow = 5000L;
        ApiResponse<GetAccountResponse> response = getApi(user).getAccount(omitZeroBalances, recvWindow);
        if (response.getData().getBalances() == null) {
            throw new RuntimeException("No balance data found");
        }
        GetAccountResponseBalancesInner balance = response.getData().getBalances().stream()
                .filter(input -> Objects.nonNull(input.getAsset()))
                .filter(input -> input.getAsset().equals(formattedSymbol))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No balance found for symbol: " + formattedSymbol));

        log.info("getting free balance is completed user:{}, symbol:{}, freeAmount:{}, lockedAmount:{}", user.getId(),
                symbol, balance.getFree(), balance.getLocked());
        return balance.getFree();
    }

    @Override
    public void createSellOcoOrder(User user, CreateOrder order) {
        String formattedSymbol = BinanceConversionUtil.convertSymbolFormat(order.getSymbol());

        OrderOcoRequest orderOcoRequest = new OrderOcoRequest();
        orderOcoRequest.symbol(formattedSymbol);
        orderOcoRequest.side(Side.SELL);
        orderOcoRequest.quantity(order.getAmount().stripTrailingZeros().doubleValue());
        orderOcoRequest.price(order.getTakeProfitPrice().stripTrailingZeros().doubleValue());
        orderOcoRequest.stopPrice(order.getStopLossPrice().stripTrailingZeros().doubleValue());
        orderOcoRequest.stopLimitPrice(order.getStopLossPrice().stripTrailingZeros().doubleValue());
        orderOcoRequest.stopLimitTimeInForce(StopLimitTimeInForce.GTC);
        log.info("request for user:{}: {}", user.getName(), orderOcoRequest);
        ApiResponse<OrderOcoResponse> response = getApi(user).orderOco(orderOcoRequest);
        log.info("response for user:{}: {}", user.getName(), response.getData());
    }
}
