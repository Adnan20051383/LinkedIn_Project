package org.example.linkedinclient;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Follow {
    @JsonProperty("follower")
    private String follower;
    @JsonProperty("followed")
    private String followed;

    public Follow(String follower, String followed) {
        this.follower = follower;
        this.followed = followed;
    }
    public Follow() {}

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getFollowed() {
        return followed;
    }

    public void setFollowed(String followed) {
        this.followed = followed;
    }
}
