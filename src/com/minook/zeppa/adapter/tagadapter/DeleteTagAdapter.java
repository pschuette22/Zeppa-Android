package com.minook.zeppa.adapter.tagadapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.minook.zeppa.R;
import com.minook.zeppa.ZeppaApplication;
import com.minook.zeppa.activity.AuthenticatedFragmentActivity;
import com.minook.zeppa.mediator.MyEventTagMediator;
import com.minook.zeppa.runnable.RemoveTagRunnable;
import com.minook.zeppa.runnable.ThreadManager;
import com.minook.zeppa.singleton.EventTagSingleton;

public class DeleteTagAdapter extends MyTagAdapter implements OnClickListener {

	public DeleteTagAdapter(AuthenticatedFragmentActivity activity,
			LinearLayout tagHolder) {
		super(activity, tagHolder, null);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		MyEventTagMediator mediator = getItem(position);
		if(convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.view_tag_delete, parent, false);
		}
		
		TextView text = (TextView) convertView.findViewById(R.id.deletetag_text);
 		text.setText(mediator.getText());
 		convertView.setTag(mediator);
 		convertView.setOnClickListener(this);
 		
		return convertView;
	}

	@Override
	public void onClick(View v) {
		MyEventTagMediator mediator = (MyEventTagMediator) v.getTag();
		
		if(mediator != null){
			deleteTagInAsync(v, mediator);
			drawTags();
		}
		
	}
	

	private void deleteTagInAsync(View v, MyEventTagMediator mediator){
		
		
		EventTagSingleton.getInstance().removeEventTagMediator(mediator);
		ThreadManager.execute(new RemoveTagRunnable((ZeppaApplication) activity.getApplication(), activity.getGoogleAccountCredential(), mediator.getTagId().longValue()));
		notifyDataSetChanged();
		
		
	}
	

}
