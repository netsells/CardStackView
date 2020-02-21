package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.yuyakaido.android.cardstackview.internal.CardStackDataObserver;
import com.yuyakaido.android.cardstackview.internal.CardStackSnapHelper;
import com.yuyakaido.android.cardstackview.internal.CardStackState;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CardStackView extends RecyclerView {

    private final CardStackDataObserver observer = new CardStackDataObserver(this);

    public CardStackView(Context context) {
        this(context, null);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardStackView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    @Override
    public void setLayoutManager(LayoutManager manager) {
        if (manager instanceof CardStackLayoutManager) {
            super.setLayoutManager(manager);
        } else {
            throw new IllegalArgumentException("CardStackView must be set CardStackLayoutManager.");
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getLayoutManager() == null) {
            setLayoutManager(new CardStackLayoutManager(getContext()));
        }
        // Imitate RecyclerView's implementation
        // http://tools.oesf.biz/android-9.0.0_r1.0/xref/frameworks/base/core/java/com/android/internal/widget/RecyclerView.java#1005
        if (getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(observer);
            getAdapter().onDetachedFromRecyclerView(this);
        }
        adapter.registerAdapterDataObserver(observer);
        super.setAdapter(adapter);
    }

    private boolean freeze = false;

    public void freeze() {
        freeze = true;
    }

    public void unfreeze() {
        freeze = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (freeze) {
            return false;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (freeze) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();

            if (manager != null) {
                manager.updateProportion(event.getX(), event.getY());
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    public void swipe() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            smoothScrollToPosition(manager.getTopPosition() + 1);
        }
    }

    public void rewind() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            smoothScrollToPosition(manager.getTopPosition() - 1);
        }
    }

    private void initialize() {
        new CardStackSnapHelper().attachToRecyclerView(this);
        setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    public void resetRightScroll() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            manager.state.next(CardStackState.Status.Idle);
            manager.state.proportion = 0.0f;
            manager.state.targetPosition = manager.getTopPosition();
            smoothScrollBy(manager.state.dx, manager.state.dy);
        }
    }

    public void completeRightScroll() {
        if (getLayoutManager() instanceof CardStackLayoutManager) {
            CardStackLayoutManager manager = (CardStackLayoutManager) getLayoutManager();
            manager.state.targetPosition = manager.getTopPosition() + 1;
            manager.onScrollStateChanged(RecyclerView.SCROLL_STATE_IDLE);
        }
    }
}