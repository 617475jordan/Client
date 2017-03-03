package com.fg.androidtext.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	// ���ݿ���
	private static final String databaseHelper = "FirstGroup.db";
	// �汾��
	private static final int version = 1;
	// ����
	private static final String tablename = "mylogin";

	public MyDatabaseHelper(Context context) {
		super(context, databaseHelper, null, version);
	}

	// �������ݱ�
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE  " + tablename + " ( "
				+ "id           INTEGER         PRIMARY KEY ,"
				+ "phonenum     VERCHAR(50)      NOT  NULL  ,"
				+ "password     VERCHAR(50)      NOT  NULL   " + ")";
		db.execSQL(sql);
	}

	// �������ݿ�İ汾����
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + tablename;
		db.execSQL(sql);
		this.onCreate(db);
	}

}
