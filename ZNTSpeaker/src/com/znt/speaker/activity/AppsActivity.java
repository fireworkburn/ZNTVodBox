
package com.znt.speaker.activity; 

import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.znt.speaker.R;

/** 
 * @ClassName: AppsActivity 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-12-8 下午4:26:31  
 */
public class AppsActivity extends BaseActivity implements OnItemClickListener
{

	private GridView mGrid = null;
	private Button btnBack = null;
	private List<ResolveInfo> mApps = null;
	
	/**
	*callbacks
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_apps);
        
        loadApps();
        
        mGrid = (GridView) findViewById(R.id.apps_list);
        btnBack = (Button) findViewById(R.id.btn_music_player_back);
        mGrid.setAdapter(new AppsAdapter());

        mGrid.setOnItemClickListener(this);
        
        btnBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private void loadApps() 
    {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
 
        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
        
    }
    
    public class AppsAdapter extends BaseAdapter 
    {
    	PackageManager packageManager = null;
        public AppsAdapter() 
        {
        	packageManager = getActivity().getPackageManager();  
        }
 
        public View getView(int position, View convertView, ViewGroup parent) 
        {
        	ViewHolder vh = null;
 
            if (convertView == null)
            {
            	vh = new ViewHolder();
            	convertView = LayoutInflater.from(getActivity()).inflate(R.layout.view_app_item, null);
            	vh.ivIcon = (ImageView)convertView.findViewById(R.id.iv_app_item_logo);
            	vh.tvName = (TextView)convertView.findViewById(R.id.tv_app_item_name);
            	convertView.setTag(vh);
            } 
            else 
                vh = (ViewHolder)convertView.getTag();
 
            ResolveInfo info = mApps.get(position);
            vh.ivIcon.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
            vh.tvName.setText(info.loadLabel(packageManager).toString());  
            return convertView;
        }
        class ViewHolder
        {
        	ImageView ivIcon = null;
        	TextView tvName = null;
        }
 
        public final int getCount()
        {
            return mApps.size();
        }
 
        public final Object getItem(int position)
        {
            return mApps.get(position);
        }
 
        public final long getItemId(int position) 
        {
            return position;
        }
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) 
    {
        ResolveInfo info = mApps.get(position);
         
        String pkg = info.activityInfo.packageName;
        String cls = info.activityInfo.name;
         
        ComponentName componet = new ComponentName(pkg, cls);
         
        Intent i = new Intent();
        i.setComponent(componet);
        startActivity(i);
    }
}
 
