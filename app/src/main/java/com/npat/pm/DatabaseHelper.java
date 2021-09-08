package com.npat.pm;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	String DB_PATH = null;
	private static String DB_NAME = "pmdatabase1.db";

	// TABLE_CONTENT

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 *
	 * @param context
	 */
	@SuppressLint("SdCardPath")
	public DatabaseHelper(Context context) {

		super(context, DB_NAME, null, PublicVariable.DATABASE_VERSION);//1 is database  version
		this.myContext = context;
		// DB_PATH = "/data/data/" + context.getPackageName() + "/" +
		// "databases/";
		DB_PATH = "/data/data/" + myContext.getPackageName() + "/" + "databases/";
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS
							| SQLiteDatabase.CREATE_IF_NECESSARY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	public void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		// SQLiteDatabase.NO_LOCALIZED_COLLATORS
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY
						| SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@SuppressLint("Range")
	@Override
	public void onCreate(SQLiteDatabase db) {
			//***************************android_metadata*********************
			if (!doesTableExist(db, "dbVersionPM")) {
				//db.execSQL("DROP TABLE IF EXISTS \"main\".\"android_metadata\"");}

				db.execSQL("CREATE TABLE dbVersionPM (\"version\" TEXT)");
				db.execSQL("INSERT INTO \"main\".\"dbVersionPM\" VALUES ('1')");
				PublicVariable.DATABASE_VERSION = 1;
			} else {
				Cursor cursors = db.rawQuery("select * from dbVersionPM", null);
				if (cursors.getCount() > 0) {
					cursors.moveToNext();
					String strVersion = cursors.getString(cursors.getColumnIndex("version"));
					PublicVariable.DATABASE_VERSION = Integer.parseInt(strVersion);
				} else {
					PublicVariable.DATABASE_VERSION = 1;
				}
				if (cursors != null) {
					cursors.close();
				}
			}
			if (PublicVariable.DATABASE_VERSION > 4 || PublicVariable.DATABASE_VERSION==1) {

				//***************************android_metadata*********************
				if (!doesTableExist(db, "android_metadata")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"android_metadata\"");}
					db.execSQL("CREATE TABLE android_metadata (\"locale\" TEXT)");
					db.execSQL("INSERT INTO \"main\".\"android_metadata\" VALUES ('en_US')");
				}

				//***************************Hamkaran*******************************
				if (!doesTableExist(db, "Hamkaran")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"Hamkaran\"");
					db.execSQL("CREATE TABLE \"Hamkaran\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"Name\"  TEXT\n" +
							");");
				}
				//***************************Login******************************************
				if (!doesTableExist(db, "login")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"login\";");
					db.execSQL("CREATE TABLE \"login\" (\"Id\"  integer PRIMARY KEY AUTOINCREMENT,\"Usercode\"  text,\"Personcode\"  text,\"Mobile\"  TEXT DEFAULT 0,\"Status\"  TEXT DEFAULT 0);");
				}
				//****************************Links*******************************************
				if (!doesTableExist(db, "Links")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"Links\";");
					db.execSQL("CREATE TABLE \"Links\" (\n" +
							"\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
							"\"Title\"  TEXT NOT NULL,\n" +
							"\"Url\"  TEXT NOT NULL,\n" +
							"\"Def\"  INTEGER\n" +
							");");
					db.execSQL("INSERT INTO \"main\".\"Links\" VALUES (1, 'اینترنت', '2.180.16.123:8074', 1)");
				}
				//****************************AcceptWork*******************************************
				if (!doesTableExist(db, "AcceptWork")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"AcceptWork\"");
					db.execSQL("CREATE TABLE \"AcceptWork\" (\n" +
							"\"Code\"  INTEGER NOT NULL,\n" +
							"\"WorkType\"  TEXT,\n" +
							"\"Subject\"  TEXT,\n" +
							"\"Location\"  TEXT,\n" +
							"\"Rade\"  TEXT,\n" +
							"\"Description\"  TEXT,\n" +
							"\"RequestType\"  TEXT,\n" +
							"\"InsertUser\"  TEXT,\n" +
							"\"InsertDate\"  TEXT,\n" +
							"\"Pic\"  TEXT\n" +
							");");
				}
				//****************************Location*******************************************
				if (!doesTableExist(db, "Location")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"Location\"");
					db.execSQL("CREATE TABLE \"Location\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"Title\"  TEXT\n" +
							");");
				}
				//****************************MyWork*******************************************
				if (!doesTableExist(db, "MyWork")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"MyWork\"");
					db.execSQL("CREATE TABLE \"MyWork\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"WorkType\"  TEXT,\n" +
							"\"Subject\"  TEXT,\n" +
							"\"Location\"  TEXT,\n" +
							"\"Rade\"  TEXT,\n" +
							"\"Description\"  TEXT,\n" +
							"\"Status\"  TEXT,\n" +
							"\"Status2\"  TEXT,\n" +
							"\"RequestType\"  TEXT,\n" +
							"\"InsertUser\"  TEXT,\n" +
							"\"InsertDate\"  TEXT,\n" +
							"\"StatusDesc\"  TEXT,\n" +
							"\"Pic\"  TEXT\n" +
							")");
				}
				else
				{
					if(!existsColumnInTable(db,"MyWork","StatusDesc")) {
						db.execSQL( "alter table MyWork add column StatusDesc" ) ;
					}
				}
				//****************************MyWorkStatusReport*******************************************
				if (!doesTableExist(db, "MyWorkStatusReport")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"MyWorkStatusReport\"");
					db.execSQL("CREATE TABLE \"MyWorkStatusReport\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"WorkType\"  TEXT,\n" +
							"\"Subject\"  TEXT,\n" +
							"\"Location\"  TEXT,\n" +
							"\"Rade\"  TEXT,\n" +
							"\"Status\"  TEXT,\n" +
							"\"Description\"  TEXT,\n" +
							"\"InsertUser\"  TEXT,\n" +
							"\"StatusDesc\"  TEXT,\n" +
							"\"Pic\"  TEXT\n" +
							")");
				}
				else
				{
					if(!existsColumnInTable(db,"MyWorkStatusReport","StatusDesc")) {
						db.execSQL( "alter table MyWorkStatusReport add column StatusDesc" ) ;
					}
				}
				//****************************OtherWorkStatusReport*******************************************
				if (!doesTableExist(db, "OtherWorkStatusReport")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"OtherWorkStatusReport\"");
					db.execSQL("CREATE TABLE \"OtherWorkStatusReport\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"WorkType\"  TEXT,\n" +
							"\"Subject\"  TEXT,\n" +
							"\"Location\"  TEXT,\n" +
							"\"Rade\"  TEXT,\n" +
							"\"Status\"  TEXT,\n" +
							"\"Description\"  TEXT,\n" +
							"\"InsertUser\"  TEXT,\n" +
							"\"InsertUser2\"  TEXT,\n" +
							"\"InsertDate\"  TEXT,\n" +
							"\"Pic\"  TEXT\n" +
							")");
				}
				//****************************Rade*******************************************
				if (!doesTableExist(db, "Rade")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"Rade\"");
					db.execSQL("CREATE TABLE \"Rade\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"Title\"  TEXT\n" +
							")");
				}
				//****************************Request*******************************************
				if (!doesTableExist(db, "Request")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"Request\"");
					db.execSQL("CREATE TABLE \"Request\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"WorkType\"  TEXT,\n" +
							"\"Subject\"  TEXT,\n" +
							"\"Location\"  TEXT,\n" +
							"\"Rade\"  TEXT,\n" +
							"\"Description\"  TEXT,\n" +
							"\"Status\"  TEXT,\n" +
							"\"Status2\"  TEXT,\n" +
							"\"RequestType\"  TEXT,\n" +
							"\"InsertUser\"  TEXT,\n" +
							"\"InsertDate\"  TEXT,\n" +
							"\"Pic\"  TEXT\n" +
							")");
				}
				//****************************RequsetType*******************************************
				if (!doesTableExist(db, "RequsetType")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"RequsetType\"");
					db.execSQL("CREATE TABLE \"RequsetType\" (\n" +
							"\"Code\"  TEXT,\n" +
							"\"Title\"  TEXT\n" +
							")");
					db.execSQL("INSERT INTO \"main\".\"RequsetType\" VALUES (1, 'عادی');");
					db.execSQL("INSERT INTO \"main\".\"RequsetType\" VALUES (2, 'فوری');");
				}
				//****************************settings*******************************************
				if (!doesTableExist(db, "settings")) {
					//db.execSQL("DROP TABLE IF EXISTS \"main\".\"settings\"");
					db.execSQL("CREATE TABLE settings (name text,value text);");
					db.execSQL("INSERT INTO \"main\".\"settings\" VALUES ('FlagPersonalInfo', 0)");
					db.execSQL("INSERT INTO \"main\".\"settings\" VALUES ('FlagSharesInfo', 0)");
					db.execSQL("INSERT INTO \"main\".\"settings\" VALUES ('FlagAboutUs', 0)");
					db.execSQL("INSERT INTO \"main\".\"settings\" VALUES ('AppVersion', 1)");
				}
				//****************************settings*******************************************
				if (!doesTableExist(db, "sqlite_sequence")) {
					db.execSQL("DROP TABLE IF EXISTS \"main\".\"sqlite_sequence\"");
					db.execSQL("CREATE TABLE sqlite_sequence(name,seq);");
					db.execSQL("INSERT INTO \"main\".\"sqlite_sequence\" VALUES ('Links', 1);");
					db.execSQL("INSERT INTO \"main\".\"sqlite_sequence\" VALUES ('login', 3);");
				}
				//*******************************************************************************
				PublicVariable.DATABASE_VERSION = 4;
				db.execSQL("Update dbVersionPM set version = '4'");
				db.setVersion(4);
			}
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		onCreate(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if(newVersion>oldVersion) {
//			try {
//				copyDataBase();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
	public boolean doesTableExist(SQLiteDatabase db, String tableName) {
		try {
			Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.close();
					return true;
				}
				cursor.close();
			}
			return false;
		}
		catch (SQLException sqle)
		{
			return false;
		}
	}
	private boolean existsColumnInTable(SQLiteDatabase inDatabase, String inTable, String columnToCheck) {
		Cursor mCursor = null;
		try {
			// Query 1 row
			mCursor = inDatabase.rawQuery("SELECT * FROM " + inTable + " LIMIT 0", null);

			// getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
			if (mCursor.getColumnIndex(columnToCheck) != -1)
				return true;
			else
				return false;

		} catch (Exception Exp) {
			return false;
		} finally {
			if (mCursor != null) mCursor.close();
		}
	}
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.setVersion(oldVersion);
	}
}
