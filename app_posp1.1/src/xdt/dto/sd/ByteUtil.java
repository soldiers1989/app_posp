package xdt.dto.sd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;


public class ByteUtil {
	/**
	 * bytes转换成十六进制字符串
	 */
	public static String bytes2HexStr(byte[] b) {
		
		if((b==null)||(b.length == 0)) return null;
		String hs="";
		String stmp="";
		for (int n=0;n<b.length;n++) {
			stmp=(Integer.toHexString(b[n] & 0XFF));
			if (stmp.length()==1) hs=hs+"0"+stmp;
			else hs=hs+stmp;
		}
		return hs.toUpperCase();
	}
	
	public static String byte2HexStr(byte b) {
		String stmp = Integer.toHexString(b & 0XFF);
		if (stmp.length()==1) stmp="0"+stmp;
		return stmp.toUpperCase();
	}

	private static byte uniteBytes(String src0, String src1) {
		byte b0 = Byte.decode("0x" + src0).byteValue();
		b0 = (byte) (b0 << 4);
		byte b1 = Byte.decode("0x" + src1).byteValue();
		byte ret = (byte) (b0 | b1);
		return ret;
	}

	/**
	 * 十六进制字符串换转成bytes
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m=0,n=0;
		int l=src.length()/2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m=i*2+1;
			n=m+1;
			ret[i] = uniteBytes(src.substring(i*2, m),src.substring(m,n));
		}
		return ret;
	}
	
	/** 
     * 合并两个byte数组 
     * @param pByteA 
     * @param pByteB 
     * @return 
     */  
    public static byte[] mergeBytes(byte[] pByteA, byte[] pByteB){  
        int aCount = pByteA.length;  
        int bCount = pByteB.length;  
        byte[] b = new byte[aCount + bCount];  
        for(int i=0;i<aCount;i++){  
            b[i] = pByteA[i];  
        }  
        for(int i=0;i<bCount;i++){  
            b[aCount + i] = pByteB[i];  
        }  
        return b;  
    } 
    
    /**
	 * 截取byte数据
	 * @param b	是byte数组
	 * @param j	是大小
	 * @return
	 */
	public static byte[] cutOutByte(byte[] b,int j){
		if(b.length==0 || j==0){
			return null;
		}
		byte[] bjq = new byte[j];
		for(int i = 0; i<j;i++){
			bjq[i]=b[i];
		}
		return bjq;
	}
	
	/** 
   * 将32位整数转换成长度为4的byte数组 
   *  
   * @param s 
   * @return byte[] 
   * */  
  public static byte[] intToByteArray(int s) {  
      byte[] targets = new byte[4];  
      for (int i = 0; i < 4; i++) {  
          int offset = (targets.length - 1 - i) * 8;  
          targets[i] = (byte) ((s >>> offset) & 0xff);  
      }  
      return targets;  
  }  
  
  public static byte[] shortToByte(short s) {  
      byte[] targets = new byte[2];  
      targets[0] = (byte)((s >> 8) & 0x000000FF);
      targets[1] = (byte)(s & 0x000000FF);
      return targets;  
  }
  
  //无符号字节转整数 如: 0xFF -> 255
  public static int byteUnsignedToInt(byte value){
	  return (int)(0x000000FF & value);
  }
  
  //整数转无符号符号字节 255 -> 0xFF
  public static byte intToByteUnsigned(int value){
	  return (byte)value;
  }
  
  //无符号short转整数 如: 0xFFFF -> 65535
  public static int shortUnsignedToInt(short value){
	  return (int)(0x0000FFFF & value);
  }
  
//整数转无符号符号字节 65535 -> 0xFFFF
  public static short intToShortUnsigned(int value){
	  return (short)value;
  }
  
  //short int 字节顺序转换为LittleEndian(BigEndian无需转换)
  public static short shortLittleEndian(short value){
	  ByteBuffer buffer =  ByteBuffer.allocate(2); 
	  buffer.putShort(value);
	  buffer.flip();
	  buffer.order(ByteOrder.LITTLE_ENDIAN); 
	  return buffer.getShort();
  }
  
  public static int byteToInt(byte high,byte low){
	  int ret = byteUnsignedToInt(high);
	  ret = ret << 8;
	  return ret | byteUnsignedToInt(low);
  }
  
  public static short[] byteToShort(byte[] b){
	  
	  int len = b.length / 2;
	  short[] ret = new short[len];
	  for(int i=0;i<len;i++){
		  ret[i] = (short)byteToInt(b[i*2],b[i*2+1]);
	  }
	  return ret;
  }
  
  //查找子数组
  public static int pos(byte[] src, byte[] sub){
	  
	  for(int i=0;i<src.length;i++){
		  
		  //查找到第一个字符
		  if(src[i] == sub[0]){
			  
			  //查找后续字符
			  boolean find = true;
			  for(int j=1;j<sub.length;j++){
				  
				  int index = i+j;
				  if((index >= src.length) || (src[index] != sub[j])){
					  find = false;
					  break;
				  }
			  }
			  if(find) return i;
		  };
	  }
	  return -1;
  }
  
  /**
   * 删除数组的左边的空字符: 0x00
   * @param bs
   * @return
   */
  public static byte[] bytesTrimLeft0(byte[] bs){
	  for(int i=0;i<bs.length;i++){
		  if(bs[i] != 0x00){
			  
			  //最左边为都不是0x00
			  if(i==0) return bs;
			  
			  //最左边有0x00
			  int len = bs.length - i;
			  byte[] newBs = new byte[len];
			  System.arraycopy(bs, i, newBs, 0, len);
			  return newBs;
		  }
	  }
	  return null; //全是0
  }
  
  //获取字节随机数,byteLen: 字节个数
  public static byte[] getRandomBytes(int byteLen){
		byte r[] = new byte[byteLen];
		Random ran = new Random();// 随机数类
		ran.nextBytes(r);
		return r;
	}
  
  public static void main(String[] args) {
	  
//	  int i = (byte)0xA2;
//	  //int i = byteUnsignedToInt((byte)0xA2);
//	  System.out.println("i: " + i);
//	  System.out.println(String.format("i: 0x%08x\n", i));
//	  
//	  byte b = intToByteUnsigned(255);
//	  System.out.println(String.format("b: 0x%02x", b));
//	  i = byteUnsignedToInt(b);
//	  System.out.println("i: " + i);
//	  System.out.println(String.format("i: 0x%08x", i));
//	  
//	  short s = 0x1234;
//	  s = shortLittleEndian(s);
//	  System.out.println(String.format("s: 0x%02x", s));
	  
//	  byte high = (byte)0xA2;
//	  byte low = (byte)0xB4;
//	  int i = byteToInt(high,low);
//	  System.out.println(String.format("i: 0x%08x", i));
	  
	  //byte[] src = {1,2,3,4,5};
	  //byte[] sub = {4,5};
	  
//	  String strSrc = "79636D6468742C6D61633D3030314334323343303939392C0D0A";
//	  String strSub = "0D0A";
//	  byte[] src = ByteUtil.hexStr2Bytes(strSrc);
//	  byte[] sub = ByteUtil.hexStr2Bytes(strSub);
//	  System.out.println(String.format("src: %s", ByteUtil.bytes2HexStr(src)));
//	  System.out.println(String.format("sub: %s", ByteUtil.bytes2HexStr(sub)));
//	  System.out.println(String.format("pos: %d", pos(src,sub)));
	  
	  byte[] bs = {0,0x00};
	  byte[] newBs = bytesTrimLeft0(bs);
	  System.out.println(String.format("trim str: %s", ByteUtil.bytes2HexStr(newBs)));
	  
  }
  

}
