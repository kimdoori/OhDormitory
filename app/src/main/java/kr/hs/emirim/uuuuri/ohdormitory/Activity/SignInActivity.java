package kr.hs.emirim.uuuuri.ohdormitory.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.hs.emirim.uuuuri.ohdormitory.R;
import kr.hs.emirim.uuuuri.ohdormitory.Model.User;

/**
 * A login screen that offers login via email/password.
 */

public class SignInActivity extends BaseActivity{

    private static final String TAG = "SIGNINACTIVITY";

    EditText mMailEt;
    EditText mPassWordEt;

    private int mRoomNumber;


    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRefer;
    private DatabaseReference mInputUserRefer;

    private ValueEventListener mUserListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigin_in);

        mMailEt = (EditText) findViewById(R.id.email);
        mPassWordEt = (EditText) findViewById(R.id.password);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
//                startSignIn(mMailEt.getText().toString(), mPassWordEt.getText().toString());
                
/*
*               test용 코드
* */
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        if(currentUser!=null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mUserListener != null) {
            mUserRefer.removeEventListener(mUserListener);
        }

    }


    private void startSignIn(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        Log.e(TAG, "signIn:" + email);
        final String longEmail = email+"@e-mirim.hs.kr";
        mUserRefer = mDatabase.getReference("user/"+email+"/allowCode");

        mUserListener = mUserRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Integer value = dataSnapshot.getValue(Integer.class);

                Log.e("이건되냐?", "Value is: " + value);
                if(value ==null){
                    createAccount(email, password);
                }else{
                    switch (value){
                        case -1:
                            inputInfoDialog(email);
                            hideProgressDialog();
                            break;
                        case 0:
                            showDialog("권한을 부여받고 있습니다.");
                            hideProgressDialog();
                            break;
                        case 1:
                            // 승인 완료
                            signIn(longEmail, password);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    private void createAccount(final String email, final String password) {
        String longEmail = email +"@e-mirim.hs.kr";
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(longEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(null, null, -1);
                            mInputUserRefer = mDatabase.getReference();
                            Log.e("OK", "OK");
                            mInputUserRefer.child("user").child(email).setValue(user);
                            Log.e("OK", "OKOK");

                            inputInfoDialog(email);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            //todo 비밀번호 예외 처리
                            showDialog("아이디 등록에 실패하였습니다.");
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mMailEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mMailEt.setError("Required.");
            valid = false;
        } else {
            mMailEt.setError(null);
        }

        String password = mPassWordEt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassWordEt.setError("Required.");
            valid = false;
        } else {
            mPassWordEt.setError(null);
        }

        return valid;
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithEmail:success");
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                        }
                        hideProgressDialog();
                    }
                });
    }


    private void inputInfoDialog(final String email){

        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_style1);

        Spinner spinner = (Spinner) dialog.findViewById(R.id.roomNumber);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomNumber = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        final EditText nameEt = (EditText) dialog.findViewById(R.id.name);

        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString();

                if(name.equals("")||name==null)
                    return;
                User user = new User(name, String.valueOf(mRoomNumber), 0);
                mInputUserRefer = mDatabase.getReference();
                mInputUserRefer.child("user").child(email).setValue(user);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDialog(String text){
        final Dialog dialog = new Dialog(this, R.style.MyDialog);
        dialog.setContentView(R.layout.dialog_style2);

        ((TextView)dialog.findViewById(R.id.dialog_text)).setText(text);
        dialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}

