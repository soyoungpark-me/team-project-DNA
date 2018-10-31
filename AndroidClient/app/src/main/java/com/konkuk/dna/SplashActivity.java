package com.konkuk.dna;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.konkuk.dna.auth.LoginActivity;
import com.konkuk.dna.utils.dbmanage.Dbhelper;

import static com.konkuk.dna.SplashActivity.prgDialog;
import static com.konkuk.dna.SplashActivity.showProgressDialog;

public class SplashActivity extends AppCompatActivity {
    static ProgressDialog prgDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        prgDialog = new ProgressDialog(this);
        //getSupportActionBar().hide();

        AuthAsyncTask aat = new AuthAsyncTask(this);
        aat.execute();

    }

    public static void showProgressDialog(){
        prgDialog.setIcon(R.mipmap.dna_round);
        prgDialog.setTitle("DNA");
        prgDialog.setProgressStyle(0);
        prgDialog.setMessage("로그인 정보를 확인 중입니다.");
        prgDialog.setCanceledOnTouchOutside(false);
        prgDialog.setCancelable(false);
        prgDialog.show();
    }
}

class AuthAsyncTask extends AsyncTask<Integer, Boolean, Boolean> {
    private Context context;
    private Dbhelper dbhelper;

    public AuthAsyncTask(Context context){
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        //로그인 되어 있는지 확인
        //결과를 리턴

        boolean isSuccess;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        dbhelper = new Dbhelper(context);
//        /*
//        * DB에 남은 토큰을 검색해서 expired 확인, 유효하면 바로 chatactivity진입
//        * */
//
//        HttpReqRes httpreq = new HttpReqRes();
//        String responseResult = httpreq.requestHttpGETAuth("https://dna.soyoungpark.me:9011/api/auth/refresh", dbhelper.getRefreshToken());
//
//        JsonToObj jto = new JsonToObj();
//        HashMap<String, String> map = jto.TokenJsonToObj(responseResult);
//
//
//        if(map.get("issuccess").equals("true")){
//            /*
//             * 성공했으면 DB에 저장
//             * */
//            dbhelper = new Dbhelper(context);
//            dbhelper.refreshTokenDB(map);
//            isSuccess = true;
//        }else{
//            /*
//             * 실패했으면 값만 반환
//             * */
//            isSuccess = false;
//        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean isToken) {
        //되어있으면 ActivityChat
        //안 되어있으면 ActivityLogin

        prgDialog.dismiss();


        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity)context).finish();

//        if(isToken){
//            Intent intent = new Intent(context, MainActivity.class);
//            context.startActivity(intent);
//            ((Activity)context).finish();
//        }else{
//            Intent intent = new Intent(context, LoginActivity.class);
//            context.startActivity(intent);
//            ((Activity)context).finish();
//        }
    }
}
