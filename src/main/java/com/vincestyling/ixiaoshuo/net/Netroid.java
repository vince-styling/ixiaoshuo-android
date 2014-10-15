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
import com.vincestyling.ixiaoshuo.net.request.*;
import com.vincestyling.ixiaoshuo.pojo.*;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.math.BigInteger;
import java.util.List;

public class Netroid {

    /**
     * The queue. :-)
     */
    private static RequestQueue mRequestQueue;

    /**
     * The image loader. :-)
     */
    private static ImageLoader mImageLoader;

    /**
     * The server api prefix.
     */
    public static final String API = "http://ixiaoshuo.vincestyling.com/";

    /**
     * Nothing to see here.
     */
    private Netroid() {
    }

    public static void init(Context ctx) {
        if (mRequestQueue == null) {
            Network network = new BasicNetwork(new HurlStack(API, null), HTTP.UTF_8);
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

    private static String makeUrl(String pageName, String params) {
        String url = API + pageName;
        if (TextUtils.isEmpty(params)) {
            AppLog.d("request : " + url);
            return url;
        }

        if (params.charAt(0) == '&') {
            url = url + '?' + params.substring(1, params.length());
            AppLog.d("request : " + url);
            return url;
        }

        url = url + '?' + params;
        AppLog.d("request : " + url);
        return url;
    }

    private static String makeUrl(String pageName) {
        return makeUrl(pageName, null);
    }

    public static void downloadChapterContent(int bookId, int chapterId, Listener<Void> listener) {
        get().add(new ChapterDownloadRequest(bookId, chapterId,
                makeUrl(String.format("/chapter/content/%d/%d", bookId, chapterId)), listener));
    }

    public static void getBookChapterList(int bookId, int pageNo, Listener<PaginationList<Chapter>> listener) {
        get().add(new ChapterListRequest(makeUrl(String.format("/chapter/list/%d/%d", bookId, pageNo)), listener));
    }

    public static void getBookDetail(int bookId, Listener<Book> listener) {
        get().add(new BookInfoRequest(makeUrl(String.format("/detail/%d", bookId)), listener));
    }

    public static void getCategories(Listener<List<Category>> listener) {
        get().add(new CategoriesRequest(makeUrl("/categories"), listener));
    }

    public static void getBookListByUpdateStatus(int updateStatus, int pageNo, Listener<PaginationList<Book>> listener) {
        get().add(new BookListRequest(makeUrl(String.format("/list_bystatus/%d/%d", updateStatus, pageNo)), listener));
    }

    public static void getBookListByCategory(int catId, int pageNo, Listener<PaginationList<Book>> listener) {
        get().add(new BookListRequest(makeUrl(String.format("/list_bycategory/%d/%d", catId, pageNo)), listener));
    }

    public static void getHottestBookList(int pageNo, Listener<PaginationList<Book>> listener) {
        get().add(new BookListRequest(makeUrl(String.format("/list_byhottest/%d", pageNo)), listener));
    }

    public static void getNewlyBookList(int pageNo, Listener<PaginationList<Book>> listener) {
        get().add(new BookListRequest(makeUrl(String.format("/list_bynewly/%d", pageNo)), listener));
    }

    public static void getHotKeywords(Listener<String[]> listener) {
        get().add(new HotKeywordsRequest(makeUrl("/hot_keywords"), listener));
    }

    public static void getBookListByKeyword(String keyword, int pageNo, Listener<PaginationList<Book>> listener) {
        get().add(new BookListRequest(makeUrl(
                String.format("/search/%d/%d", Math.abs(new BigInteger(keyword.getBytes()).longValue()), pageNo)), listener));
    }

    public static void getBookListByLocation(int pageNo, Listener<PaginationList<BookByLocation>> listener) {
        get().add(new BookListByLocationRequest(makeUrl(String.format("/list_bylocation/%d", pageNo)), listener));
    }

    public static void displayImage(String url, ImageView imageView, int defaultImageResId, int errorImageResId) {
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId);
        getImageLoader().get(url, listener, 0, 0);
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
