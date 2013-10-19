package com.duowan.mobile.ixiaoshuo.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.BookCoverLoader;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.BaseActivity;
import com.duowan.mobile.ixiaoshuo.ui.EllipseEndTextView;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

import java.io.File;
import java.util.Vector;

/**
 * 有声详情页面
 * @author gaocong
 */
public class VoiceBookInfoView extends ViewBuilder implements AbsListView.OnScrollListener, View.OnClickListener {
	private static final String TAG = "YYReader_VoiceBoookInfoView";
	private int mBookId;
	private Book mBook;

	private EndlessListAdapter<Chapter> mAdapter;
	private View mHeadView;

	private String mOrder = ORDER_ASC;
	private static final String ORDER_ASC = "asc";
	private static final String ORDER_DESC = "desc";

	private int mPageNo = 1;
	private boolean mHasNextPage = true;
	private final static int PAGE_ITEM_COUNT = 40;
	private Context mContext;
	private BaseActivity mActivity;
	private int mIndex;
	private static final String VOICE_BOOK_DOWNLOAD_DIR = "/sdcard/ixiaoshuo/voice/";
	private  Vector<Integer> mDownTaskList; //Thread safe
	
	private final static int DOWNLOAD_ALL_STATUS_UN_STARTED = 10010; //
	private final static int DOWNLOAD_ALL_STATUS_DOWINING = 10011;
	private final static int DOWNLOAD_ALL_STATUS_FINISHED = 10012;
	private int DOWNLOAD_ALL_STATUS ;
	Button btnDownloadAll;
	private boolean mIsShowAddDialog = false;
	private Handler mHandler;
	private static final int REMOVE_TASKID = 20010;

	public VoiceBookInfoView(BaseActivity baseActivity, int bookId) {
		mViewId = R.id.lsvBookInfoChapterList;
		mContext = baseActivity.getApplicationContext();
		setActivity(baseActivity);
		mActivity = baseActivity;

		mBookId = bookId;
		DOWNLOAD_ALL_STATUS = DOWNLOAD_ALL_STATUS_UN_STARTED;
	}
	
	private void initHandler(){
		mHandler = new Handler(){
			public void dispatchMessage(Message msg){
				switch (msg.what) {
				case REMOVE_TASKID:
					removeTask(msg.arg1);
					updateDownloadBtn();
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.voice_book_info, null);
	}

	@Override
	public void init() {
		initHandler();
		if (!NetService.get().isNetworkAvailable()) {
			getActivity().showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		NetService.execute(new NetService.NetExecutor<Book>() {
			ProgressDialog mPrgreDialog;

			public void preExecute() {
				mPrgreDialog = ProgressDialog.show(getActivity(), null, getActivity().getString(R.string.loading_tip_msg), false, true);
			}

			public Book execute() {
				return NetService.get().getVoiceBookDetail(mBookId);
			}

			public void callback(Book book) {
				mPrgreDialog.cancel();
				if (mIsInFront) {
					if (book == null) {
						getActivity().showToastMsg(R.string.without_data);
					} else {
						mBook = book;
						initWithData();
					}
				}
			}
		});
	}

	private void initWithData() {
		mHeadView = getActivity().getLayoutInflater().inflate(R.layout.voice_book_info_head, null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		mHeadView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
		getListView().addHeaderView(mHeadView);

		ImageView imvBookCover = (ImageView) findViewById(R.id.imvBookCover);
		BookCoverLoader.loadCover(getActivity(), mBook, imvBookCover);

		TextView txvBookName = (TextView) findViewById(R.id.txvBookName);
		txvBookName.setText(mBook.getName());

		TextView txvBookStatus = (TextView) findViewById(R.id.txvBookStatus);
		txvBookStatus.setText(mBook.getUpdateStatusStr());

		TextView txvBookAuthor = (TextView) findViewById(R.id.txvBookAuthor);
		txvBookAuthor.setText("作者：" + mBook.getAuthor());

		TextView txvBookCategory = (TextView) findViewById(R.id.txvBookCategory);
		txvBookCategory.setText("分类：" + mBook.getCatName());

		TextView txvBookCapacity = (TextView) findViewById(R.id.txvBookCapacity);
		txvBookCapacity.setText( mContext.getString(R.string.voice_size)+ mBook.getCapacityStr());

		EllipseEndTextView txvBookSummary = (EllipseEndTextView) findViewById(R.id.txvBookSummary);
		txvBookSummary.setText(mBook.getFormattedSummary());

		Button btnGotoRead = (Button) findViewById(R.id.btnGotoListen);
		btnGotoRead.setOnClickListener(this);

		btnDownloadAll = (Button) findViewById(R.id.btnDownloadAll);
		btnDownloadAll.setOnClickListener(this);

		Button mBtnChapterListReverse = (Button) findViewById(R.id.btnChapterListReverse);
		mBtnChapterListReverse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View btnView) {
				getListView().setSelection(0);
				mOrder = mOrder.equals(ORDER_DESC) ? ORDER_ASC : ORDER_DESC;
				int arrowDrawableResId = mOrder.equals(ORDER_DESC) ?
						R.drawable.book_info_chapter_arrow_up : R.drawable.book_info_chapter_arrow_down;
				((Button) btnView).setCompoundDrawablesWithIntrinsicBounds(0, 0, arrowDrawableResId, 0);

				mPageNo = 1;
				mHasNextPage = true;
				mAdapter.clear();
				loadNextPage();
			}
		});

		mAdapter = new EndlessListAdapter<Chapter>() {
			@Override
			protected View getView(final int position, View convertView) {
				Holder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
					holder = new Holder();
					holder.txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
					holder.btnChapterOperation = (Button) convertView.findViewById(R.id.btnChapterOperation);
//					holder.txvChapterSize = (TextView)convertView.findViewById(R.id.txvChapterSize);
					convertView.setTag(holder);
					convertView.setLayoutParams(new AbsListView.LayoutParams(
							getListView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				} else {
					holder = (Holder) convertView.getTag();
				}

				final Chapter chapter = getItem(position);
				holder.txvChapterTitle.setText(chapter.getTitle());
//				holder.txvChapterSize.setText(StringUtil.formatSpaceSize(chapter.getCapacity()));

				View.OnClickListener clickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(!mIsShowAddDialog){
							showAddDialog(chapter, position);
							mIsShowAddDialog = true;
						}else{
							onItemClick(chapter,position);
						}
					}
				};
				holder.txvChapterTitle.setOnClickListener(clickListener);

				//File chapterFile = new File(mBookDirectoryPath + chapter.getChapterId());
				if (isMediaFileExist(chapter)) {
//					if(mPlayService != null && mPlayService.getPlayDataSource() == getChapterLocalPath(chapter)){
//						holder.btnChapterOperation.setBackgroundResource(R.drawable.voice_listenering);
//					}else{
//						holder.btnChapterOperation.setBackgroundResource(R.drawable.voice_listener);
//					}
				} else {
//					if(chapter.getDownloadId() == -1){
//						holder.btnChapterOperation.setBackgroundResource(R.drawable.voice_chapter_download_selector);
//					}else{
//						holder.btnChapterOperation.setBackgroundResource(R.drawable.voice_pause);
//					}
//					holder.btnChapterOperation.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							if(chapter.getDownloadId() == -1){  // -1表示非进行中的任务
//								Log.i(TAG, "btnChapterOperation click : " + position);
//								dowloadChapter(chapter);
//								v.setBackgroundResource(R.drawable.voice_pause);
//							}else{
////								getOnlineManageer().cancelTask(chapter.getDownloadId()); //取消
//								Log.i(TAG, "btnChapterOperation click : " + position + "cancelTask");
//								v.setBackgroundResource(R.drawable.voice_chapter_download_selector);
//							}
//						}
//					});
				}

				return convertView;
			}

			@Override
			protected View initProgressView() {
				View progressView = getActivity().getLayoutInflater().inflate(R.layout.contents_loading, null);
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				return progressView;
			}
		};
		getListView().setAdapter(mAdapter);
		getListView().setOnScrollListener(this);
		loadNextPage();
	}

	private void loadNextPage() {
		if (!mHasNextPage) return;

		if (!NetService.get().isNetworkAvailable()) {
			getActivity().showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		mAdapter.setIsLoadingData(true);
		NetService.execute(new NetService.NetExecutor<PaginationList<Chapter>>() {
			public void preExecute() {}

			public PaginationList<Chapter> execute() {
				return NetService.get().getVoiceBookChapterList(mBook.getSourceBookId(), mOrder, mPageNo, PAGE_ITEM_COUNT);
			}

			public void callback(PaginationList<Chapter> chapterList) {
				mAdapter.setIsLoadingData(false);
				if (chapterList == null || chapterList.size() == 0) {
					if (isInFront()) getActivity().showToastMsg(R.string.without_data);
					return;
				}
				mHasNextPage = chapterList.hasNextPage();
				mAdapter.addAll(chapterList);
				mPageNo++;
			}
		});
	}
	
	//
	private void loadNextForDowanloadAll() {
		if (!mHasNextPage){
			getActivity().showToastMsg(R.string.no_next_page);
			return;
		} 

		if (!NetService.get().isNetworkAvailable()) {
			getActivity().showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		NetService.execute(new NetService.NetExecutor<PaginationList<Chapter>>() {
			public void preExecute() {}

			public PaginationList<Chapter> execute() {
				return NetService.get().getVoiceBookChapterList(mBook.getSourceBookId(), mOrder, mPageNo, PAGE_ITEM_COUNT);
			}

			public void callback(PaginationList<Chapter> chapterList) {
				if (chapterList == null || chapterList.size() == 0) {
					if (isInFront()) getActivity().showToastMsg(R.string.without_data);
					return;
				}
				mHasNextPage = chapterList.hasNextPage();
				mAdapter.addAll(chapterList);
				mPageNo++;
			}
		});
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGotoListen:
			createPlayWindow();
			gotoListener();
			break;
		case R.id.btnDownloadAll:
			processDownloadAllClick();
			updateDownloadBtn();
			break;

		default:
			break;
		}
	}
	
	
	private void processDownloadAllClick(){
		Log.i(TAG, "processDownloadAllClick : " + DOWNLOAD_ALL_STATUS);
		if(DOWNLOAD_ALL_STATUS == DOWNLOAD_ALL_STATUS_DOWINING){
//			Iterator<Integer> it = mDownTaskList.iterator();
//	        for(int i= 0; it.hasNext();i++){
//				getOnlineManageer().cancelTask(it.next());
//			}
			DOWNLOAD_ALL_STATUS = DOWNLOAD_ALL_STATUS_UN_STARTED;
		}else if(DOWNLOAD_ALL_STATUS == DOWNLOAD_ALL_STATUS_UN_STARTED){
			downLoadAall();
		}else if(DOWNLOAD_ALL_STATUS == DOWNLOAD_ALL_STATUS_FINISHED){
			loadNextForDowanloadAll();
		}
		
		updateDownloadBtn();
	}
	
	
	
	

	public void onItemClick(Chapter chapter,int pos){
		mIndex = pos;
		createPlayWindow();
//		if(mPlayService == null){
//			Log.i(TAG, "mPlayService == null ");
//			Log.i(TAG, "book url is: " + chapter.getVoiceUrl());
//			if(chapter.getVoiceUrl() == null)return;
//			mContext.bindService(new Intent(mActivity,com.duowan.mobile.ixiaoshuo.service.PlayService.class),
//					mConnection, Context.BIND_AUTO_CREATE);
//
//			gotoListener();
//		}else{
//			play();
//		}
	}
	
	private void showAddDialog(final Chapter chapter,final int pos){
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
	        builder.setMessage(R.string.add_to_shelf);
	        builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					onItemClick(chapter, pos);
				}
			});
	        builder.setPositiveButton(R.string.goon, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					 int bid = AppDAO.get().addBook(mBook, true);
				        if (bid > 0) {
				            AppDAO.get().saveBookChapters(bid, mAdapter.getData());
				            getActivity().showToastMsg(mActivity.getString(R.string.add_faild));
				        } else {
				        	getActivity().showToastMsg(mActivity.getString(R.string.add_success));
				        }
					onItemClick(chapter, pos);
					
					dialog.cancel();
				}
			});
	        builder.show();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		getListView().setVerticalScrollBarEnabled(firstVisibleItem > 0);
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

	class Holder {
		TextView txvChapterTitle;
		Button btnChapterOperation;
	}

	protected ListView getListView() {
		ListView listView ;
		return (ListView) mView;
	}

	@Override
	protected View findViewById(int viewId) {
		return mHeadView.findViewById(viewId);
	}

	@Override
	public boolean compare(ViewBuilder builder) {
		return super.compare(builder) && ((VoiceBookInfoView) builder).mBookId == mBookId;
	}

	@Override
	protected boolean isReusable() {
		return false;
	}
	
	 private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
//			  mPlayService = ((PlayService.LocalBinder)service).getService();
			  play();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
	 };
	 
	 private void createPlayWindow(){
	 }

	 private void refreshView(int taskId,String localPath){
//		 for (Chapter chapt : mAdapter.getData()) {
//			 	if(chapt.getDownloadId() == taskId){
//			 		chapt.setDownloadId(-1);
//			 		mAdapter.notifyDataSetChanged();
//			 		return;
//			 	}
//			}
	 }
	 
	 private void resetState(int taskId){
//		 for (Chapter chapt : mAdapter.getData()) {
//			 	if(chapt.getDownloadId() == taskId){
//			 		chapt.setDownloadId(-1);
//			 		mAdapter.notifyDataSetChanged();
//			 		return;
//			 	}
//			}
	 }
	 
	 private String getChapterNameByTaskId(int taskId){
//		 for(Chapter ch: mAdapter.getData()){
//			 if(ch.getDownloadId() == taskId) return ch.getTitle();
//		 }
		 return "";
	 }
	 
	 private void gotoListener(){
//		Chapter chapter = mAdapter.getItem(mIndex);
//		 if(mPlayService == null){
//				Log.i(TAG, "mPlayService == null ");
//				Log.i(TAG, "book url is: " + chapter.getVoiceUrl());
//				if(chapter.getVoiceUrl() == null)return;
//
//				mContext.bindService(new Intent(mActivity,com.duowan.mobile.ixiaoshuo.service.PlayService.class),
//						mConnection, Context.BIND_AUTO_CREATE);
//		 }else{
//				 Log.i(TAG, "book url is: " + chapter.getVoiceUrl());
//				 mPlayService.play(Uri.parse(chapter.getVoiceUrl()));
//				 mPlayWindow.setPlayer(mPlayService);
//
//				 Chapter ch = mAdapter.getData().get(mIndex);
//				  if(isMediaFileExist(ch)){
//					  mPlayService.play(Uri.parse(getChapterLocalPath(ch)));
//				  }else{
//					  mPlayService.play(Uri.parse(ch.getVoiceUrl()));
//				  }
//				 mPlayService.setPlayList(mAdapter.getData(),mIndex);
//			}
	 }
	 
	private void downLoadAall() {
		DOWNLOAD_ALL_STATUS = DOWNLOAD_ALL_STATUS_DOWINING;
		for (Chapter chapt : mAdapter.getData()) {
			dowloadChapter(chapt);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	private void dowloadChapter(Chapter chapt){
		if(!new File(VOICE_BOOK_DOWNLOAD_DIR).exists()) new File(VOICE_BOOK_DOWNLOAD_DIR).mkdir();
//		Log.i(TAG, "dowloadChapter getVoiceUrl: " + chapt.getVoiceUrl());
//		if (!isMediaFileExist(chapt)) {
////			File filePath = new File(VOICE_BOOK_DOWNLOAD_DIR + mBookId);
////			if(!filePath.exists()) filePath.mkdir();
////			String fileNameString = chapt.getVoiceUrl();
////			String fileName =  fileNameString.substring(fileNameString.lastIndexOf("/")+1);
//
////			int taskId = getOnlineManageer().requestData(chapt.getVoiceUrl(), null,
////					null, filePath + "/" +fileName, 0);
////			add2DownloadList(taskId);
////			chapt.setDownloadId(taskId);
//		}
	}
	
	private boolean isMediaFileExist(Chapter chap){
//		if(chap.getVoiceUrl() == null || TextUtils.isEmpty(chap.getVoiceUrl())) return false;//防止有些章节uri为空
		return new File(getChapterLocalPath(chap)).exists();
	}
	
	private String getChapterLocalPath(Chapter chap){
//		String fileNameString = chap.getVoiceUrl();
//		if(fileNameString == null ||TextUtils.isEmpty(fileNameString)) return null;
//		String fileName =  fileNameString.substring(fileNameString.lastIndexOf("/")+1);
//		String filePath = VOICE_BOOK_DOWNLOAD_DIR + mBookId  ;
//		return filePath  + "/" + fileName;
		return null;
	}
	
	private void add2DownloadList(int taskId){
		if(mDownTaskList == null){
			mDownTaskList = new Vector<Integer>();
		}
		mDownTaskList.add(taskId);
	}
	
	private void removeTask(Integer taskId){
		if(mDownTaskList != null && mDownTaskList.contains(taskId)){
			mDownTaskList.remove(taskId);
		}
		boolean allDownload = true;
		for(Chapter ch : mAdapter.getData()){
			if(!isMediaFileExist(ch)){
				allDownload = false;
				break;
			}
		}
		if(allDownload){
			DOWNLOAD_ALL_STATUS = DOWNLOAD_ALL_STATUS_FINISHED;
		}
	}
	
	private void updateDownloadBtn() {
		switch (DOWNLOAD_ALL_STATUS) {
		case DOWNLOAD_ALL_STATUS_UN_STARTED:
			btnDownloadAll.setText(R.string.download_all);
			break;
			
		case DOWNLOAD_ALL_STATUS_DOWINING:
			btnDownloadAll.setText(R.string.download_pause);
			break;
			
		case DOWNLOAD_ALL_STATUS_FINISHED:
			btnDownloadAll.setText(R.string.download_check);
			break;

		default:
			break;
		}
	}
	
	private void play(){
//		  mPlayWindow.setPlayer(mPlayService);
//		  mPlayService.setPlayList(mAdapter.getData(),mIndex);
//		  Chapter ch = mAdapter.getData().get(mIndex);
//		  if(isMediaFileExist(ch)){
//			  mPlayService.play(Uri.parse(getChapterLocalPath(ch)));
//		  }else{
//			  if(ch.getVoiceUrl() == null || TextUtils.isEmpty(ch.getVoiceUrl()))
//			  getActivity().showToastMsg(mPlayService.getChapName() +" "+ mActivity.getString(R.string.play_faild));
//			  mPlayService.play(Uri.parse(ch.getVoiceUrl()));
//		  }
//		  mPlayService.setPlayListener(initPlayListener());
	}

}
