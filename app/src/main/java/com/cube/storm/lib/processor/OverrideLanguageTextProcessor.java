package com.cube.storm.lib.processor;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cube.storm.language.lib.processor.LanguageTextProcessor;

/**
 * // TODO: Add class description
 *
 * @author Callum Taylor
 * @project Storm
 */
public class OverrideLanguageTextProcessor extends LanguageTextProcessor
{
	@Nullable @Override public String process(@Nullable String s)
	{
		s = super.process(s);
		return s == null ? "" : TextUtils.getReverse(s, 0, s.length()).toString();
	}
}
