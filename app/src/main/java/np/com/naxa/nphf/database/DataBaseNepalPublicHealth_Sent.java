package np.com.naxa.nphf.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Samir on 4/4/2017.
 */
public class DataBaseNepalPublicHealth_Sent extends SQLiteOpenHelper {
        private final static String db_name = "_db_NPHF_Sent.db";
        private final static int db_version = (int) 1;

        public final static String ID_TABLE = "_id_table";
        public final static String TABLE_ID  = "_table_id";
        public final static String TABLE_NAME = "_table_Name";
        public final static String TABLE_DATE = "_table_date";
        public final static String TABLE_JSON = "_table_json";
        public final static String TABLE_STATUS = "_table_status";
        public final static String TABLE_GPS = "_table_Gps";
        public final static String TABLE_PHOTO = "_table_photo";
        public final static String DELETE_FLAG = "_delete_flag";
        public final static String TABLE_MAIN = "_table_sent";

        public final static String[] COLS_TABLE_MAIN = new String[]{ID_TABLE , TABLE_ID ,TABLE_NAME ,TABLE_DATE ,TABLE_JSON , TABLE_GPS , TABLE_PHOTO , TABLE_STATUS  , DELETE_FLAG };
        static String CREATE_TABLE_MAIN = "Create table if not exists " + TABLE_MAIN + "("
                + ID_TABLE + " INTEGER PRIMARY KEY AUTOINCREMENT ," + TABLE_ID + " Text not null ,"  + TABLE_NAME + " Text not null ,"
                + TABLE_DATE + " Text not null ," + TABLE_JSON+ " Text not null ," + TABLE_GPS + " Text not null ," + TABLE_PHOTO + " Text not null ,"
                + TABLE_STATUS + " Text not null ," + DELETE_FLAG + " Text not null )";;
        static final String DROP_TABLE_MAIN = "DROP TABLE IF EXISTS " + TABLE_MAIN + ";";

        SQLiteDatabase db = null;
        long id;
        Context con;

    public DataBaseNepalPublicHealth_Sent(Context context) {
        super(context, db_name, null, db_version);
        this.con = context;
        // TODO Auto-generated constructor stub
    }

    @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(CREATE_TABLE_MAIN);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL(DROP_TABLE_MAIN);

            onCreate(db);
        }

        public void open() throws SQLException {
            db = this.getWritableDatabase();
        }

        public void close() {
            db.close();
        }


        public long insertIntoTable_Main(String[] list) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(TABLE_ID , list[0]);
            contentValues.put(TABLE_NAME, list[1] );
            contentValues.put(TABLE_DATE, list[2]);
            contentValues.put(TABLE_JSON, list[3]);
            contentValues.put(TABLE_GPS, list[4]);
            contentValues.put(TABLE_PHOTO, list[5]);
            contentValues.put(TABLE_STATUS , list[6]);
            contentValues.put(DELETE_FLAG, list[7]);
            long id = db.insert(TABLE_MAIN, null, contentValues);
            return id;
        }
        public boolean is_TABLE_MAIN_Empty(){
            boolean b=true;
            Cursor cursor = db.query(TABLE_MAIN, COLS_TABLE_MAIN, null, null, null, null, null);
            if( cursor != null && cursor.moveToFirst() ){
                b=false;
            }else{
                b=true;
            }
            return b;
        }
        public String[] return_Data_TABLE_MAIN(int id ) {
            Cursor cursor = db.query( TABLE_MAIN , COLS_TABLE_MAIN ,  "_table_id=? and _delete_flag=?", new String[]{String.valueOf(id), "0" }, null, null, null);
            String[] name = new String[8];
            try {
                while (cursor.moveToNext()) {
                    name[0] = cursor.getString(0);
                    name[1] = cursor.getString(1);
                    name[2] = cursor.getString(2);
                    name[3] = cursor.getString(3);
                    name[4] = cursor.getString(4);
                    name[5] = cursor.getString(5);
                    name[6] = cursor.getString(6);
                    name[7] = cursor.getString(7);
                }
            } finally {
                cursor.close();
            }
            return name;
        }
        public int returnTotalNoOf_TABLE_MAIN_NUM() {
            Cursor cursor = db.query( TABLE_MAIN , COLS_TABLE_MAIN , null, null, null,
                    null, null);
            int count = cursor.getCount();
            return count;
        }

    public String[] return_Data_ID(int id ) {
        Cursor cursor = db.query( TABLE_MAIN , COLS_TABLE_MAIN , ID_TABLE + "='" + id + " ' ", null , null, null, null);
        String[] name = new String[9];
        try {
            while (cursor.moveToNext()) {
                name[0] = cursor.getString(1);
                name[1] = cursor.getString(2);
                name[2] = cursor.getString(3);
                name[3] = cursor.getString(4);
                name[4] = cursor.getString(5);
                name[5] = cursor.getString(6);
                name[6] = cursor.getString(7);
                name[7] = cursor.getString(8);
                name[8] = cursor.getString(0);


            }
        } finally {
            cursor.close();
        }
        return name;
    }

    public long updateTable_DeleteFlag(String id){
        ContentValues values = new ContentValues();
        values.put(DELETE_FLAG, "1");
        int rowsUpdated = db.update( TABLE_MAIN , values , ID_TABLE  + "='" + id + " ' ", null);
        return rowsUpdated;
    }

    public void dropRowSentForms(String DBid) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();
        Log.e("", "dropRowSentFormsID: "+ DBid );
        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_MAIN + " WHERE " + ID_TABLE + "= '" + DBid + "'");
        //Close the database
        database.close();
    }

}
