package gladun.vladimir.contactlistdemo.contact_list;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gladun.vladimir.contactlistdemo.utilities.CursorRecyclerViewAdapter;
import gladun.vladimir.contactlistdemo.utilities.DBHelper;
import gladun.vladimir.contactlistdemo.R;


/**
 * Custom cursor adapter
 * for the contact list
 * (adapter for recycler view)
 *
 * @author vvgladoun@gmail.com
 */
public class ContactsCursorAdapter extends CursorRecyclerViewAdapter<ContactViewHolder> {

    SQLiteDatabase mDatabase;
    public ContactsCursorAdapter(Context context, Cursor cursor, SQLiteDatabase db) {
        super(context, cursor);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(v);
    }



    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, Cursor cursor) {
        viewHolder.mNameText.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NAME)));
        viewHolder.mEmailText.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DBHelper.COLUMN_EMAIL)));

        //bind contact to holder
        int contactId = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID));
        viewHolder.bindContact(contactId);


    }
}
