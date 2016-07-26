package org.shingo.shingoeventsapp.ui.events.viewadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEvent.SEventObject;
import org.shingo.shingoeventsapp.middle.SEvent.SRoom;
import org.shingo.shingoeventsapp.middle.SObject;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.ui.events.VenueDetailFragment;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.util.List;

/**
 * Created by dustinehoman on 7/22/16.
 */
public class MySectionedSEventObjectRecyclerView extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private final List<SectionedDataModel> mData;
    private final OnListFragmentInteractionListener mListener;

    public MySectionedSEventObjectRecyclerView(List<SectionedDataModel> data, OnListFragmentInteractionListener listener) {
        mData = data;
        mListener = listener;
    }

    @Override
    public int getSectionCount() {
        return mData.size();
    }

    @Override
    public int getItemCount(int section) {
        return mData.get(section).getItems().size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String day = mData.get(section).getHeader();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(day);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int section, int relativePosition, int absolutePosition) {
        List<? extends SObject> items = mData.get(section).getItems();

        final ItemViewHolder holder = (ItemViewHolder) vh;
        holder.mItem = (SEventObject) items.get(relativePosition);
        holder.mIdView.setText(holder.mItem.getName());
        holder.mContentView.setText(holder.mItem.getDetail().replaceAll("\\n"," ").replaceAll("\\s\\s", " "));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
                return new SectionViewHolder(v);
            case SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_seventobject, parent, false);
                return new ItemViewHolder(v);
            default:
                return new RecyclerView.ViewHolder(v) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        final TextView sectionTitle;

        public SectionViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.list_header);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public SEventObject mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}
