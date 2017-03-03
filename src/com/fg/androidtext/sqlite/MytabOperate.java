package com.fg.androidtext.sqlite;

import android.database.sqlite.SQLiteDatabase;

public class MytabOperate {
	private static final String tablename = "mylogin";
	private SQLiteDatabase db = null;

	public MytabOperate(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * 增加数据
	 */
	public void insert(String phonenum, String password) {
		String sql = "INSERT INTO " + tablename
				+ "(phonenum,password) VALUES (?,?)";// SQL语句
		Object args[] = new Object[] { phonenum, password }; // 设置参数
		this.db.execSQL(sql, args); // 执行SQL数据
		this.db.close(); // 关闭数据库连接
	}

	/**
	 * 修改数据
	 */
	public void updata(String phonenum, String password) {
		String sql = "UPDATE " + tablename + " SET password=? WHERE phonenum=?";
		Object args[] = new Object[] { phonenum, password };
		this.db.execSQL(sql, args);
		this.db.close();
	}

	/**
	 * 删除数据
	 */
	public void delete(int id) {
		String sql = "DELETE FROM " + tablename + " WHERE id=?";
		Object args[] = new Object[] { id };
		this.db.execSQL(sql, args);
		this.db.close();
	}

}
