package com.minook.zeppa.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.minook.zeppa.Constants;

public class ContactDBHelper extends SQLiteOpenHelper {

	private enum ContactStatus {
		Connected,		// Currently Friends
		NotConnected,	// Currently using, not friends
		
		Invited,		// Did send invite
		
		Blocked,		// User was blocked
		DidBlock,		// Did block this user
		
		Unknown 		// Other
		
	}
	
	// Contacts DB Vaiables
	private static final String CONTACTS_TABLE = "Phone_Contacts_Table";
	
	private static final String ENTRY_ID 		= BaseColumns._ID;
	private static final String CONTACT_ID 		= "PhoneContactReference";
	private static final String STATUS			= "SyncStatus";
	private static final String APPENGINE_ID	= "AppEngineId"; // null if user isnt on Zeppa 
	
	
	
	public ContactDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, Constants.DB_NAME, null, Constants.DB_VERSION);


	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	
	
}
