package org.shingo.shingoapp.ui.events.viewadapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoapp.R;
import org.shingo.shingoapp.middle.SEntity.SEntity;

import java.io.InputStream;
import java.util.List;

/**
 * Created by dustinehoman on 7/19/16.
 */
public class MySEntityRecyclerViewAdapter extends RecyclerView.Adapter<MySEntityRecyclerViewAdapter.ViewHolder> {

    private final List<? extends SEntity> mValues;

    public MySEntityRecyclerViewAdapter(List<? extends SEntity> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sentity_view, parent, false);
        return new ViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mView.findViewById(R.id.expand_entity_summary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mSummaryView.setVisibility((holder.mSummaryView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                if(holder.mSummaryView.getVisibility() == View.VISIBLE)
                    ((ImageView)holder.mView.findViewById(R.id.expand_entity_summary)).setImageResource(R.drawable.ic_expand_less);
                else
                    ((ImageView)holder.mView.findViewById(R.id.expand_entity_summary)).setImageResource(R.drawable.ic_expand_more);
            }
        });
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mDetailView.setText(holder.mItem.getDetail());
        holder.mSummaryView.setText(Html.fromHtml(holder.mItem.getSummary()));

        if(holder.mItem.getImage() == null) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(holder.mPictureView, holder.mItem);
            downloadImageTask.execute(holder.mItem.getImageUrl());
        } else {
            holder.mPictureView.setImageBitmap(holder.mItem.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mPictureView;
        public final TextView mNameView;
        public final TextView mDetailView;
        public final TextView mSummaryView;
        public SEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPictureView = (ImageView) view.findViewById(R.id.entity_picture);
            mNameView = (TextView) view.findViewById(R.id.entity_name);
            mDetailView = (TextView) view.findViewById(R.id.entity_detail);
            mSummaryView = (TextView) view.findViewById(R.id.entity_summary);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        SEntity entity;

        public DownloadImageTask(ImageView imageView, SEntity entity) {
            this.imageView = imageView;
            this.entity = entity;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            entity.setImage(result);
        }
    }
}
