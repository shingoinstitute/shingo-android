package org.shingo.shingoeventsapp.ui.events.viewadapters;

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

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;

import org.shingo.shingoeventsapp.R;
import org.shingo.shingoeventsapp.middle.SEntity.SEntity;
import org.shingo.shingoeventsapp.middle.SObject;
import org.shingo.shingoeventsapp.middle.SectionedDataModel;

import java.io.InputStream;
import java.util.List;

/**
 * Created by dustinehoman on 7/19/16.
 */
public class MySectionedSEntityRecyclerViewAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private final List<SectionedDataModel> mData;

    public MySectionedSEntityRecyclerViewAdapter(List<SectionedDataModel> data){
        mData = data;
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
        holder.mItem = (SEntity)items.get(relativePosition);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mDetailView.setText(holder.mItem.getDetail());
        holder.mSummaryView.setText(Html.fromHtml(holder.mItem.getSummary()));

        if(holder.mItem.getImage() == null) {
            holder.mPictureView.setVisibility(View.INVISIBLE);
            DownloadImageTask downloadImageTask = new DownloadImageTask(holder.mPictureView, holder.mItem);
            downloadImageTask.execute(holder.mItem.getImageUrl());
        } else {
            holder.mPictureView.setImageBitmap(holder.mItem.getImage());
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
                        .inflate(R.layout.fragment_sentity, parent, false);
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
        public final ImageView mPictureView;
        public final TextView mNameView;
        public final TextView mDetailView;
        public final TextView mSummaryView;
        public SEntity mItem;

        public ItemViewHolder(View view) {
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

        public DownloadImageTask(ImageView bmImage, SEntity entity) {
            this.imageView = bmImage;
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
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(result);
            entity.setImage(result);
        }
    }
}
