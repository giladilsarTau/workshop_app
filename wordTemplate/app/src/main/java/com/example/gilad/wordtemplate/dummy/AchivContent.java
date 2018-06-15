package com.example.gilad.wordtemplate.dummy;

import android.content.Context;
import android.util.Log;

import com.example.gilad.wordtemplate.MainActivity;
import com.example.gilad.wordtemplate.MyAchivRecyclerViewAdapter;
import com.example.gilad.wordtemplate.R;
import com.example.gilad.wordtemplate.UserClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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


    public static List<AchivItem> initItems(String userId, Context context) {
        for (int i = 1; i <= COUNT; i++) {
            addItem(createItem(i, userId, context));
        }
        return ITEMS;
    }

    private static void addItem(AchivItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static AchivItem createItem(int position, String userID, Context context) {
        //TODO use getters from users DB
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        //Query query = ref.child(userID).child("achievements");
        Query query = ref.child(userID);

        GetAchivLsn l = new GetAchivLsn(position);
        query.addListenerForSingleValueEvent(l);


        Log.e("TAG TAG TAG", "NOW IS:  " + l.res);

        int id = context.getResources().getIdentifier("a" + position, "array", context.getPackageName());
        String[] acheivemntStr = context.getResources().getStringArray(id);

        return new AchivItem(String.valueOf(position), acheivemntStr[0], makeDetails(position)
                , acheivemntStr[1], 0, MainActivity.maxAchivMap.get("a"+position));

//        String desc = "some description about achievement " + position;
//        if (position == 1)
//            return new AchivItem(String.valueOf(position), "Solver", makeDetails(position)
//                    , "solve 100 words", 0, 100);
//        else
//            return new AchivItem(String.valueOf(position), "Achievement " + position, makeDetails(position)
//                    , desc, 0, 10);
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
        public int current;
        public final int max;
        public MyAchivRecyclerViewAdapter.AchivHolder holder = null;

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

    static class GetAchivLsn implements ValueEventListener {

        int res;
        int pos;

        private GetAchivLsn(int pos) {
            this.pos = pos;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            //MainActivity.Achivments ac = dataSnapshot.getValue(MainActivity.Achivments.class);
            //res = ac.getAch("a" + pos);
            UserClass u = dataSnapshot.getValue(UserClass.class);
            res = (int) u.achievements.get("a" + pos);
            Log.e("TAG TAG TAG", "got:  " + res);
            ITEMS.get(pos - 1).current = res;
            if (ITEMS.get(pos - 1).holder != null) {
                String s = String.valueOf(res) + "/" + String.valueOf(ITEMS.get(pos -1 ).max); //TODO get max
                ITEMS.get(pos - 1).holder.mProgText.setText(s);
                ITEMS.get(pos - 1).holder.mProgress.setProgress(res);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

}
