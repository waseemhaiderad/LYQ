package lnq.com.lnq.custom.views.view_pagify;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import lnq.com.lnq.R;

/**
 * A view that shows items horizontally scrolling list, similar to Cover Flow effect. The items come
 * from the {@link FragmentStatePagerAdapter} associated with this view.
 * <p/>
 * Created by IgorEscodro on 14/03/2016.
 */
public class ViewPagify extends LinearLayout implements View.OnTouchListener {

    /**
     * Constant to represent the default width padding value in dip.
     */
    private static final int WIDTH_PADDING_DEFAULT = 44;

    /**
     * {@link ViewPager} reference.
     */
    private ViewPager mPager;

    /**
     * Width padding between two items items in the list.
     */
    private int mAttrHorizontalPadding;

    /**
     * Attribute to define if the option to click in the previous/next item to change it is enabled
     * or not.
     */
    private boolean mAttrChangeItemByClick;

    /**
     * {@link OnItemClickedListener} reference.
     */
    private OnItemClickedListener mListener;

    /**
     * {@link GestureDetector} reference.
     */
    private GestureDetector mDetector;

    /**
     * {@link ViewPagifyPageTransformer} reference.
     */
    private ViewPagifyPageTransformer mTransformer;

    /**
     * Create a new instance of {@link ViewPagify}.
     *
     * @param context {@link Context}
     */
    public ViewPagify(Context context) {
        this(context, null);
    }

    /**
     * Create a new instance of {@link ViewPagify}.
     *
     * @param context {@link Context} instance
     * @param attrs   {@link AttributeSet} instance
     */
    public ViewPagify(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateAttributeSetValues(context, attrs);
        init(context);
    }

    /**
     * Initialize the view and sets the basic configurations.
     *
     * @param context {@link Context} instance
     */
    private void init(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.pagify_view, this);
        mPager = (ViewPager) view.findViewById(R.id.baseViewPager);
        mTransformer = new ZoomOutPageTransformer();
        mPager.setClipToPadding(false);
        mPager.setOffscreenPageLimit(3);
        mPager.setOnTouchListener(this);
        mDetector = new GestureDetector(getContext(), new PagifyGestureDetector());
        updatePadding();
    }

    /**
     * Gets and updates the {@link AttributeSet} values.
     *
     * @param context {@link Context} instance
     * @param attrs   {@link AttributeSet} instance
     */
    private void updateAttributeSetValues(Context context, AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable
                    .ViewPagify);
            mAttrHorizontalPadding = typedArray.getDimensionPixelSize(R.styleable
                    .ViewPagify_horizontalPadding, getDPI(WIDTH_PADDING_DEFAULT));
            mAttrChangeItemByClick = typedArray.getBoolean(R.styleable.
                    ViewPagify_changeItemByClick, false);
            typedArray.recycle();
        }
    }

    /**
     * Set a {@link ViewPagifyPageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder <b>true</b> if the supplied PageTransformer requires page views to
     *                            be drawn from last to first instead of first to last.
     * @param transformer         {@link ViewPagifyPageTransformer} that will modify each page's
     *                            animation properties
     */
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPagifyPageTransformer
            transformer) {
        mTransformer = transformer;
        mTransformer.setPadding(mAttrHorizontalPadding);
        mPager.setPageTransformer(reverseDrawingOrder, mTransformer);
    }

    /**
     * Sets a padding between the items. The higher the value is, smaller and closer the items will
     * be.
     *
     * @param padding width padding in pixels
     */
    public void setHorizontalPadding(int padding) {
        if (padding > 0) {
            mAttrHorizontalPadding = padding;
            updatePadding();
        }
    }

    /**
     * Get the currently selected page.
     *
     * @return current index
     */
    public int getCurrentItemPosition() {
        return mPager.getCurrentItem();
    }

    /**
     * Set the currently selected page.
     *
     * @param position index to select
     */
    public void setCurrentItemPosition(int position) {
        mPager.setCurrentItem(position);
    }

    /**
     * Updates the padding. It ensures that the {@link Rect#left} and {@link Rect#right} has the
     * same value to keep the ratio and animation seamlessly.
     */
    private void updatePadding() {
        mPager.setPadding(mAttrHorizontalPadding, mPager.getTop(), mAttrHorizontalPadding,
                mPager.getBottom());
        setPageTransformer(true, mTransformer);
    }

    /**
     * Converts pixels in dpi.
     *
     * @param pixels value to be converted
     *
     * @return value in dpi
     */
    private int getDPI(int pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getContext()
                .getResources().getDisplayMetrics());
    }

    /**
     * Set the {@link OnItemClickedListener}.
     *
     * @param listener {@link OnItemClickedListener} to be notified
     */
    public void setOnItemClickListener(OnItemClickedListener listener) {
        mListener = listener;
    }

    /**
     * Set the {@link PagerAdapter}.
     *
     * @param adapter {@link PagerAdapter} reference.
     */
    public void setAdapter(PagerAdapter adapter) {
        mPager.setAdapter(adapter);
    }

    /**
     * Add a listener that will be invoked whenever the page changes or is incrementally scrolled.
     *
     * @param listener {@link ViewPager.OnPageChangeListener} to be notified
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mPager.addOnPageChangeListener(listener);
    }

    /**
     * Gets the {@link ViewPager} within the {@link ViewPagify}.
     *
     * @return {@link ViewPager} reference.
     */
    public ViewPager getViewPager() {
        return mPager;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    /**
     * A {@link GestureDetector} to detect when the user clicked in the {@link ViewPagify}.
     */
    private class PagifyGestureDetector extends GestureDetector.SimpleOnGestureListener {

        /**
         * {@inheritDoc}<p/> <b>Override</b><br> When the user does a single tap, it is verified if
         * the position tapped was outside the current item. If is outside right, it goes to the
         * next item, otherwise it goes to the previous item.<br>It is not needed to use {@link
         * GestureDetector.SimpleOnGestureListener#onSingleTapConfirmed(MotionEvent)} because double
         * click is not applicable in this case.
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = mPager.getCurrentItem();
            final View view = ((Fragment) mPager.getAdapter().instantiateItem(mPager,
                    position)).getView();
            if (view != null) {
                final Rect myViewRect = new Rect();
                view.getGlobalVisibleRect(myViewRect);
                float positionRight = myViewRect.right;
                float positionLeft = myViewRect.left;

                if (e.getX() > positionRight) {
                    mPager.setCurrentItem(position + 1, true);
                } else if (e.getX() < positionLeft) {
                    mPager.setCurrentItem(position - 1, true);
                } else if (mAttrChangeItemByClick) {
                    mListener.onItemClick(mPager, view, position);
                }
            }
            return super.onSingleTapUp(e);
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item in this {@link ViewPagify} has
     * been clicked.
     */
    public interface OnItemClickedListener {

        /**
         * Callback method to be invoked when an item in this {@link ViewPagify} has been clicked.
         *
         * @param parent   The {@link ViewPagify} where the click happened
         * @param view     The view within the {@link ViewPagify} that was clicked
         * @param position The position of the view in the {@link ViewPagify}
         */
        void onItemClick(ViewPager parent, View view, int position);
    }
}
