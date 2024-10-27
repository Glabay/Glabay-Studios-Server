package io.xeros.net.login;

import lombok.Getter;

@Getter
public record SuccessfulLogin(String ip, String mac, String uuid) {}
