package com.github.stkent.bugshaker.utilities;

import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	private static final String EMAIL_ADDRESS = "emailAddress";
	private static final String EMAIL_ADDRESS2 = "emailAddress2";

	private static final String KEY_FOR_EMAIL_ADDRESS = "keyForEmailAddress";
	private static final String KEY_FOR_EMAIL_ADDRESS2 = "keyForEmailAddress2";

	public static void save(Context context, String text) {
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = context.getSharedPreferences(EMAIL_ADDRESS, Context.MODE_PRIVATE);
		editor = settings.edit();

		editor.putString(KEY_FOR_EMAIL_ADDRESS, text);
		editor.commit();
	}

	public static void save(Context context, Set<String> values) {
		SharedPreferences settings;
		SharedPreferences.Editor editor;
		settings = context.getSharedPreferences(EMAIL_ADDRESS2, Context.MODE_PRIVATE);
		editor = settings.edit();

		editor.putStringSet(KEY_FOR_EMAIL_ADDRESS2, values);
		editor.commit();
	}

	public static Set<String> getValueStringSet(Context context) {
		SharedPreferences settings;

		settings = context.getSharedPreferences(EMAIL_ADDRESS2, Context.MODE_PRIVATE);
		Set<String> text = (Set<String>) settings.getStringSet(EMAIL_ADDRESS2, null);
		return text;
	}

	public static String getValueString(Context context) {
		SharedPreferences settings;
		String text;
		settings = context.getSharedPreferences(EMAIL_ADDRESS, Context.MODE_PRIVATE);
		text = settings.getString(EMAIL_ADDRESS, null);
		return text;
	}
}
