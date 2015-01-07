package com.vincestyling.ixiaoshuo.net;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.cache.BitmapImageCache;
import com.duowan.mobile.netroid.cache.DiskCache;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.duowan.mobile.netroid.toolbox.ImageLoader;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.request.*;
import com.vincestyling.ixiaoshuo.pojo.*;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class Netroid {
    private static final String API = "http://ixiaoshuo.vincestyling.com/";

    private static RequestQueue mRequestQueue;

    private static ImageLoader mImageLoader;

    private Netroid() {}

    public static void init(Context ctx) {
        if (mRequestQueue == null) {
            Network network = new BasicNetwork(new HurlStack(API, null), HTTP.UTF_8);
            // path usually would being /data/data/com.vincestyling.ixiaoshuo/netroid
            File cacheDir = new File(ctx.getCacheDir(), Const.HTTP_DISK_CACHE_DIR_NAME);
            mRequestQueue = new RequestQueue(network, 6, new DiskCache(cacheDir, Const.HTTP_DISK_CACHE_SIZE));

            mImageLoader = new SelfImageLoader(mRequestQueue, new BitmapImageCache(Const.HTTP_MEMORY_CACHE_SIZE));

            mRequestQueue.start();
        } else {
            throw new IllegalStateException("initialized");
        }
    }

    public static RequestQueue get() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    private static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    private static String buildUrl(String pageName, String params) {
        String url = API + pageName;
        if (TextUtils.isEmpty(params)) {
            return url;
        }

        if (params.charAt(0) == '&') {
            url = url + '?' + params.substring(1, params.length());
            return url;
        }

        url = url + '?' + params;
        return url;
    }

    private static String buildUrl(String pageName) {
        return buildUrl(pageName, null);
    }

    public static void downloadChapterContent(int bookId, int chapterId, Listener<Void> listener) {
        new ChapterDownloadRequest(bookId, chapterId,
                buildUrl(String.format("/chapter/content/%d/%d", bookId, chapterId)), listener);
    }

    public static void getBookChapterList(int bookId, int pageNo, Listener<PaginationList<Chapter>> listener) {
        new ChapterListRequest(buildUrl(String.format("/chapter/list/%d/%d", bookId, pageNo)), listener);
    }

    public static void getBookDetail(int bookId, Listener<Book> listener) {
        new BookInfoRequest(buildUrl(String.format("/detail/%d", bookId)), listener);
    }

    public static void getCategories(Listener<List<Category>> listener) {
        new CategoriesRequest(buildUrl("/categories"), listener);
    }

    public static void getBookListByUpdateStatus(int updateStatus, int pageNo, Listener<PaginationList<Book>> listener) {
        new BookListRequest(buildUrl(String.format("/list_bystatus/%d/%d", updateStatus, pageNo)), listener);
    }

    public static void getBookListByCategory(int catId, int pageNo, Listener<PaginationList<Book>> listener) {
        new BookListRequest(buildUrl(String.format("/list_bycategory/%d/%d", catId, pageNo)), listener);
    }

    public static void getHottestBookList(int pageNo, Listener<PaginationList<Book>> listener) {
        new BookListRequest(buildUrl(String.format("/list_byhottest/%d", pageNo)), listener);
    }

    public static void getNewlyBookList(int pageNo, Listener<PaginationList<Book>> listener) {
        new BookListRequest(buildUrl(String.format("/list_bynewly/%d", pageNo)), listener);
    }

    public static void getHotKeywords(Listener<String[]> listener) {
        new HotKeywordsRequest(buildUrl("/hot_keywords"), listener);
    }

    public static void getBookListByKeyword(String keyword, int pageNo, Listener<PaginationList<Book>> listener) {
        new BookListRequest(buildUrl(String.format("/search/%d/%d",
                Math.abs(new BigInteger(keyword.getBytes()).longValue()), pageNo)), listener);
    }

    public static void getBookListByLocation(int pageNo, Listener<PaginationList<BookByLocation>> listener) {
        new BookListByLocationRequest(buildUrl(String.format("/list_bylocation/%d", pageNo)), listener);
    }

    public static void displayImage(String url, ImageView imageView, int defaultImageResId, int errorImageResId) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId);
        getImageLoader().get(url, listener, 0, 0);
    }

    public static void displayImage(String url, ImageView imageView) {
        displayImage(url, imageView, 0, 0);
    }

    public static void displayImage(String url, NetworkImageView imageView, int defaultImageResId, int errorImageResId) {
        imageView.setDefaultImageResId(defaultImageResId);
        imageView.setErrorImageResId(errorImageResId);
        imageView.setImageUrl(url, getImageLoader());
    }

    public static void displayImage(String url, NetworkImageView imageView) {
        displayImage(url, imageView, 0, 0);
    }
}
