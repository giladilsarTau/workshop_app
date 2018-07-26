package com.giladi.trendywords;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;


public class AchivCompleteFrag extends DialogFragment {


    private static String ACHIV = null;
    private MainActivity father;
    private int pos;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    private OnFragmentInteractionListener mListener;
    String[] arr;

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


        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        view.findViewById(R.id.sweetBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        ((ImageView) view.findViewById(R.id.completeImg)).setImageResource(MyAchivRecyclerViewAdapter.posToImage(pos));
        int id = father.getResources().getIdentifier(ACHIV, "array", father.getPackageName());
        arr = father.getResources().getStringArray(id);
        ((TextView) view.findViewById(R.id.completeDescription)).setText(arr[2]);

        view.findViewById(R.id.completeShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    Bitmap bm = BitmapFactory.decodeResource(father.getResources(), MyAchivRecyclerViewAdapter.posToImage(pos));
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(bm)
                            .setCaption("I've won the " + arr[0] + " achievement in TrendyWords, check it out")
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .build();
                    shareDialog.show(content);


                } else if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("http://link-to-the-app.com"))
                            .setQuote("I've won the " + arr[0] + " achievement in TrendyWords, check it out")
                            .build();
                    shareDialog.show(linkContent);
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle("Achievement Complete!");

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
