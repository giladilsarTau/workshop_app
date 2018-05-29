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

public class CorrectFragment extends DialogFragment {

    private static int SCORE = 0;

    private OnFragmentInteractionListener mListener;


    public CorrectFragment(){}

    public static CorrectFragment newInstance(int param1) {
        CorrectFragment fragment = new CorrectFragment();
        Bundle args = new Bundle();
        args.putInt("SCORE", param1);
        fragment.setArguments(args);

        CorrectFragment.SCORE = param1;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_correct, container, false);
        Button b = ((Button)view.findViewById(R.id.getThePoints2));
        b.setText(Integer.toString(SCORE));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Correct!");

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CorrectFragment.OnFragmentInteractionListener) {
            mListener = (CorrectFragment.OnFragmentInteractionListener) context;
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