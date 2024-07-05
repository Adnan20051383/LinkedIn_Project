package org.example.linkedinclient;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static org.example.linkedinclient.FeedController.downloadProfPostImg;
import static org.example.linkedinclient.FeedController.toStringArray;
import static org.example.linkedinclient.HelloApplication.LoggedInUser;
import static org.example.linkedinclient.ProfileViewController.getFollowers;

public class FollowersPageController {

    @FXML
    private VBox followersContainer;
    HashMap<String, Button> followButtonsMap = new HashMap<>();

    public void initialize() throws IOException {
        ArrayList<User> followers = getFollowers();
        for (User user: followers) {
            addFollower(user, downloadProfPostImg(user.getId()));
            if (isFollowing(LoggedInUser.getId(), user.getId())) {
                Button followBtn = followButtonsMap.get(user.getId());
                followBtn.setText("followed");
                followBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
            }
        }
    }
    public static void handleFollowButton(User user, Button followButton) throws IOException {
        saveFollow(user, followButton);
    }
    public static void saveFollow(User user, Button followBtn) throws IOException {
        if (!isFollowing(LoggedInUser.getId(), user.getId())) {
            URL url = new URL("http://localhost:8000/follows/" + LoggedInUser.getId() + "/" + user.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("JWT", HelloApplication.token);
            con.setRequestMethod("POST");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputline;
            StringBuffer response1 = new StringBuffer();
            while ((inputline = in.readLine()) != null) {
                response1.append(inputline);
            }
            in.close();
            String response = response1.toString();

            if (response.equals("successful!")) {
                followBtn.setText("followed");
                followBtn.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
            }
            else {
                System.out.println(response);
            }
        }
        else {
            URL url = new URL("http://localhost:8000/follows/" + LoggedInUser.getId() + "/" + user.getId());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("JWT", HelloApplication.token);
            con.setRequestMethod("DELETE");
            int responseCode = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputline;
            StringBuffer response1 = new StringBuffer();
            while ((inputline = in.readLine()) != null) {
                response1.append(inputline);
            }
            in.close();
            String response = response1.toString();

            if (response.equals("successful!")) {
                followBtn.setText("follow");
                followBtn.setStyle("-fx-background-color: #0073b1; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
            }
            else {
                System.out.println(response);
            }
        }
    }
    private static ArrayList<Follow> getAllFollowings(String userId) throws IOException {
        String response;
        URL url = new URL("http://localhost:8000/follows/followings/" + userId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputline;
        StringBuffer response1 = new StringBuffer();
        while ((inputline = in.readLine()) != null) {
            response1.append(inputline);
        }
        in.close();
        response = response1.toString();
        JSONArray jsonObject = new JSONArray(response);
        String[] follows = toStringArray(jsonObject);
        ArrayList <Follow> followings = new ArrayList<>();
        for (String t: follows) {
            JSONObject obj = new JSONObject(t);
            Follow l = new Follow(obj.getString("follower"), obj.getString("followed"));
            followings.add(l);
        }
        return followings;
    }
    private static boolean isFollowing(String follower, String followed) throws IOException {
        ArrayList<Follow> followings = getAllFollowings(follower);
        for (Follow t: followings) {
            if (t.getFollowed().equals(followed) && t.getFollower().equals(follower))
                return true;
        }
        return false;
    }
    public void addFollower(User user, String profileImageUrl) {
        // Create a new HBox for the follower
        HBox followerBox = new HBox();
        followerBox.setSpacing(10.0);
        followerBox.setStyle("-fx-alignment: center-left;");

        // Create an ImageView for the profile image
        ImageView profileImageView = new ImageView(new Image(profileImageUrl));
        profileImageView.setFitHeight(50.0);
        profileImageView.setFitWidth(50.0);
        profileImageView.setStyle("-fx-shape: 'M 0.5, 0 A 0.5, 0.5 0 1,0 1,0.5 A 0.5, 0.5 0 1,0 0.5,0 Z'; -fx-clip: true;");


        Region region = new Region();
        region.setPrefHeight(Region.USE_COMPUTED_SIZE);
        region.setPrefWidth(Region.USE_COMPUTED_SIZE);
        HBox.setHgrow(region, javafx.scene.layout.Priority.ALWAYS);

        // Create a Label for the follower ID
        Label followerIdLabel = new Label(user.getId());
        followerIdLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px;");

        // Create a follow button
        Button followButton = new Button("Follow");
        followButton.setStyle("-fx-background-color: #0073b1; -fx-text-fill: white; -fx-padding: 5 10; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
        followButton.setCursor(javafx.scene.Cursor.HAND);
        followButtonsMap.put(user.getId(), followButton);
        followButton.setOnAction(e -> {
            try {
                handleFollowButton(user, followButton);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Add all elements to the HBox
        followerBox.getChildren().addAll(profileImageView, followerIdLabel, region, followButton);

        // Add the HBox to the followers container
        followersContainer.getChildren().add(followerBox);
    }

    public void closeBtnClicked(ActionEvent actionEvent) throws IOException {
        HelloApplication helloApplication = new HelloApplication();
        helloApplication.changeScene(5);
    }
    public static String[] toStringArray(JSONArray array) {
        if(array == null)
            return new String[0];

        String[] arr = new String[array.length()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = array.optString(i);
        return arr;
    }
}
