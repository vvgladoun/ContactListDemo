package gladun.vladimir.contactlistdemo.contact_details;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import gladun.vladimir.contactlistdemo.model.Address;
import gladun.vladimir.contactlistdemo.model.Company;
import gladun.vladimir.contactlistdemo.model.Contact;
import gladun.vladimir.contactlistdemo.model.Geo;
import gladun.vladimir.contactlistdemo.utilities.DBHelper;

/**
 * Contact manager
 * to get contact object from the database
 *
 * @author vvgladoun@gmail.com
 */
public class ContactManager {

    private Context mContext;
    private static final String TAG = ContactManager.class.getSimpleName();

    public ContactManager(Context context){
        mContext = context;
    }


    public Contact getContact(int id){
        Contact contact = null;

        DBHelper dbHelper = new DBHelper(mContext);
        SQLiteDatabase mDbContacts = dbHelper.getWritableDatabase();

        // select contact by id
        Cursor cursor = mDbContacts.query(DBHelper.TABLE_CONTACT, null,
                DBHelper.COLUMN_ID + " = ?", new String[]{(String.valueOf(id))},
                null, null, null);
        if (cursor.moveToFirst()) {
            try {
                Company company = new Company(
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_BS)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CATCHPHRASE)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_COMPANY)));
                Geo geo = new Geo(
                        cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_LONGITUDE)));
                Address address = new Address(
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CITY)),
                        geo,
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_STREET)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SUITE)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ZIPCODE)));
                contact = new Contact(
                        cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PHONE)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_WEBSITE)),
                        address, company);
            } catch (IllegalStateException e) {
                Log.e(TAG, "Could not fetch contact details");
            }
        }

        cursor.close();
        mDbContacts.close();
        dbHelper.close();

        return contact;
    }
}
