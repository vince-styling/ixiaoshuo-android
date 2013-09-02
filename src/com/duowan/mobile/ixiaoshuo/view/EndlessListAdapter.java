package com.duowan.mobile.ixiaoshuo.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * a ListAdapter with loading progress view
 * source from : https://github.com/mttkay/ignition
 * origins : https://github.com/mttkay/droid-fu
 */
public abstract class EndlessListAdapter<T> extends BaseAdapter {
	private ArrayList<T> data = new ArrayList<T>();
	private boolean isLoadingData;

	/**
	 * {@inheritDoc}
	 * <p>
	 * Don't use this to check for the presence of actual data items; use {@link #hasItems()}
	 * instead.
	 * </p>
	 */
	@Override
	public int getCount() {
		int size = 0;
		if (data != null) {
			size += data.size();
		}
		if (isLoadingData) {
			size += 1;
		}
		return size;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Don't use this to check for the presence of actual data items; use {@link #hasItems()}
	 * instead.
	 * </p>
	 */
	@Override
	public boolean isEmpty() {
		return getCount() == 0 && !isLoadingData;
	}

	/**
	 * @return the actual number of data items in this adapter, ignoring the progress item.
	 */
	public int getItemCount() {
		return data != null ? data.size() : 0;
	}

	/**
	 * @return true if there are actual data items, ignoring the progress item.
	 */
	public boolean hasItems() {
		return data != null && !data.isEmpty();
	}

	@Override
	public T getItem(int position) {
		return data != null ? data.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEnabled(int position) {
		return !isPositionOfProgressElement(position);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	public void setIsLoadingData(boolean isLoadingData) {
		setIsLoadingData(isLoadingData, true);
	}

	public void setIsLoadingData(boolean isLoadingData, boolean redrawList) {
		this.isLoadingData = isLoadingData;
		if (redrawList) notifyDataSetChanged();
	}

	public boolean isLoadingData() {
		return isLoadingData;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		return isPositionOfProgressElement(position) ? getProgressView() : getView(position, convertView);
	}

	protected abstract View getView(int position, View convertView);

	private View progressView;
	protected View getProgressView() {
		if (progressView == null) progressView = initProgressView();
		return progressView;
	}
	protected abstract View initProgressView();

	private boolean isPositionOfProgressElement(int position) {
		return isLoadingData && position == data.size();
	}

	@Override
	public int getItemViewType(int position) {
		return isPositionOfProgressElement(position) ? IGNORE_ITEM_VIEW_TYPE : 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public ArrayList<T> getData() {
		return data;
	}

	public void setData(List<T> items) {
		data.clear();
		data.addAll(items);
		notifyDataSetChanged();
	}

	public void addAll(List<T> items) {
		data.addAll(items);
		notifyDataSetChanged();
	}

	public void addAll(List<T> items, boolean redrawList) {
		data.addAll(items);
		if (redrawList) notifyDataSetChanged();
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}

	public void remove(int position) {
		data.remove(position);
		notifyDataSetChanged();
	}

	/**
	 * Call this method from {@link android.widget.AbsListView.OnScrollListener#onScroll(AbsListView, int, int, int)} to
	 * determine whether the adapter should fetch the next page of data.
	 *
	 * <p>
	 * A typical implementation in your Activity might look like this:
	 *
	 * <pre>
	 * public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
	 *         int totalItemCount) {
	 *
	 *     if (adapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
	 *         // fetch next page, e.g. make a Web service call
	 *         // ...
	 *         adapter.setIsLoadingData(true);
	 *     }
	 * }
	 * </pre>
	 *
	 * </p>
	 *
	 * @param firstVisibleItem
	 *            passed down from {@link android.widget.AbsListView.OnScrollListener#onScroll(AbsListView, int, int, int)}
	 * @param visibleItemCount
	 *            passed down from {@link android.widget.AbsListView.OnScrollListener#onScroll(AbsListView, int, int, int)}
	 * @param totalItemCount
	 *            passed down from {@link android.widget.AbsListView.OnScrollListener#onScroll(AbsListView, int, int, int)}
	 * @return true if the bottom of the list was reached, and hence the next page of data should be
	 *         loaded
	 */
	public boolean shouldRequestNextPage(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// subtract the progress element, or otherwise this will screw up the counts...but do it
		// ONLY if data is currently loading, or this will screw up the counts as well! :)
		if (isLoadingData) totalItemCount -= 1;
		boolean lastItemReached = (totalItemCount > 0) && (totalItemCount - visibleItemCount == firstVisibleItem);
		return !isLoadingData && lastItemReached;
	}

}
