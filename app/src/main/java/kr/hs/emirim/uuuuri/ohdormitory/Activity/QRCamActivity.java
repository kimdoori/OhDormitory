package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kr.hs.emirim.uuuuri.ohdormitory.R;

public class QRCamActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcam);

        new IntentIntegrator(this).initiateScan();


    }
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //  com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
        //  = 0x0000c0de; // Only use bottom 16 bits
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result.getContents()==null) {
                // 취소됨
                final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                mDialog.setContentView(R.layout.dialog_style2);
                ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("취소되었습니다.");
                mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();

                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                mDialog.show();
            } else {
                // 스캔된 QRCode --> result.getContents()
                Log.e("결과",result.getContents());
                recognizeSleepOut(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void recognizeSleepOut(final String contents){

        mDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference sleepOutRef = mDatabase.getReference("sleep-out");

        ValueEventListener sleepOutListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot sleepOut) {
                Iterator<DataSnapshot> childIterator = sleepOut.getChildren().iterator();
                //users의 모든 자식들의 key값과 value 값들을 iterator로 참조
                while(childIterator.hasNext()) {
                    DataSnapshot sleepOutStudent=childIterator.next();
                    String qrContent[] = contents.split("/");
                    String uid = sleepOutStudent.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getKey();

                    if(qrContent[1].equals(uid)){
                        Map<String, Object> sleepRecognizeUpdates = new HashMap<String, Object>();
                        sleepRecognizeUpdates.put(contents+"/recognize", "true");
                        sleepOutRef.updateChildren(sleepRecognizeUpdates);
                        final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                        mDialog.setContentView(R.layout.dialog_style2);
                        ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("인증되었습니다.");
                        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(QRCamActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        mDialog.show();


                    }else{
                        final Dialog mDialog = new Dialog(QRCamActivity.this, R.style.MyDialog);
                        mDialog.setContentView(R.layout.dialog_style2);
                        ((TextView)mDialog.findViewById(R.id.dialog_text)).setText("본인의 부모님께 발송된 QR코드로 인증해주세요.");
                        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(QRCamActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                        mDialog.show();


                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        sleepOutRef.addListenerForSingleValueEvent(sleepOutListener);







    }

}
