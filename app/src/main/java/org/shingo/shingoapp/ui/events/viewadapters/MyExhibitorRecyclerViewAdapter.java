package org.shingo.shingoapp.ui.events.viewadapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEntity.SOrganization;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SOrganization} (Exhibitor).
 */
public class MyExhibitorRecyclerViewAdapter extends RecyclerView.Adapter<MyExhibitorRecyclerViewAdapter.ViewHolder> {

    private final List<SOrganization> mValues;

    public MyExhibitorRecyclerViewAdapter(List<SOrganization> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_exhibitor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getName());
        holder.mContentView.setText(holder.mItem.getDetail());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public SOrganization mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}