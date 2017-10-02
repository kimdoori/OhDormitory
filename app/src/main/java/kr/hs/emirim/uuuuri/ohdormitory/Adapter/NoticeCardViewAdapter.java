package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeCardViewData;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by doori on 2017-10-01.
 */

public class NoticeCardViewAdapter extends RecyclerView.Adapter<NoticeCardViewAdapter.ViewHolder> {
    private ArrayList<NoticeCardViewData> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView_content;
        public TextView mTextView_time;


        public ViewHolder(View view) {
            super(view);
            mTextView_content = (TextView)view.findViewById(R.id.textview_content);
            mTextView_time= (TextView)view.findViewById(R.id.textview_time);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoticeCardViewAdapter(ArrayList<NoticeCardViewData> noticeCardViewDataset) {
        mDataset = noticeCardViewDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoticeCardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,null);
        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView_content.setText(mDataset.get(position).getContent());
        holder.mTextView_time.setText(mDataset.get(position).getW_time()+" - "+mDataset.get(position).getD_time());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

