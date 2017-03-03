package com.fg.androidtext.sqlite;

import android.database.sqlite.SQLiteDatabase;

public class MytabOperate {
	private static final String tablename = "mylogin";
	private SQLiteDatabase db = null;

	public MytabOperate(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * ��������
	 */
	public void insert(String phonenum, String password) {
		String sql = "INSERT INTO " + tablename
				+ "(phonenum,password) VALUES (?,?)";// SQL���
		Object args[] = new Object[] { phonenum, password }; // ���ò���
		this.db.execSQL(sql, args); // ִ��SQL����
		this.db.close(); // �ر����ݿ�����
	}

	/**
	 * �޸�����
	 */
	public void updata(String phonenum, String password) {
		String sql = "UPDATE " + tablename + " SET password=? WHERE phonenum=?";
		Object args[] = new Object[] { phonenum, password };
		this.db.execSQL(sql, args);
		this.db.close();
	}

	/**
	 * ɾ������
	 */
	public void delete(int id) {
		String sql = "DELETE FROM " + tablename + " WHERE id=?";
		Object args[] = new Object[] { id };
		this.db.execSQL(sql, args);
		this.db.close();
	}

}
