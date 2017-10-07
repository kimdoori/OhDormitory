package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.QRCamActivity;
import kr.hs.emirim.uuuuri.ohdormitory.R;


/**
 * Created by 유리 on 2017-10-01.
 */

public class SleepOutFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    Button mCameraBtn;
    TextView mTextDate;
    TextView mTextMessage;
    TextView mTextParentCall;
    TextView mTextRecognize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.sleep_out_fragment, container, false);

        mTextDate =view.findViewById(R.id.sleep_out_date);
        mTextMessage =view.findViewById(R.id.sleep_out_message);
        mTextParentCall =view.findViewById(R.id.parent_call);
        mTextRecognize =view.findViewById(R.id.sleep_out_recognize);
        mCameraBtn=view.findViewById(R.id.camera);
        mCameraBtn.setVisibility(View.GONE);
        checkRecognize(view);



        return view;
    }


    public void checkRecognize(final View view){

        mDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference sleepOutRef = mDatabase.getReference("sleep-out");

        ValueEventListener sleepOutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot sleepOut) {
                Iterator<DataSnapshot> childIterator = sleepOut.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                String recognize="";
                String date="";
                String patentNumber="";
                while(childIterator.hasNext()) {
                    DataSnapshot sleepOutDate=childIterator.next();
                    String sleepOutStudentKey=sleepOutDate.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();
                    if(sleepOutStudentKey==null){//외박신청안함
                        mTextDate.setText("");
                        mTextMessage.setText("외박 신청이 없습니다.");
                        mTextParentCall.setText("");
                        mTextRecognize.setText("");
                        mCameraBtn.setVisibility(View.GONE);

                        break;
                    }
                    recognize = sleepOutDate.child(sleepOutStudentKey).child("recognize").getValue(String.class);
                    Log.e("레코그나이즈",recognize);
                    date = sleepOutDate.getKey();
                    Log.e("외박날짜",date);
                    patentNumber= sleepOutDate.child(sleepOutStudentKey).child("parentNumber").getValue(String.class);
                    Log.e("부모님 번호",patentNumber);

                    break;
                }
                if(Boolean.parseBoolean(recognize)){//외박신청했고 인증했을 경우
                    mTextDate.setText("");
                    mTextMessage.setText("이미 인증하셨습니다.");
                    mTextParentCall.setText("");
                    mTextRecognize.setText("");
                    mCameraBtn.setVisibility(View.GONE);

//
//                    final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
//                    mDialog.setContentView(R.layout.dialog_style2);
//                    ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("이미 인증되었습니다.");
//                    mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
//                        @Override
//                        public void onClick(View view) {
//                            mDialog.dismiss();
//                        }
//                    });
//                    mDialog.show();

                }else{//인증안했을 경우
                    date+="-";
                    String dateType[]={"년 ","월 ","일  -  ","년 ","월 ","일"};
                    for(int i=0;i<3;i++){
                        date = date.replaceFirst("-",dateType[i]);
                    }
                    for(int i=5;i>=3;i--){
                        date = replaceLast(date,"-",dateType[i]);
                    }

                    mTextDate.setText(date);
                    mTextMessage.setText("인증 연락처 : ");
                    mTextParentCall.setText(patentNumber);
                    mTextRecognize.setText("미인증");
                    mCameraBtn.setVisibility(View.VISIBLE);
                    mCameraBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), QRCamActivity.class);
                            startActivity(intent);
                        }
                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        sleepOutRef.addValueEventListener(sleepOutListener);
    }

    private static String replaceLast(String string, String toReplace, String replacement) {

        int pos = string.lastIndexOf(toReplace);

        if (pos > -1) {

            return string.substring(0, pos)+ replacement + string.substring(pos +   toReplace.length(), string.length());

        } else {

            return string;

        }

    }

}
