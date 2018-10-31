package com.konkuk.dna.auth;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.R;

public class SignupAgreeActivity extends BaseActivity {

    private Button btnAgree;
    private CheckBox cbDNA;
    private CheckBox cbPrivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_signup1);
        setSupportActionBar(myToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        //체크박스 두개를 클릭하지 않으면 회원가입을 진행할 수 없게 만들었음.
        btnAgree = (Button)findViewById(R.id.btn_agree);
        btnAgree.setEnabled(false);

        cbDNA = (CheckBox)findViewById(R.id.cb_usedna);
        cbPrivate = (CheckBox)findViewById(R.id.cb_private);

        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbDNA.isChecked() && cbPrivate.isChecked()) {
                    btnAgree.setEnabled(true);
                } else if (btnAgree.isEnabled()) {
                    btnAgree.setEnabled(false);
                }
            }
        };
        cbDNA.setOnCheckedChangeListener(checker);
        cbPrivate.setOnCheckedChangeListener(checker);

        //체크박스가 모두 체크되어있어야만 버튼 클릭 가능
        btnAgree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupAgreeActivity.this, SignupFormActivity.class));
            }

        });
        }
}
