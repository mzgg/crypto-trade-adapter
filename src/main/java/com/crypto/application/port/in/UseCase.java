package com.crypto.application.port.in;

public interface UseCase<REQUEST,RESPONSE> {
    RESPONSE execute(REQUEST request);
}
