package com.vincestyling.ixiaoshuo.ui;

import android.animation.*;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class DetectorScanView extends View {
    protected boolean mIsStartPlaying, mIsTouchDown;

    private Rect[] mLightPointRects;
    private Paint mPaint;

    private Bitmap mNormalBitmap, mPressBitmap;

    private RotateAnimator mRotateAnimator;
    private ShadowFadeAnimator mShadowFadeAnimator;
    private LightPointFadeAnimator mLightPointFadeAnimator;

    private OnScanListener mOnScanListener;

    public DetectorScanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);

        mNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detector_scan_btn_bg_normal);
        mPressBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detector_scan_btn_bg_pressed);
        mShadowFadeAnimator = new ShadowFadeAnimator();

        Bitmap rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detector_scan_btn_rotator);
        mRotateAnimator = new RotateAnimator(rotateBitmap);

        int startY = 0;
        int endY = 0;

        loop:
        for (int y = 0; y < rotateBitmap.getHeight(); y++) {
            for (int x = 0; x < rotateBitmap.getWidth(); x++) {
                if (rotateBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                    startY = y;
                    break loop;
                }
            }
        }

        loop:
        for (int y = rotateBitmap.getHeight() - 1; y > -1; y--) {
            for (int x = 0; x < rotateBitmap.getWidth(); x++) {
                if (rotateBitmap.getPixel(x, y) != Color.TRANSPARENT) {
                    endY = y;
                    break loop;
                }
            }
        }

        int rotateRadius = (endY - startY) / 2;
        // 通过等腰直角三角形公式求出旋转圆内的正方形边长(半边)
        int halfSideLength = (int) Math.sqrt(Math.pow(rotateRadius, 2) / 2);

        // 计算出四个小区用于随机显示闪烁点
        Rect pointsPlaceRect = new Rect();
        pointsPlaceRect.left = startY + rotateRadius - halfSideLength;
        pointsPlaceRect.right = pointsPlaceRect.left + halfSideLength * 2;
        pointsPlaceRect.top = pointsPlaceRect.left;
        pointsPlaceRect.bottom = pointsPlaceRect.right;

        int padding = pointsPlaceRect.width() / 16;

        mLightPointRects = new Rect[4];
        // 左上
        mLightPointRects[0] = new Rect();
        mLightPointRects[0].left = pointsPlaceRect.left + padding;
        mLightPointRects[0].right = mLightPointRects[0].left + halfSideLength - padding;
        mLightPointRects[0].top = mLightPointRects[0].left;
        mLightPointRects[0].bottom = mLightPointRects[0].right;
        // 右上
        mLightPointRects[1] = new Rect();
        mLightPointRects[1].left = mLightPointRects[0].right + padding * 2;
        mLightPointRects[1].right = mLightPointRects[1].left + mLightPointRects[0].width();
        mLightPointRects[1].top = mLightPointRects[0].top;
        mLightPointRects[1].bottom = mLightPointRects[0].bottom;
        // 左下
        mLightPointRects[2] = new Rect();
        mLightPointRects[2].left = mLightPointRects[0].left;
        mLightPointRects[2].right = mLightPointRects[0].right;
        mLightPointRects[2].top = mLightPointRects[0].bottom + padding * 2;
        mLightPointRects[2].bottom = mLightPointRects[2].top + mLightPointRects[0].height();
        // 右下
        mLightPointRects[3] = new Rect();
        mLightPointRects[3].left = mLightPointRects[1].left;
        mLightPointRects[3].right = mLightPointRects[1].right;
        mLightPointRects[3].top = mLightPointRects[2].top;
        mLightPointRects[3].bottom = mLightPointRects[2].bottom;

        mLightPointFadeAnimator = new LightPointFadeAnimator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mShadowFadeAnimator.mFadeBitmap.getWidth(), mShadowFadeAnimator.mFadeBitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mShadowFadeAnimator.draw(canvas);
        if (mIsTouchDown) {
            canvas.drawBitmap(mPressBitmap, 0, 0, mPaint);
        } else if (mIsStartPlaying) {
            canvas.drawBitmap(mNormalBitmap, 0, 0, mPaint);
            mLightPointFadeAnimator.draw(canvas);
            mRotateAnimator.draw(canvas);
        } else {
            canvas.drawBitmap(mNormalBitmap, 0, 0, mPaint);
        }
    }

    private class RotateAnimator {
        private float mDegrees;
        private Bitmap mRotateBitmap;
        private Matrix mMatrix;
        private ObjectAnimator mAnim;

        private RotateAnimator(Bitmap rotateBitmap) {
            mRotateBitmap = rotateBitmap;
            mMatrix = new Matrix();

            mAnim = ObjectAnimator.ofFloat(this, "degrees", 360);
            mAnim.setDuration(1200);
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
            mAnim.setRepeatMode(ValueAnimator.INFINITE);
            mAnim.setEvaluator(new FloatEvaluator());
        }

        public void start() {
            if (mAnim.isStarted()) return;
            mAnim.start();
        }

        public void stop() {
            mAnim.cancel();
        }

        private void draw(Canvas canvas) {
            mMatrix.setRotate(mDegrees, mRotateBitmap.getWidth() / 2, mRotateBitmap.getHeight() / 2);
            canvas.drawBitmap(mRotateBitmap, mMatrix, mPaint);
        }

        public float getDegrees() {
            return mDegrees;
        }

        public void setDegrees(float degrees) {
            this.mDegrees = degrees;
            invalidate();
        }
    }

    private class ShadowFadeAnimator {
        private Bitmap mFadeBitmap;
        private ObjectAnimator mAnim;

        private int mAlpha;

        private ShadowFadeAnimator() {
            mFadeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detector_scan_btn_shadow);

            mAnim = ObjectAnimator.ofInt(this, "alpha", 255);
            mAnim.setDuration(4000);
            mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnim.setRepeatCount(ValueAnimator.INFINITE);
            mAnim.setRepeatMode(ValueAnimator.REVERSE);
            mAnim.setEvaluator(new IntEvaluator());
        }

        public void start() {
            if (mAnim.isStarted()) return;
            mAnim.start();
        }

        public void stop() {
            mAnim.cancel();
        }

        private void draw(Canvas canvas) {
            int savedAlpha = mPaint.getAlpha();
            mPaint.setAlpha(mAlpha);
            canvas.drawBitmap(mFadeBitmap, 0, 0, mPaint);
            mPaint.setAlpha(savedAlpha);
        }

        public int getAlpha() {
            return mAlpha;
        }

        public void setAlpha(int alpha) {
            mAlpha = alpha;
            invalidate();
        }
    }

    private class LightPointFadeAnimator {
        private Bitmap mFadeBitmap;
        private ObjectAnimator mAnim;

        private int mPosLeft, mPosTop;

        private int mAlpha;

        private LightPointFadeAnimator() {
            mFadeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.detector_scan_btn_light_point);
        }

        public void start() {
            int index = StringUtil.nextRandInt(mLightPointRects.length);
            int leftMaxRand = mLightPointRects[index].width() - mFadeBitmap.getWidth() * 2;
            int topMaxRand = mLightPointRects[index].height() - mFadeBitmap.getHeight() * 2;
            mPosLeft = mLightPointRects[index].left + StringUtil.nextRandInt(leftMaxRand);
            mPosTop = mLightPointRects[index].top + StringUtil.nextRandInt(topMaxRand);

            mAnim = ObjectAnimator.ofInt(this, "alpha", 255);
            mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mAnim.setRepeatMode(ValueAnimator.REVERSE);
            mAnim.setEvaluator(new IntEvaluator());
            mAnim.setDuration(1600);
            mAnim.setStartDelay(20);
            mAnim.setRepeatCount(1);

            mAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mIsStartPlaying) start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            mAnim.start();
        }

        public void stop() {
            if (mAnim != null) mAnim.cancel();
        }

        private void draw(Canvas canvas) {
            int mSavedAlpha = mPaint.getAlpha();
            mPaint.setAlpha(mAlpha);
            canvas.drawBitmap(mFadeBitmap, mPosLeft, mPosTop, mPaint);
            mPaint.setAlpha(mSavedAlpha);
        }

        public int getAlpha() {
            return mAlpha;
        }

        public void setAlpha(int alpha) {
            mAlpha = alpha;
            invalidate();
        }
    }

    public void start() {
        mOnScanListener.onPlayStart();
        mLightPointFadeAnimator.start();
        mShadowFadeAnimator.start();
        mRotateAnimator.start();
        mIsStartPlaying = true;
        touchUp();
        invalidate();
    }

    public void stop() {
        mOnScanListener.onPlayStop();
        mLightPointFadeAnimator.stop();
        mRotateAnimator.stop();
        mIsStartPlaying = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsStartPlaying) touchDown();
                break;
            case MotionEvent.ACTION_CANCEL:
                touchUp();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() < 0 || event.getY() < 0) {
                    touchUp();
                } else {
                    switchPlay();
                }
                break;
        }
        return true;
    }

    public synchronized void switchPlay() {
        if (mIsStartPlaying) {
            stop();
        } else {
            start();
        }
    }

    protected void touchDown() {
        mIsTouchDown = true;
        stop();
        invalidate();
    }

    protected void touchUp() {
        mIsTouchDown = false;
    }

    public boolean isPlaying() {
        return mIsStartPlaying;
    }

    public void onVisible() {
        mShadowFadeAnimator.start();
    }

    public void onHidden() {
        mShadowFadeAnimator.stop();
        stop();
    }

    public void setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
    }

    public interface OnScanListener {
        void onPlayStart();

        void onPlayStop();
    }

}
