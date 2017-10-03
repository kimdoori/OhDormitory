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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kr.hs.emirim.uuuuri.ohdormitory.Model.User;
import kr.hs.emirim.uuuuri.ohdormitory.R;

// TODO: 2017-10-02 패스워드 틀릴경우 안내
public class SignInActivity extends BaseActivity{

    private final String TAG = "SIGNINACTIVITY";
    private final String CHECK_HISTORY = "CHECK_HISTORY";
    private final String ALLOW_DOMAIN = "@e-mirim.hs.kr";
    EditText mMailEt;
    EditText mPassWordEt;
    Dialog mDialog;

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

        getWindow().setBackgroundDrawableResource(R.drawable.signin);

        mMailEt = (EditText) findViewById(R.id.email);
        mPassWordEt = (EditText) findViewById(R.id.password);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                startSignIn(mMailEt.getText().toString(), mPassWordEt.getText().toString());

/*
*               test용 코드
* */
//                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();

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
            String email = mAuth.getCurrentUser().getEmail().replace(ALLOW_DOMAIN, "");
            startSignIn(email, CHECK_HISTORY);
            mMailEt.setText(email);
            mMailEt.setError(null);
            mPassWordEt.setError(null);
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

        if (!validateForm() && !(password.equals(CHECK_HISTORY))) {
            return;
        }

        showProgressDialog();

        final String longEmail = email+ALLOW_DOMAIN;
        mUserRefer = mDatabase.getReference("user/"+email);

        mUserListener = mUserRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Integer value = dataSnapshot.child("allowCode").getValue(Integer.class);


                if(mDialog!=null)
                    mDialog.dismiss();

                Log.e("이건되냐?", "Value is: " + value);

                if(value == null && password!=null){
                    createAccount(email, password);
                }else{

                    switch (value){
                        case -2: // 권한 거절
                            String error = "권한을 거절당하셨습니다.\n입력하신 정보를 확인해주세요.";
                            inputInfoDialog(error, email, password, dataSnapshot.child("roomNumber").getValue(Integer.class), dataSnapshot.child("name").getValue(String.class));
                            break;
                        case -1:
                            hideProgressDialog();
                            reInputInfoDialog(longEmail, password, null, null);
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
        String longEmail = email +ALLOW_DOMAIN;
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(longEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(mAuth.getCurrentUser().getUid(), null, -1, -99);
                            mInputUserRefer = mDatabase.getReference();
                            mInputUserRefer.child("user").child(email).setValue(user);
                            inputInfoDialog(email, null, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                showDialog("비밀번호를 확인하세요.");
                            } catch (Exception e) {
                                showDialog("아이디 등록에 실패하였습니다.");
                            }
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
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

        if(password.equals(CHECK_HISTORY)){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
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
                            Log.e(TAG, "SignIn:failure", task.getException());

                        }
                        hideProgressDialog();
                    }
                });
    }


    private void inputInfoDialog(final String email, Integer roomNumber, String name){
        Log.e("유후~", email+roomNumber+name);

        mDialog = new Dialog(this, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_style1);
        int selectedPosition = 0;

        Spinner spinner = (Spinner) mDialog.findViewById(R.id.roomNumber);
        if(roomNumber!= null)
            if(roomNumber>=0 && roomNumber<37)
                spinner.setSelection(roomNumber);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mRoomNumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        final EditText nameEt = (EditText) mDialog.findViewById(R.id.name);
        if(name != null)
            nameEt.setText(name);
        mDialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEt.getText().toString();

                if(name.equals("")||name==null)
                    return;
                // TODO: 2017-10-03   -2 상태에서 다시 제출 할 때 getUid() => null
                User user = new User(getUid(), name, 0, mRoomNumber);
                mInputUserRefer = mDatabase.getReference();
                mInputUserRefer.child("user").child(email).setValue(user);
                mDialog.dismiss();
                mDialog = null;
            }
        });
        mDialog.show();
    }

    private void showDialog(String text){
        mDialog = new Dialog(this, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_style2);

        ((TextView) mDialog.findViewById(R.id.dialog_text)).setText(text);
        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mDialog = null;
                return;
            }
        });
        mDialog.show();

    }

    private void inputInfoDialog(final String text, final String email, final String password, final Integer roomNumber, final String name){
        mDialog = new Dialog(this, R.style.MyDialog);
        mDialog.setContentView(R.layout.dialog_style2);
        ((TextView) mDialog.findViewById(R.id.dialog_text)).setText(text);
        mDialog.show();
        hideProgressDialog();

        mDialog.findViewById(R.id.dialog_button_yes).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                mDialog= null;
                reInputInfoDialog(email+ALLOW_DOMAIN, password, roomNumber, name);
            }
        });

    }

    private void reInputInfoDialog(final String longEmail, String password, final Integer roomNumber, final String name){
        final String email = longEmail.replace(ALLOW_DOMAIN, "");

        mAuth.signInWithEmailAndPassword(longEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithEmail:success");
                            inputInfoDialog(email, roomNumber, name);
                        } else {
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                        }
                        hideProgressDialog();
                    }
                });
    }
}

