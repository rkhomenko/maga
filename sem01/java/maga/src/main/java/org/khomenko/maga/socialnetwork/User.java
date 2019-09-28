package org.khomenko.maga.socialnetwork;

import java.util.Objects;

public class User {
    private final String login;
    private String firstName;
    private String lastName;

    User(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            var otherNode = (User)other;
            return getLogin().equals(otherNode.getLogin());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogin());
    }
}
