package com.yuyakaido.android.cardstackview;

import android.view.View;

import androidx.annotation.NonNull;

public interface CardStackListener {
    void onCardDragging(Direction direction, float ratio);
    void onCardSwiped(Direction direction);
    void onCardRewound();
    void onCardCanceled();
    void onCardAppeared(View view, int position);
    void onCardDisappeared(View view, int position);
    boolean shouldCardDismiss(int position, Direction direction);

    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {}
        @Override
        public void onCardSwiped(Direction direction) {}
        @Override
        public void onCardRewound() {}
        @Override
        public void onCardCanceled() {}
        @Override
        public void onCardAppeared(View view, int position) {}
        @Override
        public void onCardDisappeared(View view, int position) {}
        @Override
        public boolean shouldCardDismiss(int position, @NonNull Direction direction) {
            return true;
        }
    };
}
