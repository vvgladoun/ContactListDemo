package gladun.vladimir.contactlistdemo.contact_list;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import gladun.vladimir.contactlistdemo.utilities.DBHelper;

/**
 * @author vvgladoun@gmail.com
 */
public class ContactListManager {

    public static final int SORT_ASCENDING = 1;
    public static final int SORT_DESCENDING = 2;


    public static Cursor createCursorAllContacts(SQLiteDatabase dbConnection, int sortDirection) {

        String selectContacts = "SELECT " + DBHelper.COLUMN_ID + ", " + DBHelper.COLUMN_EMAIL
                + ", " + DBHelper.COLUMN_NAME
                + " FROM " + DBHelper.TABLE_CONTACT + " ORDER BY "
                + DBHelper.COLUMN_NAME + ((sortDirection == SORT_ASCENDING)?" ASC ":" DESC ");

        //return cursor
        return dbConnection.rawQuery(selectContacts, null);
    }

    public static void removeContact(SQLiteDatabase dbConnection, int id){
        String deleteContact = "DELETE FROM " + DBHelper.COLUMN_ID + " FROM "
                + DBHelper.TABLE_CONTACT + " WHERE " + DBHelper.COLUMN_ID + "=" + id;

        dbConnection.execSQL(deleteContact);
    }

}
