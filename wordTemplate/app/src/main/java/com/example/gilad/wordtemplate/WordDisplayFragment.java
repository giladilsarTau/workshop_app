package com.example.gilad.wordtemplate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class WordDisplayFragment extends Fragment {

    String word = "Word";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        Button hintsBtn = (Button)view.findViewById(R.id.goToHints);
        hintsBtn.setOnClickListener(new HintsLsn((MainActivity)getActivity(),word));

    }


    private class HintsLsn implements View.OnClickListener{

        MainActivity main;
        String word;

        private HintsLsn(MainActivity main, String word){
            this.word = word;
            this.main = main;
        }

        @Override
        public void onClick (View v) {
            main.replaceToHints(word);
        }
    }
}
