package org.shingo.shingoapp.ui.events;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEvent.SDay;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SDay} and makes a call to the
 * specified {@link AgendaFragment.OnAgendaFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAgendaRecyclerViewAdapter extends RecyclerView.Adapter<MyAgendaRecyclerViewAdapter.ViewHolder> {

    private final List<SDay> mValues;
    private final AgendaFragment.OnAgendaFragmentInteractionListener mListener;

    public MyAgendaRecyclerViewAdapter(List<SDay> items, AgendaFragment.OnAgendaFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_agenda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());

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
        public final TextView mNameView;
        public SDay mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.info_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
