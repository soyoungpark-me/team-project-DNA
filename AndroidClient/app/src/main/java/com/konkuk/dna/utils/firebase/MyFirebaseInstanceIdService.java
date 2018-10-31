package com.konkuk.dna.utils.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

// 사용자의 기기별로 token을 생성함
// 나중에 push 알림을 특정 타겟에 보낼 때 사용된다.
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseInstanceId";

    @Override
    public void onTokenRefresh() {
        Log.d("test", "here");
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        // 생성, 등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token);
    }


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        // TODO url에는 토큰을 받아서 DB에 저장할 서버의 주소를 넣어줘야 함
        Request request = new Request.Builder()
                .url("http://서버주소/fcm/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
