package org.khomenko.maga.socialnetwork.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.khomenko.maga.socialnetwork.SocialNetwork;

class SocialNetworkTest {
    @Test
    void followersTest() {
        var sn = new SocialNetwork();
        var firstLogin = "1";
        var secondLogin = "2";
        var thirdLogin = "3";

        sn.addUser(firstLogin, "test", "test");
        sn.addUser(secondLogin, "test", "test");
        sn.addUser(thirdLogin, "test", "test");

        sn.addFollower(firstLogin, secondLogin);
        sn.addFollower(secondLogin, firstLogin);
        sn.addFollower(thirdLogin, secondLogin);
    }
}
