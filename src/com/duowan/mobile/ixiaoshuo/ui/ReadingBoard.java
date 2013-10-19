package com.duowan.mobile.ixiaoshuo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.doc.Document;
import com.duowan.mobile.ixiaoshuo.doc.LayoutUtil;
import com.duowan.mobile.ixiaoshuo.doc.OnlineDocument;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.pojo.ColorScheme;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;

public class ReadingBoard extends View implements YYReader.OnDownloadChapterListener {
//	private static final String TAG = "ReadingBoard";
	RenderPaint mPaint;
	Document mDoc;

	// added to support full-justification text layout
	// make it LARGE enough to hold even 2048 GBK chars per line, rarely possible to reach this limit in reality
	private float mLayoutPositions[] = new float[4096];

	public ReadingBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init() throws Exception {
		mDragPageShadow = (NinePatchDrawable) getContext().getResources().getDrawable(R.drawable.reading_board_page_shadow);
		mDragPageShadowWidth = (int) getContext().getResources().getDimension(R.dimen.reading_board_page_shadow_width);

		mPaint = new RenderPaint(getContext());

		mDoc = new OnlineDocument(mPaint, this, new OnlineDocument.OnTurnChapterListener() {
			@Override
			public void onTurnChapter() {
				getActivity().refreshStatusBar(mDoc.getReadingInfo());
			}
		});
		adjustReadingProgress(YYReader.getCurrentChapterInfo());

		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMainPageBitmap == null) resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
		drawSmoothSlidePage(canvas);
	}

	private Bitmap mBackupPageBitmap;
	private Bitmap mMainPageBitmap;
	private Canvas mDrawableCanvas;
	private void resetPageBitmapDrawable(int width, int height) {
		mBackupPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		mMainPageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		mDrawableCanvas = new Canvas();
		drawCurrentPageContent();
	}

	private Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private void drawCurrentPageContent() {
		mPaint.getFontMetrics(mFontMetrics);

		mDrawableCanvas.setBitmap(mBackupPageBitmap);
		mDrawableCanvas.drawBitmap(mMainPageBitmap, 0, 0, null);

		mDrawableCanvas.setBitmap(mMainPageBitmap);

		Rect rect = new Rect();
		getDrawingRect(rect);
		Drawable bgDrawable = mColorScheme.getReadingDrawable(getResources(), mDrawableCanvas.getWidth(), mDrawableCanvas.getHeight());
		bgDrawable.setBounds(rect);
		bgDrawable.draw(mDrawableCanvas);

		mDrawableCanvas.save(Canvas.MATRIX_SAVE_FLAG);
		mDrawableCanvas.translate(mPaint.getCanvasPaddingLeft(), mPaint.getCanvasPaddingTop() - mFontMetrics.ascent);

		mDoc.prepareGetLines();
		float textHeight = mPaint.getTextHeight();
		StringBuilder sb = new StringBuilder();
		float contentHeight = 0;
		byte flags;
		while (true) {
			flags = mDoc.getNextLine(sb);
			if(flags == 0) break;

			if((flags & Document.GET_NEXT_LINE_FLAG_SHOULD_JUSTIFY) > 0) {
				LayoutUtil.layoutTextJustified(sb, mPaint.getRenderWidth(), mPaint, 0, contentHeight, mLayoutPositions);
				mDrawableCanvas.drawPosText(sb.toString(), mLayoutPositions, mPaint);
			} else {
				mDrawableCanvas.drawText(sb, 0, sb.length(), 0, contentHeight, mPaint);
			}
			sb.delete(0, sb.length());

			contentHeight += textHeight;
			if((flags & Document.GET_NEXT_LINE_FLAG_PARAGRAPH_ENDS) > 0)
				contentHeight += mPaint.getParagraphSpacing();
			else
				contentHeight += mPaint.getLineSpacing();
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

	private ColorScheme mColorScheme;
	public void setColorScheme(ColorScheme colorScheme) {
		if (mColorScheme != colorScheme) {
			mPaint.setColor(colorScheme.getTextColor());
			mColorScheme = colorScheme;
			forceRedraw(false);
		}
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
	private NinePatchDrawable mDragPageShadow;
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

	ProgressDialog mLoadingPrgreDialog;

	@Override
	public void onDownloadStart(Chapter chapter) {
		mLoadingPrgreDialog = ProgressDialog.show(getContext(), null, getContext().getString(R.string.loading_tip_msg), false, true);
		mDoc.onDownloadStart();
	}

	@Override
	public void onDownloadComplete(Chapter chapter) {
		boolean willAdjust = mLoadingPrgreDialog.isShowing();
		if (willAdjust) mLoadingPrgreDialog.cancel();
		mLoadingPrgreDialog = null;

		if (chapter.isNativeChapter()) {
			mDoc.onDownloadComplete(true, willAdjust);
			if (willAdjust) adjustReadingProgress(chapter);
		} else {
			mDoc.onDownloadComplete(false, willAdjust);
			if (willAdjust) getActivity().showToastMsg(R.string.without_data);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return getActivity().onKeyUp(keyCode, event);
	}

	public void adjustReadingProgress(Chapter chapter) {
		if (mDoc.adjustReadingProgress(chapter)) forceRedraw(true);
	}

	public float calculateReadingProgress() {
		float percentage = mDoc.calculateReadingProgress();
		return percentage > 100 ? 100 : percentage;
	}

	private ReaderActivity getActivity() {
		return (ReaderActivity) getContext();
	}

}
