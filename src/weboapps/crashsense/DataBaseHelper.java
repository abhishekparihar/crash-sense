package weboapps.crashsense;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String KEY_ROWID="_id";
	public static final String KEY_ERROR_CONTROLLER="error_controller";
	public static final String KEY_ERROR="error";
	public static final String KEY_ERROR_INFO="error_info";
	
	private static final String DATABASE_NAME="crashsensor";
	private static final String DATABASE_TABLE="errorreport";
	private static final int DATABASE_VERSION=1;
	
    private static final String DATABASE_CREATE = "create table "
  	      + DATABASE_TABLE + "(" 
  	      + KEY_ROWID      + " integer primary key autoincrement, " 
  	      + KEY_ERROR_CONTROLLER      + " text, " 
  	      +KEY_ERROR		+" text, "
  	      +KEY_ERROR_INFO		+" text "
  	      +");";
  

	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
