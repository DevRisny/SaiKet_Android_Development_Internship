package com.risnysuha.saiket.contacts;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/** Step 3: fills the ListView with the contact names (plus their number and an initials avatar). */
public class ContactAdapter extends BaseAdapter {

    private static final int[] AVATAR_BG = {
            R.color.avatar_bg_1, R.color.avatar_bg_2, R.color.avatar_bg_3,
            R.color.avatar_bg_4, R.color.avatar_bg_5, R.color.avatar_bg_6};
    private static final int[] AVATAR_FG = {
            R.color.avatar_fg_1, R.color.avatar_fg_2, R.color.avatar_fg_3,
            R.color.avatar_fg_4, R.color.avatar_fg_5, R.color.avatar_fg_6};

    private final Context context;
    private final LayoutInflater inflater;
    private List<Contact> contacts = new ArrayList<>();

    public ContactAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void submit(List<Contact> newContacts) {
        contacts = new ArrayList<>(newContacts);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return contacts.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contacts.get(position);
        int color = ContactUtils.colorIndex(contact.getName(), AVATAR_BG.length);
        holder.avatar.setText(ContactUtils.initials(contact.getName()));
        holder.avatar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, AVATAR_BG[color])));
        holder.avatar.setTextColor(ContextCompat.getColor(context, AVATAR_FG[color]));
        holder.name.setText(contact.getName());
        holder.number.setText(ContactUtils.numberSummary(contact));
        return convertView;
    }

    private static final class ViewHolder {
        final TextView avatar;
        final TextView name;
        final TextView number;

        ViewHolder(View root) {
            avatar = root.findViewById(R.id.contact_avatar);
            name = root.findViewById(R.id.contact_name);
            number = root.findViewById(R.id.contact_number);
        }
    }
}
