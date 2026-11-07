package com.risnysuha.saiket.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/** Step 2: the adapter that loads each image from res/drawable into a GridView cell. */
public class ImageAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<GalleryItem> items;

    public ImageAdapter(Context context, List<GalleryItem> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public GalleryItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getImageRes();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_image, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GalleryItem item = items.get(position);
        holder.image.setImageResource(item.getImageRes());
        holder.image.setContentDescription(item.getTitle());
        holder.title.setText(item.getTitle());
        holder.number.setText(String.valueOf(position + 1));
        return convertView;
    }

    /** Keeps the view look-ups so scrolling stays smooth. */
    private static final class ViewHolder {
        final ImageView image;
        final TextView title;
        final TextView number;

        ViewHolder(View root) {
            image = root.findViewById(R.id.item_image);
            title = root.findViewById(R.id.item_title);
            number = root.findViewById(R.id.item_number);
        }
    }
}
