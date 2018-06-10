package com.example.gilad.wordtemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TimeUtils;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gilad.wordtemplate.dummy.AchivContent;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WordHintFragment.OnFragmentInteractionListener,
        AchivFragment.OnListFragmentInteractionListener,
        CorrectFragment.OnFragmentInteractionListener,
        DownloadCallback<String> {

    public static class User {
        public Map<String, Integer> achievements = null;
        public int points;
        public String trendyId;
        public String difficulty;
        public Map<String, Integer> categories = null;

        public User(int points, Map<String, Integer> achievements, Map<String, Integer> cats) {
            this.points = points;
            this.achievements = achievements;
            this.categories = cats;
        }

        public User() {
            this.points = 0;
        }
    }


    private final int numOfSelections = 14;
    private final int maxNumOfChoice = 12;
    String word;
    String translate;
    String hint1;
    String hint2;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;

    static User thisUser = null;

    List<Button> buttonList = new ArrayList<>();
    List<Button> choiceList = new ArrayList<>();
    List<Button> removedBtn = new ArrayList<>();
    Map<Integer, Integer> choiceSelectMap = new HashMap<>();
    String transSoFar = "";
    String userID = "user";

    JSONArray root = null;
    List<JSONObject> remWords = new ArrayList<>();
    JSONObject currJsonWord = null;


    ViewGroup choiceViewGroup = null;

    int pointsEarned = 600;
    int penelty = 0;
    int tries = 0;

    DownloadCallback mcall;
    MainActivity selfPointer;

    static GoogleSignInAccount account = null;
    RequestQueue myRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcall = this;
        selfPointer = this;
        if (account == null)
            account = getIntent().getParcelableExtra("GoogleAccount");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Trendy Words");
        myRequestQueue = Volley.newRequestQueue(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);
        ((TextView) header.findViewById(R.id.navMail)).setText(account.getEmail());
        ((TextView) header.findViewById(R.id.navName)).setText(account.getDisplayName());


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        //find the selection buttons
        for (int i = 1; i <= numOfSelections; i++) {
            int id = getResources().getIdentifier("selection_" + i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            buttonList.add(b);
            b.setOnClickListener(new selectionListener(i));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) b.getLayoutParams();
            params.setMargins(10, 10, 10, 10);
            b.setLayoutParams(params);
            b.setBackground(new ColorDrawable(getColor(R.color.btncolor)));
            b.setTextColor(getColor(R.color.text_white));
        }


        initWords();

        TextView text = (TextView) findViewById(R.id.theWord);
        text.setText(word);
        text.setTextColor(Color.WHITE);

        findViewById(R.id.dailyQuestAction).setOnClickListener(new AchivListener());
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();


        Query query = ref.child(account.getId());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (s != null)
                    if (s.equals("difficulty"))
                        ((TextView) findViewById(R.id.point_amount)).setText(Integer.toString((int) (long) dataSnapshot.getValue()));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (thisUser == null)
                    thisUser = u;
                ((TextView) findViewById(R.id.point_amount)).setText(Integer.toString(u.points));
                userID = u.trendyId;

                if (root == null) {
                    String url = "http://trendy-words.herokuapp.com/" + userID + "?category=ANY&level=" + u.difficulty;
                    Log.e("TRENDY WORDS", "starting to downlaod form: " + url);

                    mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), url, mcall);
                    startDownload();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        findViewById(R.id.give_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWords();
            }
        });
    }

//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        startDownload();
//    }

    private void startDownload() {
        if (!mDownloading && mNetworkFragment != null) {
            // Execute the async download.
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }

    public void initWords() {

        getNewWord();
        TextView text = (TextView) findViewById(R.id.theWord);
        text.setText(word);
        applyChars();
        setChoicesBoxes();
        findViewById(R.id.hintsActionButton).setOnClickListener(new HintsListener(word, hint1, hint2));
        penelty = 0;
        tries = 0;
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
        Random r = new Random();
        for (int i = 0; i < temp.size(); i++) {
            int uni = r.nextInt(0x05eb - 0x5d0) + 0x5d0;
            StringBuilder builder = new StringBuilder();
            builder.appendCodePoint(uni);
            temp.get(i).setText(builder.toString());
        }

    }

    public void setChoicesBoxes() {
        transSoFar = "";
        choiceSelectMap = new HashMap<>();

        for (int i = 1; i <= numOfSelections; i++) {
            int id = getResources().getIdentifier("selection_" + i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            b.setVisibility(View.VISIBLE);
            b.setClickable(true);
        }
        Collections.reverse(removedBtn);
        for (Button btn : removedBtn) {
            if (choiceViewGroup != null) {
                choiceViewGroup.addView(btn);
            }
        }
        removedBtn.clear();

        for (int i = 0; i < maxNumOfChoice - translate.length(); i++) {
            int id = getResources().getIdentifier("choice_" + (maxNumOfChoice - i), "id", getPackageName());
            Button b = (Button) findViewById(id);
            removedBtn.add(b);
            if (choiceViewGroup == null)
                choiceViewGroup = ((ViewGroup) b.getParent());
            choiceViewGroup.removeView(b);


        }
        for (int i = 1; i <= translate.length(); i++) {
            int id = getResources().getIdentifier("choice_" + i, "id", getPackageName());
            Button b = (Button) findViewById(id);
            b.setText(String.valueOf("_"));
            b.setOnClickListener(new choiceListener());
            b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            b.setMaxWidth(14);
            choiceList.add(b);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) b.getLayoutParams();
            params.setMargins(10, 10, 10, 10);
            b.setLayoutParams(params);
            b.setBackground(new ColorDrawable(getColor(R.color.choice_color)));
            //b.setTextColor(getColor(R.color.text_white));
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
                tries++;
                if (tries == translate.length() + 4 && penelty < 500) {
                    tries = 0;
                    penelty += 50;
                }
                if (transSoFar.equals(translate)) {

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference();

                    thisUser.points += (pointsEarned - penelty);
                    thisUser.achievements.put("a1", thisUser.achievements.get("a1") + 1);

                    Map<String, Object> update = new HashMap<>();
                    Map<String, Object> seenWord = new HashMap<>();
                    update.put("points", thisUser.points);
                    update.put("achievements/a1", thisUser.achievements.get("a1"));

                    seenWord.put("word", word);
                    seenWord.put("translate", translate);
                    seenWord.put("time", Calendar.getInstance().getTime().getTime());

                    try {
                        if (!currJsonWord.getString("level").equals("null")) {
                            seenWord.put("level", currJsonWord.getString("level"));

                        } else {
                            seenWord.put("level", "Beginner");
                        }
                    } catch (Exception e) {
                    }

                    update.put("solved/" + word, seenWord);

                    ref.child(account.getId()).updateChildren(update);

                    FragmentManager fm = getSupportFragmentManager();
                    CorrectFragment cf = CorrectFragment.newInstance(pointsEarned - penelty, selfPointer);

                    String postUrl = "http://trendy-words.herokuapp.com/" + userID;
                    final HashMap<String, String> map = new HashMap<>();
                    map.put("word", word);
                    map.put("knowledge", "CORRECT_ANSWER");

                    JSONObject object = new JSONObject(map);
                    JSONArray arr = new JSONArray();
                    arr.put(object);

                    Log.e("TTTT"," array is " + arr.toString());
                    JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, postUrl,arr,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.e("TTTT", "response is : " + response.toString());

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("TTTT", "error is: " + error.toString());
                                }
                            }
                    ){
                        //here I want to post data to sever
                    };

                    myRequestQueue.add(jsonobj);
                    cf.show(fm, "fragment_correct_name");
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

    private class AchivListener implements View.OnClickListener {
        FragmentManager fm;
        AchivFragment tf;

        //user id?
        private AchivListener() {
            this.fm = getSupportFragmentManager();
            this.tf = AchivFragment.newInstance(account.getId());
        }

        @Override
        public void onClick(View v) {
            tf.show(fm, "fragment_achievements");
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
            intent.putExtra("ID", account.getId());
            startActivity(intent);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void getNewWord() {
        if (root == null) {
            word = "house";
            translate = "בית";
            hint1 = "bla bla";
            hint2 = "bla bla bla bla bla";
        } else {
            try {
                currJsonWord = remWords.get(0);
                word = currJsonWord.getString("word");
                this.translate = currJsonWord.getString("translation").replaceAll(
                        "[\\u0591-\\u05c7]", ""
                );
                hint1 = currJsonWord.getString("sentence");
                hint2 = currJsonWord.getString("definition");


                remWords.remove(0);
            } catch (Exception e) {
                Log.e("TAG TAG", e.getMessage());
            }
        }
        Log.e("TAG TAG", "word is: " + word);
        Log.e("TAG TAG", "trans is: " + translate);

    }


    @Override
    public void updateFromDownload(String result) {
        try {

            root = new JSONArray(result);
            for (int i = 0; i < root.length(); i++)
                remWords.add((JSONObject) root.get(i));
            Collections.shuffle(remWords);

        } catch (Exception e) {
            Log.e("BAD ERROR", e.getMessage());
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {

    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null) {
            mNetworkFragment.cancelDownload();
        }

    }


}
