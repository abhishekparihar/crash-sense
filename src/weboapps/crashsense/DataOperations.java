package weboapps.crashsense;

import android.database.sqlite.SQLiteDatabase;

public class DataOperations {
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns={DataBaseHelper.KEY_ROWID,DataBaseHelper.KEY_ERROR_CONTROLLER,DataBaseHelper.KEY_ERROR,DataBaseHelper.KEY_ERROR_INFO};

}
