package org.khomenko.maga.socialnetwork;

class UserBuilder {
    private User user;

    static UserBuilder createUser(String login) {
        return new UserBuilder(login);
    }

    private UserBuilder(String login) {
        user = new User(login);
    }

    public UserBuilder addFirstName(String firstName) {
        user.setFirstName(firstName);
        return this;
    }

    public UserBuilder addLastName(String lastName) {
        user.setLastName(lastName);
        return this;
    }

    public User build() {
        var result = user;
        user = null;
        return result;
    }
}
