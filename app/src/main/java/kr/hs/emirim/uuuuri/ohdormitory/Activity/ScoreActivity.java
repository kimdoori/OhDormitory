package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import kr.hs.emirim.uuuuri.ohdormitory.Adapter.ScoreListAdapater;
import kr.hs.emirim.uuuuri.ohdormitory.Model.Score;
import kr.hs.emirim.uuuuri.ohdormitory.Model.StudentScore;
import kr.hs.emirim.uuuuri.ohdormitory.R;

public class ScoreActivity extends BaseActivity {

    private FirebaseDatabase mDatabase;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<StudentScore> studentScores;

    private DatabaseReference mScoreRefer;
    private ValueEventListener mScoreListener;

    private double mMinusScore;
    private double mPlusScore;
    private double mTotalScore;

    private TextView mMinusText;
    private TextView mPlusText;
    private TextView mTotalText;

    private HashMap<Integer, Score> mScoreHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        initScore();
        mDatabase = FirebaseDatabase.getInstance();

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLayoutManager);

        studentScores = new ArrayList<StudentScore>();
        mAdapter = new ScoreListAdapater(studentScores, mScoreHashMap);
        mRecyclerView.setAdapter(mAdapter);

        mPlusText = findViewById(R.id.plus);
        mMinusText = findViewById(R.id.minus);
        mTotalText = findViewById(R.id.total);

        readScore();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (mScoreListener != null) {
            mScoreRefer.removeEventListener(mScoreListener);
        }
    }

    private void initScore(){
        mScoreHashMap = new HashMap<>();
        mScoreHashMap.put(0, new Score("청소", 0.5));
        mScoreHashMap.put(1, new Score("타호실 무단 취침", -5.0));
        mScoreHashMap.put(2, new Score("무단 귀가", -5.0));
        mScoreHashMap.put(3, new Score("무단 외출", -5.0));
    }
    private void readScore(){
        showProgressDialog();

        mScoreRefer = mDatabase.getReference("score/"+getUid()); // get database reference

        mScoreListener = mScoreRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot uid) {
                studentScores.clear();
                mPlusScore = 0;
                mMinusScore = 0;
                mTotalScore = 0;
                Iterator<DataSnapshot> uidchildIterator = uid.getChildren().iterator();
                while(uidchildIterator.hasNext()){
                    DataSnapshot timeStamp = uidchildIterator.next();
                    String date = timeStamp.child("date").getValue(String.class);
                    List<Long> scores = (List<Long>)timeStamp.child("score").getValue();

                    for(int i = 0; i<scores.size(); i++) {

                        studentScores.add(new StudentScore(date, scores.get(i).intValue()));
                        double score = mScoreHashMap.get(scores.get(i).intValue()).getScore();
                        if(score < 0)
                            mMinusScore += score;
                        else
                            mPlusScore += score;

                    }
                }
                mTotalScore = mMinusScore + mPlusScore;

                mPlusText.setText("상점 : "+mPlusScore+" 점");
                mMinusText.setText("벌점 : "+mMinusScore+" 점");
                mTotalText.setText("총 "+mTotalScore+" 점");

                mAdapter.notifyDataSetChanged();
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
 *
 *  Firebase ref = new Firebase("<my-firebase-app>/names"):
 String[] names = {"John","Tim","Sam","Ben"};
 List nameList = new ArrayList<String>(Arrays.asList(names));
 // Now set value with new nameList
 ref.setValue(nameList);
 */

}
