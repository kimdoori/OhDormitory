package kr.hs.emirim.uuuuri.ohdormitory.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.NoticeDetailActivity;
import kr.hs.emirim.uuuuri.ohdormitory.Model.NoticeCardViewData;
import kr.hs.emirim.uuuuri.ohdormitory.Model.SleepOut;
import kr.hs.emirim.uuuuri.ohdormitory.R;

/**
 * Created by doori on 2017-10-01.
 */

public class NoticeCardViewAdapter extends RecyclerView.Adapter<NoticeCardViewAdapter.ViewHolder> {
    private final String PUT_EXTRA_NOTICE = "NOTICE_ITEM";
    private ArrayList<NoticeCardViewData> mDataset;

    private String mSleepOut;
    private String mFrontNumber;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mInputUserRefer;

    private EditText midNumberEt;
    private EditText rearNumberEt;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextView_content;
        TextView mTextView_time;
        LinearLayout cardTypeColor;
        CardView mMyCardView;
        public ViewHolder(View view) {
            super(view);
            mTextView_content = (TextView)view.findViewById(R.id.textview_content);
            mTextView_time= (TextView)view.findViewById(R.id.textview_time);
            cardTypeColor = (LinearLayout)view.findViewById(R.id.card_type);
            mMyCardView = (CardView)view.findViewById(R.id.myCardView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NoticeCardViewAdapter(ArrayList<NoticeCardViewData> noticeCardViewDataset) {
        mDataset = noticeCardViewDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoticeCardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //recycler view에 반복될 아이템 레이아웃 연결
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice,null);
        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String kind = mDataset.get(position).getNotice_kind();
        if (kind.equals("공지사항")){
            holder.cardTypeColor.setBackgroundColor(0xff9cb1c2);
        }else if(kind.equals("청소구역")){
            holder.cardTypeColor.setBackgroundColor(0xff7bc792);
        }else if(kind.equals("외박일지")){
            holder.cardTypeColor.setBackgroundColor(0xffe36363);
        }

        holder.mTextView_content.setText(mDataset.get(position).getNotice_title());
        if(mDataset.get(position).getW_time().equals("0") &&mDataset.get(position).getD_time().equals("0") )
            holder.mTextView_time.setText("항시공지");
        else if(mDataset.get(position).getW_time().equals(mDataset.get(position).getD_time()))
            holder.mTextView_time.setText(mDataset.get(position).getW_time());
        else
            holder.mTextView_time.setText(mDataset.get(position).getW_time()+" - "+mDataset.get(position).getD_time());

        holder.mMyCardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(kind.equals("공지사항")) {
                    Intent intent = new Intent(view.getContext(), NoticeDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(PUT_EXTRA_NOTICE, mDataset.get(position));
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }else if(kind.equals("청소구역")){
                // TODO: 2017-10-03 청소구역
                }
                else if(kind.equals("외박일지")){
                    showDialog(view, mDataset.get(position).getSleep_w_time(),mDataset.get(position).getSleep_d_time());
                }

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showDialog(View view, final String wTime, final String dTime){
        final String sleepOutArray[] = {"잔류", "금요외박", "토요외박"};
        final String frontNumberArray[] = {"010", "02", "070"};

        mDatabase = FirebaseDatabase.getInstance();

        final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_style5);

        TextView sleepWTime = mDialog.findViewById(R.id.sleep_w_time);
        TextView sleepDTime = mDialog.findViewById(R.id.sleep_d_time);

        sleepWTime.setText(wTime);
        sleepDTime.setText(dTime);

        Spinner sleepOutSpinner = mDialog.findViewById(R.id.sleep_out);
        sleepOutSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                  mSleepOut = sleepOutArray[position];
              }
              @Override
              public void onNothingSelected(AdapterView<?> adapterView) {
                  mSleepOut = sleepOutArray[0];
              }
        });

        Spinner frontNumberSpinner = mDialog.findViewById(R.id.frontNumber);
        frontNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                mFrontNumber = frontNumberArray[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mFrontNumber = frontNumberArray[0];
            }
        });
        midNumberEt = mDialog.findViewById(R.id.midNumber);
        rearNumberEt = mDialog.findViewById(R.id.rearNumber);


        mDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (!validateForm())
                    return;

                String phoneNumber = mFrontNumber +"-"+midNumberEt.getText().toString()+"-"+rearNumberEt.getText().toString();

                // phoneNumber, dtime, wtime, sleep type
                SleepOut sleepOut = new SleepOut(phoneNumber, mSleepOut);
                mInputUserRefer = mDatabase.getReference();
                mInputUserRefer.child("sleep-out").child(wTime+"-"+dTime)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(sleepOut); // update the firebase database

                mDialog.dismiss();
                return;
            }
        });
        mDialog.show();
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = midNumberEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            midNumberEt.setError("Required.");
            valid = false;
        } else {
            midNumberEt.setError(null);
        }

        String password = rearNumberEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            rearNumberEt.setError("Required.");
            valid = false;
        } else {
            rearNumberEt.setError(null);
        }


        return valid;
    }

}

