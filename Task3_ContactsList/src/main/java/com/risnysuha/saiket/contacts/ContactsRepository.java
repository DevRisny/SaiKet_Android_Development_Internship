package com.risnysuha.saiket.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Step 2: reads the contacts (name and phone number) from the device with a ContentResolver. */
public final class ContactsRepository {

    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private ContactsRepository() {
    }

    public static List<Contact> load(ContentResolver resolver) {
        // One contact can have several numbers, so rows are grouped by CONTACT_ID.
        Map<Long, Contact> byId = new LinkedHashMap<>();
        try (Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC")) {
            if (cursor == null) {
                return new ArrayList<>();
            }
            int idCol = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            int nameCol = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY);
            int numberCol = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idCol);
                Contact contact = byId.get(id);
                if (contact == null) {
                    contact = new Contact(id, cursor.getString(nameCol));
                    byId.put(id, contact);
                }
                contact.addNumber(cursor.getString(numberCol));
            }
        }
        return new ArrayList<>(byId.values());
    }
}
