package com.minook.zeppa.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ZeppaDataHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "ZeppaContent.db";
	private final static int VERSION = 1;
	private Context context;
	private CursorFactory factory;

	public ZeppaDataHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, VERSION);
		this.context = context;
		this.factory = factory;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ZeppaContract.UserInfoContract.SQL_MAKE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
	
	public Cursor getContactsSyncCursor() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(ZeppaContract.UserInfoContract.TABLE_NAME,
				ZeppaContract.UserInfoContract.USER_SYNC_PROJECTION, "USER_RELATIONSHIP > 4 " /*Only Get Unknown/ */,
				null, null, null, ZeppaContract.UserInfoContract.CONTACTS_ID + " asc");
		db.close();
		return c;
	}
	
	/**
	 * Once a runtime data object has been made, insert it
	 * @param data
	 * @return
	 */
	public ZeppaUserData insertUserData(ZeppaUserData data){
		long rowId = -1;
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(ZeppaContract.UserInfoContract.ZEPPA_ID, data.zeppaId);
		values.put(ZeppaContract.UserInfoContract.GOOGLE_ACCOUNT, data.accountName);
		values.put(ZeppaContract.UserInfoContract.ZEPPA_RELATIONSHIP_ID, data.dataRelationship.ordinal());
		values.put(ZeppaContract.UserInfoContract.GIVEN_NAME, data.givenName);
		values.put(ZeppaContract.UserInfoContract.FAMILY_NAME, data.familyName);
		values.put(ZeppaContract.UserInfoContract.IMAGE_URL, data.imageUrl);
		values.put(ZeppaContract.UserInfoContract.PHONE_NUMBER, data.phoneNumber);
		values.put(ZeppaContract.UserInfoContract.CONTACTS_ID, data.contactsId); // Local Data Id
		values.put(ZeppaContract.UserInfoContract.ZEPPA_RELATIONSHIP_ID, data.relationshipId);
		
		rowId = db.insert(ZeppaContract.UserInfoContract.TABLE_NAME, null, values);		
		data.localId = rowId;
		db.close();

		return data;
	}
	
	public void updateUserData(ZeppaUserData data){
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(ZeppaContract.UserInfoContract.ZEPPA_ID, data.zeppaId);
		values.put(ZeppaContract.UserInfoContract.GOOGLE_ACCOUNT, data.accountName);
		values.put(ZeppaContract.UserInfoContract.ZEPPA_RELATIONSHIP_ID, data.dataRelationship.ordinal());
		values.put(ZeppaContract.UserInfoContract.GIVEN_NAME, data.givenName);
		values.put(ZeppaContract.UserInfoContract.FAMILY_NAME, data.familyName);
		values.put(ZeppaContract.UserInfoContract.IMAGE_URL, data.imageUrl);
		values.put(ZeppaContract.UserInfoContract.PHONE_NUMBER, data.phoneNumber);
		values.put(ZeppaContract.UserInfoContract.CONTACTS_ID, data.contactsId); // Local Data Id
		values.put(ZeppaContract.UserInfoContract.ZEPPA_RELATIONSHIP_ID, data.relationshipId);
		
		String selection = ZeppaContract.UserInfoContract._ID + " = ?";
		String[] args = {String.valueOf(data.localId)};
		
		db.update(ZeppaContract.UserInfoContract.TABLE_NAME, values, selection, args);
		db.close();
		
	}
	

}
