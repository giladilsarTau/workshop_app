package com.example.gilad.wordtemplate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class AchivCompleteFrag extends DialogFragment {


    private static String ACHIV = null;
    private MainActivity father;
    private int pos;

    private OnFragmentInteractionListener mListener;

    public AchivCompleteFrag() {
        // Required empty public constructor
    }


    public static AchivCompleteFrag newInstance(String param1, MainActivity father) {
        AchivCompleteFrag fragment = new AchivCompleteFrag();
        Bundle args = new Bundle();
        args.putString(ACHIV, param1);
        fragment.setArguments(args);
        fragment.father = father;
        AchivCompleteFrag.ACHIV = param1;
        fragment.pos = Integer.parseInt(param1.substring(1));
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_achiv_complete, container, false);


        view.findViewById(R.id.sweetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        ((ImageView)view.findViewById(R.id.completeImg)).setBackgroundResource(MyAchivRecyclerViewAdapter.posToImage(pos));
        int id = father.getResources().getIdentifier(ACHIV, "array", father.getPackageName());
        String[] arr = father.getResources().getStringArray(id);
        ((TextView)view.findViewById(R.id.completeDescription)).setText(arr[2]);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Achievement Complete!");

    }

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
