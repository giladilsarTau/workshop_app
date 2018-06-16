package com.example.gilad.wordtemplate;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gilad.wordtemplate.AchivFragment.OnListFragmentInteractionListener;
import com.example.gilad.wordtemplate.dummy.AchivContent.AchivItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AchivItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAchivRecyclerViewAdapter extends RecyclerView.Adapter<MyAchivRecyclerViewAdapter.AchivHolder> {

    private final List<AchivItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyAchivRecyclerViewAdapter(List<AchivItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public AchivHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_achiv, parent, false);
        return new AchivHolder(view);
    }

    @Override
    public void onBindViewHolder(final AchivHolder holder, int position) {
        AchivItem item = mValues.get(position);
        holder.mItem = item;
        holder.mItem.holder = holder;
        holder.mContentView.setText(item.content);

        holder.mProgress.setMax(item.max);
        holder.mProgress.setProgress(item.current);
        holder.mDesc.setText(item.description);
        String s = String.valueOf(item.current) + "/" + String.valueOf(item.max);
        holder.mProgText.setText(s);


        if(holder.mProgress.getMax() > holder.mProgress.getProgress())
            holder.mImage.setImageResource(posToImage(position+1));
        else
            holder.mImage.setImageResource(R.drawable.medal_a1_completed);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class AchivHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public AchivItem mItem;
        public ImageView mImage;
        public ProgressBar mProgress;
        public TextView mProgText;
        public TextView mDesc;

        public AchivHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            mImage = (ImageView) view.findViewById(R.id.item_image);
            mProgress = (ProgressBar) view.findViewById(R.id.item_progress);
            mProgText = (TextView) view.findViewById(R.id.text_progress);
            mDesc = (TextView) view.findViewById(R.id.achiv_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public static int posToImage(int pos){
        switch (pos){
            case 1: return R.drawable.medal_a1;
            case 2: return R.drawable.medal_a2;
            case 3: return R.drawable.medal_a3;
            case 4: return R.drawable.medal_a4;
            case 5: return R.drawable.medal_a5;
            case 6: return R.drawable.medal_a6;
            case 7: return R.drawable.medal_a7;
            case 8: return R.drawable.medal_a8;
            case 9: return R.drawable.medal_a9;
            case 10: return R.drawable.medal_a10;
            default:
                Log.e("TTTTT", "Wrong position: " + pos);
                return -1;

        }
    }
}
