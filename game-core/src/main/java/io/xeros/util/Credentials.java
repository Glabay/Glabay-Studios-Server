package io.xeros.util;

import java.util.Objects;

public class Credentials {

    private final String url;
    private final String username;
    private final String password;

    public Credentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private Credentials() {
        url = null;
        username = null;
        password = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, username, password);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
