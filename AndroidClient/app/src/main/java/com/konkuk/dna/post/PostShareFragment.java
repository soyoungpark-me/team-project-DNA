package com.konkuk.dna.post;

import android.app.DialogFragment;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konkuk.dna.R;
import com.konkuk.dna.utils.helpers.AnimHelpers;

public class PostShareFragment extends DialogFragment implements View.OnClickListener{
    private static Typeface NSB;
    private static Typeface fontAwesomeS;

    private LinearLayout shareChatBtn, shareDMBtn;
    private TextView shareTitle, shareChatText, shareChatIcon, shareDMText, shareDMIcon;

    public PostShareFragment() {}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_share, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        return v;
    }

    public void init() {
        if(NSB == null) {
            NSB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NanumSquareB.ttf");
        }
        if(fontAwesomeS == null) {
            fontAwesomeS = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fa-solid-900.ttf");
        }

        shareTitle = getView().findViewById(R.id.shareTitle);
        shareChatText = getView().findViewById(R.id.shareChatText);
        shareChatIcon = getView().findViewById(R.id.shareChatIcon);
        shareDMText = getView().findViewById(R.id.shareDMtext);
        shareDMIcon = getView().findViewById(R.id.shareDMIcon);
        shareTitle.setTypeface(NSB);
        shareChatText.setTypeface(NSB);
        shareChatIcon.setTypeface(fontAwesomeS);
        shareDMText.setTypeface(NSB);
        shareDMIcon.setTypeface(fontAwesomeS);

        shareChatBtn = getView().findViewById(R.id.shareChatBtn);
        shareDMBtn = getView().findViewById(R.id.shareDMBtn);
        shareChatBtn.setOnClickListener(this);
        shareDMBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setBackgroundDrawableResource(R.drawable.layout_friend_popup);
        window.setLayout(AnimHelpers.dpToPx(getActivity(), 240), AnimHelpers.dpToPx(getActivity(), 180));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shareChatBtn:
                Log.d("PostShare", "share chat");
                break;

            case R.id.shareDMBtn:
                Log.d("PostShare", "share dm");
                break;
        }

    }
}
