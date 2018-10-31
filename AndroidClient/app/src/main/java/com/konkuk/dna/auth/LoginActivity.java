package com.konkuk.dna.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.konkuk.dna.MainActivity;
import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.ServerURL;
import com.konkuk.dna.utils.dbmanage.Dbhelper;
import com.konkuk.dna.R;
import com.konkuk.dna.utils.HttpReqRes;
import com.konkuk.dna.utils.JsonToObj;

import java.util.HashMap;

import static com.konkuk.dna.auth.LoginActivity.DialogCannotConnect;
import static com.konkuk.dna.auth.LoginActivity.loginDialog;
import static com.konkuk.dna.auth.LoginActivity.showLoginDialog;
import static com.konkuk.dna.utils.ConvertType.getStringNoQuote;

public class LoginActivity extends BaseActivity {

    private Context context;

    static ProgressDialog loginDialog;
    private android.app.AlertDialog.Builder dialogCNC;

    private Button button;
    private EditText UserID;
    private EditText UserPW;
    private TextView MissPW;
    private TextView SignUp;

    public HashMap<String, String> hm = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialogCNC = new android.app.AlertDialog.Builder(this);

        //getSupportActionBar().hide();
        button = (Button) findViewById(R.id.button_login);
        UserID = (EditText) findViewById(R.id.editText_id);
        UserPW = (EditText) findViewById(R.id.editText_pw);
//        MissPW = (TextView)findViewById(R.id.miss_pw);
        SignUp = (TextView)findViewById(R.id.sign_up);

        // TODO 추후 삭제해야 합니다
        // TODO 테스트 편하게 하기 위해 추가한 부분입니다.
        UserID.setText("3457soso");
        UserPW.setText("qwer1234");
        // TODO 추후 삭제해야 합니다

        //링크에 밑줄처리하기
//        String udata="비밀번호가 기억나지 않는다면?";
//        SpannableString content = new SpannableString(udata);
//        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
//        MissPW.setText(content);

        String udata="회원가입하기";
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        SignUp.setText(content);

        //로그인 부분
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 받아온 메일과 비밀번호로 Auth받아오기
                if(UserID.getText().toString().equals("") || UserPW.getText().toString().equals("")){
                    // 빈칸이라면?
                    Toast.makeText(getApplicationContext(),"정보를 입력해주세요!",Toast.LENGTH_SHORT).show();
                }else{
                    // 로그인 시도하기
                    loginDialog = new ProgressDialog(view.getContext());

                    LoginAsyncTask lat = new LoginAsyncTask(view.getContext(), dialogCNC);
                    lat.execute(UserID.getText().toString(), UserPW.getText().toString());
                }
            }
        });

        //비밀번호가 기억나지 않는다면?
//        MissPW.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, ActivityMissPW.class));
//            }
//        });

        //회원가입하기
        SignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               startActivity(new Intent(LoginActivity.this, SignupAgreeActivity.class));
            }
        });
    }

    //로그인 실패 대화상자 출력
    public static void DialogCannotConnect(android.app.AlertDialog.Builder alt_bld, String msg_server){
        //android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(this);
        alt_bld.setIcon(R.mipmap.dna_round);
        alt_bld.setTitle("DNA");
        alt_bld.setMessage(getStringNoQuote(msg_server)).setCancelable(
                false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alt_bld.create();
        alert.show();
    }

//
//    public void showDenyDialog(int code){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setIcon(R.mipmap.dna_round);
//        builder.setTitle("DNA");
//
//        switch(code){
//            case 0:
//                builder.setMessage("없는 ID입니다.");
//                break;
//            case 1:
//                builder.setMessage("서버에 연결하는데 실패했습니다. 다시 확인하세요.");
//                break;
//        }
//
//        builder.setPositiveButton("확인",null);
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

    public static void showLoginDialog(){
        loginDialog.setIcon(R.mipmap.dna_round);
        loginDialog.setTitle("DNA");
        loginDialog.setProgressStyle(0);
        loginDialog.setMessage("로그인 중입니다.");
        loginDialog.show();
    }
}


class LoginAsyncTask extends AsyncTask<String, Integer, HashMap<String, String>> {
    private Context context;
    private android.app.AlertDialog.Builder dialogCNC;
    private Dbhelper dbhelper;

    public LoginAsyncTask(Context context, android.app.AlertDialog.Builder dialogCNC){
        this.context=context;
        this.dialogCNC=dialogCNC;
    }

    @Override
    protected void onPreExecute() {
        showLoginDialog();
        super.onPreExecute();
    }

    @Override
    protected HashMap<String, String> doInBackground(String... strings) {
        //로그인 되어 있는지 확인
        //결과를 리턴

        boolean isSuccess = false;

        HttpReqRes httpreq = new HttpReqRes();
        String responseResult = httpreq.requestHttpPostLogin(ServerURL.DNA_SERVER+ServerURL.PORT_USER_API+"/users/login", strings[0], strings[1]);

        JsonToObj jto = new JsonToObj();
        HashMap<String, String> map = jto.LoginJsonToObj(responseResult);

        /*
        * 리턴은 결과만 반환해서 밴할지 로그인 성공시킬지 결정
        * */
        return map;
    }

    @Override
    protected void onPostExecute(HashMap<String, String> map) {
        //되어있으면 ActivityChat
        //안 되어있으면 ActivityLogin

        if(map.get("issuccess").equals("true")){
            /*
             * 성공했으면 DB에 저장
             * */
            dbhelper = new Dbhelper(context);
            dbhelper.saveUserInfo(map);
            loginDialog.dismiss();

            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        }else{
            /*
             * 실패했으면 값만 반환
             * */
            loginDialog.dismiss();

            DialogCannotConnect(dialogCNC, map.get("message"));

        }
    }
}

