package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.NoticeCardViewAdapter;
import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeCardViewData;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by 유리 on 2017-10-01.
 */

public class NoticeFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<NoticeCardViewData> noticeDataset;


    private TextView textView;

    private FirebaseDatabase mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.notice_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_notice);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);


        noticeDataset = new ArrayList<>();
        mAdapter = new NoticeCardViewAdapter(noticeDataset);
        mRecyclerView.setAdapter(mAdapter);


        changeNotice();

        return view;

    }
    public static class Notice{
        public String content;
        public String w_time;
        public String d_time;
        public Notice(){

        }

        public Notice(String content, String w_time,String d_time) {
            this.content=content;
            this.w_time=w_time;
            this.d_time=d_time;
        }
    }
    public void changeNotice(){

        mDatabase = FirebaseDatabase.getInstance();



        final DatabaseReference noticeRef = mDatabase.getReference("notice");

        ValueEventListener noticeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> childIterator = dataSnapshot.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot child=childIterator.next();
                    String content = child.child("content").getValue(String.class);
                    String w_time = child.child("w_time").getValue(String.class);
                    String d_time = child.child("d_time").getValue(String.class);

                    noticeDataset.add(new NoticeCardViewData("공지 내용 : " + content, "올린 날짜 : " + w_time, "내릴 날짜 : " + d_time));
                    mAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        noticeRef.addValueEventListener(noticeListener);



    }

}

