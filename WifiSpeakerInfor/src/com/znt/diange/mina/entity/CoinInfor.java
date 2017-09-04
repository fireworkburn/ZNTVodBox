package com.znt.diange.mina.entity;

public class CoinInfor 
{
	private String balance = "";//余额
	private String freezeAmount = "";//冻结金币
	private String sumAmount = "";//总共获取到的金币

	public void setBalance(String balance)
	{
		this.balance = balance;
	}
	public String getBalance()
	{
		return balance;
	}
	
	public void setFreeze(String freezeAmount)
	{
		this.freezeAmount = freezeAmount;
	}
	public String getFreeze()
	{
		return freezeAmount;
	}
	
	public void setSumAmount(String sumAmount)
	{
		this.sumAmount = sumAmount;
	}
	public String getSumAmount()
	{
		return sumAmount;
	}
}
