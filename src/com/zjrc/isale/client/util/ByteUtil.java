package com.zjrc.isale.client.util;

/**
 * 项目名称：销售管家
 * 版本号：V1.00
 * 创建者: 高林荣
 * 功能描述：byte[]工具类
 */

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class ByteUtil { 
	private static String LOG_TAG = ByteUtil.class.getName();
    /**  
     * 数组扩容  
     * @param src byte[] 源数组数据  
     * @param size int 扩容的增加量  
     * @return byte[] 扩容后的数组  
     */   
    public static byte[] grow(byte[] src, int size) {   
        byte[] tmp = new byte[src.length + size];   
        System.arraycopy(src, 0, tmp, 0, src.length);   
        return tmp;   
    } 	
    
    public static void memset(byte des[], int des_offset, byte dat, int len) 
    { 
	    for(int i = 0; i < len; i++) 
	    	des[des_offset + i] = dat; 
    } 
    
	
	/**
	 * 功能：单个字段加上字段长度
	 * @param sField String 字段值
	 * @return byte[] 加了长度的字段值 
	 */
	public static byte[] EncodeField(String sField){
		if (sField==null){
			sField = "";
		}
		ByteBuffer FieldBuffer = Charset.forName("UTF-8").encode(sField);
		byte[] bField = new byte[FieldBuffer.limit()+4];		
		//增加长度数据4个字节
		int sLength = FieldBuffer.limit();
		byte[] bLength = ByteUtil.intToByte(sLength);
		System.arraycopy(bLength,0,bField,0,4);
		//增加字符串值		 
		System.arraycopy(FieldBuffer.array(),0,bField,4,FieldBuffer.limit());		
		return bField;
	}

	/**
	 * 功能：字段列表加上字段长度
	 * @param sField String 字段值
	 * @return byte[] 加了长度的字段值 
	 */	
	public static byte[] EncodeFields(List<String> lFields){
		byte[] bFields = new byte[0];
		int offset = 0;
		for (int i=0;i<lFields.size();i++){
			//定义字段数组
			byte[] bField = EncodeField(lFields.get(i));
			//字段数组放到返回包数据字节数组
			bFields = grow(bFields,bField.length);
			System.arraycopy(bField, 0, bFields, offset, bField.length);
            offset += bField.length;					
		}
		return bFields;
	}
	
	/**
	 * 功能：字段列表加上字段长度
	 * @param sField String 字段值
	 * @return byte[] 加了长度的字段值 
	 */	
	public static byte[] EncodeFields(String[] sFields){
		byte[] bFields = new byte[0];
		int offset = 0;
		for (int i=0;i<sFields.length;i++){
			//定义字段数组
			byte[] bField = EncodeField(sFields[i]);
			//字段数组放到返回包数据字节数组
			bFields = grow(bFields,bField.length);
			System.arraycopy(bField, 0, bFields, offset, bField.length);
            offset += bField.length;					
		}
		return bFields;
	}	
	
	/**
	 * 功能：从字节中得到字段字符值数组
	 * 	@param data 字段值
	 *  @return String 字段值数组
	 */	
	public static List<String> parseField(byte[] data){
		List<String> lFields = new ArrayList<String>();
		if (data==null){
			return lFields;
		}
		if (data.length==0){
			return lFields;
		}		
		int offset = 0;
		int i = 1;
		while (offset<data.length){
			try{
				byte[] bLength = new byte[4];
				System.arraycopy(data, offset, bLength, 0, bLength.length);
				int iLength = ByteUtil.byteToInt(bLength);
				//Log.i(LOG_TAG, "解析请求数据包第"+i+"个字段长度为"+iLength);
				if (iLength<=20480){
					byte[] fielddata = null;
					try{
						fielddata = new byte[iLength];
	            	}catch(OutOfMemoryError ome){
	            		Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，分配内存(大小"+iLength+")出现错误,错误原因["+ome.getMessage()+"]，退出解析字段");
	            		return lFields;	
	            	}					
					System.arraycopy(data, offset+4, fielddata, 0, iLength);
					String sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
					lFields.add(sField);
					//Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					offset += iLength +4;
					i++;
				}else{
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段，字段长度"+iLength+"大于20K，退出解析字段");
					return lFields;			
				}
			}catch(Exception ex){
				if (ex.getMessage()!=null){
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，原因["+ex.getMessage()+"]，退出解析字段");
				}else{
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，未知原因，退出解析字段");
				}
				return lFields;
			}
		}
		return lFields;
	}	
	
    /**
    * short转换到字节数组
    * @param number
    * @return
    */
    public static byte[] shortToByte(short number){
        byte[] b = new byte[2];   
        for (int i = 1; i >= 0; i--) {   
          b[i] = (byte) (number % 256);   
          number >>= 8;   
        } 
       return b;
    }
    
    /**
    * 字节到short转换
    * @param b
    * @return
    * @throws DataTranslateException
    */
    public static short byteToShort(byte[] b){
       return (short) ((((b[0] & 0xff)<< 8)| b[1] & 0xff));
    }
    
    /**
    * 整型转换到字节数组
    * @param number
    * @return
    */
    public static byte[] intToByte(int n)   
    {   
       byte[] b = new byte[4];   
       b[0] = (byte)(n & 0xff);   
       b[1] = (byte)(n >> 8 & 0xff);   
       b[2] = (byte)(n >>16 & 0xff);   
       b[3] = (byte)(n >> 24 & 0xff);   
       return b;   
    }  

    /**
    * 字节数组到整型转换
    * @param b
    * @return
    * @throws DataTranslateException
    */
    public static int byteToInt(byte[] b)   
    {   
       int s = 0;   
       for(int i=3;i>=0;i--){   
           s *= 256;   
           s += b[i]<0?b[i]+256:b[i];   
       }   
       return s;   
    }
    
    /**
    * long转换到字节数组
    * @param number
    * @return
    */
    public static byte[] longToByte(long n) {  
        byte[] b = new byte[8];   
        b[0] = (byte)(n & 0xff);   
        b[1] = (byte)(n >> 8 & 0xff);   
        b[2] = (byte)(n >> 16 & 0xff);   
        b[3] = (byte)(n >> 24 & 0xff); 
        b[4] = (byte)(n >> 32 & 0xff);  
        b[5] = (byte)(n >> 40 & 0xff); 
        b[6] = (byte)(n >> 48 & 0xff);
        b[7] = (byte)(n >> 56 & 0xff); 
        return b;     	  
    }
    
    /**
    * 字节数组到整型的转换
    * @param b
    * @return
    * @throws DataTranslateException
    */
    public static long byteToLong(byte[] b){
        long s = 0;   
        for(int i=7;i>=0;i--){   
            s *= 256;   
            s += b[i]<0?b[i]+256:b[i];   
        }   
        return s;       	
    }
    
    /**
    * double转换到字节数组
    * @param d
    * @return
    */
    public static byte[] doubleToByte(double d){
      
       byte[] bytes = new byte[8];
       long l = Double.doubleToLongBits(d);
       for(int i = 0; i < bytes.length; i++ ){
        bytes[i]=  Long.valueOf(l).byteValue();
        l=l>>8;
       }
       return bytes;
    }
    
    /**
    * 字节数组到double转换
    * @param b
    * @return
    * @throws DataTranslateException
    */
    public static double byteToDouble(byte[] b){
       long l;
       l=b[0];
       l&=0xff;
       l|=((long)b[1]<<8);
       l&=0xffff;
       l|=((long)b[2]<<16);
       l&=0xffffff;
       l|=((long)b[3]<<24);
       l&=0xffffffffl;
       l|=((long)b[4]<<32);
       l&=0xffffffffffl;
      
       l|=((long)b[5]<<40);
       l&=0xffffffffffffl;
       l|=((long)b[6]<<48);
       l&=0xffffffffffffffl;
      
       l|=((long)b[7]<<56);
      
       return Double.longBitsToDouble(l);  
    }
    
    /**
    * float转换到字节数组
    * @param d
    * @return
    */
    public static byte[] floatToByte(float d){
      
       byte[] bytes = new byte[4];
       int l = Float.floatToIntBits(d);
       for(int i = 0; i < bytes.length; i++ ){
        bytes[i]= Integer.valueOf(l).byteValue();
        l=l>>8;
       }
       return bytes;
    }
    /**
    * 字节数组到float的转换
    * @param b
    * @return
    * @throws DataTranslateException
    */
    public static float byteToFloat(byte[] b){
       int l;
       l=b[0];
       l&=0xff;
       l|=((long)b[1]<<8);
       l&=0xffff;
       l|=((long)b[2]<<16);
       l&=0xffffff;
       l|=((long)b[3]<<24);
       l&=0xffffffffl;

       return Float.intBitsToFloat(l);  
    }
    
    /**
    * 字符串到字节数组转换
    * @param s
    * @return
    */
    public static byte[] stringToByte(String s){
       return s.getBytes();
    }
    
    /**
    * 字节数组带字符串的转换
    * @param b
    * @return
    */
    public static String byteToString(byte[] b){
       return new String(b);      
    }    
    
    
    
    //设定int类型长度为8
    static public byte[] ByteCat(byte[] pSource, long value)
    {
    	  byte[] pRet = null;
          
          if (pSource == null)
          {
              	return longToByte(value);
          }
          else{
          	 pRet = new byte[pSource.length + 8];          	 
          	 System.arraycopy(pSource, 0, pRet, 0, pSource.length);          	 
          	 System.arraycopy(longToByte(value),0,pRet,pSource.length, 8);	  	 
          }
          
          return pRet;
    }
    
    //设定int类型长度为4
    static public byte[] ByteCat(byte[] pSource, int value)
    {
        byte[] pRet = null;
        
        if (pSource == null)
        {
           	return intToByte(value);
        }
        else{
        	 pRet = new byte[pSource.length + 4];        	 
        	 System.arraycopy(pSource, 0, pRet, 0, pSource.length);        	 
        	 System.arraycopy(intToByte(value),0,pRet,pSource.length, 4);	  	 
        }
        
        return pRet;
    }
        
    static public byte[] ByteCat(byte[] pSource, String value, int length)
    {
        byte[] pRet = null;
        
        ByteBuffer FieldBuffer = Charset.forName("UTF-8").encode(value);
        int iCurLength = FieldBuffer.limit();
        
        if (pSource == null)
        {
        	 pRet = new byte[length];
        	
        	 System.arraycopy(FieldBuffer.array(),0,pRet,0,(length > iCurLength ? iCurLength : length));	
        }
        else{
        	 pRet = new byte[pSource.length + length];
        	 
        	 System.arraycopy(pSource, 0, pRet, 0, pSource.length);
        	 
        	 System.arraycopy(FieldBuffer.array(),0,pRet,pSource.length,(length > iCurLength ? iCurLength : length));	  	 
        }
        
        return pRet;
       
    }
    
    
    /**
	 * 功能：单个字段加上字段长度	文件传输控制包头
	 * @param sField String 字段值
	 * @return byte[] 加了长度的字段值 
	 */
    /*
	public static byte[] EncodeFileHeadField(String sToken, int caseId, String sFileName, long ifileLen ){
		if(sToken == null || sToken.length() == 0 || sFileName== null || sFileName.length() == 0|| caseId <= 0 ){
			Log.i(LOG_TAG,"ByteUtil.java, The data check is fail.");
			return null;
		}
		
		byte [] bField = null;
		//cToken            登录令牌
		bField = ByteCat(null, sToken, 80);
		Log.i(LOG_TAG,"token length=" + bField.length);
		 //iAlbumId          相册ID
		bField = ByteCat(bField, caseId);
		Log.i(LOG_TAG,"iAlbumId length=" + bField.length);
        //cFileName         图片文件名
		bField = ByteCat(bField, sFileName, 32);
		Log.i(LOG_TAG,"cFileName length=" + bField.length);
        //lFileSize         图片大小
		bField = ByteCat(bField, ifileLen);
		Log.i(LOG_TAG,"lFileSize length=" + bField.length);
        //lStartPos         开始传输位置
		bField = ByteCat(bField, (long)0);
		Log.i(LOG_TAG,"lStartPos length=" + bField.length);
        //lLength           本次传输大小
		bField = ByteCat(bField, ifileLen);	
		Log.i(LOG_TAG,"lLength lLength=" + bField.length);
		
		return bField;
	}
	*/
	
	/**
	 * 功能：从字节中得到字段字符值数组

	 * 	@param data 字段值

	 *  @return String 字段值数组

	 */	
	/*
	public static List<String> parseFileHeadField(byte[] data){
		List<String> lFields = new ArrayList<String>();
		if (data==null){
			return lFields;
		}
		if (data.length==0){
			return lFields;
		}		
		int offset = 0;
		int i = 1;
		while (offset<data.length){
			try{
				byte[] fielddata = null;
				String sField = null;
				switch(i){
				case 1:
					fielddata = new byte[80];
					System.arraycopy(data, offset, fielddata, 0, 80);
					sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
					lFields.add(sField);
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					Log.i(LOG_TAG,"The No " + i +",value = "+ sField);
					offset += 80;
					i++;
					break;
				case 2:
					fielddata = new byte[4];
					System.arraycopy(data, offset, fielddata, 0, 4);
					int iAlbumId = byteToInt(fielddata);
	//				sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
					lFields.add(iAlbumId + "");
	//				Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					Log.i(LOG_TAG,"The No " + i +",value = "+ iAlbumId);
					offset += 4;
					i++;
					break;
				case 3:
					fielddata = new byte[32];
					System.arraycopy(data, offset, fielddata, 0, 32);
					sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
					lFields.add(sField);
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					Log.i(LOG_TAG,"The No " + i +",value = "+ sField);
					offset += 32;
					i++;
					break;
				case 4:
					fielddata = new byte[8];
					System.arraycopy(data, offset, fielddata, 0, 8);
					long lTotalLen = byteToLong(fielddata);
			//		sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
			//		lFields.add(sField);
					lFields.add(lTotalLen + "");
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					Log.i(LOG_TAG,"The No " + i +",value = "+ lTotalLen);
					offset += 8;
					i++;
					break;
				case 5:
					fielddata = new byte[8];
					System.arraycopy(data, offset, fielddata, 0, 8);
					long lStartPos = byteToLong(fielddata);
		//			sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
		//			lFields.add(sField);
					lFields.add(lStartPos + "");
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					Log.i(LOG_TAG,"The No " + i +",value = "+ lStartPos);
					offset += 8;
					i++;
					break;
				case 6:
					fielddata = new byte[8];
					System.arraycopy(data, offset, fielddata, 0, 8);
					long lCurLen = byteToLong(fielddata);
	//				sField = Charset.forName("UTF-8").decode(ByteBuffer.wrap(fielddata)).toString().trim();
//					lFields.add(sField);
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段值为："+sField);
					lFields.add(lCurLen + "");
					Log.i(LOG_TAG,"The No " + i +",value = "+ lCurLen);
					offset += 8;
					i++;
					break;
				default:
						break;
				}				
			}catch(OutOfMemoryError ome){
        		Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，分配内存(大小"+")出现错误,错误原因["+ome.getMessage()+"]，退出解析字段");
        		return lFields;	
        	}catch(Exception ex){
				if (ex.getMessage()!=null){
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，原因["+ex.getMessage()+"]，退出解析字段");
				}else{
					Log.i(LOG_TAG,"解析请求数据包第"+i+"个字段出错，未知原因，退出解析字段");
				}
				return lFields;
			}
		}
		return lFields;
	}	
	*/
    
}
