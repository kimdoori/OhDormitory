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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import kr.hs.emirim.uuuuri.ohdormitory.Activity.QRCamActivity;
import kr.hs.emirim.uuuuri.ohdormitory.R;

import static kr.hs.emirim.uuuuri.ohdormitory.R.id.sleep_out_date;


/**
 * Created by 유리 on 2017-10-01.
 */

// TODO: 2017-10-21  외박일지 신청안함 메세지 고정
public class SleepOutFragment extends Fragment {

    private FirebaseDatabase mDatabase;

    Button mCameraBtn;
    TextView mTextDate;
    TextView mTextMessage;
    TextView mTextParentCall;
    TextView mTextRecognize;

    private String maxDate="2000.1.1";
    private String maxFBdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.sleep_out_fragment, container, false);

        mTextDate =view.findViewById(sleep_out_date);
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

                while(childIterator.hasNext()) {
                    DataSnapshot sleepDataSnapshot = childIterator.next();
                    String sleepOutDate = sleepDataSnapshot.getKey();
                    checkDate(sleepOutDate);
                }

                String recognize="";
                String date="";
                String patentNumber="";
                int cnt=0;
                boolean sleepOutConfirm=true;


                    Iterator<DataSnapshot> sleepOutStudentMember=sleepOut.child(maxFBdate).getChildren().iterator();
                    while(sleepOutStudentMember.hasNext()) {
                        DataSnapshot sleepOutMember=sleepOutStudentMember.next();
                        String myId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String student=sleepOutMember.getKey();
                        if(student.equals("send")) continue;
                        Log.e("key",student);
                        Log.e("my",myId);

                        Log.e("confirm", String.valueOf(sleepOutConfirm));

                        //외박신청안함
                        if(!myId.equals(student)){
                            mTextDate.setText("");
                            mTextMessage.setText("외박 신청이 없습니다.");
                            mTextParentCall.setText("");
                            mTextRecognize.setText("");
                            mCameraBtn.setVisibility(View.GONE);
                            sleepOutConfirm=false;
                            continue;
                        }
                        sleepOutConfirm=true;

                        recognize = sleepOutMember.child("recognize").getValue(String.class);
                        Log.e("레코그나이즈",recognize);
                        date = maxFBdate;
                        Log.e("외박날짜",date);
                        patentNumber= sleepOutMember.child("parentNumber").getValue(String.class);
                        Log.e("부모님 번호",patentNumber);
                        break;
                    }



                Log.e("sadladlald", String.valueOf(sleepOutConfirm));

                if(Boolean.parseBoolean(recognize) && sleepOutConfirm){//외박신청했고 인증했을 경우
                    Log.e("외박","인증했습니다.");
                    mTextDate.setText("");
                    mTextMessage.setText("이미 인증하셨습니다.");
                    mTextParentCall.setText("");
                    mTextRecognize.setText("");
                    mCameraBtn.setVisibility(View.GONE);

                }else if(!Boolean.parseBoolean(recognize) && sleepOutConfirm){//인증안했을 경우
                    Log.e("외박","인증안했습니다.");

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
    //TODO : 제일최근날짜체크
    public void checkDate(String fbDate) {
        String simpleFBDate=fbDate.replaceFirst("-",".");
        simpleFBDate=simpleFBDate.replaceFirst("-",".");

        int index = simpleFBDate.indexOf("-");
        simpleFBDate = simpleFBDate.substring(0,index);

        Log.e("파베 저장되있는 날짜",simpleFBDate);
        Log.e("maxDate",maxDate);

        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            Date max = format.parse(maxDate);

            Date fb = format.parse(simpleFBDate);

            long calDate = max.getTime() - fb.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);
            if(calDateDays <= 0) {//max가 더 빠르다면
                maxDate=simpleFBDate;
                maxFBdate=fbDate;
            }

        } catch (ParseException e) {
            Log.e("날짜 파싱에러","서로 타입이 맞지 않음.");
        }

    }

}
