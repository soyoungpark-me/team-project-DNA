package com.konkuk.dna.auth;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.konkuk.dna.R;

public class ActivityMissPW extends AppCompatActivity {

    private Button button;
    private EditText Uid;
    private EditText Umail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_pw);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_misspw);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        Uid = (EditText)findViewById(R.id.et_id) ;
        Umail = (EditText)findViewById(R.id.et_mail) ;

        button = (Button) findViewById(R.id.btn_send_pw);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //비밀번호 전송하고 대화상자 띄우기
            }
        });

    }


}
