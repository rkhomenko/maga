package org.khomenko.maga.socialnetwork.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.khomenko.maga.socialnetwork.SocialNetwork;

import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.stream.Collectors;

class SocialNetworkTest {
    private static SocialNetwork socialNetwork;
    private static HashMap<String, List<String>> followers;
    private static HashMap<String, List<List<String>>> friends;

    static void loadGraph() {
        final String fileName = "graph.json";

        socialNetwork = new SocialNetwork();

        var inputStream = SocialNetworkTest.class.getClassLoader().getResourceAsStream(fileName);
        try {
            var mapper = new ObjectMapper();
            var jsonFactory = mapper.getFactory();
            var jsonParser = jsonFactory.createParser(inputStream);
            JsonNode actualObject = mapper.readTree(jsonParser);

            JsonNode graphNodes = actualObject.get("nodes");
            JsonNode adjacencyNodes = actualObject.get("adjacency");

            int nodeCount = graphNodes.size();
            for (int i = 0; i < nodeCount; i++) {
                var firstName = String.format("firstName%d", i);
                var lastName = String.format("lastName%d", i);

                socialNetwork.addUser(String.valueOf(i), firstName, lastName);
            }

            int nodeId = 0;
            for (JsonNode adjNodeArray : adjacencyNodes) {
                var login = String.valueOf(nodeId);
                for (JsonNode adjNode : adjNodeArray) {
                    String id = adjNode.get("id").toString();

                    socialNetwork.addFollower(id, login);
                }
                nodeId++;
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static void loadFollowersAndFriends() {
        final String fileName = "frd_fol.json";

        followers = new HashMap<>();
        friends = new HashMap<>();

        var inputStream = SocialNetworkTest.class.getClassLoader().getResourceAsStream(fileName);
        try {
            var mapper = new ObjectMapper();
            var jsonFactory = mapper.getFactory();
            var jsonParser = jsonFactory.createParser(inputStream);
            JsonNode actualObject = mapper.readTree(jsonParser);

            JsonNode followersNode = actualObject.get("followers");
            JsonNode friendsNode = actualObject.get("friends");

            for (int i = 0; i < followersNode.size(); i++) {
                var login = String.valueOf(i);
                var followersArray = followersNode.get(login);

                var list = new ArrayList<String>();

                if (followersArray != null) {
                    for (JsonNode follower : followersArray) {
                        list.add(follower.toString());
                    }
                }

                list.sort(String::compareTo);
                followers.put(String.valueOf(i), list);
            }

            for (int level = 1; level < friendsNode.size() + 1; level++) {
                var levelNode = friendsNode.get(String.valueOf(level));

                for (int i = 0; i < levelNode.size(); i++) {
                    var login = String.valueOf(i);
                    var friendNode = levelNode.get(login);
                    var list = new ArrayList<String>();

                    if (friendNode != null) {
                        for (JsonNode friend : friendNode) {
                            list.add(friend.toString());
                        }
                    }

                    var friendLevelList = friends.get(login);
                    if (friendLevelList == null) {
                        var newList = new ArrayList<List<String>>();
                        newList.add(list);
                        friends.put(login, newList);
                    }
                    else {
                        friendLevelList.add(list);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @BeforeAll
    static void loadJsons() {
        loadGraph();
        loadFollowersAndFriends();
    }

    @Test
    void followersTest() {
        final int userCount = followers.size();

        for (int i = 0; i < userCount; i++) {
            var login = String.valueOf(i);
            var followers = socialNetwork.getFollowers(login).stream()
                    .map(obj -> Objects.toString(obj, null))
                    .collect(Collectors.toList());
            var expectedFollowers = SocialNetworkTest.followers.get(login);
            expectedFollowers.sort(String::compareTo);

            Assertions.assertEquals(expectedFollowers.size(), followers.size());
            Assertions.assertEquals(expectedFollowers, followers);
        }
    }
}
