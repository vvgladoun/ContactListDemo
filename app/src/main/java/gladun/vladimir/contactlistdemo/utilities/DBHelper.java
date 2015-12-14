package gladun.vladimir.contactlistdemo.utilities;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import gladun.vladimir.contactlistdemo.model.Contact;

/**
 * SQLite helper class for contacts database
 *
 * @author vvgladoun@gmail.com
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Contacts.db";

    //Table name (contacts, company and address were denormalized)
    public static final String TABLE_CONTACT = "contact";

    // Places table columns names
    //  contact details
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "contact_name";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_WEBSITE = "website";
    //  address
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_SUITE = "suite";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_ZIPCODE = "zipcode";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    //  company info
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_CATCHPHRASE = "catchphrase";
    public static final String COLUMN_BS = "bs";

    // the only table in a database - denormalized data of contacts, addresses and companies
    final String CREATE_TABLE_CONTACT = "CREATE TABLE " + TABLE_CONTACT + " ("
            + COLUMN_ID + " integer primary key unique, "
            + COLUMN_NAME + " text, "
            + COLUMN_USERNAME + " text, "
            + COLUMN_PHONE + " text, "
            + COLUMN_WEBSITE + " text, "
            + COLUMN_EMAIL + " text, "
            + COLUMN_STREET + " text, "
            + COLUMN_SUITE + " text, "
            + COLUMN_CITY + " text, "
            + COLUMN_ZIPCODE + " text, "
            + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real, "
            + COLUMN_COMPANY + " text, "
            + COLUMN_CATCHPHRASE + " text, "
            + COLUMN_BS + " text )";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,
                    DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //According to requirements there is no need to save users data on db update
        // (data to be fetched from JSON file)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        onCreate(db);
        Log.i(TAG, "Database was updated from ver." + oldVersion + " to ver." + newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    public static boolean batchLoadContacts(SQLiteDatabase dbConnection, ArrayList<Contact> contacts) {
        if (contacts.isEmpty()) {
            return false;
        }

        boolean loaded = false;

        try {
            // truncate table
            dbConnection.execSQL("delete from " + DBHelper.TABLE_CONTACT);
            // insert all contacts
            dbConnection.beginTransaction();
            String sql = "INSERT INTO " + DBHelper.TABLE_CONTACT + " ("
                    + DBHelper.COLUMN_ID + ", "
                    + DBHelper.COLUMN_NAME + ", "
                    + DBHelper.COLUMN_USERNAME + ", "
                    + DBHelper.COLUMN_PHONE + ", "
                    + DBHelper.COLUMN_WEBSITE + ", "
                    + DBHelper.COLUMN_EMAIL + ", "
                    + DBHelper.COLUMN_STREET + ", "
                    + DBHelper.COLUMN_SUITE + ", "
                    + DBHelper.COLUMN_CITY + ", "
                    + DBHelper.COLUMN_ZIPCODE + ", "
                    + DBHelper.COLUMN_LATITUDE + ", "
                    + DBHelper.COLUMN_LONGITUDE + ", "
                    + DBHelper.COLUMN_COMPANY + ", "
                    + DBHelper.COLUMN_CATCHPHRASE + ", "
                    + DBHelper.COLUMN_BS
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement insertStmt = dbConnection.compileStatement(sql);

            for (Contact contact : contacts) {
                insertStmt.clearBindings();
                insertStmt.bindLong(1, contact.getId());
                insertStmt.bindString(2, contact.getName());
                insertStmt.bindString(3, contact.getUsername());
                insertStmt.bindString(4, contact.getPhone());
                insertStmt.bindString(5, contact.getWebsite());
                insertStmt.bindString(6, contact.getEmail());
                insertStmt.bindString(7, contact.getAddress().getStreet());
                insertStmt.bindString(8, contact.getAddress().getSuite());
                insertStmt.bindString(9, contact.getAddress().getCity());
                insertStmt.bindString(10, contact.getAddress().getZipcode());
                insertStmt.bindDouble(11, contact.getAddress().getGeo().getLatitude());
                insertStmt.bindDouble(12, contact.getAddress().getGeo().getLongitude());
                insertStmt.bindString(13, contact.getCompany().getName());
                insertStmt.bindString(14, contact.getCompany().getCatchPhrase());
                insertStmt.bindString(15, contact.getCompany().getBs());

                insertStmt.executeInsert();
            }
            // commit transaction
            dbConnection.setTransactionSuccessful();
            loaded = true;
        } catch (Exception e) {
            Log.e(TAG, "Db loading failed");
        } finally {
            dbConnection.endTransaction();
        }
        return loaded;
    }
}
