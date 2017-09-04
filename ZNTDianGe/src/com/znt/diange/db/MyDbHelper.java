
package com.znt.diange.db; 

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.znt.diange.R;
import com.znt.diange.dlna.mediaserver.util.LogFactory;
import com.znt.diange.entity.Constant;
import com.znt.diange.utils.FileUtils;
import com.znt.diange.utils.MyLog;
import com.znt.diange.utils.SystemUtils;

/** 
 * @ClassName: MyDbHelper 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-18 下午4:02:36  
 */
public class MyDbHelper extends SQLiteOpenHelper
{

	protected SQLiteDatabase db = null;
	
	private File dbFile = null;
	private final String DB_NAME = "wifispeaker_medias.db";
	private final String ROW_ID = "_id";
	protected final String ORDER_ASC = "modify_time asc";//升序排序
	protected final String ORDER_DESC = "modify_time desc";//降序排序
	protected final String TBL_MUSIC = "tbl_play_list";//音乐表
	protected final String TBL_DEVICE = "tbl_device_list";//设备表
	protected final String TBL_SONG_LIST = "song_list";//点播列表
	protected final String TBL_SEARCH_RECORD = "search_record";//搜索记录
	private String dbDir = null;
	
	public MyDbHelper(Context c)
	{
		super(c, null, null, 2);
		
		dbDir = SystemUtils.getAvailableDir(c, Constant.WORK_DIR + "/db").getAbsolutePath();
		
		dbFile = new File(dbDir + "/" + DB_NAME);
		
		db = getWritableDatabase();
		
		//createDb(dbFile);
		openDatabase(c);
	}
	
	public void deleteDbFile()
	{
		if(dbFile != null && dbFile.exists())
		{
			close();
			dbFile.delete();
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
	* @Description: 打开数据库，如果没有就创建
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
	
	public int openDatabase(Context context) 
	{
        try 
        {
            // 获得dictionary.db文件的绝对路径
            File dir = new File(dbDir);
            // 如果/sdcard/dictionary目录中存在，创建这个目录
            if (!dir.exists())
                dir.mkdirs();
            // 如果在/sdcard/dictionary目录中不存在
            // dictionary.db文件，则从res\raw目录中复制这个文件到
            // SD卡的目录（/sdcard/dictionary）
            if (dbFile != null && !dbFile.exists()) 
            {
                // 获得封装dictionary.db文件的InputStream对象
                InputStream is = context.getResources().openRawResource(R.raw.wifispeaker_medias);
                dbFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                // 开始复制dictionary.db文件
                while ((count = is.read(buffer)) > 0) 
                {
                    fos.write(buffer, 0, count);
                }

                fos.close();
                is.close();
            }
           
            // 打开/sdcard/dictionary目录中的dictionary.db文件
            db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            if(db != null)
            	return 0;
        } 
        catch (Exception e) 
        {
        	LogFactory.createLog().e(e.getMessage() + "");
        }
        return 1;
    }
	
	/**
	* @Description: 创建表格
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
			if (db == null)  
	            db = getWritableDatabase();  
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
	* @Description: 插入一条数据
	* @param @param values
	* @param @param tblName
	* @param @return   
	* @return long 
	* @throws
	 */
	public long insert(ContentValues values, String tblName)
	{
		if (db == null)  
            db = getWritableDatabase();
		return db.insert(tblName, null, values);
	}
	
	/**
	* @Description: 查询
	* @param @param tblName
	* @param @return   
	* @return Cursor 
	* @throws
	 */
	public Cursor query(String tblName)
	{
		
		//Cursor cur = db.rawQuery("SELECT * FROM " + tblName, null);
		
		if (db == null || !db.isOpen())  
            db = getWritableDatabase();
		Cursor cursor = db.query(tblName, null, null, null, null, null, ORDER_DESC);
		return cursor;
	}
	
	public void edit(String tblName, int id, String key, String newValue)
	{
		if(TextUtils.isEmpty(newValue) || TextUtils.isEmpty(key))
			return ;
		if (db == null)  
            db = getWritableDatabase();  
		ContentValues values = new ContentValues();
		values.put(key, newValue);
		try
		{
			db.update(tblName, values, ROW_ID + "=" + Integer.toString(id), null);
		} 
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}
	
	/**
	 *  更新多个字段
	 * @param tblName
	 * @param id
	 * @param values
	 * @return
	 */
	protected int edit(String tblName, String id, ContentValues values)
	{
		if (db == null)  
            db = getWritableDatabase();  
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			return db.update(tblName, values, "music_id=?", new String[]{id});
		} 
		catch (Exception e)
		{
			MyLog.e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	protected int edit(String tblName, String key, String id, ContentValues values)
	{
		if (db == null)  
			db = getWritableDatabase();  
		values.put("modify_time", System.currentTimeMillis());
		try
		{
			return db.update(tblName, values, key + "=?", new String[]{id});
		} 
		catch (Exception e)
		{
			MyLog.e("edit-->"+e.getMessage());
			// TODO: handle exception
		}
		return -1;
	}
	
	/**
	* @Description: 删除一条数据
	* @param @param id
	* @param @param tblName
	* @param @return   
	* @return int 
	* @throws
	 */
	public int delete(int id, String tblName) 
	{  
        if (db == null)  
            db = getWritableDatabase();  
        return db.delete(tblName, ROW_ID + "=?", new String[] { String.valueOf(id) });  
    }  
	protected int delete(String key, String value, String tblName) 
	{  
		if (db == null)  
			db = getWritableDatabase();  
		return db.delete(tblName, key + "=?", new String[] { value });  
	}  
	
	/**
	* @Description: 删除数据表
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
		if (db == null)  
            db = getWritableDatabase();  
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
 
