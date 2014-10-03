package com.minook.zeppa.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.minook.zeppa.contentprovider.ZeppaDataHelper;

public class SyncContactDatabaseTask extends AsyncTask<Void, Void, Void>{

	private Context context;
	// Query Params
	private Uri contactsContractUri = ContactsContract.Contacts.CONTENT_URI;
	private String[] contactsProjection = {
			Contacts._ID,
			Contacts.LOOKUP_KEY
	};
	private String selectionOrder = "_id ASC";
	
	
	public SyncContactDatabaseTask(Context context){
		this.context = context;
				
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		ContentResolver resolver = context.getContentResolver();
		ZeppaDataHelper helper = new ZeppaDataHelper(context, null, null, 0);
		
		Cursor contactsCursor = resolver.query(contactsContractUri, contactsProjection, null,null, selectionOrder);
		Cursor zeppaLocalDataCursor = helper.getContactsSyncCursor();
		
		boolean moreContacts = contactsCursor.moveToFirst();
		boolean moreZeppaUserInfo = zeppaLocalDataCursor.moveToFirst();
		
		while(moreContacts && moreZeppaUserInfo){
			moreContacts = contactsCursor.moveToNext();
			moreZeppaUserInfo = zeppaLocalDataCursor.moveToNext();
		}
		
		
		return null;
	}

}
