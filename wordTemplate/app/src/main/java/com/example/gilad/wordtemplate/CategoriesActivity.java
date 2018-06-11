package com.example.gilad.wordtemplate;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {


    ImageButton cat1;
    ImageButton cat2;
    ImageButton cat3;
    ImageButton cat4;
    ImageButton cat5;
    ImageButton cat6;

    SeekBar seekBar;

    String id;

    CategoriesActivity selfPointer;

    private static final Map<String, Integer> catMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cat_toolbar);
        setSupportActionBar(toolbar);

        id = getIntent().getStringExtra("ID");
        selfPointer = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        cat1 = (ImageButton) findViewById(R.id.imageViewCat1);
        cat2 = (ImageButton) findViewById(R.id.imageViewCat2);
        cat3 = (ImageButton) findViewById(R.id.imageViewCat3);
        cat4 = (ImageButton) findViewById(R.id.imageViewCat4);
        cat5 = (ImageButton) findViewById(R.id.imageViewCat5);
        cat6 = (ImageButton) findViewById(R.id.imageViewCat6);


        //TODO load up categories and mark them as "selected"

        cat1.setOnClickListener(new CatBtnLis(1));
        cat2.setOnClickListener(new CatBtnLis(2));
        cat3.setOnClickListener(new CatBtnLis(3));
        cat4.setOnClickListener(new CatBtnLis(4));
        cat5.setOnClickListener(new CatBtnLis(5));
        cat6.setOnClickListener(new CatBtnLis(6));

        seekBar = (SeekBar) findViewById(R.id.catSeekBar);

        for (int i = 1; i <= 6; i++) {
            int id = getResources().getIdentifier("imageViewCat" + i,
                    "id", getPackageName());
            ImageButton btn = (ImageButton) findViewById(id);
            btn.setBackground(new ColorDrawable(getColor(R.color.catGray)));
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        //Query query = ref.child(userID).child("achievements");
        Query query = ref.child(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass u = dataSnapshot.getValue(UserClass.class);


                catMap.putAll(u.categories);
                for (String key : catMap.keySet()) {
                    int eIndex = CategoryEnum.getCatFromString(key).index;
                    int id = getResources().getIdentifier("imageViewCat" + eIndex, "id", getPackageName());
                    ImageButton c = (ImageButton) findViewById(id);
                    if (catMap.get(key) != 0)
                        c.setBackground(new ColorDrawable(getColor(R.color.colorPrimary)));
                }
                seekBar.setProgress(DiffEnum.getDiffFromString(u.difficulty).index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Button apply = (Button) findViewById(R.id.catApply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> map = new HashMap<>();
                map.put("difficulty", DiffEnum.getDiffFromIndex(seekBar.getProgress()).name);
                map.put("categories", catMap);

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference();
                ref.child(id).updateChildren(map);


                NavUtils.navigateUpFromSameTask(selfPointer);

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cat_menu, menu);
        return true;
    }


    private class CatBtnLis implements View.OnClickListener {

        int myNum;

        private CatBtnLis(int num) {
            this.myNum = num;
        }

        @Override
        public void onClick(View v) {
            String myName = CategoryEnum.getCatFromIndex(myNum).name;
            if (catMap.get(myName) != 0) {
                catMap.put(myName, 0);
                v.setBackground(new ColorDrawable(getColor(R.color.catGray)));
            } else { //cat is 0
                catMap.put(myName, 1);
                v.setBackground(new ColorDrawable(getColor(R.color.colorPrimary)));

            }

        }
    }


    public enum CategoryEnum {
        HEALTH(5, "HEALTH"),
        NEWS(6, "NEWS"),
        ECONOMY(1, "ECONOMY"),
        ENTERTAINMENT(2, "ENTERTAINMENT"),
        TECHNOLOGY(3, "TECHNOLOGY"),
        SPORTS(4, "SPORTS");


        public final int index;
        public final String name;

        CategoryEnum(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static CategoryEnum getCatFromIndex(int index) {
            switch (index) {
                case 1:
                    return ECONOMY;
                case 2:
                    return ENTERTAINMENT;
                case 3:
                    return TECHNOLOGY;
                case 4:
                    return SPORTS;
                case 5:
                    return HEALTH;
                case 6:
                    return NEWS;
                default:
                    return null;
            }
        }

        public static CategoryEnum getCatFromString(String str) {
            if (str.equals(HEALTH.name)) return HEALTH;
            if (str.equals(ENTERTAINMENT.name)) return ENTERTAINMENT;
            if (str.equals(SPORTS.name)) return SPORTS;
            if (str.equals(TECHNOLOGY.name)) return TECHNOLOGY;
            if (str.equals(NEWS.name)) return NEWS;
            if (str.equals(ECONOMY.name)) return ECONOMY;
            return null;
        }
    }

    public enum DiffEnum {
        BEGINNER(0, "BEGINNER"),
        INTERMEDIATE(1, "INTERMEDIATE"),
        ADVANCED(2, "ADVANCED");


        public final int index;
        public final String name;

        DiffEnum(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static DiffEnum getDiffFromIndex(int index) {
            switch (index) {
                case 0:
                    return BEGINNER;
                case 1:
                    return INTERMEDIATE;
                case 2:
                    return ADVANCED;
                default:
                    return null;
            }
        }

        public static DiffEnum getDiffFromString(String str) {
            if (str.equals(BEGINNER.name)) return BEGINNER;
            if (str.equals(INTERMEDIATE.name)) return INTERMEDIATE;
            if (str.equals(ADVANCED.name)) return ADVANCED;
            return null;
        }

    }
}
