package org.shingo.shingoapp.ui.events.viewadapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.SessionFragment.*;

import java.util.List;

/**
 * Created by dustinehoman on 7/12/16.
 */
public class MySessionSectionedRecylclerViewAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private final List<SectionedSessionDataModel> mData;
    private final OnListFragmentInteractionListener mListener;

    public MySessionSectionedRecylclerViewAdapter(List<SectionedSessionDataModel> data, OnListFragmentInteractionListener listener){
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
        String day = mData.get(section).getDay();
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.sectionTitle.setText(day);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int section, int relativePosition, int absolutePosition) {
        List<SSession> items = mData.get(section).getItems();

        final ItemViewHolder holder = (ItemViewHolder) vh;
        holder.mItem = items.get(relativePosition);
        holder.mTitleView.setText(holder.mItem.getName());
        holder.mTimeView.setText(holder.mItem.getTimeString());
        holder.mSummaryView.setText(Html.fromHtml(holder.mItem.getSummary()));

        holder.mExpandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mSummaryView.setVisibility((holder.mSummaryView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                if(holder.mSummaryView.getVisibility() == View.VISIBLE)
                    ((ImageView)holder.mView.findViewById(R.id.expand_session)).setImageResource(R.drawable.ic_expand_less);
                else
                    ((ImageView)holder.mView.findViewById(R.id.expand_session)).setImageResource(R.drawable.ic_expand_more);
            }
        });

        if(holder.mItem.type == SSession.SSessionType.Social){
            holder.mSpeakersView.setVisibility(View.INVISIBLE);
        } else {
            holder.mSpeakersView.setVisibility(View.VISIBLE);
            holder.mSpeakersView.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType){
            case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_header, parent, false);
                return new SectionViewHolder(v);
            case SectionedRecyclerViewAdapter.VIEW_TYPE_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_session, parent, false);
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

    public static class SectionViewHolder extends RecyclerView.ViewHolder{
        final TextView sectionTitle;

        public SectionViewHolder(View itemView){
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.list_header);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTimeView;
        public final ImageView mExpandView;
        public final TextView mSummaryView;
        public final ImageView mSpeakersView;

        public SSession mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.session_title);
            mTimeView = (TextView) view.findViewById(R.id.session_time);
            mExpandView = (ImageView) view.findViewById(R.id.expand_session);
            mSummaryView = (TextView) view.findViewById(R.id.session_summary);
            mSpeakersView = (ImageView) view.findViewById(R.id.session_speakers);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
