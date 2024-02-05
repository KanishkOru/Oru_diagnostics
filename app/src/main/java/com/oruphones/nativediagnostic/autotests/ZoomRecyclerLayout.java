package com.oruphones.nativediagnostic.autotests;

// ZoomRecyclerLayout.java

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class ZoomRecyclerLayout extends LinearLayoutManager {

    private final float shrinkAmount = 0.15f;
    private final float shrinkDistance = 0.9f;
    private SmoothScroller smoothScroller;
    private boolean scaleView = true;

    public ZoomRecyclerLayout(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        if (smoothScroller == null) {
            smoothScroller = new SmoothScroller(context);
        }
    }

    public void scaleView(boolean scaleView) {
        this.scaleView = scaleView;
    }

    private boolean isScaleView() {
        return scaleView;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scrollVerticallyBy(0, recycler, state);
        scrollHorizontallyBy(0, recycler, state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            if (isScaleView()) {
                float midpoint = getWidth() / 2f;
                float d0 = 0f;
                float d1 = shrinkDistance * midpoint;
                float s0 = 1f;
                float s1 = 1f - shrinkAmount;
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2f;
                    float d = Math.min(d1, Math.abs(midpoint - childMidpoint));
                    float scale = s0 + (s1 - s0) * (d - d0) / (d1 - d0);
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                }
            }
            return scrolled;
        } else {
            return 0;
        }
    }

    public void setScrollSpeed(float scrollSpeed) {
        smoothScroller.setScrollSpeed(scrollSpeed);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public class SmoothScroller extends LinearSmoothScroller {

        private float speed = 150f;

        public SmoothScroller(Context context) {
            super(context);
        }

        public void setScrollSpeed(float scrollSpeed) {
            speed = scrollSpeed;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return ZoomRecyclerLayout.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + boxEnd) / 2 - (viewStart + viewEnd) / 2;
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return speed / displayMetrics.densityDpi;
        }
    }
}
