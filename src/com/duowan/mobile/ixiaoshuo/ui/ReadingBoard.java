package com.duowan.mobile.ixiaoshuo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.doc.Document;
import com.duowan.mobile.ixiaoshuo.doc.LayoutUtil;
import com.duowan.mobile.ixiaoshuo.doc.OnlineDocument;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.pojo.ColorScheme;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.utils.Encoding;
import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReadingBoard extends View {
	private static final String TAG = "ReadingBoard";
	RenderPaint mPaint;
	Document mDoc;

	// added to support full-justification text layout
	// make it LARGE enough to hold even 2048 GBK chars per line, rarely possible to reach this limit in reality
	private float mLayoutPositions[] = new float[4096];

	public ReadingBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new RenderPaint(context);
		mDragPageShadowWidth = (int) context.getResources().getDimension(R.dimen.reading_board_page_shadow_width);
		mDragPageShadow = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.reading_board_page_shadow);
	}

	public void init(Book book) throws Exception {
		createDocument(book);
	}

	private void createDocument(final Book book) throws Exception {
		mDoc = new OnlineDocument(book, mPaint, new OnlineDocument.ProcessCallback() {
			public void fetchChapter(final Chapter chapter) {
				NetService.execute(new NetService.NetExecutor<String>() {
					ProgressDialog mPrgreDialog;

					public void preExecute() {
						if (NetService.get().isNetworkAvailable()) {
							mPrgreDialog = ProgressDialog.show(getContext(), null, getContext().getString(R.string.loading_tip_msg), false, true);
							mPrgreDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									NetService.get().abortLast();
								}
							});
						} else {
							getActivity().showToastMsg(R.string.network_disconnect_msg);
						}
					}

					public String execute() {
						return NetService.get().getChapterContent(book.getBookId(), chapter.getId());
					}

					public void callback(String content) {
						boolean isShowing = mPrgreDialog.isShowing();
						if (isShowing) mPrgreDialog.cancel();
						mDoc.turnOffLoadding();

						if (StringUtil.isEmpty(content)) {
							if (isShowing) getActivity().showToastMsg(R.string.without_data);
							return;
						}

						try {
							String fileName = String.valueOf(chapter.getId());
							File chapterFile = new File(Paths.getCacheDirectorySubFolder(book.getBookId()), fileName);
							FileOutputStream contentOutput = new FileOutputStream(chapterFile);
							ZipOutputStream zops = new ZipOutputStream(contentOutput);
							zops.putNextEntry(new ZipEntry(fileName));
							zops.write(content.getBytes(Encoding.GBK.getName()));
							zops.close();
							contentOutput.close();
						} catch (Exception e) {
							Log.e(TAG, e.getMessage(), e);
							return;
						}

						if (isShowing) adjustReadingProgress(chapter);
					}
				});
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMainPageBitmap == null) resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
//		canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
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
		mPaint.drawBg(mDrawableCanvas);

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

		getActivity().refreshStatusBar(mDoc.getReadingInfo(), calculateReadingProgress());
		mDoc.calculatePagePosition();
		mDrawableCanvas.restore();
	}

	public void forceRedraw() {
		mAutoSlide = true;
		resetDraggingBitmapX();
		drawCurrentPageContent();
		invalidate();
	}

	//0: previous page, 1: next page
	private int mTurningPageDirection;

	public boolean turnPreviousPage() {
		mTurningPageDirection = 0;
		return mDoc.turnPreviousPage();
	}

	public boolean turnNextPage() {
		mTurningPageDirection = 1;
		return mDoc.turnNextPage();
	}

	public boolean turnPageByDirection() {
		if(mTurningPageDirection == 0) return turnPreviousPage();
		return turnNextPage();
	}

	public boolean turnPreviousPageAndRedraw() {
		if(turnPreviousPage()) {
			forceRedraw();
			return true;
		}
		return false;
	}

	public boolean turnNextPageAndRedraw() {
		if(turnNextPage()) {
			forceRedraw();
			return true;
		}
		return false;
	}

	public void setColorScheme(ColorScheme colorScheme) {
		mPaint.setColorScheme(colorScheme);
	}

	private void slideSmoothly(float velocityX) {
		mDragging = false;
		mAutoSlide = true;

		//adjust page direction
		if(velocityX > 0) {	// fling from left to right
			mTurningPageDirection = 0;
		} else if(velocityX < 0) { // fling from right to left
			mTurningPageDirection = 1;
		} else {
			if(mStartDraggingDirection == 1) {
				mTurningPageDirection = Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.2 ? 1 : 0;
			} else {
				mTurningPageDirection = mDrawableCanvas.getWidth() - Math.abs(mDraggingBitmapX) > mDrawableCanvas.getWidth() * 0.2 ? 0 : 1;
			}
		}

		if(mStartDraggingDirection != mTurningPageDirection) {
			turnPageByDirection();
			drawCurrentPageContent();
		}

		invalidate();
	}

	private void drawSmoothSlidePage(Canvas canvas) {
		if(mAutoSlide) {
			mAutoSlide = autoSlideDraw(canvas);
			if(!mAutoSlide) mDraggingBitmapX = 0;
		} else {
			staticDragDraw(canvas);
		}
	}

	private boolean autoSlideDraw(Canvas canvas) {
		float DIVIDER = 2f;
		if(mTurningPageDirection == 1) {
			float dragDistance = canvas.getWidth() + mDraggingBitmapX;
			dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
			mDraggingBitmapX -= dragDistance;

			canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
			if(mDraggingBitmapX > -canvas.getWidth()) {
				canvas.drawBitmap(mBackupPageBitmap, mDraggingBitmapX, 0, null);
				showPageShadow(canvas);
				invalidate();
				return true;
			}
		} else {
			float dragDistance = Math.abs(mDraggingBitmapX);
			dragDistance /= dragDistance < DIVIDER ? dragDistance / 2 : DIVIDER;
			mDraggingBitmapX += dragDistance;

			if(mDraggingBitmapX < 0) {
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
		if(mDraggingBitmapX == 0) {
			canvas.drawBitmap(mDragging ? mBackupPageBitmap : mMainPageBitmap, 0, 0, null);
			return;
		}

		//用户一开始想向后翻页(下一页)，此时，mainBitmap居底，backupBitmap居顶并滑动，座标从0开始呈递减趋势
		if(mStartDraggingDirection == 1) {
			//当用户在滑动过程中改变方向时，backupBitmap的座标最终有可能会复位到0(不会大于，因为上面会判断)
			//当backupBitmap被完全移入屏幕时，没必要再显示mainBitmap及阴影
			if(mDraggingBitmapX < 0) {
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
			if(mDraggingBitmapX > -canvas.getWidth()) {
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
		if(mTurningPageDirection == 1) {
			mDraggingBitmapX -= pixels;
			if(mDraggingBitmapX < -mDrawableCanvas.getWidth()) mDraggingBitmapX = -mDrawableCanvas.getWidth();
		} else {
			mDraggingBitmapX += pixels;
			if(mDraggingBitmapX > 0) mDraggingBitmapX = 0;
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
				if (!turnPreviousPageAndRedraw()) {
					if (!mDoc.hasPreviousChapter()) getActivity().showToastMsg("已到达第一页");
				}
			} else if (x > portionWidth * 2 || (x > portionWidth && y > portionHeight * 2)) {
				if (!turnNextPageAndRedraw()) {
					if (!mDoc.hasNextChapter()) getActivity().showToastMsg("已到达最后一页");
				}
			}
			return true;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			xScrollSum = 0;
			yScrollSum = 0;
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (mDragging) slideSmoothly(velocityX);
			return true;
		}

		private float xScrollSum, yScrollSum;

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			xScrollSum += Math.abs(distanceX);
			yScrollSum += Math.abs(distanceY);
			if (xScrollSum < 5 && yScrollSum < 5) return true;

			if (xScrollSum < yScrollSum) return true;
			if (mDragging) {
				changeTurningPageDirection(distanceX > 0 ? 1 : 0);
				dragRedraw(distanceX);
			} else {
				if (distanceX > 0) {
					if (turnNextPage()) {
						startDragging();
					} else if (!mDoc.hasNextChapter()) {
						getActivity().showToastMsg("已到达最后一页");
					}
				} else {
					if (turnPreviousPage()) {
						startDragging();
					} else if (!mDoc.hasPreviousChapter()) {
						getActivity().showToastMsg("已到达第一页");
					}
				}

			}
			return true;
		}
	}

	public void adjustReadingProgress(Chapter chapter) {
		if(mDoc.adjustReadingProgress(chapter)) forceRedraw();
	}

	private float calculateReadingProgress() {
		float percentage = mDoc.calculateReadingProgress();
		return percentage > 100 ? 100 : percentage;
	}

	private ReaderActivity getActivity() {
		return (ReaderActivity) getContext();
	}

	public Book getBook() {
		return mDoc.getBook();
	}

}
