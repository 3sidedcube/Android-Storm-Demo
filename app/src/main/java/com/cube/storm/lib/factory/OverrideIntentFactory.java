package com.cube.storm.lib.factory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cube.storm.example.fragment.OverrideFragment;
import com.cube.storm.ui.activity.StormActivity;
import com.cube.storm.ui.data.FragmentIntent;
import com.cube.storm.ui.lib.factory.IntentFactory;
import com.cube.storm.ui.model.descriptor.PageDescriptor;

import lombok.NonNull;

/**
 * Example intent factory for overriding fragment
 *
 * @author Callum Taylor
 * @project StormExample
 */
public class OverrideIntentFactory extends IntentFactory
{
	@Nullable @Override public FragmentIntent getFragmentIntentForPageDescriptor(@NonNull PageDescriptor pageDescriptor)
	{
		FragmentIntent ret = super.getFragmentIntentForPageDescriptor(pageDescriptor);

		if ("cache://pages/56.json".equalsIgnoreCase(pageDescriptor.getSrc()))
		{
			if (ret != null)
			{
				ret.setFragment(OverrideFragment.class);
			}
			else
			{
				Bundle args = new Bundle();
				args.putSerializable(StormActivity.EXTRA_URI, pageDescriptor.getSrc());

				ret = new FragmentIntent(OverrideFragment.class, null, args);
			}
		}

		return ret;
	}

	@Nullable @Override public Intent getIntentForPageDescriptor(@NonNull Context context, @NonNull PageDescriptor pageDescriptor)
	{
		return super.getIntentForPageDescriptor(context, pageDescriptor);
	}
}
