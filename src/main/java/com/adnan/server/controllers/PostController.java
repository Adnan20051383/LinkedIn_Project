package com.adnan.server.controllers;

import com.adnan.server.dataAccess.PostDataAccess;
import com.adnan.server.dataAccess.UserDataAccess;
import com.adnan.server.models.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class PostController {
    private static PostDataAccess PDA = null;
    private static UserDataAccess UDA = null;

    public PostController() throws SQLException {
        PDA = new PostDataAccess();
        UDA = new UserDataAccess();
    }
    public String getPost(String postId) throws SQLException, JsonProcessingException {
        Post post = PDA.getPost(postId);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(post);
    }
    public String getPosts() throws SQLException, JsonProcessingException {
        ArrayList<Post> posts = PDA.getPosts();
        if (posts == null) return "NO POST!";
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(posts);
    }
    public String createPost(String postId, String posterId, String content) throws SQLException {
        if(!UDA.userExists(posterId))
            return "USER NOT FOUND!";
        Post post = new Post(postId, posterId, content);
        if (PDA.postExists(postId))
            PDA.updatePost(post);
        else
            PDA.addPost(post);
        return "successful!";
    }
    public String deletePost(String postId) throws SQLException {
        if(!PDA.postExists(postId))
            return "POST NOT FOUND";
        PDA.deletePost(postId);
        return "successful!";
    }
}
