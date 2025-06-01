package com.crypto.adapter.in.rest.controller;

import com.crypto.adapter.in.rest.model.request.CreateOrderRequest;
import com.crypto.application.domain.model.CurrencyPairInfo;
import com.crypto.application.domain.usecase.CreateOrderUseCase;
import com.crypto.application.port.command.CreateOrderCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final CreateOrderUseCase orderUseCase;

    @PostMapping(value = "/binance")
    public CurrencyPairInfo createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderCommand command = request.toCommand();
        return orderUseCase.execute(command);
    }
}
