
package com.znt.diange.utils; 

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Debug;

/** 
 * @ClassName: MyBitmapUtils 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-2-11 下午5:24:46  
 */
public class MyBitmapUtil
{

	/**
	* @Description: 解码本地图片
	* @param @param fileUrl
	* @param @param width
	* @param @param height
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public static Bitmap decodeLocal(String fileUrl, int width, int height)
	{
		
		File file = new File(fileUrl);
		
		if(!file.exists())
			return null;
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
		/*只加载基础信息,并不真正解码图片*/
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFile(fileUrl, options);
		
		if (options.outWidth < 1 || options.outHeight < 1) 
		{
			String fn = fileUrl;
			File ft = new File(fn);
			if (ft.exists()) 
			{
				ft.delete();
				return null;
			}
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		
		int[] size = calculateSize(options.outWidth, options.outHeight, width, height);
		
		/*计算缩放率*/
		options.inSampleSize = getSampleSize(options, size[0], size[1]);
		options.inJustDecodeBounds = false;
		Bitmap bm1 = null;
		InputStream inputStream = null;
		try 
		{
			inputStream = new FileInputStream(file);
			bm1 = BitmapFactory.decodeStream(inputStream, null, options);
		} 
		catch (OutOfMemoryError e) 
		{
			MyLog.e("MyBitmapUtisl_e1-->" + e.getMessage());
			/*发生错误进行再次压缩*/
			Runtime.getRuntime().runFinalization();
			try 
			{
				System.gc();
				Thread.sleep(600);
				
				options.inSampleSize += 1;
				options.inJustDecodeBounds = false;
				options.inDither = true;
				options.inPreferredConfig = null;
				try 
				{
					bm1 = BitmapFactory.decodeStream(inputStream, null, options);
				} 
				catch (OutOfMemoryError e2) 
				{
					MyLog.e("MyBitmapUtisl_e2-->" + e.getMessage());
					/*继续压缩并且在sdcard中建立缓存区*/
					Runtime.getRuntime().runFinalization();
					try 
					{
						System.gc();
						Thread.sleep(600);
						options.inSampleSize += 1;
						
						/*内存不足的情况下尝试在sdcard开辟空间存储内存*/
						options.inTempStorage = new byte[12 * 1024];
						options.inJustDecodeBounds = false;
						options.inDither = true;
						options.inPreferredConfig = null;

						try 
						{
							bm1 = BitmapFactory.decodeStream(inputStream, null, options);
						} 
						catch (OutOfMemoryError e4) 
						{
							MyLog.e("MyBitmapUtisl_e4-->" + e.getMessage());
							/*实在不行了返回null,解码失败*/
							Runtime.getRuntime().runFinalization();
							bm1 = null;
						}
					} 
					catch (InterruptedException e3) 
					{
						MyLog.e("MyBitmapUtisl_e3-->" + e.getMessage());
					}
				}
			} 
			catch (InterruptedException e1) 
			{
			}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			if(inputStream != null)
				inputStream.close();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bm1;
	}
	
	/**
	* @Description: 解码资源图片
	* @param @param context
	* @param @param res
	* @param @param width
	* @param @param height
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public static Bitmap decodeResource(Context context, int res, int width, int height)
	{
		if(res <= 0)
			return null;
		if(context == null )
			return null;
		
		InputStream is = context.getResources().openRawResource(res);
		BitmapFactory.Options options = new BitmapFactory.Options();
		
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap btp = BitmapFactory.decodeResource(context.getResources(), res, options);
		
		if(width <= 0)
			width = options.outWidth;
		if(height <= 0)
			height = options.outHeight;
		
		int[] size = calculateSize(options.outWidth, options.outHeight, width, height);
		
		/*计算缩放率*/
		options.inSampleSize = getSampleSize(options, size[0], size[1]);
		
		options.inJustDecodeBounds = false;
	    btp = BitmapFactory.decodeStream(is,null,options);
	    
	    try
		{
			if(is != null)
				is.close();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return btp;
	}
	
	/** 
	 * 将Drawable转换成Bitmap 
	 * @param drawable 
	 * @return 
	 */  
	public static Bitmap drawableToBitmap(Drawable drawable) 
	{
		if(drawable == null)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	} 
	
	/**
	 * 图片缩放
	 * @param oldBitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap resize(Bitmap oldBitmap, int width, int height) 
	{
		Bitmap newBitmap = Bitmap.createScaledBitmap(oldBitmap, width, height, true);
		return newBitmap;
	}
	
	/**
	* @Description: 质量压缩
	* @param @param image
	* @param @param size
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	 public static Bitmap compressImage(File file, Bitmap image, int size) 
	 {
		 if(file == null || image == null)
			 return null;
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 
		 /*质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中*/
		 image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		 
		 if(size > 0)
		 {
			 int options = 100;
			 int len = 0;
			 try
			 {
				len = baos.toByteArray().length;
			 } 
			 catch (OutOfMemoryError e)
			 {
				// TODO: handle exception
			 }
			 /*循环判断如果压缩后图片是否大于sizekb,大于继续压缩*/	
			 /*while( len / 1024 > size) 
			 {	
				 重置baos即清空baos
				 baos.reset();
				 这里压缩options%，把压缩后的数据存放到baos中
				 image.compress(Bitmap.CompressFormat.PNG, options, baos);
				 try
				 {
					len = baos.toByteArray().length;
				 } 
				 catch (OutOfMemoryError e)
				 {
					// TODO: handle exception
				 }
				 每次都减少10
				 options -= 10;
				 if(options <= 0)
				 {
					 options = 0;
					 break;
				 }
			 }*/
		 }
		 
		 Bitmap bitmap = null;
		 try 
		 {
			 /*把压缩后的数据baos存放到ByteArrayInputStream中*/
			 ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
			 FileOutputStream fos = new FileOutputStream(file);
			 fos.write(baos.toByteArray());
			 
			 /*把ByteArrayInputStream数据生成图片*/
			 //bitmap = BitmapFactory.decodeStream(isBm, null, null);
			 
			 fos.close();
			 isBm.close();
		 } 
		 catch (OutOfMemoryError e) 
		 {
			// TODO: handle exception
			 MyLog.e("compressImage", "OutOfMemoryError:"+e.getMessage());
			 System.gc();
		 }
		 catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		 return bitmap;
	}
	
	 public static ByteArrayInputStream compressImage(File file, int w, int h, int size) 
	 {
		 if(file == null)
			 return null;
		 ByteArrayInputStream isBm = null;
		 Bitmap image = null;
		 try 
		 {
			 image = decodeLocal(file.getAbsolutePath(), w, h);//解码图片
			 if(image == null)
				 return null;
			 
			 ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 
			 /*质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中*/
			 image.compress(Bitmap.CompressFormat.PNG, 100, baos);
			 /*int options = 100;
			 int len = 0;
			 try
			 {
				len = baos.toByteArray().length;
			 } 
			 catch (OutOfMemoryError e)
			 {
				// TODO: handle exception
			 }
			 循环判断如果压缩后图片是否大于sizekb,大于继续压缩	
			 while( len > size * 1024) 
			 {	
				 每次都减少10
				 options -= 10;
				 
				 重置baos即清空baos
				 baos.reset();
				 这里压缩options%，把压缩后的数据存放到baos中
				 image.compress(Bitmap.CompressFormat.PNG, options, baos);
				 try
				 {
					len = baos.toByteArray().length;
				 } 
				 catch (OutOfMemoryError e)
				 {
					// TODO: handle exception
				 }
				 
				 if(options <= 0)
				 {
					 options = 0;
					 break;
				 }
			 }*/
			 
			 /*把压缩后的数据baos存放到ByteArrayInputStream中*/
			 isBm = new ByteArrayInputStream(baos.toByteArray());
	 	}
		 catch (OutOfMemoryError e) 
		 {
			// TODO: handle exception
			 MyLog.e("compressImage", "OutOfMemoryError:"+e.getMessage());
			 if(image != null && !image.isRecycled())
				 image.recycle();
			 System.gc();
		 }
		 if(image != null && !image.isRecycled())
			 image.recycle();
		 return isBm;
	}
	 
	/**
	* @Description: 按照高度，根据宽高比缩放图片
	* @param @param old 原始图片
	* @param @param newHeight 新图片的高度
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public static Bitmap scaleImgFixedHeight(Bitmap old, int newHeight) 
	{
		if(old == null)
			return null;
		if(newHeight <= 0)
			return null;
		
		int width = old.getWidth();
		int height = old.getHeight();
		// 设置想要的大小
		int newWidth = newHeight * width / height;

		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBm = null;
		// 得到新的图片
		try 
		{
			newBm = Bitmap.createBitmap(old, 0, 0, width, height, matrix, true);
			matrix.reset();
		} 
		catch (OutOfMemoryError e) 
		{
			// TODO: handle exception
			MyLog.e("scaleImgFixedHeight", "OutOfMemoryError:"+e.getMessage());
		}
		
		return newBm;
	}
	
	/**
	* @Description: 按照宽度，根据宽高比缩放图片
	* @param @param old 原始图片
	* @param @param newHeight 新图片的高度
	* @param @return   
	* @return Bitmap 
	* @throws
	 */
	public static Bitmap scaleImgFixedWidth(Bitmap old, int newWidth) 
	{
		int width = old.getWidth();
		int height = old.getHeight();
		// 设置想要的大小
        int newHeight = newWidth * height / width;
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBm = null;
		// 得到新的图片
		try 
		{
			newBm = Bitmap.createBitmap(old, 0, 0, width, height, matrix, true);
		} 
		catch (OutOfMemoryError e) 
		{
			// TODO: handle exception
			MyLog.e("scaleImgFixedWidth", "OutOfMemoryError:"+e.getMessage());
		}
		return newBm;
	}
	
	/**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) 
    {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
   /*
    * 旋转图片 
    * @param angle 
    * @param bitmap 
    * @return Bitmap 
    */ 
   public static Bitmap rotaingImageView(int angle , Bitmap bitmap) 
   {  
       //旋转图片 动作   
       Matrix matrix = new Matrix();;  
       matrix.postRotate(angle);  
       System.out.println("angle2=" + angle);  
       // 创建新的图片   
       Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
               bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
       return resizedBitmap;  
   }
	
	public static Bitmap resizeBitmap(Bitmap oldBimap, int degree)
	{
		//创建操作图片是用的matrix对象
		Matrix matrix = new Matrix();
		//缩放图片动作
		matrix.postScale(1, 1);
		//旋转图片动作
		matrix.postRotate(degree);//
		if(oldBimap != null && !oldBimap.isRecycled())
		{
			//创建新图片
			Bitmap resizedBitmap = Bitmap.createBitmap(oldBimap, 0, 0, oldBimap.getWidth(), oldBimap.getHeight(), matrix,true);
			/*if(oldBimap != null && !oldBimap.isRecycled())
				oldBimap.recycle();*/
			return resizedBitmap;
		}
		return null;
	}
	
	/**
	* @Description: 保存bitmap到本地
	* @param @param mBitmap
	* @param @param fileUrl
	* @param @return   
	* @return File 
	* @throws
	 */
	public static File saveBitmap(Bitmap mBitmap, String fileUrl)
	{
		if(mBitmap == null || mBitmap.isRecycled())
			return null;
		File file = new File(fileUrl);
		  try 
		  {
			  if(file != null && file.exists())
				  file.delete();
			  file.createNewFile();
		  } 
		  catch (IOException e) 
		  {
		   // TODO Auto-generated catch block
			  return null;
		  }
		  FileOutputStream fOut = null;
		  try 
		  {
			  fOut = new FileOutputStream(file);
		  } 
		  catch (FileNotFoundException e) 
		  {
			  e.printStackTrace();
			  return null;
		  }
		  mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		  
		  try 
		  {
			  fOut.flush();
		  } 
		  catch (IOException e) 
		  {
			  e.printStackTrace();
			  return null;
		  }
		  try 
		  {
			  fOut.close();
		  } 
		  catch (IOException e) 
		  {
			  e.printStackTrace();
			  return null;
		  }

		  return file;
	}
	
	/**
	* @Description: 计算缩放比例
	* @param @param options
	* @param @param reqWidth
	* @param @param reqHeight
	* @param @param maxMultiple
	* @param @return   
	* @return int 
	* @throws
	 */
	private static int getSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
		int inSampleSize = 1;
		final int height = options.outHeight;
		final int width = options.outWidth;

		if (height > reqHeight || width > reqWidth) 
		{
			final float totalPixels = width * height;
			final float totalReqPixelsCap = (reqWidth * reqHeight);

			/*计算缩放倍数*/
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) 
			{
				inSampleSize++;
			}
			
			/*检测是否有足够的内存对缩放倍数进行缩放,不行则继续缩放*/
			while(!checkBitmapFitsInMemory(reqWidth / inSampleSize, reqHeight / inSampleSize, options.inPreferredConfig))
			{
				inSampleSize++;
			}
		}
		return inSampleSize;
	}
	
	/**
	* @Description: 当前空闲的堆内存
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long FreeMemory() 
	{
		return Runtime.getRuntime().maxMemory() - Debug.getNativeHeapAllocatedSize();
	}

	/**
	* @Description: 检测当前是否有足够的内存进行读取bitmap
	* @param @param bmpwidth
	* @param @param bmpheight
	* @param @param config
	* @param @return   
	* @return boolean 
	* @throws
	 */
	public static boolean checkBitmapFitsInMemory(long bmpwidth,
			long bmpheight, Bitmap.Config config) 
	{
		return (getBitmapSize(bmpwidth, bmpheight, config) < FreeMemory());
	}
	
	/**
	* @Description: 按照宽高计算bitmap所占内存大小
	* @param @param bmpwidth
	* @param @param bmpheight
	* @param @param config
	* @param @return   
	* @return long 
	* @throws
	 */
	public static long getBitmapSize(long bmpwidth, long bmpheight, Bitmap.Config config) 
	{
		return bmpwidth * bmpheight *  getBytesxPixel(config);
	}

	/**
	* @Description: 计算bitmap所占空间,单位bytes
	* @param @param bitmap
	* @param @param config
	* @param @return   
	* @return int 
	* @throws
	 */
	@SuppressLint("NewApi")
	public static int sizeOfBitmap(Bitmap bitmap, Bitmap.Config config) 
	{
		int size = 1;
		
		//3.1或者以上 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) 
		{
			size = bitmap.getByteCount() * getBytesxPixel(config)>>10;
		}
		else //3.1以下
		{
			size = bitmap.getRowBytes() * bitmap.getHeight()
					* getBytesxPixel(config)>>10;
		}
		return size;
	}

	/**
	* @Description: 按照不同格式计算所占字节数
	* @param @param config
	* @param @return   
	* @return int 
	* @throws
	 */
	public static int getBytesxPixel(Bitmap.Config config) 
	{
		int bytesxPixel = 1;
		
		/*3.1或者以上*/ 
		switch (config) 
		{
		case RGB_565:
		case ARGB_4444:
			bytesxPixel = 2;
			break;
		case ALPHA_8:
			bytesxPixel = 1;
			break;
		case ARGB_8888:
			bytesxPixel = 4;
			break;
		}
		return bytesxPixel;
	}
	
	/**
	* @Description: 根据图片大小和目标大小。按照图片的宽高比计算新的图片大小
	* 				根据目标大小的大的那个值max计算，保留大的
	* @param @param w
	* @param @param h
	* @param @param target_W
	* @param @param target_H
	* @param @return   
	* @return int[] 
	* @throws
	 */
	private  static int[] calculateSize(int w, int h, int target_W, int target_H)
	{
		int[] dest_size = new int[2];
		int destW = 0, destH = 0;
		if(w > target_W && h > target_H)
		{
        	if(w/target_W > h/target_H)
        	{
        		destW = target_H*w/h;
        		destH = target_H;	
        	}
        	else
        	{
        		destW = target_W;
        		destH = target_W*h/w;
        	}
        }
		else if(w > target_W && h <= target_H)
		{
        	destW = target_W;
    		destH = target_W*h/w;
        }
		else if(w<=target_W && h>target_H)
		{
        	destH = target_H;
    		destW = target_H*w/h;
        }
		else
		{
        	destH = h;
    		destW = w;
        }
		dest_size[0] = destW;
		dest_size[1] = destH;
		
		return dest_size;
	}
	
	/** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        System.out.println("w"+bitmap.getWidth());  
        System.out.println("h"+bitmap.getHeight());  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
	
	
}
 
