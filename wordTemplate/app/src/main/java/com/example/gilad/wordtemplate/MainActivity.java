package com.example.gilad.wordtemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.gilad.wordtemplate.dummy.AchivContent;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WordHintFragment.OnFragmentInteractionListener,
        AchivFragment.OnListFragmentInteractionListener,
        CorrectFragment.OnFragmentInteractionListener,
        AchivCompleteFrag.OnFragmentInteractionListener,
        DownloadCallback<String> {


    private final int numOfSelections = 14;
    private final int maxNumOfChoice = 12;
    String word;
    String translate;
    String hint1;
    String hint2;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;

    static UserClass thisUser = null;

    List<Button> buttonList = new ArrayList<>();
    List<Button> choiceList = new ArrayList<>();
    List<Button> removedBtn = new ArrayList<>();
    Map<Integer, Integer> choiceSelectMap = new HashMap<>();
    String transSoFar = "";
    String userTrendyID = "user";

    String currentWordCategory;

    static JSONArray root = null;
    static List<JSONObject> remWords = new ArrayList<>();
    static JSONObject currJsonWord = null;

    TextToSpeech t1;
    ViewGroup choiceViewGroup = null;

    int pointsEarned = 600;
    int penalty = 0;
    int tries = 0;

    DownloadCallback mcall;
    MainActivity selfPointer;

    static GoogleSignInAccount account = null;
    static AccessToken token = null;
    static boolean accountInit = false;
    static String myDBId = null;
    RequestQueue myRequestQueue;
    boolean isPerfectWord;
    boolean usedHint;


    public static Map<String, Integer> maxAchivMap = null;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcall = this;
        selfPointer = this;
        if (!accountInit) {
            account = getIntent().getParcelableExtra("GoogleAccount");
            token = getIntent().getParcelableExtra("FacebookToken");
            accountInit = true;
        }
        myDBId = account == null ? token.getUserId() : account.getId();
        initAchivMap();
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
        if (account != null) {
            ((TextView) header.findViewById(R.id.navMail)).setText(account.getEmail());
            ((TextView) header.findViewById(R.id.navName)).setText(account.getDisplayName());
        }

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

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();


        Query query = ref.child(myDBId);
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
                UserClass u = dataSnapshot.getValue(UserClass.class);
                if (thisUser == null)
                    thisUser = u;
                ((TextView) findViewById(R.id.point_amount)).setText(Integer.toString(u.points));
                userTrendyID = u.trendyId;

                if (root == null) {
                    String url = "http://trendy-words.herokuapp.com/" + userTrendyID;
                    Log.e("TRENDY WORDS", "starting to download form: " + url);

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


                for (int i = 1; i <= translate.length(); i++) {
                    int id = getResources().getIdentifier("choice_" + (translate.length() - i + 1), "id", getPackageName());
                    Button b = (Button) findViewById(id);
                    b.setText(String.valueOf(translate.charAt(i - 1)));
                }



                Map<String, Object> update = new HashMap<>();
                setAchiv(update,"a7",0);


                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference();
                ref.child(myDBId).updateChildren(update);

                final HashMap<String, String> map = new HashMap<>();
                map.put("word", word);
                map.put("knowledge", "NO_ANSWER");

                JSONObject object = new JSONObject(map);
                JSONArray arr = new JSONArray();
                arr.put(object);

                postToServer(arr);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initWords();
                    }
                }, 2000);

            }
        });


    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


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
        penalty = 0;
        tries = 0;

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
        findViewById(R.id.textToVoiceBtn).setOnClickListener(new TextLsn(t1, word));
        isPerfectWord = true;
        usedHint = false;
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


    private class TextLsn implements View.OnClickListener {

        private TextToSpeech t1;
        private String word;

        private TextLsn(TextToSpeech t, String word) {
            this.t1 = t;
            this.word = word;
        }

        @Override
        public void onClick(View v) {
            t1.speak(word, TextToSpeech.QUEUE_FLUSH, null, word);
        }
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
                if (tries == translate.length() + 4 && penalty < 500) {
                    tries = 0;
                    penalty += 50;
                }
                if (transSoFar.equals(translate)) {

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference();

                    thisUser.points += (pointsEarned - penalty);

                    Map<String, Object> update = new HashMap<>();
                    Map<String, Object> seenWord = new HashMap<>();
                    update.put("points", thisUser.points);
                    if(thisUser.points < maxAchivMap.get("a6"))
                        setAchiv(update,"a6",thisUser.points);


                    increaseAchiv(update, "a1");
                    increaseAchiv(update, "a7");
                    if (!usedHint)
                        increaseAchiv(update, "a4");


                    if (isPerfectWord)
                        increaseAchiv(update, "a3");


                    seenWord.put("word", word);
                    seenWord.put("translate", translate);
                    seenWord.put("time", Calendar.getInstance().getTime().getTime());
                    seenWord.put("category", currentWordCategory);

                    try {
                        String level = currJsonWord.getString("level");
                        if (!level.equals("null")) {

                            seenWord.put("level", level);
                            if (level.equals("ADVANCED"))
                                increaseAchiv(update, "a5");

                        } else {
                            seenWord.put("level", "BEGINNER");
                        }
                    } catch (Exception e) {
                    }

                    update.put("solved/" + word, seenWord);

                    ref.child(myDBId).updateChildren(update);

                    FragmentManager fm = getSupportFragmentManager();
                    CorrectFragment cf = CorrectFragment.newInstance(pointsEarned - penalty, selfPointer);

                    final HashMap<String, String> map = new HashMap<>();
                    map.put("word", word);
                    map.put("knowledge", "CORRECT_ANSWER");

                    JSONObject object = new JSONObject(map);
                    JSONArray arr = new JSONArray();
                    arr.put(object);

                    postToServer(arr);

                    cf.show(fm, "fragment_correct_name");
                }
            }
        }
    }

    private void increaseAchiv(Map<String, Object> map, String achiv) {

        int curr = thisUser.achievements.get(achiv);
        if (curr < maxAchivMap.get(achiv)) {

            thisUser.achievements.put(achiv, thisUser.achievements.get(achiv) + 1);

            map.put("achievements/" + achiv, thisUser.achievements.get(achiv));

            if ((int) thisUser.achievements.get(achiv) == maxAchivMap.get(achiv)) { //achievement complete

                FragmentManager fm;
                AchivCompleteFrag af;

                fm = getSupportFragmentManager();
                af = AchivCompleteFrag.newInstance(achiv, selfPointer);
                af.show(fm, "fragment_edit_name");

            }

        }
    }
    private void setAchiv(Map<String, Object> map, String achiv, int value) {

        int curr = thisUser.achievements.get(achiv);
        if (curr < maxAchivMap.get(achiv)) {

            thisUser.achievements.put(achiv,value);

            map.put("achievements/" + achiv, thisUser.achievements.get(achiv));

            if ((int) thisUser.achievements.get(achiv) >= maxAchivMap.get(achiv)) { //achievement complete

                FragmentManager fm;
                AchivCompleteFrag af;

                fm = getSupportFragmentManager();
                af = AchivCompleteFrag.newInstance(achiv, selfPointer);
                af.show(fm, "fragment_edit_name");

            }

        }
    }

    private void initAchivMap() {
        if (maxAchivMap == null) {
            maxAchivMap = new HashMap<>();
            maxAchivMap.put("a1", 100);
            maxAchivMap.put("a2", 7);
            maxAchivMap.put("a3", 50);
            maxAchivMap.put("a4", 50);
            maxAchivMap.put("a5", 50);
            maxAchivMap.put("a6", 25000);
            maxAchivMap.put("a7", 50);
            maxAchivMap.put("a8", 100);
            maxAchivMap.put("a9", 100);
            maxAchivMap.put("a10", 1);
        }

    }


    private void postToServer(JSONArray arr) {
        String postUrl = "http://trendy-words.herokuapp.com/" + userTrendyID + "/update";

        JsonArrayRequest jsonobj = new JsonArrayRequest(Request.Method.POST, postUrl, arr,
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
        ) {
            //here I want to post data to sever
        };

        myRequestQueue.add(jsonobj);
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
                isPerfectWord = false;
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
            this.tf = WordHintFragment.newInstance(word, hint1, hint2, myDBId, selfPointer);
        }

        @Override
        public void onClick(View v) {
            tf.show(fm, "fragment_edit_name");

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_stats) {
            //go stats
            Intent intent = new Intent(this, statsActivity.class);
            intent.putExtra("ID", myDBId);
            startActivity(intent);


        } else if (id == R.id.nav_cats) {
            //go to categories
            Intent intent = new Intent(this, CategoriesActivity.class);
            intent.putExtra("ID", myDBId);
            startActivity(intent);

        } else if (id == R.id.logout) {
            if (account != null) {
                LoginActivity.mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                account = null;
                                accountInit = false;
                                root = null;
                                LoginActivity.mGoogleSignInClient = null;
                                Intent intent = new Intent(selfPointer, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            } else {
                LoginManager.getInstance().logOut();
                token = null;
                account = null;
                accountInit = false;
                root = null;
                Intent intent = new Intent(selfPointer, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (id == R.id.nav_achiv) {
            FragmentManager fm;
            AchivFragment tf;
            fm = getSupportFragmentManager();
            tf = AchivFragment.newInstance(myDBId, selfPointer);

            tf.show(fm, "fragment_achievements");
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
            word = "Achievement";
            translate = "הישג";
            hint1 = "My biggest achievement is my son";
            hint2 = "Accomplishment";
        } else {
            try {
                currJsonWord = remWords.get(0);
                word = currJsonWord.getString("word");
                this.translate = currJsonWord.getString("translation").replaceAll(
                        "[\\u0591-\\u05c7]", ""
                );
                hint1 = currJsonWord.getString("sentence");
                hint2 = currJsonWord.getString("definition");
                currentWordCategory = getJsonCategory(currJsonWord);

                remWords.remove(0);
            } catch (Exception e) {
                Log.e("TAG TAG", e.getMessage());
            }
        }
        Log.e("TAG TAG", "word is: " + word);
        Log.e("TAG TAG", "trans is: " + translate);

    }

    private String getJsonCategory(JSONObject json) {
        try {
            String cat = json.getJSONArray("categories").getString(0);
            if (cat.equals("CS") || cat.equals("PROGRAMMING"))
                cat = "TECHNOLOGY";
            return cat;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void updateFromDownload(String result) {
        try {

            root = new JSONArray(result);
            for (int i = 0; i < root.length(); i++)
                remWords.add((JSONObject) root.get(i));
            Collections.shuffle(remWords);
            //sort by favorite category
            Collections.sort(remWords, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    int a1 = thisUser.categories.get(getJsonCategory(o1));
                    int a2 = thisUser.categories.get(getJsonCategory(o2));
                    return Integer.compare(a1, a2);
                }
            });
            Log.e("TTTTTT", "Amount of words is " + root.length());
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
