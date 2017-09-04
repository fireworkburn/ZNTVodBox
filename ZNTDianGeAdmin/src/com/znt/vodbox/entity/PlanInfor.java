/*  
* @Project: ZNTVodBox 
* @User: Administrator 
* @Description: 家庭音乐
* @Author： yan.yu
* @Company：http://www.zhunit.com/
* @Date 2016-6-12 上午1:32:14 
* @Version V1.1   
*/ 

package com.znt.vodbox.entity; 

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.text.TextUtils;

import com.znt.diange.mina.cmd.DeviceInfor;
import com.znt.vodbox.utils.DateUtils;

/** 
 * @ClassName: PlanInfor 
 * @Description: TODO
 * @author yan.yu 
 * @date 2016-6-12 上午1:32:14  
 */
public class PlanInfor  implements Serializable 
{

	public static String PLAN_TYPE_EVERYDAY = "1";
	public static String PLAN_TYPE_YEAR = "0";
	
	/** 
	* @Fields serialVersionUID : TODO
	*/ 
	private static final long serialVersionUID = 1L;
	private String planId = "";
	private String planName = "";
	private String planFlag = "0";//0,全部的  1，指定的
	private String terminalList = "";
	private String publishTime = "";
	private String planType = "1";//1每天  ;0，节日，日期必须要填写
	private String startDate = "";
	private String endDate = "";
	private int selectIndex = -1;
	
	private List<SubPlanInfor> subPlanList = new ArrayList<SubPlanInfor>();
	private List<DeviceInfor> deviceList = new ArrayList<DeviceInfor>();
	
	public void setPlanId(String planId)
	{
		this.planId = planId;
	}
	public String getPlanId()
	{
		return planId;
	}
	
	public void setPlanType(String planType)
	{
		this.planType = planType;
	}
	public String getPlanType()
	{
		return planType;
	}
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	public String getStartDate()
	{
		return startDate;
	}
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	public String getEndDate()
	{
		return endDate;
	}
	
	public void setPublishTime(String publishTime)
	{
		this.publishTime = publishTime;
	}
	public String getPublishTime()
	{
		return publishTime;
	}
	public String getPublishTimeFormat()
	{
		if(!TextUtils.isEmpty(publishTime))
		{
			long time = Long.parseLong(publishTime);
			return DateUtils.getStringTime(time);
		}
		return publishTime;
	}
	
	public void setPlanName(String planName)
	{
		this.planName = planName;
	}
	public String getPlanName()
	{
		return planName;
	}
	
	public void setPlanFlag(String planFlag)
	{
		this.planFlag = planFlag;
	}
	public String getPlanFlag()
	{
		return planFlag;
	}
	
	public void setSelectIndex(int selectIndex)
	{
		this.selectIndex = selectIndex;
	}
	public int getSelectIndex()
	{
		return selectIndex;
	}
	
	public SubPlanInfor getSelelctPlanInfor()
	{
		if(selectIndex >= 0 && selectIndex < subPlanList.size())
			return subPlanList.get(selectIndex);
		return null;
	
	}
	public void updateSelect(SubPlanInfor infor)
	{
		SubPlanInfor tempInfor = getSelelctPlanInfor();
		infor.setId(tempInfor.getId());
		subPlanList.set(selectIndex, infor);
	}
	public void addSubPlanInfor(SubPlanInfor infor)
	{
		subPlanList.add(infor);
	}
	public void deleteSubPlanInfor(int index)
	{
		subPlanList.remove(index);
	}
	
	public void setTerminalList(String terminalList)
	{
		this.terminalList = terminalList;
	}
	public String getTerminalList()
	{
		return terminalList;
	}
	
	public void setSubPlanList(List<SubPlanInfor> subPlanList)
	{
		this.subPlanList = subPlanList;
	}
	public List<SubPlanInfor> getSubPlanList()
	{
		return subPlanList;
	}
	
	public void setDeviceList(List<DeviceInfor> deviceList)
	{
		this.deviceList = deviceList;
	}
	public List<DeviceInfor> getDeviceList()
	{
		return deviceList;
	}
	
	public SubPlanInfor getCurSubPlanInfor()
	{
		SubPlanInfor curPlanInfor = null;
		int size = subPlanList.size();
		String curTime = DateUtils.getTimeShortHead();
		int curTimeInt = DateUtils.timeToInt(curTime, ":");
		for(int i=0;i<size;i++)
		{
			SubPlanInfor infor = subPlanList.get(i);
			int startTime = DateUtils.timeToInt(infor.getStartTime(), ":");
			int endTime = DateUtils.timeToInt(infor.getEndTime(), ":");
			if(startTime > endTime)
			{
				if(curTimeInt < endTime)
					curTimeInt += 24 * 60;
				endTime += 24 * 60;
			}
				
			if(curTimeInt > startTime && curTimeInt < endTime)
			{
				curPlanInfor = infor;
				break;
			}
		}
		return curPlanInfor;
	}
	
	public String getAllStartTimes()
	{
		String startTimes = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(i < size - 1)
				startTimes += tempInfor.getStartTime() + ",";
			else
				startTimes += tempInfor.getStartTime();
		}
		return startTimes;
	}
	public String getAllEndTimes()
	{
		String endTimes = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(i < size - 1)
				endTimes += tempInfor.getEndTime() + ",";
			else
				endTimes += tempInfor.getEndTime();
		}
		return endTimes;
	}
	public String getAllCategoryIds()
	{
		String categoryIds = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(i < size - 1)
				categoryIds += tempInfor.getPlanAlbumIds() + ",";
			else
				categoryIds += tempInfor.getPlanAlbumIds();
		}
		return categoryIds;
	}
	public String getAllScheduleIds()
	{
		String scheduleIds = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			String id = tempInfor.getId();
			if(TextUtils.isEmpty(id))
				id = "0";
			if(i < size - 1)
				scheduleIds += id + ",";
			else
				scheduleIds += id;
		}
		return scheduleIds;
	}
	public String getAllTerminalIds()
	{
		String scheduleIds = "";
		int size = deviceList.size();
		for(int i=0;i<size;i++)
		{
			DeviceInfor tempInfor = deviceList.get(i);
			if(i < size - 1)
				scheduleIds += tempInfor.getCode() + ",";
			else
				scheduleIds += tempInfor.getCode();
		}
		return scheduleIds;
	}
	
	public String getCycleTypes()
	{
		String cycleTypes = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(!TextUtils.isEmpty(tempInfor.getCycleType()))
			{
				if(i < size - 1)
					cycleTypes += tempInfor.getCycleType() + ",";
				else
					cycleTypes += tempInfor.getCycleType();
			}
		}
		return cycleTypes;
	}
	public String getLoopAddNums()
	{
		String addNums = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(!TextUtils.isEmpty(tempInfor.getLoopAddNum()))
			{
				if(i < size - 1)
					addNums += tempInfor.getLoopAddNum() + ",";
				else
					addNums += tempInfor.getLoopAddNum();
			}
		}
		return addNums;
	}
	public String getLoopMusicInfoIds()
	{
		String musics = "";
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			SubPlanInfor tempInfor = subPlanList.get(i);
			if(!TextUtils.isEmpty(tempInfor.getLoopMusicInfoId()))
			{
				if(i < size - 1)
					musics += tempInfor.getLoopMusicInfoId() + ",";
				else
					musics += tempInfor.getLoopMusicInfoId();
			}
		}
		return musics;
	}
	
	public boolean checkPlanTime(String startTime, String endTime)
	{
		boolean isValid = true;
		int start = DateUtils.timeToInt(startTime, ":");
		int end = DateUtils.timeToInt(endTime, ":");
		if(start == end)
			return false;
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			if(i !=selectIndex)
			{
				SubPlanInfor tempInfor = subPlanList.get(i);
				int tempS = DateUtils.timeToInt(tempInfor.getStartTime(), ":");
				int tempE = DateUtils.timeToInt(tempInfor.getEndTime(), ":");
				if((tempS > tempE))
				{
					if(start > end)
					{
						isValid = false;
						break;
					}
				}
				if(isTimeOverlap(tempS, tempE, start))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(tempS, tempE, end))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempS))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempE))
				{
					isValid = false;
					break;
				}
			}
		}
		
		
		/*boolean isValid = true;
		int start = DateUtils.timeToInt(startTime, ":");
		int end = DateUtils.timeToInt(endTime, ":");
		if(start > end)
			end += 24 * 60;
		
		int size = subPlanList.size();
		for(int i=0;i<size;i++)
		{
			if(i !=selectIndex)
			{
				SubPlanInfor tempInfor = subPlanList.get(i);
				int tempS = DateUtils.timeToInt(tempInfor.getStartTime(), ":");
				int tempE = DateUtils.timeToInt(tempInfor.getEndTime(), ":");
				if(tempS > tempE)
				{
					if(start < tempE)
						start += 24 * 60;
					tempE += 24 * 60;
				}
				if(isTimeOverlap(tempS, tempE, start))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(tempS, tempE, end))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempS))
				{
					isValid = false;
					break;
				}
				if(isTimeOverlap(start, end, tempE))
				{
					isValid = false;
					break;
				}
			}
		}*/
		
		return isValid;
	}
	private boolean isTimeOverlap(int start, int end, int dest)
	{
		if(start > end)
		{
			if(dest > start && dest < end + 24 * 60)
				return true;
		}
		else
		{
			if(dest > start && dest < end)
				return true;
		}
		
		return false;
	}
}
 
