package com.vincestyling.ixiaoshuo.view.finder;

import android.view.View;
import android.widget.TextView;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.ui.ComplexBookNameView;

public abstract class FinderAmplyBaseBookView extends FinderBaseListView {
    @Override
    protected View adapterGetView(int position, View convertView) {
        AHolder holder;
        if (convertView == null) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_amply_book_list_item, null);

            holder = new AHolder();
            holder.lotDivider = convertView.findViewById(R.id.lotDivider);

            holder.imvBookCover = (NetworkImageView) convertView.findViewById(R.id.imvBookCover);
            holder.txvBookName = (ComplexBookNameView) convertView.findViewById(R.id.txvBookName);
            holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);

            holder.txvBookTips = (TextView) convertView.findViewById(R.id.txvBookTips);
            holder.txvBookCapacity = (TextView) convertView.findViewById(R.id.txvBookCapacity);

            convertView.setTag(holder);
        } else {
            holder = (AHolder) convertView.getTag();
        }

        Book book = mAdapter.getItem(position);

        Netroid.displayImage(book.getCoverUrl(), holder.imvBookCover,
                R.drawable.book_cover_default, R.drawable.book_cover_default);

        holder.txvBookName.setSatisficer(this);
        holder.txvBookName.setBook(book);

        setBookTips(holder.txvBookTips, book);
        holder.txvBookSummary.setText(book.getSummary());
        holder.txvBookCapacity.setText(book.getCapacityStr());

        if (!mHasNextPage) {
            int posDiffer = mAdapter.getItemCount() - position;
            holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
        }

        return convertView;
    }

    private class AHolder extends Holder {
        NetworkImageView imvBookCover;
    }
}
