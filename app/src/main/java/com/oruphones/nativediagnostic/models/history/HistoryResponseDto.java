package com.oruphones.nativediagnostic.models.history;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.oruphones.nativediagnostic.models.DeviceInformation;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.util.BaseUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class HistoryResponseDto {
    @SerializedName("sessionHistoryId")
    private long sessionHistoryId ;

    @SerializedName("sessionStatus")
    private String sessionStatus ;
    @SerializedName("startDateTime")
    private long startDateTime ;

    @SerializedName("endDateTime")
    private long endDateTime ;

    @SerializedName("message")
    private String message ;

   @SerializedName("eCommandHistoryDtoList")
    private List<HistoryCommandResponseDto> mCommandList;


    @SerializedName("deviceStatus")
    private String deviceStatus ;

    @SerializedName("deviceMake")
    private String deviceMake ;

    @SerializedName("deviceModel")
    private String deviceModel ;

    @SerializedName("deviceFirmware")
    private String deviceFirmware ;

    @SerializedName("uniqueDeviceID")
    private String uniqueDeviceID ;


    public boolean isValidTransaction(){
       return mCommandList!=null&&!mCommandList.isEmpty();
   }

   public HistoryInfo getRespectiveInfo(List<TestInfo> listTest){
       HistoryInfo historyInfo = new HistoryInfo(sessionHistoryId,startDateTime, BaseUtils.DateUtil.format(endDateTime, BaseUtils.DateUtil.DateFormats.dd_MM_yyyy_HH_mm_Slash));
       HashMap<String, TestInfo> testInfoHashMap= new HashMap<>();
       List<TestInfo> list= new ArrayList<>();
       for (HistoryCommandResponseDto h : mCommandList) {
           TestInfo testInfo =findTest(listTest,h.getCommandName());
           if(testInfo==null)
               continue;
           testInfo = h.getRespectiveTestInfo(testInfo);
           testInfoHashMap.put(h.getCommandName(),testInfo);
           list.add(testInfo);
       }

       historyInfo.updateTestPassFailCount(list);
       historyInfo.setManualTestResult(testInfoHashMap);

       HashMap<String,String> messageKeyPair= getMessageMap();
       historyInfo.setCatagoryName(messageKeyPair.get("IssueFlow"));
       historyInfo.setDeviceInformation(new DeviceInformation(deviceMake,deviceModel,deviceFirmware,messageKeyPair.get("OSVersion"),uniqueDeviceID));
       return historyInfo;
   }

   /*"Model=Realme 2 Pro|certified=true|apiVersion=3.2.190903|capacity=64000000000|serialNo=|OSVersion=10|lastRestart=1609797389475|totalRAM=5986537472|SOH=-1|BatteryHealth=NA|DeviceLocale=en|ModelName=|InitAvlInternalStorage=17702273024|InitAvlRAM=2654965760|AvlInternalStorage=17702273024|AvlRAM=2654965760|IssueFlow=Apps|SummaryFileUrl=https://diagnosticsreports.s3.sa-east-1.amazonaws.com/ssd/Telefonica/864132040680494,864132040680486_20210111084155000.jpeg"*/
   private HashMap<String,String> getMessageMap(){
       if(TextUtils.isEmpty(message))
           return new HashMap<>();
       StringTokenizer st = new StringTokenizer(message, "|");
       HashMap<String,String> messagePairMap = new HashMap<>();
       while (st.hasMoreTokens()) {
           String messagePair = st.nextToken();

           int indexOfAssignment = messagePair.indexOf("=");
           String key  =  messagePair.substring(0,indexOfAssignment);
           String value  =  messagePair.substring(indexOfAssignment+1);
           messagePairMap.put(key,value);
       }
       return  messagePairMap;
   }

   public TestInfo findTest(List<TestInfo> listTest, String testName){
       for (TestInfo testInfo:listTest) {
           if(testInfo==null)
               continue;
           if(testName.equalsIgnoreCase(testInfo.getName())){
               try {
                   return (TestInfo) testInfo.clone();
               } catch (CloneNotSupportedException e) {
                   e.printStackTrace();
               }
           }
       }

       if(!TextUtils.isEmpty(testName)){
           return new TestInfo(testName,testName);
       }

       return null;
   }

// "sessionStatus": "Success",


}
