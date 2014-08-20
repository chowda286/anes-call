package com.company;
import java.util.*;
import java.text.SimpleDateFormat;

public class Person
{
  private String name, rotation;
  private int rank, obCall, gorCall, totalCall;
  private int weekendCall, weekdayCall;
  private int friCall, satCall, sunCall;
  private boolean[] gorEligible = new boolean[31];
  private boolean[] obEligible = new boolean[31];
  private int[] callType = new int[31];
  private int[] callDayOfWeek = new int[31];
  private String[] callOrigin = new String[31];
  private int maxWeekendCall, maxSatSunCall, maxOBCall, maxTotalCall;
  private double callFactor=-1.0;
  private int callDayListCount = 0;
  private List<Integer> callDayList = new ArrayList<Integer>();
  
 public Person()
  {
    name="**NoName**";
    rank= obCall = gorCall = totalCall = weekendCall = weekdayCall = friCall = satCall = sunCall = 0;
    rotation="NoRotation";
    
    for(int cv=0;cv<gorEligible.length;cv++)
    {
      gorEligible[cv]=false;
      obEligible[cv]=false;
    }
  }
  
  
  public Person(String myName, String myRotation, boolean myobEligible, boolean mygorEligible, int myRank,int myMaxWeekendCall, int myMaxSatSunCall, int myMaxOBCall, int myMaxTotalCall)
  {
    name=myName;
    rank=myRank;
    rotation=myRotation;
    maxWeekendCall=myMaxWeekendCall;
    maxSatSunCall=myMaxSatSunCall;
    maxOBCall=myMaxOBCall;
    maxTotalCall=myMaxTotalCall;
    obCall = gorCall = totalCall = weekendCall = weekdayCall = friCall = satCall = sunCall = 0;
    
    for(int cv=0;cv<gorEligible.length;cv++)
    {
      gorEligible[cv]=mygorEligible;
      obEligible[cv]=myobEligible;
      callType[cv]=-1;
      callDayOfWeek[cv]=-1;
      callOrigin[cv]="None";
    }

  }
  
  public void reset(String myName, String myRotation, boolean myobEligible, boolean mygorEligible, int myRank,int myMaxWeekendCall, int myMaxSatSunCall, int myMaxOBCall, int myMaxTotalCall)
  {
    name=myName;
    rank=myRank;
    rotation=myRotation;
    maxWeekendCall=myMaxWeekendCall;
    maxSatSunCall=myMaxSatSunCall;
    maxOBCall=myMaxOBCall;
    maxTotalCall=myMaxTotalCall;
    obCall = gorCall = totalCall = weekendCall = weekdayCall = friCall = satCall = sunCall = 0;
    callFactor=-1.0;
    callDayListCount = 0;
    
    for(int cv=0;cv<gorEligible.length;cv++)
    {
      gorEligible[cv]=mygorEligible;
      obEligible[cv]=myobEligible;
      callType[cv]=-1;
      callDayOfWeek[cv]=-1;
      callOrigin[cv]="None";
    }

      callDayList = new ArrayList<Integer>();

  }
  
  public String getName()
  {
    return name;
  }
  
  public int getRank()
  {
    return rank;
  }
  
  public String getRotation()
  {
    return rotation;
  }
  
  public boolean getGOREligible(int day)
  {
    return gorEligible[day];
  }
  
   public boolean getOBEligible(int day)
  {
    return obEligible[day];
  }
  
 public int getCallType(int day)
  {
    return callType[day];
  }
 
 public int[] getCallType()
 {
   return callType;
 }
  
  public void setName(String newName)
  {
    name=newName;
  }
  
  public void setRank(int newRank)
  {
    rank=newRank;
  }
  
  public void setRotation(String newRotation)
  {
    rotation=newRotation;
  }
  
  public void setgorEligible(int day, boolean newgorEligible)
  {
    gorEligible[day]=newgorEligible;
  }
  
   public void setobEligible(int day, boolean newobEligible)
  {
    obEligible[day]=newobEligible;
  }
   
   public int getOBCall()
   {
     return obCall;
   }
   
   public void setMaxOBCall(int max)
   {
     maxOBCall=max;
   }

    public List<Integer> getCallDayList(){
        Collections.sort(callDayList);
        return callDayList; //sorted
    }
   public void setMaxTotalCall(int max)
   {
     maxTotalCall=max;
   }
   
   public int getGORCall()
   {
     return gorCall;
   }
   
   public int getTotalCall()
   {
     return totalCall;
   }
   
   public boolean isWeekendEligible()
   {
     return weekendCall<maxWeekendCall;
   }
   
   public boolean isSatSunEligible()
   {
      return (satCall+sunCall)<maxSatSunCall;
   }
   
   public boolean isOverallEligible()
   {
     return totalCall<maxTotalCall;
   }
   
   public int getMaxWeekendCall()
   {
     return maxWeekendCall;
   }
   
   public int getMaxSatSunCall()
   {
     return maxSatSunCall;
   }
   
   public int getMaxOBCall()
   {
     return maxOBCall;
   }
   public int getWeekendCall()
   {
     return weekendCall;
   }
   
   public int getWeekdayCall()
   {
     return weekdayCall;
   }
   
   public int getMaxTotalCall()
   {
     return maxTotalCall;
   }
 
  public int[] getCallDayOfWeek()
  {
    return callDayOfWeek;
  }
  
  public String[] getCallOrigin()
  {
    return callOrigin;
  }
  
  public boolean[] getGOREligible()
  {
    return gorEligible;
  }
  
  public boolean[] getOBEligible()
  {
    return obEligible;
  }
  
  public int getFriCall()
  {
     return friCall;
  }
  
  public int getSatCall()
  {
     return satCall;
  }
  
  public int getSunCall()
  {
     return sunCall;
  }
  
  public int getSatSunCall()
  {
     return satCall+sunCall;
  }
   
   public void incrementCall(int myCallType, int myDayOfWeek)
   {
     totalCall++;
     
     if(myCallType==0)
       obCall++;
     else if(myCallType>=1 && myCallType <=3)
       gorCall++;
     
     if(myDayOfWeek==1)
     {
        sunCall++;
        weekendCall++;
     }
     
     else if(myDayOfWeek==6)
     {
        friCall++;
        weekendCall++;
     }
     
     else if(myDayOfWeek==7)
     {
        satCall++;
        weekendCall++;
     }
     
     else
       weekdayCall++;
   }
  
  public void setCall(int day, int newCallType, int myDayOfWeek, String myOrigin)
  {
    callType[day]=newCallType;
    callDayOfWeek[day]=myDayOfWeek;
    callOrigin[day]=myOrigin;
    incrementCall(newCallType, myDayOfWeek);
    callDayList.add(day);
  }

  public void displayMyCall(double avgCall) throws java.io.IOException
  {
    System.out.println("\n*********************************************");
    System.out.println("Displaying call for " + name + ", Rotation: " + rotation + "\n");

    for(int day=1;day<callDayOfWeek.length;day++)
    {
      if(callType[day]>=0)
      {
        if(callType[day]==0)
          System.out.printf("%-15s %-8s %-15s %n", textDayOfWeek(callDayOfWeek[day]) + " " + day, "OB", callOrigin[day]);
        else
          System.out.printf("%-15s %-8s %-15s %n", textDayOfWeek(callDayOfWeek[day]) + " " + day, "OR " + callType[day], callOrigin[day]);
      }
    }
    
    System.out.printf("%-3s %-4s %-8s %-8s %-8s %-8s %-6s %-12s %n","OB", "GOR", "Weekday", "Fri", "Sat","Sun","Total", "Call Factor");
    System.out.printf("%-3s %-4s %-8s %-8s %-8s %-8s %-6s %-12s %n",obCall, gorCall, weekdayCall, friCall, satCall, sunCall, totalCall,getCallFactor());
  }
  
  
  
  public String textDayOfWeek(int d)
  {
    String[] myWeek={"**Sunday","Monday","Tuesday","Wednesday","Thursday","**Friday","**Saturday"};
    return myWeek[d-1];
  }
  
  public int getNumDayOfWeek(int d)
  {
     int count=0;
     for(int day=0;day<callDayOfWeek.length;day++)
     {
        if(callDayOfWeek[day]==d)
           count++;
     }
     return count;
  }
  
  public boolean[] deepCopy(boolean[] myObj)
  {
    boolean[] destination=new boolean[myObj.length];
    
    for(int cv=0;cv<myObj.length;cv++)
      destination[cv]=myObj[cv];
      
    return destination;
  }
  
  public int[] deepCopy(int[] myObj)
  {
    int[] destination=new int[myObj.length];
    
    for(int cv=0;cv<myObj.length;cv++)
      destination[cv]=myObj[cv];
    return destination;
  }
    public List<Integer> deepCopy(List<Integer> myObj)
    {
        List<Integer> destination=new ArrayList<Integer>();

        for(int cv=0;cv<myObj.size();cv++)
            destination.add(myObj.get(cv));
        return destination;
    }
    
  public String[] deepCopy(String[] myObj)
  {
    String[] destination=new String[myObj.length];
    
    for(int cv=0;cv<myObj.length;cv++)
      destination[cv]=myObj[cv];
      
    return destination;
  }
  
  public void deepCopy(Person[] source, int num)
  {
    name=source[num].getName();
    rotation=source[num].getRotation();
    rank=source[num].getRank();
    obCall=source[num].getOBCall();
    gorCall=source[num].getGORCall();
    totalCall=source[num].getTotalCall();
    weekendCall=source[num].getWeekendCall();
    friCall=source[num].getFriCall();
    satCall=source[num].getSatCall();
    sunCall=source[num].getSunCall();
    weekdayCall=source[num].getWeekdayCall();
    gorEligible=deepCopy(source[num].getGOREligible());
    obEligible=deepCopy(source[num].getOBEligible());
    callType=deepCopy(source[num].getCallType());
    callDayOfWeek=deepCopy(source[num].getCallDayOfWeek());
    callOrigin=deepCopy(source[num].getCallOrigin());
    callFactor=source[num].getCallFactor();
    maxWeekendCall=source[num].getMaxWeekendCall();
    maxOBCall=source[num].getMaxOBCall();
    maxTotalCall=source[num].getMaxTotalCall();
    callDayList=deepCopy(source[num].getCallDayList());
  }
  
  public double getCallFactor()
  {
    if(callFactor==-1.0)
    {
      callFactor=calcCallFactor();
    }
    
    return callFactor;
  }
 
  
  public double calcCallFactor()
  {
    double tempCallFactor=0.0;
    int satSun=0, fri=0;
    for(int day=1;day<callDayOfWeek.length;day++)
      {
        if(callType[day]>=0)
        {
          // Factor in day of week
          if(callDayOfWeek[day]==Calendar.SUNDAY)
          {
            tempCallFactor+=3.0;
            satSun++;
          }
          
          else if(callDayOfWeek[day]==Calendar.MONDAY)
            tempCallFactor+=1.0;
          
          else if(callDayOfWeek[day]==Calendar.TUESDAY)
            tempCallFactor+=1.0;
          
          else if(callDayOfWeek[day]==Calendar.WEDNESDAY)
            tempCallFactor+=1.0;
          
          else if(callDayOfWeek[day]==Calendar.THURSDAY)
            tempCallFactor+=1.0;
          
          else if(callDayOfWeek[day]==Calendar.FRIDAY)
          {
            tempCallFactor+=2.0;
            fri++;
          }
          
          else if(callDayOfWeek[day]==Calendar.SATURDAY)
          {
            tempCallFactor+=3.0;
            satSun++;
          }
          
          // Factor in type of call
          if(callType[day]==0)
            tempCallFactor+=0.0;
          
          else if(callType[day]>=1 && callType[day]<=3)
            tempCallFactor+=0.0;
        }   
        
        //  Penalty against 6 or more calls
        if(totalCall>=6)
           tempCallFactor+=2.0;
        
         // Penalty against 2 Sat/Sun calls
        if(satSun==2)
           tempCallFactor+=5.0;
        
        // Penalty against 3 or more Sat/Sun calls
        if(satSun>=3)
           tempCallFactor+=5.0;
        
        // Penalty against 2 Sat/Sun calls and 1 Friday call
        if(satSun>=2 && fri>=1)
           tempCallFactor+=3.0;

        //Penalty for number of q3/q4 calls
        if(getQCallInterval(3) > 0) {
            tempCallFactor = tempCallFactor + (getQCallInterval(3) * 4);
        }
          if(getQCallInterval(4) > 0) {
              tempCallFactor = tempCallFactor + (getQCallInterval(4) * 1);
          }

     
      }  
    
    callFactor=tempCallFactor;
    return tempCallFactor;
  }
  
  public static double avgCall(Person[] myResidents)
  {
    double sum=0.0;
    double offService=0.0;
    for(int cv=0;cv<myResidents.length;cv++)
    {
        sum=sum+myResidents[cv].getTotalCall();
        
        if (myResidents[cv].getTotalCall()==0)
          offService++;
    }
    
    return Math.round(sum/(myResidents.length-offService));
  }
  
  public boolean isSatOnlyRotation()
  {
     return (rotation=="UMRAS" ||
             rotation=="Pain" ||
             rotation=="OrthoPain" ||
             rotation=="Echo" ||
             rotation=="Kernan" ||
             rotation=="NeuroMon" ||
             rotation=="VA" ||
             rotation=="ORMgmt" ||
             rotation==""
             );
  }
  
  public boolean isOffService()
  {
     return (rotation=="Trauma" ||
             rotation=="SICU" ||
             rotation=="CSICU" ||
             rotation=="PGY1" 
             );
  }


public int getQCallInterval(int interval) {
    int count=0;
    List<Integer> calldays = new ArrayList<Integer>();
    calldays = getCallDayList();
        for (int j=0; j < calldays.size()-1;){
            j++;
            //If the difference between two adjacent calls are = to qcall then count
            if (calldays.get(j) - calldays.get(j-1) == interval)
                count++;
        }
    return count; //returns the number of q 'interval' calls
}

}
