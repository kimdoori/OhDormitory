package kr.hs.emirim.uuuuri.ohdormitory.Fragment;


import android.app.Dialog;
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
    String recognize;
    Button mCameraBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.sleep_out_fragment, container, false);

        view.findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRecognize(view);
            }
        });

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
                while(childIterator.hasNext()) {
                    DataSnapshot sleepOutStudent=childIterator.next();
                    recognize = sleepOutStudent.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("recognize").getValue(String.class);
                    Log.e("레코그나이즈",recognize);
                    break;
                }
                if(Boolean.parseBoolean(recognize)){
                    final Dialog mDialog = new Dialog(view.getContext(), R.style.MyDialog);
                    mDialog.setContentView(R.layout.dialog_style2);
                    ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("이미 인증되었습니다.");
                    mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
                    mDialog.show();

                }else{
                    Intent intent = new Intent(view.getContext(), QRCamActivity.class);
                    startActivity(intent);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        sleepOutRef.addListenerForSingleValueEvent(sleepOutListener);
    }


}
