package com.example.gilad.wordtemplate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class old_WordHintFragment extends Fragment {

    String word;
    String translation;
    public void setWord(String s){this.word = s;}




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.content_word_hint, container, false);
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState){

        Button moreHintBtn = view.findViewById(R.id.MoreHintsBtn);

        moreHintBtn.setOnClickListener(new HintListener(view));

        Button giveUpbtn = view.findViewById(R.id.give_up_button);
        giveUpbtn.setOnClickListener(new GiveUpListener(word,translation ));
    }


    private static class HintListener implements View.OnClickListener{

        View fragView;

        private HintListener(View v){this.fragView = v;}


        @Override
        public void onClick(View v) {
            TextView hint1 = fragView.findViewById(R.id.FirstHint);
            TextView hint2 = fragView.findViewById(R.id.SecondHint);
            if(hint1.getVisibility() == View.INVISIBLE){
                hint1.setVisibility(View.VISIBLE);
                return;
            }
            hint2.setVisibility(View.VISIBLE);
        }
    }

    private class GiveUpListener implements View.OnClickListener{

        String word;
        String translation;

        private GiveUpListener(String word, String translation) {
            this.word = word;
            this.translation = translation;
        }

        private void showEditDialog() {


            FragmentManager fm = getChildFragmentManager();
//
//            WordHintFragment tf = WordHintFragment.newInstance("Android", "1.The coolest OS ever\n 2.(in science fiction) a robot with a human appearance.\n 3. Hebrew comes here", null,null);
//
//            tf.show(fm, "fragment_edit_name");

        }

        @Override
        public void onClick(View v) {
            this.showEditDialog();
        }
    }
}
