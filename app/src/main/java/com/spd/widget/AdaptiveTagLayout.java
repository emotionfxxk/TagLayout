package com.spd.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.spd.taglayout.R;

/**
 * Created by Sean.Xu on 12/14/15.
 */
public class AdaptiveTagLayout extends ViewGroup {
    private final static String TAG = "AdaptiveTagLayout";
    private final static int DEFAULT_SLOT_COUNT = 3;
    private final static float DEFAULT_TAG_MARGIN = 8.0f;
    int deviceWidth;
    int slotCount;
    float tagMargin;

    public AdaptiveTagLayout(Context context) {
        this(context, null, 0);
    }

    public AdaptiveTagLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdaptiveTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
        final DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AdaptiveTagLayout, 0, 0);
        try {
            slotCount = ta.getInt(R.styleable.AdaptiveTagLayout_slot_count, DEFAULT_SLOT_COUNT);
            tagMargin = dm.density * ta.getDimension(R.styleable.AdaptiveTagLayout_tag_margin, DEFAULT_TAG_MARGIN);
            Log.i(TAG, "deviceWidth:" + deviceWidth + ", slotCount:" + slotCount +
                    ", tagMargin:" + tagMargin + ", dm.density:" + dm.density);
        } finally {
            ta.recycle();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        final int childWidth = childRight - childLeft;
        final int childHeight = childBottom - childTop;

        final int slotWidth = (int)((childWidth - tagMargin * (slotCount + 1)) / slotCount);
        Log.i(TAG, "onLayout slotWidth:" + slotWidth);

        maxHeight = 0;
        curLeft = childLeft;
        curTop = childTop + (int)tagMargin;
        int remainEmptySlotInRow = slotCount;
        int occupiedSlots;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            Log.i(TAG, "onLayout child index:" + i + ", curWidth:" + curWidth + ", curHeight:" + curHeight);

            curLeft += (int)tagMargin;
            if (curWidth <= slotWidth) {
                // current child take 1 slot
                occupiedSlots = 1;
            } else if(curWidth > slotWidth && curWidth <= (slotWidth * 2 + tagMargin)) {
                if (remainEmptySlotInRow > 1) {
                    // current child take 2 slot
                    occupiedSlots = 2;
                } else {
                    // current child take 1 slot
                    occupiedSlots = 1;
                }
            } else {
                // current child take 3 slot
                occupiedSlots = remainEmptySlotInRow;
            }
            Log.i(TAG, "onLayout remainEmptySlotInRow:" + remainEmptySlotInRow + ", occupiedSlots:" + occupiedSlots);
            remainEmptySlotInRow -= occupiedSlots;
            curWidth = (int)(occupiedSlots * slotWidth + (occupiedSlots - 1) * tagMargin);
            Log.i(TAG, "after adjust child index:" + i + ", curWidth:" + curWidth + ", curHeight:" + curHeight);
            child.measure(MeasureSpec.makeMeasureSpec(curWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(curHeight, MeasureSpec.EXACTLY));
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            if (remainEmptySlotInRow == 0) {
                // next line
                curLeft = childLeft;
                curTop += maxHeight + tagMargin;
                maxHeight = 0;
                remainEmptySlotInRow = slotCount;
            } else {
                if (maxHeight < curHeight)
                    maxHeight = curHeight;
                curLeft += curWidth;
            }
        }
    }
}