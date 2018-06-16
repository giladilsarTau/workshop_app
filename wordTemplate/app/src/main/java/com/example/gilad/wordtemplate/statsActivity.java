package com.example.gilad.wordtemplate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class statsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {




    private String id;
    private UserClass u;

    private Series wordsOverTime;
    private Series wordsPerDay;
    private Series hintsOverTime;
    private Series hintsPerDay;


    private GraphView graph;
    private TextView subText;
    private Map<Integer, Integer> timeCount = new HashMap<>();
    private Map<Integer, Integer> hintsCount = new HashMap<>();


    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id = getIntent().getStringExtra("ID");

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        Query query = ref.child(id);


        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitleTextSize(45);
        subText = (TextView) findViewById(R.id.statSubText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(UserClass.class);


                long currentTime = Calendar.getInstance().getTime().getTime();
                for (Object obj : u.solved.values()) {
                    Map<String, Object> solved = (Map<String, Object>) obj;
                    long diff = currentTime - (long) solved.get("time");
                    int days = (int) (diff / (1000 * 60 * 60 * 24));
                    if (timeCount.containsKey(days))
                        timeCount.put(days, timeCount.get(days) + 1);
                    else
                        timeCount.put(days, 1);
                }

                if(u.hints != null){
                    for(String timeStr : u.hints.keySet()){
                        long time = Long.parseLong(timeStr);
                        long diff = currentTime - time;
                        int days = (int) (diff / (1000 * 60 * 60 * 24));
                        Log.e("TTTTT", "days is is " + days);
                        if (hintsCount.containsKey(days))
                            hintsCount.put(days, hintsCount.get(days) + 1);
                        else
                            hintsCount.put(days, 1);
                    }
                } else{
                    hintsCount.put(0,0);
                }


                wordsOverTime = wordsOverTime();
                hintsOverTime = hintsOverTime();

                wordsPerDay = wordsPerDay();
                hintsPerDay = hintsPerDay();

                //amount of words over time

                graph.addSeries(wordsPerDay);
                setWordsPerDayGraph();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        spinner = (Spinner)findViewById(R.id.statsSpinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stats_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private Series wordsOverTime() {

        int earlies = 0;
        for (int key : timeCount.keySet()) {
            if (key > earlies)
                earlies = key;
        }
        int pre = 0;
        DataPoint[] arr = new DataPoint[earlies + 2];
        arr[0] = new DataPoint(0, 0);
        for (int i = earlies; i >= 0; i--) {
            pre = timeCount.containsKey(i) ? pre + timeCount.get(i) : pre;
            arr[earlies - i + 1] = new DataPoint(earlies - i + 1, pre);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(arr);
        return series;

    }

    private Series hintsOverTime() {

        int earlies = 0;
        for (int key : hintsCount.keySet()) {
            if (key > earlies)
                earlies = key;
        }
        int pre = 0;
        DataPoint[] arr = new DataPoint[earlies + 2];
        arr[0] = new DataPoint(0, 0);
        for (int i = earlies; i >= 0; i--) {
            pre = hintsCount.containsKey(i) ? pre + hintsCount.get(i) : pre;
            arr[earlies - i + 1] = new DataPoint(earlies - i + 1, pre);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(arr);
        return series;

    }

    private Series wordsPerDay() {

        int earlies = 0;
        for (int key : timeCount.keySet()) {
            if (key > earlies)
                earlies = key;
        }
        DataPoint[] arr = new DataPoint[earlies + 2];
        arr[0] = new DataPoint(0, 0);
        for (int i = earlies; i >= 0; i--) {
           int res = timeCount.containsKey(i) ? timeCount.get(i) : 0;
            arr[earlies - i + 1] = new DataPoint(earlies - i + 1, res);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(arr);
        return series;

    }

    private Series hintsPerDay() {

        int earlies = 0;
        for (int key : hintsCount.keySet()) {
            if (key > earlies)
                earlies = key;
        }
        DataPoint[] arr = new DataPoint[earlies + 2];
        arr[0] = new DataPoint(0, 0);
        for (int i = earlies; i >= 0; i--) {
            int res = hintsCount.containsKey(i) ? hintsCount.get(i) : 0;
            arr[earlies - i + 1] = new DataPoint(earlies - i + 1, res);
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(arr);
        return series;

    }


    private void setWordsOverTimeGraph() {

        graph.removeAllSeries();
        graph.addSeries(wordsOverTime);
        graph.setTitle("Total number of words completed over time");
        String txt = "Total: " + (int)wordsOverTime.getHighestValueY();
        subText.setText(txt);

    }

    private void setHintsOverTimeGraph() {

        graph.removeAllSeries();
        graph.addSeries(hintsOverTime);
        graph.setTitle("Total number of hints used");
        String txt = "Total: " + (int)hintsOverTime.getHighestValueY();
        subText.setText(txt);

    }

    private void setWordsPerDayGraph(){
        graph.removeAllSeries();
        graph.addSeries(wordsPerDay);
        graph.setTitle("Words completed per day");
        int count = 0;
        double sum = 0;

        Iterator<DataPoint> it = wordsPerDay.getValues(wordsPerDay.getLowestValueX(), wordsPerDay.getHighestValueX());
        while(it.hasNext()){
            DataPoint d = it.next();
            if(d.getX() != 0) {
                sum += d.getY();
                count++;
            }
        }

        String txt = "Average: " + (sum / count);
        subText.setText(txt);
    }
    private void setHintsPerDayGraph(){
        graph.removeAllSeries();
        graph.addSeries(hintsPerDay);
        graph.setTitle("Hints used per day");
        int count = 0;
        double sum = 0;

        Iterator<DataPoint> it = hintsPerDay.getValues(hintsPerDay.getLowestValueX(), hintsPerDay.getHighestValueX());
        while(it.hasNext()){
            DataPoint d = it.next();
            if(d.getX() != 0) {
                sum += d.getY();
                count++;
            }
        }

        String txt = "Average: " + (sum / count);
        subText.setText(txt);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String itemString = parent.getItemAtPosition(position).toString();
        if(itemString.equals("Total Words"))
            setWordsOverTimeGraph();
        else if (itemString.equals("Words per day"))
            setWordsPerDayGraph();
        else if(itemString.equals("Total Hints used"))
            setHintsOverTimeGraph();
        else if (itemString.equals("Hits per day"))
            setHintsPerDayGraph();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
