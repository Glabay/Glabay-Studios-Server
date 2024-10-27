package io.xeros.net.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginReturnCode {
    SUCCESS(2),
    INVALID_USERNAME_OR_PASSWORD(3),
    ACCOUNT_DISABLED(4),
    ACCOUNT_ALREADY_ONLINE(5),
    CLIENT_OUT_OF_DATE(6),
    WORLD_FULL(7),
    UNABLE_TO_CONNECT(8),
    LOGIN_LIMIT_EXCEEDED(9),
    BAD_SESSION_ID(10),
    USERNAME_TOO_LONG(11),
    TOO_MANY_CONNECTION_ATTEMPTS(13),
    SERVER_BEING_UPDATED(14),
    UNUSED_SUCCESS_CODE(15),
    FORCE_RETRY_LOGIN(21),
    ERROR_OCCURRED_ON_PLAYER_LOAD(26),
    CAPTCHA_REQUIRED(27),
    CAPTCHA_INCORRECT(28)
    ;

    private final int code;
}
