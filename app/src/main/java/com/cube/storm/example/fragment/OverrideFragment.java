package com.cube.storm.example.fragment;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Overrides a storm fragment, page id 56
 *
 * @author Callum Taylor
 * @project StormExample
 */
public class OverrideFragment extends Fragment
{
	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().setTitle("Test override page");
	}
}
