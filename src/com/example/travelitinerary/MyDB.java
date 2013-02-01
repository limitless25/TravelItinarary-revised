package com.example.travelitinerary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDB {
	
	public static final String KEY_LONGI = "Longitude";
	public static final String KEY_LATI = "Latitude";
	public static final String KEY_PATH = "Path";
	public static final String KEY_INDEX = "Index";
	public static final String KEY_ROWID = "_id";
	
	private static final String TAG = "DbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_CREATE = 
			"create table data (_id integer primary key autoincrement," + 
	"Longitude int not null, Latitude int not null, Index int not null, Path text not null, );";
	
	private static final String DATABASE_NAME = "datum.db";
	private static final String DATABASE_TABLE = "data";
	private static final int DATABASE_VERSION = 1;
	
	private Context mCtx;
	
	private class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading db from version" + oldVersion + " to" + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABEL IF EXISTS data");
			onCreate(db);			
		}		
	}
	
	public MyDB(Context ctx){
		this.mCtx = ctx;
	}
	
	public MyDB open() throws SQLException{
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		mDb.close();
		mDbHelper.close();
	}
	
	public long createRec(int longi, int lat, String path, int index){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LONGI, longi);
		initialValues.put(KEY_LATI, lat);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_INDEX, index);
		
		return mDb.insert(DATABASE_TABLE, null, initialValues);		
	}
	
	public boolean deleteRec(long rowID){
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null) > 0;				
	}
	
	public void dropTable(){
		mDb.execSQL("DROP table data");		
	}
	
	public Cursor fetchAllRec(){
		return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_LONGI, KEY_LATI, KEY_INDEX, KEY_PATH }, null, null, null, null, null);
	}
	
	public Cursor fetchRec(long rowID) throws SQLException{
		Cursor mCursor = 
				mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_LONGI, 
						KEY_LATI, KEY_INDEX, KEY_PATH }, KEY_ROWID + "=" + rowID, null, null, null, null);
		if(mCursor != null)
			mCursor.moveToFirst();		
		return mCursor;
	}
	
	public boolean updateRec(long rowID, String longi, String lati, String path, int index){
		ContentValues args = new ContentValues();
		args.put(KEY_LONGI, longi);
		args.put(KEY_LATI, lati);
		args.put(KEY_PATH, path);
		args.put(KEY_INDEX, index);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowID, null) > 0;		
	}	
}




