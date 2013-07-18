package com.duowan.mobile.ixiaoshuo.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * more detail by : http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
 * @author : vince
 */
public class BitmapLruCache extends LruCache<Integer, Bitmap> {

	private static int calculateCacheSize() {
		// Get max available VM memory, Stored in kilobytes
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		// Use 1/8th of the available memory
		return maxMemory / 8;
	}

	public BitmapLruCache() {
		super(BitmapLruCache.calculateCacheSize());
	}

	@Override
	protected int sizeOf(Integer key, Bitmap bitmap) {
		// The cache size will be measured in kilobytes rather than number of items
		return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
	}

	private void addBitmap(Integer key, Bitmap bitmap) {
		if (get(key) == null) put(key, bitmap);
	}

	public void loadBitmap(int key, String path, ImageView imageView) {
		Bitmap bitmap = get(key);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
//			imageView.setImageResource(R.drawable.cover_less);
			BitmapWorkerTask task = new BitmapWorkerTask(imageView);
			task.execute(String.valueOf(key), path);
		}
	}

	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private ImageView mImageView;

		private BitmapWorkerTask(ImageView imageView) {
			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap = BitmapUtil.loadBitmapInFile(params[1], mImageView);
			addBitmap(Integer.parseInt(params[0]), bitmap);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			mImageView.setImageBitmap(bitmap);
		}
	}

}
