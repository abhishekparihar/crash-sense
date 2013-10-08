package weboapps.crashsense;

import java.io.ObjectInputStream.GetField;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataOperations {

	private static final String TAG=GetField.class.getName();
	
	private SQLiteDatabase database;
	private DataBaseHelper dbHelper;
	private String[] allColumns={DataBaseHelper.KEY_ROWID,DataBaseHelper.KEY_ERROR_CONTROLLER,DataBaseHelper.KEY_ERROR,DataBaseHelper.KEY_ERROR_INFO};
	
	public DataOperations(Context context,String [] errorRecord)
	{
		dbHelper=new DataBaseHelper(context);
		open();
		insertIntoTable(errorRecord);
	}

	private void open() {
		database=dbHelper.getWritableDatabase();
	}
	public void close(){
		dbHelper.close();
	}

	private void insertIntoTable(String[] errorRecord) {
		ContentValues values= new ContentValues();
		values.put(DataBaseHelper.KEY_ERROR_CONTROLLER,errorRecord[0]);
		values.put(DataBaseHelper.KEY_ERROR,errorRecord[1]);
		values.put(DataBaseHelper.KEY_ERROR_INFO,errorRecord[2]);
		long insertId=database.insert(DataBaseHelper.DATABASE_TABLE, null, values);
		Log.i(TAG,"data inserted");	
	}
	public void deleteFromTable(long id)
	{
		database.delete(DataBaseHelper.DATABASE_TABLE,DataBaseHelper.KEY_ROWID+" = "+id,null);
	}

}
