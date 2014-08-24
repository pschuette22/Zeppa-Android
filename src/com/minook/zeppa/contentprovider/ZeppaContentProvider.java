package com.minook.zeppa.contentprovider;


import android.content.ContentProvider;
import android.content.Context;
import android.content.ContentValues;
import android.content.ContentUris;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.provider.Settings.System;

public class ZeppaContentProvider extends ContentProvider{
	static final String TAG = "ZeppaContentProvider";
	
	static final String AUTHORITY = 
			"content://com.minook.zeppa.contentprovider.ZeppaContentProvider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	static final String SINGLE_RECORD_MIME_TYPE =
			"vnd.android.database.cursor.item/vnd.minook.zeppa.ZeppaContract";
	static final String MULITPLE_RECORDS_MIME_TYPE = 
			"vnd.android.database.cursor.dir/vnd.minook.zeppa.ZeppaContract";
	private LocalDatabaseHelper zeppaHelper;
	private static final String DBNAME = "localZeppa.db";
	public static final int DB_Version = 1;
	private static final String SQL_CREATE_Local = "CREATE TABLE " +
			"local " +
			"(" +
			" _ID INTEGER PRIMARY KEY, " +
			" WORD TEXT" +
			" FREQUENCY INTEGER " +
			" LOCAL TEXT )";
	
	public static final class LocalDatabaseHelper extends SQLiteOpenHelper
	{
		
		LocalDatabaseHelper(Context context)
		{
			super(context, DBNAME, null, DB_Version);
		}
		
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(SQL_CREATE_Local);
		}
		
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("Drop table if exists");
			onCreate(db);
		}
	}
	
	
	@Override
	public boolean onCreate() {
		
		Context context = getContext();
		zeppaHelper = new LocalDatabaseHelper(context);
		return (zeppaHelper == null) ? false : true;
		//Log.d(TAG, "onCreate");
		//return true;
		// TODO Auto-generated method stub
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Log.d(TAG, "query with uri: " + uri.toString());
		SQLiteDatabase db = zeppaHelper.getWritableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		qb.setTables(DBNAME);
		
		qb.appendWhere(ZeppaContract.CommonColumns._ID + "=" +
		uri.getLastPathSegment());
		
		/*String orderBy = TextUtils.isEmpty(sortOrder) 
								? ZeppaContract.CommonColumns.DEFAULT_SORT_ORDER
								: sortOrder;*/
		
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		// TODO Auto-generated method stub
		return c;
		
	}

	@Override
	public String getType(Uri uri) {
		
		String ret = getContext().getContentResolver().getType(System.CONTENT_URI);
		Log.d(TAG, "getType returning: " + ret);
		// TODO Auto-generated method stub
		return ret;

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		Uri result = null;
		
		
		if(uri == null || values == null)
		{
			return result;
		}
		
		SQLiteDatabase db = zeppaHelper.getWritableDatabase();
		long rowID = db.insert(DBNAME, null, values);
		
		if (rowID > 0)
		{
			result = ContentUris.withAppendedId(ZeppaContract.ZEPPAEVENT_URI, rowID);
			
			getContext().getContentResolver().notifyChange(result, null);
		}
		//Log.d(TAG, "insert uri: " + uri.toString());
		// TODO Auto-generated method stub
		 
		 
		return result;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		//Log.d(TAG, "delete uri: " + uri.toString());
		// TODO Auto-generated method stub
		
		int count;
		
		SQLiteDatabase db = zeppaHelper.getWritableDatabase();
		count = db.delete(DBNAME, selection, selectionArgs); //only deletes directory
		
		if(count > 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return count;
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		SQLiteDatabase db = zeppaHelper.getWritableDatabase();
		int count;
		
		count = db.update(DBNAME, values, selection, selectionArgs);
		
		if( count > 0)
		{
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		//Log.d(TAG, "update uri: " + uri.toString());
		
		// TODO Auto-generated method stub
		return count;
		
	}
	

	

}



