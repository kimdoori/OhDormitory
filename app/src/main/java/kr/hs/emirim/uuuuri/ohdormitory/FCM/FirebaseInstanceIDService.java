package kr.hs.emirim.uuuuri.ohdormitory.FCM;

/**
 * Created by doori on 2017-10-07.
 */

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;


public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private FirebaseDatabase mDatabase;

    private static final String TAG = "MyFirebaseIIDService";
    String token;
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token= FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + token);

    }

    public void sendRegistrationToServer() {
        onTokenRefresh();
        // Add custom implementation, as needed.
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference tokenRef = mDatabase.getReference("token");

        Map<String, Object> tokenUpdates = new HashMap<String, Object>();
        tokenUpdates.put("students/"+ FirebaseAuth.getInstance().getCurrentUser().getUid(), token);
        tokenRef.updateChildren(tokenUpdates);

    }
}