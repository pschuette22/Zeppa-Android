package com.minook.zeppa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.R;

public class SettingsFragment extends Fragment {

	View layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		layout = inflater.inflate(R.layout.fragment_settings, container, false);

		return layout;
	}

}
