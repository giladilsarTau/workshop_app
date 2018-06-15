package com.example.gilad.wordtemplate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class WordHintFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.choiceViewGroup. ARG_ITEM_NUMBER
    private static String WORD = "param1";
    private static String HINT_1 = "param2";
    private static String HINT_2 = "param3";

    private Button gotItButton;
    private Button hintBtn;

    boolean isHint1 = false;
    boolean isHint2 = false;

    private TextView hint1;
    private TextView hint2;
    private String userId;
    private  MainActivity father;
    private OnFragmentInteractionListener mListener;


    public WordHintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WordHintFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WordHintFragment newInstance(String param1, String param2, String param3, String userId, MainActivity father) {
        WordHintFragment fragment = new WordHintFragment();
        Bundle args = new Bundle();
        args.putString(WORD, param1);
        args.putString(HINT_1, param2);
        args.putString(HINT_2, param3);
        fragment.setArguments(args);

        WordHintFragment.WORD = param1;
        WordHintFragment.HINT_1 = param2;
        WordHintFragment.HINT_2 = param3;
        fragment.userId = userId;
        fragment.father = father;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hints, container, false);
        ((TextView)view.findViewById(R.id.transWord)).setText(WORD);
        ((TextView)view.findViewById(R.id.hint1)).setText(HINT_1);
        ((TextView)view.findViewById(R.id.hint2)).setText(HINT_2);


        //Find the gotItButton
        gotItButton = view.findViewById(R.id.back);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });



        hint1 = view.findViewById(R.id.hint1);
        hint2 =  view.findViewById(R.id.hint2);

        if(!isHint1) hint1.setVisibility(View.INVISIBLE);
        if(!isHint2) hint2.setVisibility(View.INVISIBLE);


        hintBtn = view.findViewById(R.id.next_hint);

        hintBtn.setOnClickListener(new View.OnClickListener() {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference ref = db.getReference();

            @Override
            public void onClick(View v) {
                if(!isHint1 && MainActivity.thisUser.points >= 200) {
                    hint1.setVisibility(View.VISIBLE);
                    isHint1 = true;

                    Map<String, Object> update = new HashMap<>();
                    MainActivity.thisUser.points -= 200;
                    update.put("points", MainActivity.thisUser.points);
                    ref.child(userId).updateChildren(update);
                    father.usedHint = true;
                }
                else if(!isHint2 && MainActivity.thisUser.points >= 200){
                    hint2.setVisibility(View.VISIBLE);
                    isHint2 = true;

                    Map<String, Object> update = new HashMap<>();
                    MainActivity.thisUser.points -= 200;
                    update.put("points", MainActivity.thisUser.points);
                    ref.child(userId).updateChildren(update);

                    ((ViewGroup)v.getParent()).removeView(v);
                }
            }
        });

        if(isHint2)
            ((ViewGroup)hintBtn.getParent()).removeView(hintBtn);
        
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.transWord)).setText(WORD);
        getDialog().setTitle("Hints");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
