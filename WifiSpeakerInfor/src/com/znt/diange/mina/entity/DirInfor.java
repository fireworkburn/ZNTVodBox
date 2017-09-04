package com.znt.diange.mina.entity;

import java.io.Serializable;

public class DirInfor implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String dirId = "";
	private String dirName = "";
	private int musicSize = 0;
	
	public void setDirId(String dirId)
	{
		this.dirId = dirId;
	}
	public String getDirId()
	{
		return dirId;
	}
	
	public void setDirName(String dirName)
	{
		this.dirName = dirName;
	}
	public String getDirName()
	{
		return dirName;
	}
	
	public void setMusicSize(int musicSize)
	{
		this.musicSize = musicSize;
	}
	public int getMusicSize()
	{
		return musicSize;
	}
	public void addMusicSize()
	{
		musicSize = musicSize + 1;
	}

}
