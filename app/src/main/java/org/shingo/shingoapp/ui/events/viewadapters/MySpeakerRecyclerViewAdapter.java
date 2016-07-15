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
import org.shingo.shingoapp.middle.SEntity.SPerson;

import java.io.InputStream;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SPerson} (Speaker).
 */
public class MySpeakerRecyclerViewAdapter extends RecyclerView.Adapter<MySpeakerRecyclerViewAdapter.ViewHolder> {

    private final List<SPerson> mValues;

    public MySpeakerRecyclerViewAdapter(List<SPerson> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sperson_content_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        (holder.mView.findViewById(R.id.expand_person)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mSummaryView.setVisibility((holder.mSummaryView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
                if(holder.mSummaryView.getVisibility() == View.VISIBLE)
                    ((ImageView)holder.mView.findViewById(R.id.expand_person)).setImageResource(R.drawable.ic_expand_less);
                else
                    ((ImageView)holder.mView.findViewById(R.id.expand_person)).setImageResource(R.drawable.ic_expand_more);
            }
        });
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mDetailView.setText(mValues.get(position).getDetail());
        holder.mSummaryView.setText(Html.fromHtml(mValues.get(position).getSummary()));

        if(mValues.get(position).getImage() == null) {
            DownloadImageTask downloadImageTask = new DownloadImageTask(holder.mPictureView, mValues.get(position));
            downloadImageTask.execute(mValues.get(position).getImageUrl());
        } else {
            holder.mPictureView.setImageBitmap(mValues.get(position).getImage());
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
        public SPerson mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPictureView = (ImageView) view.findViewById(R.id.person_picture);
            mNameView = (TextView) view.findViewById(R.id.person_name);
            mDetailView = (TextView) view.findViewById(R.id.person_detail);
            mSummaryView = (TextView) view.findViewById(R.id.person_bio);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        SPerson person;

        public DownloadImageTask(ImageView bmImage, SPerson person) {
            this.bmImage = bmImage;
            this.person = person;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
            person.setImage(result);
        }
    }
}
