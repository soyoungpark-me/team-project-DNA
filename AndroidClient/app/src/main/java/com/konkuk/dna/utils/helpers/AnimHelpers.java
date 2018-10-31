package com.konkuk.dna.utils.helpers;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class AnimHelpers {
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static void animateListHeight(final Context context, final ListView listView, final int height, int from, int to) {
        PropertyValuesHolder topList = PropertyValuesHolder.ofInt("top", from, to);

        ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(topList);
        animator.setDuration(150L);

        ValueAnimator.AnimatorUpdateListener listUpdater = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int top = ((Integer)animation.getAnimatedValue("top")).intValue();
                listView.setTop(top);
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) listView.getLayoutParams();
                for (int i=1; i<=height; i++)
                    params.height = top * dpToPx(context, i);
                listView.requestLayout();
            }
        };

        animator.addUpdateListener(listUpdater);
        animator.start();
    }

    public static void animateViewHeight(final Context context, final View target, int from, int to) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(200L);

        ValueAnimator.AnimatorUpdateListener viewUpdater = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
                layoutParams.height = val;
                target.setLayoutParams(layoutParams);
            }
        };

        animator.addUpdateListener(viewUpdater);
        animator.start();
    }

    public static void animateMargin(final Context context, final View target, final String type, long duration, int from, int to) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(duration);

        ValueAnimator.AnimatorUpdateListener viewUpdater = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) target.getLayoutParams();
                if (type.equals("top")) {
                    params.setMargins(0, value, 0, 0);
                } else if (type.equals("bottom")) {
                    params.setMargins(0, 0, 0, value);
                } else if (type.equals("right")) {
                    params.setMargins(0, 0, value, 0);
                } else if (type.equals("left")) {
                    params.setMargins(value, 0, 0, 0);
                } else if (type.equals("main")) {
                    params.setMargins(0, 0, dpToPx(context, 20), value);
                } else if (type.equals("chat")) {
                    params.setMargins(0, dpToPx(context,value * 3), dpToPx(context, (int)((value-50) * -0.9)), 0);
                }
                target.setLayoutParams(params);
            }
        };

        animator.addUpdateListener(viewUpdater);
        animator.start();
    }
}
