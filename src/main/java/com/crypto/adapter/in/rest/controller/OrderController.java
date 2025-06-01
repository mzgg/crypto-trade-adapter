package com.crypto.adapter.in.rest.controller;

import com.crypto.adapter.in.rest.model.request.CreateOrderRequest;
import com.crypto.adapter.in.rest.model.request.SellOcoOrderRequest;
import com.crypto.application.domain.model.CreateOrder;
import com.crypto.application.domain.model.CurrencyPairInfo;
import com.crypto.application.domain.model.User;
import com.crypto.application.domain.model.enums.OrderType;
import com.crypto.application.port.out.ExchangeMarketOperationPort;
import com.crypto.application.port.out.UserPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final ExchangeMarketOperationPort exchangeMarketOperationPort;

    private final UserPort userPort;

    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        User user = userPort.findByUserById(request.getUserId());
        exchangeMarketOperationPort.createBuyOrder(user, request.getSymbol(), request.getInitialBalance(), OrderType.MARKET);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/precision")
    public ResponseEntity<CurrencyPairInfo> getCurrencyPrecision(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "symbol") String symbol) {
        User user = userPort.findByUserById(userId);
        return ResponseEntity.ok(exchangeMarketOperationPort.getCurrencyPrecision(user, symbol));
    }

    @GetMapping(value = "/free-balance", produces = "application/json")
    public String freeBalance(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "symbol") String symbol) {

        User user = userPort.findByUserById(userId);
        return exchangeMarketOperationPort.getFreeBalance(user, symbol);

    }

    @PostMapping("/sell-oco")
    public ResponseEntity<Void> createSellOcoOrder(@Validated @RequestBody SellOcoOrderRequest request) {
        User user = userPort.findByUserById(request.getUserId());

        CreateOrder createOrder = new CreateOrder();
        createOrder.setSymbol(request.getSymbol());
        createOrder.setTakeProfitPrice(BigDecimal.valueOf(request.getProfitPrice()));
        createOrder.setStopLossPrice(BigDecimal.valueOf(request.getStopPrice()));
        createOrder.setAmount(new BigDecimal(request.getAmount()));

        createOrder.roundAmount(request.getAmount(), 5);

        exchangeMarketOperationPort.createSellOcoOrder(user,createOrder);
        return ResponseEntity.ok().build();
    }
}
