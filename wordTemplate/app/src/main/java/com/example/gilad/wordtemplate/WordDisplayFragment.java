package com.example.gilad.wordtemplate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class WordDisplayFragment extends Fragment {

    String word = "Word";
    String translation = "אנדרואיד";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

//        //find the selection buttons
//        List<Button> buttonList = new ArrayList<>();
//        for(int i = 1; i < 14; i++){
//            int id = getResources().getIdentifier("selection_" + i, "id")
//            buttonList.add((Button)view.findViewById(R.id.selection_1));
//        }

    }

//
//    private class HintsLsn implements View.OnClickListener{
//
//        MainActivity main;
//        String word;
//
//        private HintsLsn(MainActivity main, String word){
//            this.word = word;
//            this.main = main;
//        }
//
//        @Override
//        public void onClick (View v) {
//            main.replaceToHints(word);
//        }
//    }
}
