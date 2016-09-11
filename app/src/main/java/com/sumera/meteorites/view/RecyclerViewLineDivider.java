package com.sumera.meteorites.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sumera.meteorites.R;

public class RecyclerViewLineDivider extends RecyclerView.ItemDecoration {

    private final Drawable m_divider;

    public RecyclerViewLineDivider(Context context) {
        m_divider = ResourcesCompat.getDrawable(context.getResources(), R.drawable.line_divider, null);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + m_divider.getIntrinsicHeight();

            m_divider.setBounds(left, top, right, bottom);
            m_divider.draw(c);
        }
    }
}