package com.vincestyling.ixiaoshuo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.*;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.doc.Document;
import com.vincestyling.ixiaoshuo.doc.LayoutUtil;
import com.vincestyling.ixiaoshuo.event.OnDownloadChapterListener;
import com.vincestyling.ixiaoshuo.event.ReaderSupport;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;

public class ReadingBoard extends View {
    private Document mDoc;

    // added to support full-justification text layout, make it LARGE enough to hold
    // even 2048 GBK chars per line, rarely possible to reach this limit in reality.
    private float mLayoutPositions[] = new float[4096];

    public ReadingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Document doc) throws Exception {
        mDragPageShadowWidth = getContext().getResources().getDimensionPixelSize(R.dimen.reading_board_page_shadow_width);
        mDragPageShadow = getContext().getResources().getDrawable(R.drawable.reading_board_page_shadow);

        ReaderSupport.setDownloadChapterListener(new OnDownloadChapterListener() {
            private ProgressDialog mLoadingPrgreDialog;

            @Override
            public void onDownloadStart(Chapter chapter) {
                mLoadingPrgreDialog = ProgressDialog.show(getContext(), null,
                        getResources().getString(R.string.loading_tip_msg), false, false);
                mDoc.onDownloadStart();
            }

            @Override
            public void onDownloadComplete(Chapter chapter) {
                boolean willAdjust = mLoadingPrgreDialog.isShowing();
                if (willAdjust) mLoadingPrgreDialog.cancel();
                mLoadingPrgreDialog = null;

                if (chapter.isNativeChapter()) {
                    mDoc.onDownloadComplete(true, willAdjust);
                    if (willAdjust) turnToChapter(chapter);
                } else {
                    mDoc.onDownloadComplete(false, willAdjust);
                    if (willAdjust) getActivity().showToastMsg(R.string.without_data);
                }
            }
        });

        mDoc = doc;
        turnToChapter(ReaderSupport.getCurrentChapter());

        setFocusable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMainPageBitmap == null) resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
        drawSmoothSlidePage(canvas);
    }

    private Bitmap mMainPageBitmap, mBackupPageBitmap;
    private Canvas mDrawableCanvas;

    private void resetPageBitmapDrawable(int width, int height) {
        mBackupPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mMainPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mDrawableCanvas = new Canvas();
        drawCurrentPageContent();
    }

    private Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

    private void drawCurrentPageContent() {
        RenderPaint.get().getFontMetrics(mFontMetrics);

        mDrawableCanvas.setBitmap(mBackupPageBitmap);
        mDrawableCanvas.drawBitmap(mMainPageBitmap, 0, 0, null);

        mDrawableCanvas.setBitmap(mMainPageBitmap);

        RenderPaint.get().drawReadingBg(mDrawableCanvas);

        mDrawableCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mDrawableCanvas.translate(RenderPaint.get().getCanvasPaddingLeft(), RenderPaint.get().getCanvasPaddingTop() - mFontMetrics.ascent);

        mDoc.prepareGetLines();
        StringBuilder sb = new StringBuilder();
        float contentHeight = 0;
        byte flags;
        while (true) {
            flags = mDoc.getNextLine(sb);
            if (flags == 0) break;

            if ((flags & Document.GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY) > 0) {
                LayoutUtil.layoutTextJustified(sb, RenderPaint.get(), 0, contentHeight, mLayoutPositions);
                mDrawableCanvas.drawPosText(sb.toString(), mLayoutPositions, RenderPaint.get());
            } else {
                mDrawableCanvas.drawText(sb, 0, sb.length(), 0, contentHeight, RenderPaint.get());
            }
            sb.delete(0, sb.length());

            contentHeight += RenderPaint.get().getTextHeight();
            if ((flags & Document.GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS) > 0)
                contentHeight += RenderPaint.get().getParagraphSpacing();
            else
                contentHeight += RenderPaint.get().getLineSpacing();
        }

        mDoc.calculatePagePosition();
        mDrawableCanvas.restore();
    }

    public void forceRedraw(boolean applyEffect) {
        // 这个方法有可能在初始化(onDraw)未完成时就调用
        if (mDrawableCanvas != null) {
            if (applyEffect) {
                resetDraggingBitmapX();
                mAutoSlide = true;
            }
            drawCurrentPageContent();
        }
        invalidate();
    }

    // 0: previous page, 1: next page
    private int mTurningPageDirection;

    public boolean turnPreviousPage(boolean redraw) {
        mTurningPageDirection = 0;
        if (mDoc.turnPreviousPage()) {
            if (redraw) forceRedraw(true);
            return true;
        }
        return false;
    }

    public boolean turnNextPage(boolean redraw) {
        mTurningPageDirection = 1;
        if (mDoc.turnNextPage()) {
            if (redraw) forceRedraw(true);
            return true;
        }
        return false;
    }

    public boolean turnPageByDirection() {
        if (mTurningPageDirection == 0) return turnPreviousPage(false);
        return turnNextPage(false);
    }

    public void setColorScheme(ColorScheme colorScheme) {
        RenderPaint.get().setColor(colorScheme.getTextColor());

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Drawable bgDrawable = colorScheme.getSchemeDrawable(getResources());
        bgDrawable.setBounds(0, 0, display.getWidth(), display.getHeight());
        RenderPaint.get().setReadingBg(bgDrawable);

        forceRedraw(false);
    }

    public void adjustTextSize(int textSize) {
        RenderPaint.get().setTextSize(textSize);
        forceRedraw(false);
    }

    private void slideSmoothly(float velocityX) {
        mDragging = false;
        mAutoSlide = true;

        // adjust page direction
        if (velocityX > 0) {    // fling from left to right
            mTurningPageDirection = 0;
        } else if (velocityX < 0) { // fling from right to left
            mTurningPageDirection = 1;
        } else {
            if (mStartDraggingDirection == 1) {
                mTurningPageDirection = Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.2 ? 1 : 0;
            } else {
                mTurningPageDirection = mDrawableCanvas.getWidth() - Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.2 ? 0 : 1;
            }
        }

        if (mStartDraggingDirection != mTurningPageDirection) {
            turnPageByDirection();
            drawCurrentPageContent();
        }

        invalidate();
    }

    private void drawSmoothSlidePage(Canvas canvas) {
        if (mAutoSlide) {
            mAutoSlide = autoSlideDraw(canvas);
            if (!mAutoSlide) mDraggingBitmapX = 0;
        } else {
            staticDragDraw(canvas);
        }
    }

    private boolean autoSlideDraw(Canvas canvas) {
        float DIVIDER = 2f;
        if (mTurningPageDirection == 1) {
            float dragDistance = canvas.getWidth() + mDraggingBitmapX;
            dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
            mDraggingBitmapX -= dragDistance;

            canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
            if (mDraggingBitmapX > -canvas.getWidth()) {
                canvas.drawBitmap(mBackupPageBitmap, mDraggingBitmapX, 0, null);
                showPageShadow(canvas);
                invalidate();
                return true;
            }
        } else {
            float dragDistance = Math.abs(mDraggingBitmapX);
            dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
            mDraggingBitmapX += dragDistance;

            if (mDraggingBitmapX < 0) {
                canvas.drawBitmap(mBackupPageBitmap, 0, 0, null);
                canvas.drawBitmap(mMainPageBitmap, mDraggingBitmapX, 0, null);
                showPageShadow(canvas);
                invalidate();
                return true;
            }
            canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
        }
        return false;
    }

    private void staticDragDraw(Canvas canvas) {
        //show其他阅读组件时，ReadingBoard的OnDraw会被调用，这里做处理
        if (mDraggingBitmapX == 0) {
            canvas.drawBitmap(mDragging ? mBackupPageBitmap : mMainPageBitmap, 0, 0, null);
            return;
        }

        //用户一开始想向后翻页(下一页)，此时，mainBitmap居底，backupBitmap居顶并滑动，座标从0开始呈递减趋势
        if (mStartDraggingDirection == 1) {
            //当用户在滑动过程中改变方向时，backupBitmap的座标最终有可能会复位到0(不会大于，因为上面会判断)
            //当backupBitmap被完全移入屏幕时，没必要再显示mainBitmap及阴影
            if (mDraggingBitmapX < 0) {
                canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
                showPageShadow(canvas);
            }
            canvas.drawBitmap(mBackupPageBitmap, mDraggingBitmapX, 0, null);
        }
        //用户一开始想向前翻页(上一页)，此时，backupBitmap居底，mainBitmap居顶并滑动，座标从-canvasWidth开始呈递增趋势
        else {
            canvas.drawBitmap(mBackupPageBitmap, 0, 0, null);
            //当用户在滑动过程中改变方向时，mainBitmap的座标最终有可能会复位到-canvasWidth(不会小于，因为上面会判断)
            //当mainBitmap被完全移出屏幕时，没必要再显示mainBitmap及阴影
            if (mDraggingBitmapX > -canvas.getWidth()) {
                canvas.drawBitmap(mMainPageBitmap, mDraggingBitmapX, 0, null);
                showPageShadow(canvas);
            }
        }
    }

    private void showPageShadow(Canvas canvas) {
        int left = (int) (canvas.getWidth() - Math.abs(mDraggingBitmapX));
        mDragPageShadow.setBounds(left, 0, left + mDragPageShadowWidth, canvas.getHeight());
        mDragPageShadow.draw(canvas);
    }

    private boolean mAutoSlide;
    private boolean mDragging;

    public void changeTurningPageDirection(int pageDirection) {
        mTurningPageDirection = pageDirection;
    }

    private int mStartDraggingDirection;
    private float mDraggingBitmapX;
    private Drawable mDragPageShadow;
    private int mDragPageShadowWidth;

    public void startDragging() {
        mDragging = true;
        drawCurrentPageContent();
        resetDraggingBitmapX();
        mStartDraggingDirection = mTurningPageDirection;
    }

    private void resetDraggingBitmapX() {
        mDraggingBitmapX = mTurningPageDirection == 1 ? 0 : -mDrawableCanvas.getWidth();
    }

    public void dragRedraw(float pixels) {
        pixels = Math.abs(pixels);
        if (mTurningPageDirection == 1) {
            mDraggingBitmapX -= pixels;
            if (mDraggingBitmapX < -mDrawableCanvas.getWidth()) mDraggingBitmapX = -mDrawableCanvas.getWidth();
        } else {
            mDraggingBitmapX += pixels;
            if (mDraggingBitmapX > 0) mDraggingBitmapX = 0;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector == null) mGestureDetector = new GestureDetector(new ReaderGestureListener());
        if (mGestureDetector.onTouchEvent(event)) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mDragging) slideSmoothly(0f);
                return true;
        }

        return false;
    }

    private GestureDetector mGestureDetector;

    class ReaderGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            float portionWidth = getWidth() / 3;
            float portionHeight = getHeight() / 3;
            if (x < portionWidth || (x < portionWidth * 2 && y < portionHeight)) {
                if (!turnPreviousPage(true) && !mDoc.isDownloading()) {
                    getActivity().showToastMsg("已到达第一页");
                }
                getActivity().getReadingMenu().hideMenu();
            } else if (x > portionWidth * 2 || (x > portionWidth && y > portionHeight * 2)) {
                if (!turnNextPage(true) && !mDoc.isDownloading()) {
                    getActivity().showToastMsg("已到达最后一页");
                }
                getActivity().getReadingMenu().hideMenu();
            } else {
                getActivity().getReadingMenu().switchMenu();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            xScrollSum = 0;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (mDragging) slideSmoothly(velocityX);
            return true;
        }

        private float xScrollSum;

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mDoc.isDownloading()) {
                xScrollSum = 0;
                return false;
            }

            getActivity().getReadingMenu().hideMenu();
            xScrollSum += Math.abs(distanceX);
            if (xScrollSum < 5) return true;

            if (mDragging) {
                changeTurningPageDirection(distanceX > 0 ? 1 : 0);
                dragRedraw(distanceX);
            } else {
                if (distanceX > 0) {
                    if (turnNextPage(false)) {
                        startDragging();
                    } else if (!mDoc.isDownloading()) {
                        getActivity().showToastMsg("已到达最后一页");
                    }
                } else {
                    if (turnPreviousPage(false)) {
                        startDragging();
                    } else if (!mDoc.isDownloading()) {
                        getActivity().showToastMsg("已到达第一页");
                    }
                }
            }
            return true;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return getActivity().onKeyUp(keyCode, event);
    }

    public void turnToChapter(Chapter chapter) {
        if (mDoc.turnToChapter(chapter)) forceRedraw(true);
    }

//	public float calculateReadingProgress() {
//		float percentage = mDoc.calculateReadingProgress();
//		return percentage > 100 ? 100 : percentage;
//	}

    private ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }
}
