package com.designwall.moosell.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;

public class TestDatabaseHelper extends OrmLiteSqliteOpenHelper {

	/**
	 * The name of the database for our application.
	 */
	private static final String DATABASE_NAME = "test.db";
	/**
	 * The version number of the database. This allows us to do conversions from various older versions to newer.
	 */
	private static final int DATABASE_VERSION = 1;

	public TestDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource cs) {
		Log.i(TestDatabaseHelper.class.getName(), "onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource cs, int oldVersion, int newVersion) {
		Log.i(TestDatabaseHelper.class.getName(), "onUpgrade from version " + oldVersion + " to version " + newVersion);
		onCreate(database, cs);
	}

	@Override
	public void close() {
		super.close();
	}
}