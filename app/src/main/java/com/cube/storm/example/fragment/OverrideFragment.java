package com.cube.storm.example.fragment;

import android.os.Bundle;

import com.cube.storm.ui.fragment.StormListFragment;

/**
 * Overrides a storm fragment, page id 56
 *
 * @author Callum Taylor
 * @project StormExample
 */
public class OverrideFragment extends StormListFragment
{
	@Override public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().setTitle("Test override page");
	}
}
