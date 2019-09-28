package org.khomenko.maga.socialnetwork;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocialNetwork {
    private HashMap<String, User> users;
    private MutableGraph<User> graph;

    public SocialNetwork() {
        users = new HashMap<>();
        graph = GraphBuilder.directed().build();
    }

    public boolean containsUser(String login) {
        return users.containsKey(login);
    }

    public boolean addUser(String login, String firstName, String lastName) {
        if (containsUser(login)) {
            return false;
        }

        var node = UserBuilder.createUser(login)
                .addFirstName(firstName)
                .addLastName(lastName)
                .build();

        users.put(login, node);
        graph.addNode(node);

        return true;
    }

    public User getUser(String login) {
        return users.get(login);
    }

    private boolean addLink(String loginFirst, String loginSecond) {
        var userFirst = getUser(loginFirst);
        var userSecond = getUser(loginSecond);

        if (userFirst == null) {
            throw new UserNotExistsException(loginFirst);
        }

        if (userSecond == null) {
            throw new UserNotExistsException(loginSecond);
        }

        return graph.putEdge(userFirst, userSecond);
    }

    public boolean addFollower(String userLogin, String followerLogin) {
        return addLink(followerLogin, userLogin);
    }

    public boolean addFriend(String userLogin, String friendLogin) {
        return addLink(userLogin, friendLogin) || addLink(friendLogin, userLogin);
    }

    public List<User> getFollowers(String login) {
        if (!containsUser(login)) {
            throw new UserNotExistsException(login);
        }

        var result = new ArrayList<User>();
        var user = getUser(login);

        if (graph.inDegree(user) == 0) {
            return result;
        }

        var endpointPairs = graph.incidentEdges(user);

        for (var endpointPair : endpointPairs) {
            var otherUser = endpointPair.adjacentNode(user);

            if (graph.hasEdgeConnecting(otherUser, user) && !graph.hasEdgeConnecting(user, otherUser)) {
                result.add(otherUser);
            }
        }

        return result;
    }

}
