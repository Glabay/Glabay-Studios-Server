package io.xeros.sql;

import java.net.URI;
import java.util.Objects;

public class DatabaseCredentials {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseCredentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    private DatabaseCredentials() {
        url = null;
        username = null;
        password = null;
    }

    public URI getURI() {
        if (url == null)
            return null;
        return URI.create(url.substring(5));
    }

    public String getName() {
        return getURI().getPath().substring(1);
    }

    @Override
    public String toString() {
        return "DatabaseCredentials{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseCredentials that = (DatabaseCredentials) o;
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
