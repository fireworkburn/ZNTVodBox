package com.znt.speaker.factory;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.znt.speaker.entity.LocalDataEntity;
import com.znt.speaker.entity.LocationInfor;
import com.znt.speaker.util.LogFactory;
import com.znt.speaker.util.SystemUtils;

public class LocationFactory implements 
AMapLocationListener, OnGeocodeSearchListener, OnPoiSearchListener
{

	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private GeocodeSearch geocoderSearch;
	
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	
	private boolean isFirst = true;
	private boolean isStop = false;
	private boolean isNearBy = true;
	
	private List<PoiItem> poiNear = new ArrayList<PoiItem>();
	private List<PoiItem> poiList = new ArrayList<PoiItem>();
	
	private LocationInfor locationInfor = null;
	
	private String poiProvince = null;
	private String poiCity = null;
	private String poiStrict = null;
	
	private int retryTime = 0;
	
	private Activity activity = null;
	
	private HttpFactory httpFactory = null;
	
	public LocationFactory(Activity activity)
	{
		this.activity = activity;
		locationInfor = new LocationInfor();
		
		httpFactory = new HttpFactory(activity);
	}
	
	public void startLocation()
	{
		
		if(SystemUtils.isNetConnected(activity))
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					showToast("开始定位");
					stopLocation();
					initMapLoc();
				}
			}).start();
		}
		else
			showToast("网络异常");
	}
	
	public void initMapLoc()
	{
		isStop = false;
		isFirst = true;
		aMapLocManager = LocationManagerProxy.getInstance(activity);
		aMapLocManager.setGpsEnable(true);
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				if (aMapLocation == null)
				{
					if(!isStop)
					{
						retryTime ++;
						if(retryTime > 10)
						{
							isStop = true;
							retryTime = 0;
							showToast("定位失败");
						}
						else
							startLocation();// 重新开始定位
						//showToast("位置获取失败");
					}
				}
			}
		}, 12000);// 设置超过12秒还没有定位到就停止定位
	}
	
	private void startSearch(final LatLonPoint latLonPoint)
	{
		geocoderSearch = new GeocodeSearch(activity);
		geocoderSearch.setOnGeocodeSearchListener(this);
		RegeocodeQuery regecodeQuery = new RegeocodeQuery(latLonPoint, 10000,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(regecodeQuery);// 设置同步逆地理编码请求
	}
	
	/**
	 * 销毁定位
	 */
	public void stopLocation()
	{
		if (aMapLocManager != null) 
		{
			isStop = true;
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0)
		{
			if (result != null && result.getQuery() != null)
			{
				// 搜索poi的结果
				if (result.getQuery().equals(query)) 
				{
					// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> tempList = poiResult.getPois();
					if(tempList != null)
					{
						poiList.addAll(tempList);// 取得第一页的poiitem数据，页数从数字0开始
						/*List<SuggestionCity> suggestionCities = poiResult
								.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
						*/
						showToast("位置获取成功-->"+poiList.get(0).getTitle());
						LogFactory.createLog().e("位置获取成功-->"+poiList.get(0).getTitle());
						LocalDataEntity.newInstance(activity)
						.setDeviceAddr(poiList.get(0).getTitle());
						
					}
					else
						poiList.clear();
				}
			} 
			else 
			{
				showToast("定位无结果");
			}
		} 
		else if (rCode == 27) 
		{
			showToast("网络错误");
		} 
		else if (rCode == 32) 
		{
			showToast("key无效");
		} 
		else 
		{
			//showToast(getString(R.string.error_other) + rCode);
		}
		httpFactory.register();//不管位置获取是否成功，都提交信息
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(AMapLocation location) 
	{
		// TODO Auto-generated method stub
		if (location != null) 
		{
			if(isFirst)
			{
				isFirst = false;
				isStop = true;
				this.aMapLocation = location;// 判断超时机制
				
				locationInfor.setLat(location.getLatitude() + "");
				locationInfor.setLon(location.getLongitude() + "");
				
				if(!TextUtils.isEmpty(location.getCity()))
					poiCity = location.getCity();
				if(!TextUtils.isEmpty(location.getProvince()))
					poiProvince = location.getProvince();
				if(!TextUtils.isEmpty(location.getDistrict()))
					poiStrict = location.getDistrict();
				locationInfor.setCity(poiCity);
				locationInfor.setProvince(poiProvince);
				locationInfor.setDistrict(poiStrict);
				
				locationInfor.setPoi(location.getStreet());
				
				Bundle locBundle = location.getExtras();
				String desc = locBundle.getString("desc");
				//showToast("定位成功,准备搜索位置-->"+desc);
				
				LocalDataEntity.newInstance(activity)
				.setDeviceAddr(desc);
				
				showToast("############-->获取坐标成功-->"+locationInfor.getLon() + "  " + locationInfor.getLat() + "\n" + desc);
				//保存坐标
				LocalDataEntity.newInstance(activity)
				.setDeviceLocation(locationInfor.getLon() + "", locationInfor.getLat() + "");
				httpFactory.register();
				//LatLonPoint latLonPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
				//startSearch(latLonPoint);//按照坐标搜索
			}
		}
		else
			LogFactory.createLog().e("############-->获取坐标失败了");
	}
	
	private void showToast(final String text)
	{
		/*activity.runOnUiThread(new Runnable() 
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Toast.makeText(activity, text, 0).show();
			}
		});*/
	}
	
}
