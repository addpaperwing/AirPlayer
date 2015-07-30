package com.airplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

/**
 * Created by ZiyiTsang on 15/7/28.
 * blah blah blah
 */
public class AirSwipeRefreshLayout extends SwipeRefreshLayout {

    private static final String LOG_TAG = AirSwipeRefreshLayout.class.getSimpleName();

    private static final int MAX_ALPHA = 255;
    private static final int STARTING_PROGRESS_ALPHA = (int) (.3f * MAX_ALPHA);

    private static final int CIRCLE_DIAMETER = 40;

    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .5f;

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private static final float MAX_PROGRESS_ANGLE = .8f;

    private static final int SCALE_DOWN_DURATION = 150;

    private static final int ALPHA_ANIMATION_DURATION = 300;

    private static final int ANIMATE_TO_TRIGGER_DURATION = 200;

    private static final int ANIMATE_TO_START_DURATION = 200;

    // Default background for the progress spinner
    private static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private OnLoadListener nListener;
//    private View nTarget;
    private boolean nLoading = false;
    private int nTouchSlop;
    private float nTotalPullDistance = -1;
    private int nMediumAnimationDuration;
    private int nCurrentTargetOffsetBottom;

    private boolean nOriginalOffsetCalculated = false;

    private float nInitialMotionY;
    private float nInitialUpY;
    private boolean nIsBeingPull;
    private int nActivePointerId = INVALID_POINTER;

    // Default value is false
    private boolean nScale;

    private boolean nReturnToEnd;
    private DecelerateInterpolator nDecelerateInterpolator;
    private static final int[] LAYOUT_ATTRS = new int[] {
            android.R.attr.enabled
    };


    private CircleImageView nCircleView;
//    private int nCircleViewIndex = -1;

    protected int nFrom;

    private float nStartingScale;

    protected int nOriginalOffsetBottom;

    private MaterialProgressDrawable nProgress;

    private Animation nScaleAnimation;

    private Animation nScaleDownAnimation;

    private Animation nAlphaStartAnimation;

    private Animation nAlphaMaxAnimation;

    private Animation nScaleDownToStartAnimation;

    private float nSpinnerFinalOffset;

    private boolean nNotify;

    private int nCircleWidth;
    private int nCircleHeight;

//    private boolean nUsingCustomStart;

    private Animation.AnimationListener nLoadingListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) { }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (nLoading) {
                nProgress.setAlpha(MAX_ALPHA);
                nProgress.start();
                if (nNotify) {
                    if (nListener != null) {
                        nListener.onLoad();
                    }
                }
            } else {
                nProgress.stop();
                nCircleView.setVisibility(View.GONE);
                nCircleView.getBackground().setAlpha(MAX_ALPHA);
                nProgress.setAlpha(MAX_ALPHA);
                if (nScale) {
                    setAnimationProgress(0 /* animation complete and view is hidden */);
                } else {
                    setTargetOffsetTopAndBottom(mOriginalOffsetTop - nCurrentTargetOffsetBottom,
                            true /* requires update */);
                }
            }
            nCurrentTargetOffsetBottom = nCircleView.getBottom();
        }

        @Override
        public void onAnimationRepeat(Animation animation) { }
    };

    public AirSwipeRefreshLayout(Context context) {
        super(context);
    }

    public AirSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        nTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        nMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

        setWillNotDraw(false);
        nDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        final TypedArray a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS);
        setEnabled(a.getBoolean(0, true));
        a.recycle();

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        nCircleWidth = (int) (CIRCLE_DIAMETER * metrics.density);
        nCircleHeight = (int) (CIRCLE_DIAMETER * metrics.density);

        createProgressView();
        ViewCompat.setChildrenDrawingOrderEnabled(this, true);
        // the absolute offset has to take into account that the circle starts at an offset
        nSpinnerFinalOffset = DEFAULT_CIRCLE_TARGET * metrics.density;
        nTotalPullDistance = nSpinnerFinalOffset;
    }

    private void createProgressView() {
        nCircleView = new CircleImageView(getContext(), CIRCLE_BG_LIGHT, CIRCLE_DIAMETER/2);
        nProgress = new MaterialProgressDrawable(getContext(), this);
        nProgress.setBackgroundColor(CIRCLE_BG_LIGHT);
        nCircleView.setImageDrawable(nProgress);
        nCircleView.setVisibility(View.GONE);
        addView(nCircleView);
    }

    public void setOnListener(OnLoadListener listener) {
        nListener = listener;
    }

    /**
     * Pre API 11, alpha is used to make the progress circle appear instead of scale.
     */
    // for Pre API 11
    private boolean isAlphaUsedForScale() {
        return android.os.Build.VERSION.SDK_INT < 11;
    }


    public void setLoading(boolean loading) {
        if (loading && nLoading != loading) {
            // scale and show
            nLoading = loading;
            int endTarget = (int) (nSpinnerFinalOffset + nOriginalOffsetBottom);
//            if (!nUsingCustomStart) {
//                endTarget = (int) (nSpinnerFinalOffset + nOriginalOffsetBottom);
//            } else {
//                endTarget = (int) nSpinnerFinalOffset;
//            }
            setTargetOffsetTopAndBottom(endTarget - nCurrentTargetOffsetBottom, true /* requires update */);
            nNotify = false;
            startScaleUpAnimation(nLoadingListener);
        } else {
            setLoading(loading, false);
        }
    }

    private void startScaleUpAnimation(Animation.AnimationListener listener) {
        nCircleView.setVisibility(View.VISIBLE);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            // Pre API 11, alpha is used in place of scale up to show the
            // progress circle appearing.
            // Don't adjust the alpha during appearance otherwise.
            nProgress.setAlpha(MAX_ALPHA);
        }
        nScaleAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(interpolatedTime);
            }
        };
        nScaleAnimation.setDuration(nMediumAnimationDuration);
        if (listener != null) {
            nCircleView.setAnimationListener(listener);
        }
        nCircleView.clearAnimation();
        nCircleView.startAnimation(nScaleAnimation);
    }

    /**
     * Doesn't Compatible Pre API 11.
     * If need to be Compatible, this does an alpha animation.
     * @param progress progress
     */
    protected void setAnimationProgress(float progress) {
        // for Pre API 11
        if (isAlphaUsedForScale()) {
            setColorViewAlpha((int) (progress * MAX_ALPHA));
        } else {
            ViewCompat.setScaleX(nCircleView, progress);
            ViewCompat.setScaleY(nCircleView, progress);
        }
    }

    private void setColorViewAlpha(int targetAlpha) {
        nCircleView.getBackground().setAlpha(targetAlpha);
        nProgress.setAlpha(targetAlpha);
    }

    private void setLoading(boolean loading, final boolean notify) {
        if (nLoading != loading) {
            nNotify = notify;
//            ensureTarget();
            nLoading = loading;
            if (nLoading) {
                animateOffsetToCorrectPosition(nCurrentTargetOffsetBottom, nLoadingListener);
            } else {
                startScaleDownAnimation(nLoadingListener);
            }
        }
    }

    private void startScaleDownAnimation(Animation.AnimationListener listener) {
        nScaleDownAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setAnimationProgress(1 - interpolatedTime);
            }
        };
        nScaleDownAnimation.setDuration(SCALE_DOWN_DURATION);
        nCircleView.setAnimationListener(listener);
        nCircleView.clearAnimation();
        nCircleView.startAnimation(nScaleDownAnimation);
    }

    private void startProgressAlphaStartAnimation() {
        nAlphaStartAnimation = startAlphaAnimation(nProgress.getAlpha(), STARTING_PROGRESS_ALPHA);
    }

    private void startProgressAlphaMaxAnimation() {
        nAlphaMaxAnimation = startAlphaAnimation(nProgress.getAlpha(), MAX_ALPHA);
    }

    private Animation startAlphaAnimation(final int startingAlpha, final int endingAlpha) {
        // Pre API 11, alpha is used in place of scale. Don't also use it to
        // show the trigger point.
        if (/* mScale &&  for custom scale */isAlphaUsedForScale()) {
            return null;
        }
        Animation alpha = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                nProgress
                        .setAlpha((int) (startingAlpha+ ((endingAlpha - startingAlpha)
                                * interpolatedTime)));
            }
        };
        alpha.setDuration(ALPHA_ANIMATION_DURATION);
        // Clear out the previous animation listeners.
        nCircleView.setAnimationListener(null);
        nCircleView.clearAnimation();
        nCircleView.startAnimation(alpha);
        return alpha;
    }

    /**
     * Set the colors used in the progress animation. The first
     * color will also be the color of the bar that grows in response to a user
     * swipe gesture.
     *
     * @param colors colors
     */
    @Override
    public void setColorSchemeColors(int... colors) {
        super.setColorSchemeColors(colors);
//        ensureTarget();
        nProgress.setColorSchemeColors(colors);
    }

    public boolean isLoading() {
        return nLoading;
    }

//    private void ensureTarget() {
//        // Don't bother getting the parent height if the parent hasn't been laid
//        // out yet.
//        if (nTarget == null) {
//            for (int i = getChildCount(); i > 0; i--) {
//                View child = getChildAt(i);
//                if (!child.equals(nCircleView)) {
//                    nTarget = child;
//                    break;
//                }
//            }
//        }
//    }

    public void setDistanceToTriggerLoad(int distance) {
        nTotalPullDistance = distance;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        final int width = getMeasuredWidth();
        int circleWidth = nCircleView.getMeasuredWidth();
        int circleHeight = nCircleView.getMeasuredHeight();
        nCircleView.layout((width / 2 - circleWidth / 2), nCurrentTargetOffsetBottom,
                (width / 2 + circleWidth / 2), nCurrentTargetOffsetBottom + circleHeight);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//        if (nTarget == null) {
//            ensureTarget();
//        }
//        if (nTarget == null) {
//            return;
//        }
        nCircleView.measure(MeasureSpec.makeMeasureSpec(nCircleWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(nCircleHeight, MeasureSpec.EXACTLY));
        if (/* !nUsingCustomStart && */ !nOriginalOffsetCalculated) {
            nOriginalOffsetCalculated = true;
            nCurrentTargetOffsetBottom = nOriginalOffsetBottom =
                    getMeasuredHeight() + nCircleView.getMeasuredHeight();
        }
//        nCircleViewIndex = -1;
//        // Get the index of the circleview.
//        for (int index = 0; index < getChildCount(); index++) {
//            if (getChildAt(index) == nCircleView) {
//                nCircleViewIndex = index;
//                break;
//            }
//        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (super.onInterceptTouchEvent(ev)) {
            return true;
        } else {
//            ensureTarget();

            final int action = MotionEventCompat.getActionMasked(ev);

            if (nReturnToEnd && action == MotionEvent.ACTION_DOWN) {
                nReturnToEnd = false;
            }

            if (!isEnabled() || nReturnToEnd || canChildScrollUp() || nLoading) {
                // Fail fast if we're not in a state where a swipe is possible
                return false;
            }

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    setTargetOffsetTopAndBottom(nOriginalOffsetBottom - nCircleView.getBottom(), true);
                    nActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    nIsBeingPull = false;
                    final float initialUpY = getMotionEventY(ev, nActivePointerId);
                    if (initialUpY == -1) {
                        return false;
                    }
                    nInitialUpY = initialUpY;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (nActivePointerId == INVALID_POINTER) {
                        Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                        return false;
                    }

                    final float y = getMotionEventY(ev, nActivePointerId);
                    if (y == -1) {
                        return false;
                    }
                    final float yDiff = y - nInitialUpY;
                    if (yDiff > nTouchSlop && !nIsBeingPull) {
                        nInitialMotionY = nInitialUpY + nTouchSlop;
                        nIsBeingPull = true;
                        nProgress.setAlpha(STARTING_PROGRESS_ALPHA);
                    }
                    break;

                case MotionEventCompat.ACTION_POINTER_UP:
                    onSecondaryPointerUp(ev);
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    nIsBeingPull = false;
                    nActivePointerId = INVALID_POINTER;
                    break;
            }
        }
        return nIsBeingPull;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private boolean isAnimationRunning(Animation animation) {
        return animation != null && animation.hasStarted() && !animation.hasEnded();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (super.onTouchEvent(ev)) {
            return true;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        if (nReturnToEnd && action == MotionEvent.ACTION_DOWN) {
            nReturnToEnd = false;
        }

        if (!isEnabled() || nReturnToEnd || canChildScrollUp()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                nActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                nIsBeingPull = false;
                break;

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, nActivePointerId);
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - nInitialMotionY) * DRAG_RATE;
                if (nIsBeingPull) {
                    nProgress.showArrow(true);
                    float originalDragPercent = overscrollTop / nTotalPullDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
                    float extraOS = Math.abs(overscrollTop) - nTotalPullDistance;
                    float slingshotDist = /* nUsingCustomStart ? nSpinnerFinalOffset
                            - nOriginalOffsetBottom : */ nSpinnerFinalOffset;

                    float tensionSlingshotPercent = Math.max(0,
                            Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                    float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                            (tensionSlingshotPercent / 4), 2)) * 2f;
                    float extraMove = (slingshotDist) * tensionPercent * 2;

                    int targetY = nOriginalOffsetBottom
                            + (int) ((slingshotDist * dragPercent) + extraMove);
                    // where 1.0f is a full circle
                    if (nCircleView.getVisibility() != View.VISIBLE) {
                        nCircleView.setVisibility(View.VISIBLE);
                    }
                    if (!nScale) {
                        ViewCompat.setScaleX(nCircleView, 1f);
                        ViewCompat.setScaleY(nCircleView, 1f);
                    }
                    if (overscrollTop < nTotalPullDistance) {
                        if (nScale) {
                            setAnimationProgress(overscrollTop / nTotalPullDistance);
                        }
                        if (nProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                                && !isAnimationRunning(nAlphaStartAnimation)) {
                            // Animate the alpha
                            startProgressAlphaStartAnimation();
                        }
                        float strokeStart = adjustedPercent * .8f;
                        nProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
                        nProgress.setArrowScale(Math.min(1f, adjustedPercent));
                    } else {
                        if (nProgress.getAlpha() < MAX_ALPHA
                                && !isAnimationRunning(nAlphaMaxAnimation)) {
                            // Animate the alpha
                            startProgressAlphaMaxAnimation();
                        }
                    }
                    float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
                    nProgress.setProgressRotation(rotation);
                    setTargetOffsetTopAndBottom(targetY - nCurrentTargetOffsetBottom,
                            true /* requires update */);
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                nActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (nActivePointerId == INVALID_POINTER) {
                    if (action == MotionEvent.ACTION_UP) {
                        Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    }
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, nActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - nInitialMotionY) * DRAG_RATE;
                nIsBeingPull = false;
                if (overscrollTop > nTotalPullDistance) {
                    setLoading(true, true /* notify */);
                } else {
                    // cancel refresh
                    nLoading = false;
                    nProgress.setStartEndTrim(0f, 0f);
                    Animation.AnimationListener listener = null;
                    if (!nScale) {
                        listener = new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                if (!nScale) {
                                    startScaleDownAnimation(null);
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                        };
                    }
                    animateOffsetToStartPosition(nCurrentTargetOffsetBottom, listener);
                    nProgress.showArrow(false);
                }
                nActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }

    private void animateOffsetToCorrectPosition(int from, Animation.AnimationListener listener) {
        nFrom = from;
        nAnimateToCorrectPosition.reset();
        nAnimateToCorrectPosition.setDuration(ANIMATE_TO_TRIGGER_DURATION);
        nAnimateToCorrectPosition.setInterpolator(nDecelerateInterpolator);
        if (listener != null) {
            nCircleView.setAnimationListener(listener);
        }
        nCircleView.clearAnimation();
        nCircleView.startAnimation(nAnimateToCorrectPosition);
    }

    private void animateOffsetToStartPosition(int from, Animation.AnimationListener listener) {
        if (nScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener);
        } else {
            mFrom = from;
            nAnimateToStartPosition.reset();
            nAnimateToStartPosition.setDuration(ANIMATE_TO_START_DURATION);
            nAnimateToStartPosition.setInterpolator(nDecelerateInterpolator);
            if (listener != null) {
                nCircleView.setAnimationListener(listener);
            }
            nCircleView.clearAnimation();
            nCircleView.startAnimation(nAnimateToStartPosition);
        }
    }

    private final Animation nAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop;
            int endTarget = (int) (nSpinnerFinalOffset - Math.abs(nOriginalOffsetBottom));
//            if (!nUsingCustomStart) {
//                endTarget = (int) (nSpinnerFinalOffset - Math.abs(nOriginalOffsetBottom));
//            } else {
//                endTarget = (int) nSpinnerFinalOffset;
//            }
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - nCircleView.getTop();
            setTargetOffsetTopAndBottom(offset, false /* requires update */);
            nProgress.setArrowScale(1 - interpolatedTime);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop;
        targetTop = (nFrom + (int) ((nOriginalOffsetBottom - nFrom) * interpolatedTime));
        int offset = targetTop - nCircleView.getTop();
        setTargetOffsetTopAndBottom(offset, false /* requires update */);
    }

    private final Animation nAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private void startScaleDownReturnToStartAnimation(int from,
                                                      Animation.AnimationListener listener) {
        mFrom = from;
        if (isAlphaUsedForScale()) {
            nStartingScale = nProgress.getAlpha();
        } else {
            nStartingScale = ViewCompat.getScaleX(nCircleView);
        }
        nScaleDownToStartAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                float targetScale = (nStartingScale + (-nStartingScale  * interpolatedTime));
                setAnimationProgress(targetScale);
                moveToStart(interpolatedTime);
            }
        };
        nScaleDownToStartAnimation.setDuration(SCALE_DOWN_DURATION);
        if (listener != null) {
            nCircleView.setAnimationListener(listener);
        }
        nCircleView.clearAnimation();
        nCircleView.startAnimation(nScaleDownToStartAnimation);
    }

    private void setTargetOffsetTopAndBottom(int offset, boolean requiresUpdate) {
        nCircleView.bringToFront();
        nCircleView.offsetTopAndBottom(offset);
        nCurrentTargetOffsetBottom = nCircleView.getBottom();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == nActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            nActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    public interface OnLoadListener {
        void onLoad();
    }
}
