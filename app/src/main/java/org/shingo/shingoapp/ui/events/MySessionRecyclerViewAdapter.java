package org.shingo.shingoapp.ui.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEvent.SSession;
import org.shingo.shingoapp.ui.events.SessionFragment.OnSessionListFragmentInteractionListener;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SSession} and makes a call to the
 * specified {@link OnSessionListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySessionRecyclerViewAdapter extends RecyclerView.Adapter<MySessionRecyclerViewAdapter.ViewHolder> {

    private final List<SSession> mValues;
    private final OnSessionListFragmentInteractionListener mListener;

    public MySessionRecyclerViewAdapter(List<SSession> items, OnSessionListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getName());
        holder.mTimeView.setText(mValues.get(position).getTimeString());

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
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mTimeView;
        public SSession mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.session_title);
            mTimeView = (TextView) view.findViewById(R.id.session_time);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
