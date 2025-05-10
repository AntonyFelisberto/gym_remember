package com.example.gym_remember.model;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    private String title;
    private int repetitions;
    private int numberTimes;
    private int relaxationMinutes;

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("title", title);
        json.put("repetitions", repetitions);
        json.put("relaxationMinutes", relaxationMinutes);
        json.put("numberTimes", numberTimes);
        return json;
    }
}
