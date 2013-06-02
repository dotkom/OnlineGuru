package no.ntnu.online.onlineguru.plugin.plugins.github.model;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class User implements Serializable {
    private String email;
    private String name;
    private String login;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
