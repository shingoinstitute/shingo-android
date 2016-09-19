package org.shingo.shingoeventsapp.ui.events.viewadapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEvent.SSession;
import org.shingo.shingoeventsapp.ui.interfaces.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SSession} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 *
 */
public class MySessionRecyclerViewAdapter extends RecyclerView.Adapter<MySessionRecyclerViewAdapter.ViewHolder> {

    private List<SSession> mValues;
    private final OnListFragmentInteractionListener mListener;

    private ViewGroup mParent;
    private int mExpandedPosition = -1;

    public MySessionRecyclerViewAdapter(List<SSession> items, OnListFragmentInteractionListener listener){
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_session, parent, false);
        if(mParent == null) mParent = parent;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.mExpandedView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        ((ImageView)holder.mView.findViewById(R.id.expand_session)).setImageResource( isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        holder.mItem = mValues.get(position);
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
        holder.mSummaryView.setText( Html.fromHtml("<p>" + holder.mItem.getSummary().substring(0, holder.mItem.getSummary().length() > 3000 ? 3000 : holder.mItem.getSummary().length() - 1) + "</p>"));
        holder.mExpandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                TransitionManager.beginDelayedTransition(mParent);
                notifyDataSetChanged();
            }
        });

        if(holder.mItem.type == SSession.SSessionType.Social){
            holder.mSpeakersView.setVisibility(View.INVISIBLE);
        } else {
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
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTimeView;
        public final ImageView mExpandView;
        public final TextView mSummaryView;
        public final ImageView mSpeakersView;
        public final TextView mRoomView;
        public final View mExpandedView;

        public SSession mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.session_title);
            mTimeView = (TextView) view.findViewById(R.id.session_time);
            mExpandView = (ImageView) view.findViewById(R.id.expand_session);
            mSummaryView = (TextView) view.findViewById(R.id.session_summary);
            mSpeakersView = (ImageView) view.findViewById(R.id.session_speakers);
            mRoomView = (TextView) view.findViewById(R.id.room);
            mExpandedView = view.findViewById(R.id.expanded_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
