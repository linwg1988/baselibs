package stoneonline.framework.utils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;

public class CarIdUtil {
	
	 private static byte[] Wi=new byte[17];
   private static final byte fPart = 6;
   private static final byte fMod = 11;
   private static final byte oldIDLen = 15;
   private static final byte newIDLen = 18;
   private static final String yearFlag = "19";
   private static final String CheckCode="10X98765432";
   private static final int minCode = 150000;
   private static final int maxCode = 700000;
//   private String oldIDCard="";
//   private String newIDCard="";

   
   //private String Area[][2] = 
   private static void setWiBuffer(){
       for(int i=0;i<Wi.length;i++){    
           int k = (int) Math.pow(2, (Wi.length-i));
           Wi[i] = (byte)(k % fMod);
       }
   }
   
   private static String getCheckFlag(String idCard){
       int sum = 0;
       for(int i=0; i<17; i++){
           sum += Integer.parseInt(idCard.substring(i,i+1)) * Wi[i];
       }
       byte iCode = (byte) (sum % fMod);
       return CheckCode.substring(iCode,iCode+1);
   }
   
   private static boolean checkLength(final String idCard,boolean newIDFlag){
       boolean right = (idCard.length() == oldIDLen) || (idCard.length() == newIDLen);
       newIDFlag = false;
       if(right){
           newIDFlag = (idCard.length() == newIDLen);
       }
       return right;
   }
   
   private static String getIDDate(final String idCard,boolean newIDFlag){
       String dateStr = "";
       if(newIDFlag)
           dateStr = idCard.substring(fPart,fPart+8);
       else
           dateStr = yearFlag + idCard.substring(fPart,fPart+6);
       return dateStr;
   }
   
   @SuppressLint("SimpleDateFormat") private static boolean checkDate(final String dateSource){
       String dateStr = dateSource.substring(0,4)+"-"+dateSource.substring(4,6)+"-"+dateSource.substring(6,8);
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
       df.setLenient(false);
       try {
           Date date= df.parse(dateStr);
           return (date!=null);
       } catch (ParseException e) {
           return false;
       }
   }
   
   public static String getNewIDCard(final String oldIDCard){
	   CarIdUtil.setWiBuffer();
       if(!checkIDCard(oldIDCard)){
           return oldIDCard;
       }
       String newIDCard = oldIDCard.substring(0, fPart);
       newIDCard += yearFlag;
       newIDCard += oldIDCard.substring(fPart, oldIDCard.length());
       String ch = getCheckFlag(newIDCard);
       newIDCard += ch;
       return newIDCard;
   }
   
   public static String getOldIDCard(final String newIDCard){
	   CarIdUtil.setWiBuffer();
       if(!checkIDCard(newIDCard)){
           return newIDCard;
       }
       String oldIDCard = newIDCard.substring(0,fPart)+
                   newIDCard.substring(fPart+yearFlag.length(),newIDCard.length()-1);
       return oldIDCard;
   }
   
   public static boolean checkIDCard(final String idCard){
	   CarIdUtil.setWiBuffer();
       boolean isNew = idCard.length()==15?false:true;
       //String message = "";
       if (!checkLength(idCard,isNew)){
           return false;
       }
       String idDate = getIDDate(idCard, isNew);
       if(!checkDate(idDate)){
           return false;
       }
       if(isNew){
           String checkFlag = getCheckFlag(idCard);
           String theFlag = idCard.substring(idCard.length()-1,idCard.length());
           if(!checkFlag.equals(theFlag)){
               return false;
           }
       }
       return true;
   }
   
   public static String getRandomIDCard(final boolean idNewID){
	   CarIdUtil.setWiBuffer();
       Random ran = new Random();
       String idCard = getAddressCode(ran)+getRandomDate(ran,idNewID)+getIDOrder(ran);
       if(idNewID){
           String ch = getCheckFlag(idCard);
           idCard += ch;
       }
       return idCard;
   }
   
   private static String getAddressCode(Random ran) {
       if(ran==null){
           return "";
       }else{
           int addrCode = minCode + ran.nextInt(maxCode-minCode);
           return Integer.toString(addrCode);
       }
   }
   
   private static String getRandomDate(Random ran, boolean idNewID) {
       if(ran==null){
           return "";
       }
       int year = 0;
       if(idNewID){
           year = 1900 + ran.nextInt(2007-1900);
       }else{
           year = 1 + ran.nextInt(99);
       }
       int month = 1+ran.nextInt(12);
       int day = 0;
       if(month==2){
           day= 1+ran.nextInt(28);
       }else if(month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12){
           day= 1+ran.nextInt(31);
       }else{
           day= 1+ran.nextInt(30);
       }
       NumberFormat nf = NumberFormat.getIntegerInstance();
       nf.setMaximumIntegerDigits(2);
       nf.setMinimumIntegerDigits(2);
       String dateStr = Integer.toString(year)+nf.format(month)+nf.format(day);
       return dateStr;
   }

   private static String getIDOrder(Random ran) {
       NumberFormat nf = NumberFormat.getIntegerInstance();
       nf.setMaximumIntegerDigits(3);
       nf.setMinimumIntegerDigits(3);
       if(ran==null){
           return "";
       }else{
           int order = 1+ran.nextInt(999);
           return nf.format(order);
       }
   }

   public CarIdUtil(){
       setWiBuffer();
   }
}
