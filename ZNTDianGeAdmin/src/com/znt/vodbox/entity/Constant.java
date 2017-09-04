
package com.znt.vodbox.entity; 

import com.znt.diange.mina.cmd.DeviceInfor;


/** 
 * @ClassName: Constant 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午6:06:18  
 */
public class Constant
{
	public static final String HTTP_TAG = "http://";
	
	public static final int COMPRESS_SIZE = 300;//质量压缩大小
	public static final int COMPRESS_WIDTH = 720;//压缩宽度
	public static final int COMPRESS_HEIGHT = 1080;//压缩高度
	
	public static final int ONE_PAGE_SIZE = 20;
	public static final int SUC_DELAY_TIME = 3000;
	
	public static final String UUID_TAG = "_znt_ios_rrdg_sp";
	public static final String DNS_TAG = "_znt_ios_rrdg_sp";
	public static final String PKG_END = "znt_pkg_end";
	public static String PHONE_UUID = "";
	public static final String WIFI_HOT_NAME = "rrdg";
	public static String WIFI_HOT_PWD = "00000000";
	public static final int NET_CHANGE_DELAY_TIME = 5000;
	
	public static final int SOCKET_PORT = 6789;
	public static final String SOCKET_IP = "192.168.43.1";
	
	public static final String WORK_DIR = "/ZNTMusic";
	
	public static boolean isSongUpdate = true;
	public static boolean isPlayUpdate = false;
	public static boolean isCheckUpdate = true;
	public static boolean isSongSended = false;
	public static boolean isCoinUpdate = true;

	public static boolean isAlbumMusicUpdated = false;
	public static boolean isPlanUpdated = false;
	public static boolean isShopUpdated = false;
	public static boolean isAlbumUpdated = false;
	
	public static boolean isInnerVersion = false;
	
	public static DeviceInfor deviceInfor = null;
	
	public static final String MEDIA_ADD = "android.intent.action.MEDIA_MOUNTED";
	public static final String MEDIA_REMOVE = "android.intent.action.MEDIA_REMOVED";
	
	//友盟APP key
	public static final String YM_APP_KEY = "5707184667e58e7ab5000a27";
	public static final String WX_APP_ID = "wx136c439fd96e800d";
	public static final String WX_APP_SECRET = "c89edb8360a996026ede5ebd9134a524";
	
	
	public static final String EXTRA_KEY = "key";
    public static final String EXTRA_SECRET = "secret";
    public static final String EXTRA_SAMPLE = "sample";
    public static final String EXTRA_SOUND_START = "sound_start";
    public static final String EXTRA_SOUND_END = "sound_end";
    public static final String EXTRA_SOUND_SUCCESS = "sound_success";
    public static final String EXTRA_SOUND_ERROR = "sound_error";
    public static final String EXTRA_SOUND_CANCEL = "sound_cancel";
    public static final String EXTRA_INFILE = "infile";
    public static final String EXTRA_OUTFILE = "outfile";

    public static final String EXTRA_LANGUAGE = "language";
    public static final String EXTRA_NLU = "nlu";
    public static final String EXTRA_VAD = "vad";
    public static final String EXTRA_PROP = "prop";

    public static final String EXTRA_OFFLINE_ASR_BASE_FILE_PATH = "asr-base-file-path";
    public static final String EXTRA_LICENSE_FILE_PATH = "license-file-path";
    public static final String EXTRA_OFFLINE_LM_RES_FILE_PATH = "lm-res-file-path";
    public static final String EXTRA_OFFLINE_SLOT_DATA = "slot-data";
    public static final String EXTRA_OFFLINE_SLOT_NAME = "name";
    public static final String EXTRA_OFFLINE_SLOT_SONG = "song";
    public static final String EXTRA_OFFLINE_SLOT_ARTIST = "artist";
    public static final String EXTRA_OFFLINE_SLOT_APP = "app";
    public static final String EXTRA_OFFLINE_SLOT_USERCOMMAND = "usercommand";

    public static final int SAMPLE_8K = 8000;
    public static final int SAMPLE_16K = 16000;

    public static final String VAD_SEARCH = "search";
    public static final String VAD_INPUT = "input";
    
    
    
    public static final String APP_KEY_WEIBO = "2616775278";
	
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
}
 
