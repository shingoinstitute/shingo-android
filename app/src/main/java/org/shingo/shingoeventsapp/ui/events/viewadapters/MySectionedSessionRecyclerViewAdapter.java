package org.shingo.shingoeventsapp.ui.events.viewadapters;

import android.animation.LayoutTransition;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEvent.SSession;
import org.shingo.shingoeventsapp.middle.SObject;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.util.List;

/**
 * Created by dustinehoman on 7/12/16.
 */
public class MySectionedSessionRecyclerViewAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private final List<SectionedDataModel> mData;
    private int mExpandedPosition = -1;
    private ViewGroup mParent;

    private final OnListFragmentInteractionListener mListener;

    public MySectionedSessionRecyclerViewAdapter(List<SectionedDataModel> data, OnListFragmentInteractionListener listener){
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int section, int relativePosition, final int absolutePosition) {
        List<? extends SObject> items = mData.get(section).getItems();
        final boolean isExpanded = absolutePosition == mExpandedPosition;

        final ItemViewHolder holder = (ItemViewHolder) vh;
        holder.mItem = (SSession)items.get(relativePosition);
        holder.mTitleView.setText(String.format("%s: %s", holder.mItem.type, holder.mItem.getName()));
        holder.mTimeView.setText(holder.mItem.getTimeString());
        if(holder.mItem.getRoom() != null) {
            holder.mRoomView.setVisibility(View.VISIBLE);
            holder.mRoomView.setText(String.format("Room: %s", holder.mItem.getRoom().getName()));
            holder.mRoomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onListFragmentInteraction(holder.mItem.getRoom());
                }
            });
        } else {
            holder.mRoomView.setVisibility(View.GONE);
        }
        ((ImageView)holder.mView.findViewById(R.id.expand_session)).setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        holder.mExpandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.mSummaryView.setText( Html.fromHtml("<p>" + holder.mItem.getSummary().substring(0, holder.mItem.getSummary().length() > 3000 ? 3000 : holder.mItem.getSummary().length() - 1) + "</p>"));
        holder.mExpandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : absolutePosition;
                TransitionManager.beginDelayedTransition(mParent);
                notifyDataSetChanged();
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
        if(mParent == null) mParent = parent;
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
        public final TextView mRoomView;
        public final LinearLayout mExpandedView;

        public SSession mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.session_title);
            mTimeView = (TextView) view.findViewById(R.id.session_time);
            mExpandView = (ImageView) view.findViewById(R.id.expand_session);
            mSummaryView = (TextView) view.findViewById(R.id.session_summary);
            mSpeakersView = (ImageView) view.findViewById(R.id.session_speakers);
            mRoomView = (TextView) view.findViewById(R.id.room);
            mExpandedView = (LinearLayout) view.findViewById(R.id.expanded_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
