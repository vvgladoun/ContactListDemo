package gladun.vladimir.contactlistdemo.contact_list;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.CursorLoader;
import gladun.vladimir.contactlistdemo.utilities.DBHelper;


/**
 * @author vvgladoun@gmail.com
 */
public class ContactListCursorLoader extends CursorLoader {

    private DBHelper mDbHelper;
    private int mSortDirection;

    public ContactListCursorLoader(Context context, DBHelper dbHelper, int sortDirection) {
        super(context);
        mDbHelper = dbHelper;
        mSortDirection = sortDirection;
    }

    public void setSortDirection(int mSortDirection) {
        this.mSortDirection = mSortDirection;
    }

    @Override
    public Cursor loadInBackground() {

        SQLiteDatabase mDbContacts = mDbHelper.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = ContactListManager.createCursorAllContacts(mDbContacts, mSortDirection);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return cursor;
    }
}
