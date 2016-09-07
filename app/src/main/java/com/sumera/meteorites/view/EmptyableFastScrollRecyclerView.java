package com.sumera.meteorites.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by martin on 30/01/16.
 */
public class EmptyableFastScrollRecyclerView extends RecyclerView {

    private View m_emptyView;

    private View m_fastScrollView;

    final private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIsIsAdapterEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIsIsAdapterEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIsIsAdapterEmpty();
        }
    };

    public EmptyableFastScrollRecyclerView(Context context) {
        super(context);
    }

    public EmptyableFastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyableFastScrollRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void checkIsIsAdapterEmpty() {
        if (m_emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            m_emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }

        if(m_fastScrollView != null && getAdapter() != null) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            m_fastScrollView.setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIsIsAdapterEmpty();
    }

    public void setEmptyView(View emptyView) {
        m_emptyView = emptyView;
        checkIsIsAdapterEmpty();
    }

    public void setFastScrollView(View fastScrollView) {
        m_fastScrollView = fastScrollView;
        checkIsIsAdapterEmpty();
    }
}