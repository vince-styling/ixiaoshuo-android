package com.duowan.mobile.ixiaoshuo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.doc.Document;
import com.duowan.mobile.ixiaoshuo.doc.LayoutUtil;
import com.duowan.mobile.ixiaoshuo.doc.OnlineDocument;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.BaseActivity;
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

	// added by kevin, to support full-justification text layout
	// make it LARGE enough to hold even 2048 GBK chars per line, rarely possible to reach this limit in reality
	private float mLayoutPositions[] = new float[4096];

	public ReadingBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new RenderPaint(context);
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
						} else {
							getActivity().showToastMsg(R.string.network_disconnect_msg);
						}
					}

					public String execute() {
						return NetService.get().getChapterContent(book.getBookId(), chapter.getId());
					}

					public void callback(String content) {
						if (StringUtil.isEmpty(content)) return;
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
						if (mPrgreDialog.isShowing()) {
							adjustReadingProgress(chapter);
							mPrgreDialog.cancel();
						}
					}
				});
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBackupPageBitmap == null) resetPageBitmapDrawable(canvas.getWidth(), canvas.getHeight());
		canvas.drawBitmap(mMainPageBitmap, 0, 0, null);
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
			// TODO : 这个方法的逻辑需要理解！！！！，目测有优化的空间！
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

	public void forceRedraw() {
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

	public boolean hasNextChapter() { return mDoc.hasNextChapter(); }
	public boolean hasPreviousChapter() { return mDoc.hasPreviousChapter(); }

	public void adjustReadingProgress(Chapter chapter) {
		if(mDoc.adjustReadingProgress(chapter)) forceRedraw();
	}

	private BaseActivity getActivity() {
		return (BaseActivity) getContext();
	}

	public Book getBook() {
		return mDoc.getBook();
	}

}
