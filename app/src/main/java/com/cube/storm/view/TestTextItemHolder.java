package com.cube.storm.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cube.storm.ui.model.list.TextListItem;
import com.cube.storm.ui.view.holder.Holder;

/**
 * // TODO: Add class description
 *
 * @author Callum Taylor
 * @project Storm
 */
public class TestTextItemHolder extends Holder<TextListItem>
{
	@Override public View createView(ViewGroup parent)
	{
		TextView view = new TextView(parent.getContext());
		view.setBackgroundColor(0xffabcdef);
		view.setText("test 123");

		return view;
	}

	@Override public void populateView(TextListItem model)
	{

	}
}
