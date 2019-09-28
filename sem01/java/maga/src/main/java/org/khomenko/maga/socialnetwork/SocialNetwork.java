package org.khomenko.maga.socialnetwork;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;

public class SocialNetwork {
    public SocialNetwork() {
        MutableNetwork<String, String> net = NetworkBuilder.directed().build();
    }
}
