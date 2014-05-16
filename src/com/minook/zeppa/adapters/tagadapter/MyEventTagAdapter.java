package com.minook.zeppa.adapters.tagadapter;

import android.support.v4.app.FragmentActivity;
import android.widget.LinearLayout;

import com.minook.zeppa.singleton.EventTagSingleton;
import com.minook.zeppa.zeppaeventendpoint.model.ZeppaEvent;

public class MyEventTagAdapter extends MyTagAdapter{

	public MyEventTagAdapter(FragmentActivity activity, LinearLayout tagHolder, ZeppaEvent event) {
		super(activity, tagHolder);
		
		if(EventTagSingleton.getInstance().hasLoadedTags())
			tags = EventTagSingleton.getInstance().getTagsFrom(event.getTagIds());
	}
	

}
