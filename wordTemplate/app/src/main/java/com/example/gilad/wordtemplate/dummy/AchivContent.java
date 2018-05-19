package com.example.gilad.wordtemplate.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AchivContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<AchivItem> ITEMS = new ArrayList<AchivItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, AchivItem> ITEM_MAP = new HashMap<String, AchivItem>();

    private static final int COUNT = 10;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createItem(i));
        }
    }

    private static void addItem(AchivItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static AchivItem createItem(int position) {
        //TODO use getters from users DB

        String desc = "some description about achievement " + position;
        return new AchivItem(String.valueOf(position), "Achievement " + position, makeDetails(position)
        ,desc, 3,10);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class AchivItem {
        public final String id;
        public final String content;
        public final String description;
        public final String details;
        public final int current;
        public final int max;

        public AchivItem(String id, String content, String details, String description, int current, int max) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.description = description;
            this.current = current;
            this.max = max;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
