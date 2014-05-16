package com.minook.zeppa.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minook.zeppa.R;
import com.minook.zeppa.activities.MainActivity;

public class InviteFragment extends Fragment {

	private View layout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		((MainActivity) getActivity()).setCurrentFragment(this);
		layout = inflater.inflate(R.layout.fragment_feedback, container, false);

		return layout;
	}

}