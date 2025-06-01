package com.crypto.application.port.out;

import com.crypto.application.domain.model.User;

public interface UserPort {

    User findByUserById(Long userId);
}
