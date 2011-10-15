package no.ntnu.online.onlineguru.plugin.plugins.git.github.jsonmodel;

import java.io.Serializable;

/**
 * @author Roy Sindre Norangshol
 */
public class User implements Serializable {
    private String email;
    private String name;

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

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
