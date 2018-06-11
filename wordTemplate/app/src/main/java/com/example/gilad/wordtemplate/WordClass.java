package com.example.gilad.wordtemplate;

import org.json.JSONObject;

public class WordClass {
    CategoriesActivity.DiffEnum level;
    long time;
    String word;
    String translate;
    CategoriesActivity.CategoryEnum category;

    public WordClass(JSONObject wordJson) {
        try {
            String currentWordCategory = wordJson.getJSONArray("categories").getString(0);

            if (currentWordCategory.equals("CS") || currentWordCategory.equals("PROGRAMMING"))
                currentWordCategory = "TECHNOLOGY";

            this.category = CategoriesActivity.CategoryEnum.getCatFromString(currentWordCategory);
            this.word = wordJson.getString("word");
            this.translate = wordJson.getString("translation").replaceAll(
                    "[\\u0591-\\u05c7]", ""
            );
            String level = wordJson.getString("level");
            if (!level.equals("null"))
                this.level = CategoriesActivity.DiffEnum.getDiffFromString(level);
            else
                this.level =  CategoriesActivity.DiffEnum.BEGINNER;


        } catch (Exception e) {
        }
    }


}
