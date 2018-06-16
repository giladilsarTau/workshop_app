package com.example.gilad.wordtemplate;


import java.util.Map;

public class UserClass {
    public Map<String, Integer> achievements = null;
    public int points;
    public String trendyId;
    public String difficulty;
    public Map<String, Integer> categories = null;
    public Map<String, Object> solved = null;
    public Map<String, Integer> hints = null;



    public UserClass(int points, Map<String, Integer> achievements, Map<String, Integer> cats) {
        this.points = points;
        this.achievements = achievements;
        this.categories = cats;
    }

    public UserClass() {
        this.points = 0;
    }
}
