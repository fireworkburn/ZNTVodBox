
package com.znt.speaker.db; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.znt.speaker.R;
import com.znt.speaker.entity.Constant;
import com.znt.speaker.util.FileUtils;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;

/** 
 * @ClassName: MyDbHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-18 涓嬪崍4:02:36  
 */
public class MyDbHelper extends SQLiteOpenHelper
{

	protected SQLiteDatabase db = null;
	private Context context = null;
	private File dbFile = null;
	private final String DB_NAME = "wifi_speaker_client.db";
	private final String ROW_ID = "_id";
	protected final String COIN_ORDER_ASC = "music_coin asc";//閲戝竵鍗囧簭鎺掑簭
	protected final String COIN_DESC = "music_coin desc";//閲戝竵闄嶅簭鎺掑簭
	protected final String TIME_ORDER_ASC = "modify_time asc";//鍗囧簭鎺掑簭
	protected final String TIME_ORDER_DESC = "modify_time desc";//闄嶅簭鎺掑簭
	protected final String TBL_SONG_LIST = "song_list";//鐐规挱鍒楄〃
	protected final String TBL_SONG_LIST_ADMIN = "song_list_admin";//鐐规挱鍒楄〃
	protected final String TBL_SONG_RECORD = "song_record_list";//鐐规挱璁板綍鍒楄〃
	protected final String TBL_USER_INFOR = "user_infor";//鐢ㄦ埛淇℃伅琛�
	protected final String TBL_USER_RECORD = "user_record";//鐢ㄦ埛鐧婚檰璁板綍琛�
	protected final String TBL_ADMIN = "admin_list";//绠＄悊鍛樺垪琛�
	protected final String TBL_WIFI = "wifi_list";//杩炴帴鐨剋ifi璁板綍
	protected final String TBL_CUR_PLAN_LIST = "cur_plan_list";
	protected final String TBL_CUR_PLAN_MUSIC = "cur_plan_music";
	private String dbDir = null;
	
	public MyDbHelper(Context context)
	{
		super(context, null, null, 2);
		this.context = context;
		
		dbDir = SystemUtils.getAvailableDir(context, Constant.WORK_DIR + "/db/").getAbsolutePath();
		
		dbFile = new File(dbDir + "/" + DB_NAME);
		
		db = getWritableDatabase();
		//createDb(dbFile);
		openDatabase();
	}
	
	public void deleteDbFile()
	{
		if(dbFile != null && dbFile.exists())
		{
			close();
			/*final File to = new File(dbFile.getAbsolutePath() + System.currentTimeMillis());
			dbFile.renameTo(to);
			to.delete();*/
			dbFile.delete();
			openDatabase();
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		
	}

	/**
	*callbacks
	*/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
		/*String sql = "drop table " + TBL_NAME;
        db.execSQL(sql);
        onCreate(db);*/
	}

	/**
	* @Description: 鎵撳紑鏁版嵁搴擄紝濡傛灉娌℃湁灏卞垱寤�
	* @param @param file   
	* @return void 
	* @throws
	 */
	public int createDb(File file)
	{
		if(file == null)
			return 1;
		if(!file.exists())
		{
			int result = FileUtils.createFile(dbFile.getAbsolutePath());
			if(result != 0)
			{
				db = getWritableDatabase();
				return result;
			}
		}
		
		db = SQLiteDatabase.openOrCreateDatabase(file, null);
		
		if(db == null)
			return 1;
		return 0;
	}
	
	public int openDatabase() 
	{
        try 
        {
            File dir = new File(dbDir);
            if (!dir.exists())
                dir.mkdirs();
            if (!dbFile.exists()) 
            {
                InputStream is = context.getResources().openRawResource(R.raw.wifi_speaker_client);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 寮�濮嬪鍒禿ictionary.db鏂囦欢
                while ((count = is.read(buffer)) > 0) 
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
           
            // 鎵撳紑鐩綍涓殑.db鏂囦欢
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            if(db != null)
            {
            	return 0;
            }
        } 
        catch (Exception e) 
        {
        	LogFactory.createLog().e("db init error-->"+e.getMessage());
        }
        return 1;
    }
	
	protected void checkDbStatus()
	{
		if(db == null)
			db = getWritableDatabase();
	}
	
	/**
	* @Description: 鍒涘缓琛ㄦ牸
	* @param @param tblName   
	* @return void 
	* @throws
	 */
	public int cretaeTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String CREATE_TBL = " create table "  
	            + tblName + " (" + ROW_ID + " integer primary key autoincrement, name text, url text, size long) "; 
		try
		{
			db.execSQL(CREATE_TBL);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			return 1;
		}
		return 0;
	}
	
	/**
	*callbacks
	*/
	@Override
	public void onOpen(SQLiteDatabase db)
	{
		// TODO Auto-generated method stub
		super.onOpen(db);
	}
	
	/**
	* @Description: 鎻掑叆涓�鏉℃暟鎹�
	* @param @param values
	* @param @param tblName
	* @param @return   
	* @return long 
	* @throws
	 */
	public long insert(ContentValues values, String tblName)
	{
		checkDbStatus();
		return db.insert(tblName, null, values);
	}
	
	/**
	* @Description: 鏌ヨ
	* @param @param tblName
	* @param @return   
	* @return Cursor 
	* @throws
	 */
	public Cursor query(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		/*if(!tblName.equals(TBL_SONG_LIST))//涓嶆槸鐐规挱鍒楄〃鎸夌収鏃堕棿鎺掑簭
			order = TIME_ORDER_DESC;*/
		if(tblName.equals(TBL_SONG_RECORD))
			order = TIME_ORDER_ASC;//鏃堕棿鍗囧簭鎺掑垪
		else if(tblName.equals(TBL_ADMIN))
			order = "";//涓嶆帓搴�
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryAsc(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = COIN_DESC;
		
		if(!tblName.equals(TBL_SONG_LIST))//涓嶆槸鐐规挱鍒楄〃鎸夌収鏃堕棿鎺掑簭
			order = TIME_ORDER_ASC;
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	public Cursor queryNormal(String tblName)
	{
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		String order = TIME_ORDER_DESC;
		
		checkDbStatus();
		Cursor cursor = null;
		try
		{
			String[] s = {};
			cursor = db.query(tblName, s, "", s, "", "", order);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			close();
			db = getWritableDatabase();
			cursor = db.query(tblName, null, null, null, null, null, order);
		}
		return cursor;
	}
	
	public void edit(String tblName, int id, String key, String newValue)
	{
		if(TextUtils.isEmpty(newValue) || TextUtils.isEmpty(key))
			return ;
		ContentValues values = new ContentValues();
		values.put(key, newValue);
		try
		{
			checkDbStatus();
			db.update(tblName, values, ROW_ID + "=" + Integer.toString(id), null);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	/**
	 *  鏇存柊澶氫釜瀛楁
	 * @param tblName
	 * @param id
	 * @param values
	 * @return
	 */
	protected int editWithTime(String tblName, String id, ContentValues values)
	{
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
			LogFactory.createLog().e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, String id, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
			LogFactory.createLog().e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, String id, String state, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "user_id=? AND play_state=?", new String[]{id, state});
		} 
		catch (Exception e)
		{
			LogFactory.createLog().e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	protected int editWifi(String tblName, String wifiName, ContentValues values)
	{
		try
		{
			checkDbStatus();
			return db.update(tblName, values, "wifi_name=?", new String[]{wifiName});
		} 
		catch (Exception e)
		{
			LogFactory.createLog().e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	
	/**
	* @Description: 鍒犻櫎涓�鏉℃暟鎹�
	* @param @param id
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int delete(int id, String tblName) 
	{  
		checkDbStatus();
        return db.delete(tblName, ROW_ID + "=?", new String[] { String.valueOf(id) });  
    }  
	protected int delete(String key, String value, String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, key + "=?", new String[] { value }); 
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	protected int deleteAll(String tblName) 
	{  
		try {
			checkDbStatus();
			return db.delete(tblName, null, null);  
		} catch (Exception e) {
			// TODO: handle exception
		}
		return -1;
	}  
	
	/**
	* @Description: 鍒犻櫎鏁版嵁琛�
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int deleteTbl(String tblName)
	{
		if(TextUtils.isEmpty(tblName))
			return 1;
		String sql = "drop table " + tblName;
		checkDbStatus();
        db.execSQL(sql);
        
        return 0;
	}
	
    public void close() 
    {  
        if (db != null)  
        {
        	db.close();  
        	db = null;
        }
    }  
}
 
