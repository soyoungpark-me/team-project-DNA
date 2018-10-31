package com.konkuk.dna.user;

import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.konkuk.dna.utils.helpers.BaseActivity;
import com.konkuk.dna.utils.helpers.InitHelpers;
import com.konkuk.dna.R;

public class UserFormActivity extends BaseActivity {
    protected DrawerLayout menuDrawer;
    private EditText IDEdit, emailEdit, PWEdit, PWnewEdit, PWcheckEdit, nicknameEdit, infoEdit;
    private ImageView avatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        init();
    }

    public void init() {
        menuDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        InitHelpers.initDrawer(this, menuDrawer, 2);

        IDEdit = (EditText) findViewById(R.id.IDEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        PWEdit = (EditText) findViewById(R.id.PWEdit);
        PWnewEdit = (EditText) findViewById(R.id.PWnewEdit);
        PWcheckEdit = (EditText) findViewById(R.id.PWcheckEdit);
        nicknameEdit = (EditText) findViewById(R.id.nicknameEdit);
        infoEdit = (EditText) findViewById(R.id.infoEdit);
        avatarImage = (ImageView) findViewById(R.id.avatarImage);

        // TODO 기존의 정보를 EditText에 넣어놔야 합니다.
        IDEdit.setText("3457soso");
        emailEdit.setText("soyoungpark.me@gmail.com");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;

            case R.id.menuBtn: // 메뉴 버튼 클릭
                if (!menuDrawer.isDrawerOpen(Gravity.RIGHT)) {
                    menuDrawer.openDrawer(Gravity.RIGHT);
                }
                break;

            case R.id.selectImgBtn: // 이미지 선택 버튼
                break;

            case R.id.profileUpdateBtn: // 프로필 수정 버튼
                break;

        }
    }
}
