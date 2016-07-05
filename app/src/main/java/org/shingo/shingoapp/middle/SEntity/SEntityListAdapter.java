package org.shingo.shingoapp.middle.SEntity;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.shingo.shingoapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * A custom list adapter for {@link SEntity}.
 * Extends {@link BaseAdapter}.
 *
 * @author Dustin Homan
 * @see org.shingo.shingoapp.R.layout#img_adapter_row
 */
public class SEntityListAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private List<Object> data = new ArrayList<>();
    private TreeSet<Integer> sectionHeader = new TreeSet<>();
    private Context context;
    private static LayoutInflater inflater;

    /**
     * The constructor for the list adapter.
     * @param context the context of the Activity the {@link android.widget.ListView} belongs to
     */
    public SEntityListAdapter(Context context){
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    /**
//     * Adds a single item to {@link SponsorsListAdapter#data}
//     * @param item expecting {@link SEntity} or {@link String}
//     */
//    public void addItem(Object item){
//        data.add(item);
//        notifyDataSetChanged();
//    }

    /**
     * Adds all items in a {@link List} to {@link SEntityListAdapter#data}
     * @param items expecting a {@link List} of {@link SEntity}
     */
    public void addAllItems(List items){
        data.addAll(items);
    }

    /**
     * Adds a section header to {@link SEntityListAdapter#data}
     * @param item Header name
     */
    public void addSectionHeaderItem(String item){
        data.add(item);
        sectionHeader.add(data.size() - 1);
        notifyDataSetChanged();
    }

    /**
     * Returns whether the position at {@link SEntityListAdapter#data}
     * @param position index into {@link SEntityListAdapter#data}
     * @return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
     */
    @Override
    public int getItemViewType(int position){
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    /**
     * Returns the type count
     * @return 2
     */
    @Override
    public int getViewTypeCount(){
        return 2;
    }

    /**
     * Get the size of the source data.
     * @return {@link SEntityListAdapter#data}.size()
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     *
     * @param position an index to {@link SEntityListAdapter#data}
     * @return the {@link Object}
     *  at {@link SEntityListAdapter#data}.get(position)
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     *
     * @param position an index to {@link SEntityListAdapter#data}
     * @return position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get view utilizing the holder/recycling method
     * @param position an index to {@link SEntityListAdapter#data}
     * @param convertView a view already initialized, enables view recycling
     * @param parent the containing {@link ViewGroup}
     * @return a {@link View} inflated with {@link org.shingo.shingoapp.R.layout#img_adapter_row}
     *  and populated with info from the item at position in {@link SEntityListAdapter#data}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object holder = null;
        int rowType = getItemViewType(position);

        if(convertView == null){
            switch (rowType){
                case TYPE_ITEM:
                    holder = new ContentHolder();
                    convertView = inflater.inflate(R.layout.img_adapter_row, parent, false);
                    ((ContentHolder)holder).image = (ImageView)convertView.findViewById(R.id.list_image);
                    ((ContentHolder)holder).name = (TextView)convertView.findViewById(R.id.list_name);
                    ((ContentHolder)holder).detail = (TextView)convertView.findViewById(R.id.list_title);
                    break;
                case TYPE_SEPARATOR:
                    holder = new HeaderHolder();
                    convertView = inflater.inflate(R.layout.list_header, parent, false);
                    ((HeaderHolder)holder).header = (TextView)convertView.findViewById(R.id.list_header);
                    break;
            }
            assert convertView != null;
            convertView.setTag(holder);
        } else {
            holder = convertView.getTag();
        }

        if(holder instanceof ContentHolder) {
            Object item = getItem(position);
            ((ContentHolder) holder).image.setImageDrawable(new BitmapDrawable(context.getResources(), ((SEntity) item).getImage()));
            ((ContentHolder) holder).name.setText(((SEntity) item).getName());
            ((ContentHolder) holder).detail.setText(((SEntity) item).getDetail());
            if(((ContentHolder) holder).detail.getText().equals("")){
                ((ContentHolder) holder).detail.setVisibility(View.GONE);
            }
        } else {
            ((HeaderHolder)holder).header.setText((String) getItem(position));
        }

        return convertView;
    }

    /**
     * Content holder class
     */
    public static class ContentHolder{
        public ImageView image;
        public TextView name;
        public TextView detail;
    }

    /**
     * Header holder class
     */
    public static class HeaderHolder{
        public TextView header;
    }
}
