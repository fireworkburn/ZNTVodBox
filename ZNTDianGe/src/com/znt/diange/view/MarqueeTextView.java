
package com.znt.diange.view; 

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.znt.diange.utils.StringUtils;

/** 
 * @ClassName: MarqueeTextView 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-3-19 上午9:02:30  
 */
public class MarqueeTextView extends TextView
{

	private Paint paint = null;
	
	private float textLenght = 0;//字符串占的宽度
	private float textHeight = 0;//字符串占的高度
	private float speed = 0.5f;//移动速度
	private float xPosition = 0;//x方向位移
	private float yposition = 0;//y方向位移
	private int scrollWidth = 0;//允许移动的水平范围
	private int scrollHeight = 0;//允许移动的垂直范围
	private int color = 0;
	private int size = 14;
	private String text = "";
	private boolean isScroll = false;
	private String direction = "up";
	private TextPaint textPaint;
	private StaticLayout layout;
	
	private Context context = null;
	
	/** 
	* <p>Title: </p> 
	* <p>Description: </p> 
	* @param context
	* @param attrs 
	*/
	public MarqueeTextView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		paint = super.getPaint();
	}
	public MarqueeTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		paint = super.getPaint();
	}
	
	public String getDirection() 
	{
		return direction;
	}

	public void setDirection(String direction) 
	{
		this.direction = direction;
	}
	public float getSpeed() 
	{
		return speed;
	}
	
	public void setSpeed(float speed) 
	{
		this.speed = speed;
	}
	public void setTextColor(int color)
	{
		this.color = color;
	}
	
	public void setTextSize(int size){
		this.size = size;
		paint.setTextSize(getTextConvertSize());
	}
	public void startScroll()
	{
		if(color != 0)
			paint.setColor(color);
		isScroll = true;
		//invalidate();
	}
	public void stopScroll()
	{
		isScroll = false;
	}
	public void clear(){
		textPaint = null;
		layout = null;
		//paint = null;
	}
	
	private int getTextConvertSize()
	{
		return StringUtils.dip2px(context, size);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		text = super.getText().toString();
		
		scrollWidth = getWidth();
		scrollHeight = getHeight();
		
		if(direction.equals("left"))
		{
			textLenght = paint.measureText(text);
			xPosition = scrollWidth;//left
			yposition = getTextSize() + getPaddingTop();
		}
		else if(direction.equals("right"))
		{
			textLenght = paint.measureText(text);
			xPosition = -textLenght;//right
			yposition = getTextSize() + getPaddingTop();
		}
		else if(direction.equals("up"))
		{
			textHeight = getTextSize();
			yposition = scrollHeight;//up
		}
		else if(direction.equals("down"))
		{
			textHeight = getTextSize();
			yposition = 0;//down
		}
	}
	
	/**
	*callbacks
	*/
	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		
		if(isScroll)
		{
			if(direction.equals("left"))
			{
				scrollToLeft(canvas);
			}
			else if(direction.equals("right"))
			{
				scrollToRight(canvas);
			}
			else if(direction.equals("up"))
			{
				scrollToUp(canvas);
			}
			else if(direction.equals("down"))
			{
				scrollToDown(canvas);
			}
			invalidate();
		}
		else
			super.onDraw(canvas);
	}
	
	private void scrollToLeft(Canvas canvas)
	{
		canvas.drawText(text, 0, text.length(), xPosition, yposition, paint);
		xPosition -= speed;
		if(xPosition <= -textLenght)
			xPosition = scrollWidth;
	}
	private void scrollToRight(Canvas canvas)
	{
		canvas.drawText(text, 0, text.length(), xPosition, yposition, paint);
		xPosition += speed;
		if(xPosition >= scrollWidth)
			xPosition = -textLenght;
	}
	private void scrollToUp(Canvas canvas)
	{
		if(textPaint == null){
			textPaint = new TextPaint();
			textPaint.setColor(color);
			textPaint.setTextSize(getTextConvertSize());
		}
		if(layout == null){
			layout = new StaticLayout(text, textPaint, scrollWidth, Alignment.ALIGN_NORMAL,1.0F,0,true);
		}
		canvas.translate(0, yposition);
		layout.draw(canvas);
		yposition -= speed;
		if(yposition <= 0 - scrollHeight)
			yposition = scrollHeight;
	}
	private void scrollToDown(Canvas canvas)
	{
		canvas.drawText(text, 0, text.length(), xPosition, yposition, paint);
		yposition += speed;
		if(yposition >= scrollHeight + textHeight)
			yposition = 0;
	}
}
 
