package com.example.gilad.wordtemplate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gilad.wordtemplate.dummy.AchivContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WordHintFragment.OnFragmentInteractionListener,
        AchivFragment.OnListFragmentInteractionListener    {

    private final int numOfSelections = 14;
    private final int maxNumOfChoice = 12;
    String word;
    String translate;
    String hint1;
    String hint2;

    List<Button> buttonList = new ArrayList<>();
    List<Button> choiceList = new ArrayList<>();
    Map<Integer, Integer> choiceSelectMap = new HashMap<>();
    String transSoFar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Trendy Words");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        initWords();

        TextView text = (TextView) findViewById(R.id.theWord);
        text.setText(word);

        //find the selection buttons
        for (int i = 1; i <= numOfSelections; i++) {
            int id = getResources().getIdentifier("selection_" + i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            buttonList.add(b);
            b.setOnClickListener(new selectionListener(i));
        }

        applyChars();
        setChoicesBoxes();

        findViewById(R.id.hintsActionButton).setOnClickListener(new HintsListener(word, hint1, hint2));
        findViewById(R.id.dailyQuestAction).setOnClickListener(new AchivListener());

    }

    public void initWords() {

        word = getNewWord();
        translate = getTranslation(word);
        hint1 = getHint1(word);
        hint2 = getHint2(word);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public void applyChars() {
        List<Button> temp = new ArrayList<>(buttonList);
        for (int i = 0; i < translate.length(); i++) {
            //poll a random button
            int randomNum = ThreadLocalRandom.current().nextInt(0, temp.size());
            temp.get(randomNum).setText(String.valueOf(translate.charAt(i)));
            temp.remove(randomNum);
        }
        for (int i = 0; i < temp.size(); i++)
            temp.get(i).setText("X");

    }

    public void setChoicesBoxes() {
        for (int i = 0; i < maxNumOfChoice - translate.length(); i++) {
            int id = getResources().getIdentifier("choice_" + (maxNumOfChoice - i), "id", getPackageName());
            Button b = (Button) findViewById(id);
            ((ViewGroup) b.getParent()).removeView(b);
        }
        for (int i = 1; i <= translate.length(); i++) {
            int id = getResources().getIdentifier("choice_" + i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            b.setText(String.valueOf("_"));
            b.setOnClickListener(new choiceListener());
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            b.setMaxWidth(14);
            choiceList.add(b);

        }

    }

    @Override
    public void onListFragmentInteraction(AchivContent.AchivItem item) {

    }


    private class selectionListener implements View.OnClickListener {

        private int index = -1;

        private selectionListener(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            if (choiceSelectMap.size() < translate.length()) {
                Button btn = (Button) v;
                int choiceIndex = translate.length() - choiceSelectMap.size();
                choiceSelectMap.put(choiceIndex, index);
                int id = getResources().getIdentifier("choice_" + choiceIndex, "id", getPackageName());
                Button b = (Button) findViewById(id);
                b.setText(String.valueOf(btn.getText()));
                btn.setClickable(false);
                btn.setVisibility(View.INVISIBLE);
                transSoFar = transSoFar + btn.getText();

                if (transSoFar.equals(translate)) {
                    Context c = getApplicationContext();
                    CharSequence texrt = "HORRAY!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(c, texrt, duration);
                    toast.show();
                }
            }
        }
    }

    private class choiceListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (choiceSelectMap.size() > 0) {
                int choiceIndex = translate.length() - choiceSelectMap.size() + 1;
                int selectionIndex = choiceSelectMap.get(translate.length() - choiceSelectMap.size() + 1);
                int id = getResources().getIdentifier("selection_" + selectionIndex,
                        "id", getPackageName());
                Button selection = (Button) findViewById(id);

                id = getResources().getIdentifier("choice_" + choiceIndex,
                        "id", getPackageName());
                Button choice = (Button) findViewById(id);
                choice.setText(String.valueOf("_"));

                selection.setClickable(true);
                selection.setVisibility(View.VISIBLE);
                choiceSelectMap.remove(choiceIndex);
                transSoFar = transSoFar.substring(0, transSoFar.length() - 1);
            }
        }
    }

    private class HintsListener implements View.OnClickListener {

        String word;
        String hint1;
        String hint2;
        FragmentManager fm;
        WordHintFragment tf;

        private HintsListener(String word, String hint1, String hint2) {
            this.word = word;
            this.hint1 = hint1;
            this.hint2 = hint2;
            this.fm = getSupportFragmentManager();
            this.tf = WordHintFragment.newInstance(word, hint1, hint2);
        }

        @Override
        public void onClick(View v) {
            tf.show(fm, "fragment_edit_name");

        }
    }

    private class AchivListener implements View.OnClickListener{
        FragmentManager fm;
        AchivFragment tf;

        //user id?
        private AchivListener() {
            this.fm = getSupportFragmentManager();
            this.tf = AchivFragment.newInstance();
        }

        @Override
        public void onClick(View v) {
            tf.show(fm,"fragment_achievements");
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_cats) {
            //go to categories
            Intent intent = new Intent(this, CategoriesActivity.class);
            startActivity(intent);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public String getNewWord() {
        //TODO implement this
        return "house";
    }

    public String getTranslation(String word) {
        //TODO
        return "בית";
    }

    public String getHint1(String word) {
        //TODO implement this
        return "bla bla bla bla bla";
    }

    public String getHint2(String word) {
        //TODO implement this
        return "test test test test test test test test test test test test test test";
    }
}
