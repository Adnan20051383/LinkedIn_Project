package org.example.linkedinclient;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import static org.example.linkedinclient.HelloApplication.LoggedInUser;

public class SkillPageController {

    @FXML
    private Button closeBtn;

    @FXML
    private Button addSkillBtn;

    @FXML
    private VBox skillsContainer;

    private int skillCount = 0;

    public void initialize() throws IOException {
        ArrayList<Skill> skills = getSkills();
        if (!skills.isEmpty()) {
            for (Skill skill : skills) {
                addSkill(skill.getText(), skill.getNthSkill());
            }
        }
    }
    @FXML
    private void handleClose(ActionEvent event) throws IOException {
        HelloApplication helloApplication = new HelloApplication();
        helloApplication.changeScene(5);
    }

    @FXML
    private void handleAddSkill(ActionEvent event) throws IOException {
        HelloApplication helloApplication = new HelloApplication();
        helloApplication.changeScene(10);
    }

    private void addSkill(String skillText, int nthSkill) {
        skillCount++;
        HBox skillItem = new HBox(10);
        skillItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        skillItem.getStyleClass().add("skill-item");

        Button removeButton = new Button("X");
        removeButton.setCursor(Cursor.HAND);
        removeButton.setOnAction(e -> {
            try {
                handleRemoveSkill(skillItem, nthSkill);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeButton.setStyle("-fx-background-color: transparent;");
        removeButton.getStyleClass().add("remove-button");

        TextArea skillTextArea = new TextArea(skillText);
        skillTextArea.setEditable(false);
        skillTextArea.setPrefHeight(38.0);
        skillTextArea.setPrefWidth(290.0);
        skillTextArea.setStyle("-fx-border-width: 0;");
        skillTextArea.getStyleClass().add("skill-text");

        skillItem.getChildren().addAll(removeButton, skillTextArea);
        skillsContainer.getChildren().add(0, skillItem);  // Add to the top of the VBox
    }

    private void handleRemoveSkill(HBox skillItem, int nthSkill) throws IOException {
        removeSkill(skillItem, nthSkill);

    }
    private ArrayList<Skill> getSkills() throws IOException {
        URL url = new URL("http://localhost:8000/skills/" + LoggedInUser.getId());
        String response;
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
        String[] skills = toStringArray(jsonObject);
        ArrayList<Skill> skills1 = new ArrayList<>();
        for (String t : skills) {
            JSONObject obj = new JSONObject(t);
            Skill skill = new Skill(LoggedInUser.getId(), obj.getInt("nthSkill"), obj.getString("text"));
            skills1.add(skill);
        }
        return skills1;
    }
    private void removeSkill(HBox skillItem ,int nthSkill) throws IOException {
        URL url = new URL("http://localhost:8000/skills/" + LoggedInUser.getId() + "/" + nthSkill);
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
        if (response.equals("successful")) {
            skillsContainer.getChildren().remove(skillItem);
            skillCount--;
        }
        else {
            System.out.println(response);
        }

    }
    public static String[] toStringArray(JSONArray array) {
        if(array == null)
            return new String[0];

        String[] arr = new String[array.length()];
        for(int i = 0; i < arr.length; i++)
            arr[i] = array.optString(i);
        return arr;
    }

    public void removeSkillBtnClicked(ActionEvent actionEvent) {
    }
}
