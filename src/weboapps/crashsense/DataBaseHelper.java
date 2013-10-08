package weboapps.crashsense;

import java.io.ObjectInputStream.GetField;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG=GetField.class.getName();
	
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
  

    private static final String DATABASE_UPGRADE="DROP TABLE IF EXISTS "+DATABASE_TABLE;
    
	public DataBaseHelper(Context context) {
		super(context,DATABASE_NAME,null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.i(TAG,"database created");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DATABASE_UPGRADE);
		onCreate(db);
		Log.i(TAG,"databasr upgraded");
		
	}

}
