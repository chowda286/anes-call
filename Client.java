package com.company;
/*
 * Currently working on:
 * 1) Tweeking weights of weekend call and weights of deviation from avg call factor
 * - If assigned 2 Sat/Sun calls, then ineligible for anymore Fridays.  Complicated bc if already has a Friday call, then only can have 1 Sat/Sun
 *   Maybe workaround by pre-filling Sat/Sun first, then pre-fill Fri.
 * 
 * 
 * */

import java.util.*;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Client {
    int maxqcalls = 3;
    public static void main(String args[]) throws java.io.IOException {
        // Create an array of persons and names
        String[] name1 = {"Ayoola", "Chancer", "Chriss", "Devita", "Formanek", "Henderson", "Running", "Schorr", "Srinivas", "Wallace", "Watterworth", "Yu"};
        String[] rotation1 = {"VascTx", "General", "General", "General", "VA", "General", "OffFloor", "OrthoPain", "General", "General", "General", "PACUGen"};
        boolean[] obEligible1 = {false, false, false, false, false, false, false, false, false, false, false, false};
        boolean[] gorEligible1 = {true, true, true, true, true, true, true, true, true, true, true, true};
        int[] rank1 = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        String[] name2 = {"Cardillo", "Chow", "Cunniff", "Humsi", "Kiefer", "Kokajko", "Lofton", "Neary", "Nwosu", "Pierre", "SmithE", "Wright"};
        String[] rotation2 = {"Neuro", "Peds", "SICU", "OB", "Trauma", "ChronicPain", "SICU", "Kernan", "Cardiac", "OB1", "Kernan", "Peds"};
        boolean[] obEligible2 = {true, true, false, false, false, true, false, false, false, false, false, true};
        boolean[] gorEligible2 = {true, true, false, true, false, true, false, true, true, true, true, true};
        int[] rank2 = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};

        String[] name3 = {"Chettiar", "Jeyakumar", "Kim", "Le", "Neely", "Nguyen", "Ofosu", "Patel", "Shteyman", "Watanaskul", "Williams", "Zhu"};
        String[] rotation3 = {"Neuro", "Trauma", "ChronicPain", "Thoracic", "Peds", "Cardiac", "OB", "NeuroMon", "General", "Cardiac", "Peds", "UMRAS"};
        boolean[] obEligible3 = {true, false, true, true, true, true, true, true, true, true, true, true};
        boolean[] gorEligible3 = {true, false, true, true, true, true, false, true, true, true, true, true};
        int[] rank3 = {3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};


        Connection conn = null;

        try {
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost/calendar?" +
                            "user=anes&password=anes");

            // Do something with the Connection


        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        ////////////////////////////////////////
        // All inputs by the user go here
        int iMonth = Calendar.SEPTEMBER;
        int iYear = 2014;

        int maxWeekendCall = 3;
        int maxSatSunCall = 2;
        int maxTotalCall = 6;
        int maxOBCall = 5;

        int repeatCount = 10000;

        ////////////////////////////////////////

        String[] name = mergeArrays(name1, name2, name3);
        String[] rotation = mergeArrays(rotation1, rotation2, rotation3);
        boolean[] obEligible = mergeArrays(obEligible1, obEligible2, obEligible3);
        boolean[] gorEligible = mergeArrays(gorEligible1, gorEligible2, gorEligible3);
        int[] rank = mergeArrays(rank1, rank2, rank3);

        ///////// Initialize calendar /////////////

        // Current month

        int days = calcDaysInMonth(iMonth, iYear);

        // Create array that stores each day of the week for days 1-31
        int[] myCalendar = new int[days + 1];
        myCalendar = calcDayOfWeek(iMonth, iYear);

        // Create array that stores all weekend calls and Saturday calls for the month
        int[] friSatSun = new int[15];
        int[] sat = new int[5];
        int[] monThurs = new int[20];
        friSatSun = generateFriSatSun(myCalendar, friSatSun);  // List of all fri, sat, sun calls for the month
        sat = generateSat(myCalendar, sat);  // List of all sat calls for the month
        monThurs = generateMonThurs(myCalendar, monThurs);  // List of all mon-thurs calls for the month

        ///////// Initialize residents /////////////

        // Initialize residents
        Person[] Residents = new Person[name.length];
        Person[] efficientResidents = new Person[name.length];


        for (int cv = 0; cv < name.length; cv++) {
            Residents[cv] = new Person(name[cv], rotation[cv], obEligible[cv], gorEligible[cv], rank[cv], maxWeekendCall, maxSatSunCall, maxOBCall, maxTotalCall);
            efficientResidents[cv] = new Person(name[cv], rotation[cv], obEligible[cv], gorEligible[cv], rank[cv], maxWeekendCall, maxSatSunCall, maxOBCall, maxTotalCall);
        }

        ///////// Initialize call schedule /////////////

        // Create empty call schedule
        double deviation = 1000000.0, minDeviation = 1000000.0;
        String[][] callSchedule = new String[days + 1][4];
        String[][] efficientCallSchedule = new String[days + 1][4];
        int efficientCount = -1;

        for (int count = 0; count < repeatCount; count++)  // Make x number of versions of the randomly generated call schedule.
        {

            // Initialize the call schedule
            for (int cv = 1; cv <= days; cv++) {
                for (int cv2 = 0; cv2 <= 3; cv2++) {
                    callSchedule[cv][cv2] = "**NoName**";
                }
            }

            // Pre-fill call schedule
            callSchedule = preFillElective(Residents, callSchedule, myCalendar, sat, 1, "VA");
            callSchedule = preFillElective(Residents, callSchedule, myCalendar, sat, 1, "Kernan");
            callSchedule = preFillElective(Residents, callSchedule, myCalendar, sat, 1, "NeuroMon");
            callSchedule = preFillElective(Residents, callSchedule, myCalendar, sat, 1, "UMRAS");
            callSchedule = preFillElective(Residents, callSchedule, myCalendar, sat, 1, "ChronicPain");
            //callSchedule = preFillOB(Residents,callSchedule,myCalendar,friSatSun,1);
            callSchedule = preFillOB(Residents, callSchedule, myCalendar, monThurs, 4);
            callSchedule = preFillWeekend(Residents, callSchedule, myCalendar, friSatSun);

            //    if(count==0)
            //       displayCall(callSchedule, myCalendar);

            callSchedule = calculateCall(Residents, callSchedule, days, myCalendar);

            deviation = deviationTotalCall(Residents, myCalendar);

            if (deviation < minDeviation && isScheduleFilled(callSchedule, days) == true) {
                System.out.println("Found more efficient schedule- Version: " + count + " Deviation: " + deviation);
                efficientCallSchedule = copy(callSchedule);

                efficientCount = count;
                for (int cv = 0; cv < Residents.length; cv++) {
                    efficientResidents[cv].deepCopy(Residents, cv);
                }
                minDeviation = deviation;
            }

// Reset arrays
            for (int cv = 0; cv < Residents.length; cv++)
                Residents[cv].reset(name[cv], rotation[cv], obEligible[cv], gorEligible[cv], rank[cv], maxWeekendCall, maxSatSunCall, maxOBCall, maxTotalCall);

        }

        // Display resident info.
        System.out.println("\nCreated " + repeatCount + " versions of call schedule");
        System.out.println("\nMost efficient version: " + efficientCount + " Deviation " + minDeviation);
        displayCall(efficientCallSchedule, myCalendar);
        //displayInfo(Residents);
        //displayCalendar(myCalendar);

        displayStats(efficientResidents, myCalendar, avgCall(efficientResidents));

        displayAllCall(efficientResidents, avgCall(efficientResidents));

        displayMyCall("Ofosu", efficientResidents, avgCall(efficientResidents));
        //displayMyCall("Chancer",efficientResidents, avgCall(efficientResidents));
        //displayMyCall("Neary",efficientResidents, avgCall(efficientResidents));

    }

    public static int[] calcDayOfWeek(int myMonth, int myYear) {
        int[] dayOfWeek = new int[calcDaysInMonth(myMonth, myYear) + 1];
        Calendar c = new GregorianCalendar();

        c.set(myYear, myMonth, 1);
        int maxDays = calcDaysInMonth(myMonth, myYear); // Get maximum number of days in the month

        for (int myDay = 1; myDay <= maxDays; myDay++) {
            c.set(myYear, myMonth, myDay);
            dayOfWeek[myDay] = c.get(Calendar.DAY_OF_WEEK);
        }

        //  Account for overflow days (day 0, and days past 30 when there are only 30 days)
        dayOfWeek[0] = -1;

        for (int overflow = maxDays + 1; overflow < dayOfWeek.length; overflow++) {
            dayOfWeek[overflow] = -1;
        }

        return dayOfWeek;
    }

    public static int calcDaysInMonth(int myMonth, int myYear) {
        Calendar c = new GregorianCalendar();
        c.set(myYear, myMonth, 1);

        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int[] generateFriSatSun(int[] myCalendar, int[] myFriSatSun) {
        int count = 0;

        for (int day = 1; day < myCalendar.length; day++) {
            if (myCalendar[day] == 1 || myCalendar[day] == 6 || myCalendar[day] == 7) {
                myFriSatSun[count] = day;
                count++;
            }
        }

        for (int cv = count; cv < myFriSatSun.length; cv++)
            myFriSatSun[cv] = -1;

        return myFriSatSun;
    }

    public static int[] generateSat(int[] myCalendar, int[] mySat) {
        int count = 0;

        for (int day = 1; day < myCalendar.length; day++) {
            if (myCalendar[day] == 7) {
                mySat[count] = day;
                count++;
            }
        }

        for (int cv = count; cv < mySat.length; cv++)
            mySat[cv] = -1;

        return mySat;
    }

    public static int[] generateMonThurs(int[] myCalendar, int[] myMonThurs) {
        int count = 0;

        for (int day = 1; day < myCalendar.length; day++) {
            if (myCalendar[day] >= 2 && myCalendar[day] <= 5) {
                myMonThurs[count] = day;
                count++;
            }
        }

        for (int cv = count; cv < myMonThurs.length; cv++)
            myMonThurs[cv] = -1;

        return myMonThurs;
    }

    public static boolean isWeekend(int[] myCalendar, int myDay) {
        if (myCalendar[myDay] == 1 || myCalendar[myDay] == 6 || myCalendar[myDay] == 7)
            return true;

        else
            return false;
    }

    public static boolean isSatSun(int[] myCalendar, int myDay) {
        if (myCalendar[myDay] == 1 || myCalendar[myDay] == 7)
            return true;

        else
            return false;
    }

    public static boolean isFri(int[] myCalendar, int myDay) {
        if (myCalendar[myDay] == 6)
            return true;

        else
            return false;
    }


    public static void displayCalendar(int[] myCalendar) throws java.io.IOException {
        for (int day = 0; day < myCalendar.length; day++)
            System.out.println("Day " + day + " is a " + myCalendar[day]);
    }


    public static void displayInfo(Person[] myResidents) throws java.io.IOException {
        for (int cv = 0; cv < myResidents.length; cv++)
            System.out.println(myResidents[cv].getName() + "  CA" + myResidents[cv].getRank() + "  " + myResidents[cv].getRotation());
    }

    public static String[][] preFillWeekend(Person[] myResidents, String[][] myCall, int[] myCalendar, int[] myDaysOfWeek) throws java.io.IOException {
        int[] order = new int[myResidents.length];
        int residentChosen = -1;

        //  Cycle through each day of the month
        for (int day = 0; day < myDaysOfWeek.length; day++) {
            if (myDaysOfWeek[day] != -1) {
                order = shuffle(myResidents);  // Randomly shuffle the order in which residents will be selected for call.

                for (int callType = 0; callType <= 3; callType++)  // Cycle through OB, OR1, OR2, and OR3 call on that day
                {
                    for (int cv = 0; cv < order.length; cv++) {
                        if (callRules(myResidents, myCalendar, myCall, order[cv], myDaysOfWeek[day], callType) == true)   // Check to see if the resident meets criteria for call.
                        {
                            residentChosen = recursive(myResidents, myCalendar, myCall, order, myDaysOfWeek[day], callType, cv);
                            break;
                        }
                    }

                    if (residentChosen >= 0) { //resident chosen after going through recursive function
                        myCall[myDaysOfWeek[day]][callType] = myResidents[order[residentChosen]].getName();  // Assign that resident to call
                        maxQ3(myResidents[order[residentChosen]], myDaysOfWeek[day]); // Make this resident ineligible for call for the next 2 days
                        myResidents[order[residentChosen]].setCall(myDaysOfWeek[day], callType, myCalendar[myDaysOfWeek[day]], "PreFill Weekend");  // Update this resident's call list for the month.
                    }

                    residentChosen = -1;  // Reset the residentChosen to the starting value of -1 (no resident chosen).
                }
            }
        }

        return myCall;
    }

    public static String[][] preFillElective(Person[] myResidents, String[][] myCall, int[] myCalendar, int[] myDaysOfWeek, int numCalls, String myRotation) throws java.io.IOException {
        int[] day = new int[myDaysOfWeek.length];
        day = shuffle(myDaysOfWeek);
        int[] callTypeList = shuffle(new int[]{0, 1, 2, 3});
        int dayChosen = -1;
        int callTypeChosen = -1;

        for (int res = 0; res < myResidents.length; res++)  // Go through each resident.
        {
            if (myResidents[res].getRotation() == myRotation)  // Find a resident on the specified rotation.
            {
                for (int cv = 0; cv < myCalendar.length; cv++)   // Set all days as ineligible except for Saturday
                {
                    if (myCalendar[cv] != 7) {
                        myResidents[res].setgorEligible(cv, false);
                        myResidents[res].setobEligible(cv, false);
                    }
                }

                myResidents[res].setMaxTotalCall(numCalls);

                dayChosen = -1;
                callTypeChosen = -1;

                day = shuffle(myDaysOfWeek);
                callTypeList = shuffle(new int[]{0, 1, 2, 3});

                for (int count = 1; count <= numCalls; count++) {
                    outerLoop:
                    for (int rand = 0; rand < day.length; rand++)  // Go through each randomly selected day.
                    {
                        if (day[rand] >= 1)  // Make sure that rand is filled with a valid date (not -1)
                        {
                            for (int randType = 0; randType <= 3; randType++) {
                                if (callRules(myResidents, myCalendar, myCall, res, day[rand], callTypeList[randType]) == true) {
                                    dayChosen = day[rand];
                                    callTypeChosen = callTypeList[randType];
                                    break outerLoop;
                                }
                            }
                        }
                    }

                    if (dayChosen >= 0) {
                        myCall[dayChosen][callTypeChosen] = myResidents[res].getName();  // Add the resident's name to the call schedule
                        maxQ3(myResidents[res], dayChosen); // Make this resident ineligible for call for the next 2 days
                        myResidents[res].setCall(dayChosen, callTypeChosen, myCalendar[dayChosen], "PreFill" + myRotation);  // Update this resident's call list for the month
                    }
                }
            }
        }

        return myCall;
    }

    public static String[][] preFillOB(Person[] myResidents, String[][] myCall, int[] myCalendar, int[] myDaysOfWeek, int numCalls) throws java.io.IOException {
        int[] day = new int[myDaysOfWeek.length];
        day = shuffle(myDaysOfWeek);
        int dayChosen = -1;

        for (int res = 0; res < myResidents.length; res++)  // Go through each resident.
        {

            if (myResidents[res].getRotation() == "OB")  // Find a resident on the OB rotation.
            {
                myResidents[res].setMaxOBCall(myResidents[res].getMaxTotalCall());
                dayChosen = -1;
                day = shuffle(myDaysOfWeek);
                for (int count = 1; count <= numCalls; count++) {
                    for (int rand = 0; rand < day.length; rand++)  // Go through each randomly selected day.
                    {
                        if (day[rand] >= 1)  // Make sure that rand is filled with a valid date (not -1)
                        {
                            if (callRules(myResidents, myCalendar, myCall, res, day[rand], 0) == true) {
                                dayChosen = day[rand];
                                break;
                            }
                        }
                    }

                    if (dayChosen >= 0) {
                        myCall[dayChosen][0] = myResidents[res].getName();  // Add the resident's name to the call schedule
                        maxQ3(myResidents[res], dayChosen); // Make this resident ineligible for call for the next 2 days
                        myResidents[res].setCall(dayChosen, 0, myCalendar[dayChosen], "OB Pre-Fill");  // Update this resident's call list for the month
                    }
                }
            }
        }

        return myCall;
    }

    public static void displayInt(int[] myInt) throws java.io.IOException {
        for (int cv = 0; cv < myInt.length; cv++)
            System.out.println(myInt[cv]);
    }

    public static String[][] calculateCall(Person[] myResidents, String[][] myCall, int numDays, int[] myCalendar) throws java.io.IOException {
        int[] order = new int[myResidents.length];
        int residentChosen = -1;

        //  Cycle through each day of the month
        for (int day = 1; day <= numDays; day++) {
            order = shuffle(myResidents);  // Randomly shuffle the order in which residents will be selected for call.

            for (int callType = 0; callType <= 3; callType++)  // Cycle through OB, OR1, OR2, and OR3 call on that day
            {
                for (int cv = 0; cv < order.length; cv++) {
                    if (callRules(myResidents, myCalendar, myCall, order[cv], day, callType) == true)   // Check to see if the resident meets criteria for call.
                    {
                        residentChosen = recursive(myResidents, myCalendar, myCall, order, day, callType, cv);
                        break;
                    }
                }

                if (residentChosen >= 0) {
                    myCall[day][callType] = myResidents[order[residentChosen]].getName();  // Assign that resident to call
                    maxQ3(myResidents[order[residentChosen]], day); // Make this resident ineligible for call for the next 2 days
                    myResidents[order[residentChosen]].setCall(day, callType, myCalendar[day], "Pool");  // Update this resident's call list for the month.
                }

                residentChosen = -1;  // Reset the residentChosen to the starting value of -1 (no resident chosen).
            }
        }

        return myCall;
    }


    public static int recursive(Person[] myResidents, int[] myCalendar, String[][] myCall, int[] order, int day, int callType, int startPos) {
        // This function will attempt to balance the call schedule as it is made

        for (int newPos = startPos + 1; newPos < order.length; newPos++) {
            List<Integer> calldaylist = myResidents[order[startPos]].getCallDayList();
            if ((callRules(myResidents, myCalendar, myCall, order[newPos], day, callType) == true)  //  Make sure that resident meets call rules
                    &&
                    ((myResidents[order[newPos]].getTotalCall() < myResidents[order[startPos]].getTotalCall() && !isWeekend(myCalendar, day))
                            || isWeekend(myCalendar, day))  // Check if someone has less total call only if it's not a weekend
                    &&
                    ((callType == 0 && myResidents[order[newPos]].getOBCall() < myResidents[order[startPos]].getOBCall() && !isWeekend(myCalendar, day))
                            || callType > 0               // Check if someone has less OB call
                            || isWeekend(myCalendar, day)) // only if it's not a weekend
                    &&
                    ((isWeekend(myCalendar, day) && myResidents[order[newPos]].getWeekendCall() < myResidents[order[startPos]].getWeekendCall())
                            || !isWeekend(myCalendar, day)) // Check if someone has less weekend call
                    &&
                    ((day - calldaylist.get(calldaylist.size()-1) == 3) && myResidents[order[newPos]].getQCallInterval(3) < myResidents[order[startPos]].getQCallInterval(3) && !isWeekend(myCalendar, day)) //check if less Q3 calls
                    ) {
                startPos = newPos;

                return recursive(myResidents, myCalendar, myCall, order, day, callType, startPos);
            }
        }

        return startPos; //initial resident is best resident
    }

    public static boolean callRules(Person[] myResidents, int[] myCalendar, String[][] myCall, int resNum, int day, int callType) {
        return (
                (myCall[day][callType] == "**NoName**")                 // Verify that call slot is empty
                        &&
                        ((callType == 0 && myResidents[resNum].getOBEligible(day)) ||     // Check if OB eligible
                                (callType > 0 && myResidents[resNum].getGOREligible(day))      // or GOR eligible on that day
                        )
                        &&
                        (myResidents[resNum].getCallType(day) == -1)            // Resident is not already assigned to call
                        &&
                        (myResidents[resNum].isOverallEligible())             // Don't exceed max total calls for month
                        &&
                        ((callType == 0 && myResidents[resNum].getOBCall() < myResidents[resNum].getMaxOBCall()) // Don't exceeded max OB calls for month
                                || callType > 0)                                                                    // If not an OB call, do nothing
                        &&
                        ((isWeekend(myCalendar, day) && myResidents[resNum].isWeekendEligible()) || // Don't exceed max weekend calls for month
                                !isWeekend(myCalendar, day))                                             // If not a weekend, do nothing
                        &&
                        ((isSatSun(myCalendar, day) && myResidents[resNum].isSatSunEligible()) || // Don't exceed max SatSun calls for month
                                !isSatSun(myCalendar, day))                                             // If not a weekend, do nothing
                        &&
                        ((callType == 0) ||                                          // OB call, do nothing
                                ((callType == 1 && myResidents[resNum].getRank() >= 2)) ||  // OR 1 call must be a CA-2 or CA-3
                                ((callType == 2 && myResidents[resNum].getRank() >= 1)) ||  // OR 2 call must be a CA-2 or CA-3
                                ((callType == 3 && myResidents[resNum].getRank() >= 1))     // OR 3 call must be a CA-1
                        )
                                == true
        );
    }


    public static int[] shuffle(Person[] myResidents) {
        int A[] = new int[myResidents.length];

        Random rand = new Random();

        for (int cv = 0; cv < myResidents.length; cv++) {
            A[cv] = rand.nextInt(myResidents.length);

            while (search(A, myResidents, cv) == 1) {
                A[cv] = rand.nextInt(myResidents.length);
            }
        }

        return A;
    }

    public static int search(int myArray[], Person[] myResidents, int pos) {
        int flag = 0;

        if (myArray[pos] >= myResidents.length)
            flag = 1;

        for (int cv = 0; cv < pos; cv++) {
            if (myArray[cv] == myArray[pos]) {
                flag = 1;
                break;
            }
        }

        return flag;
    }

    public static int[] shuffle(int[] myInt) {
        int A[] = new int[myInt.length];
        int B[] = new int[myInt.length];

        Random rand = new Random();

        for (int cv = 0; cv < myInt.length; cv++) {
            A[cv] = rand.nextInt(myInt.length);

            while (search(A, myInt, cv) == 1) {
                A[cv] = rand.nextInt(myInt.length);
            }
        }

        for (int cv1 = 0; cv1 < myInt.length; cv1++)
            B[A[cv1]] = myInt[cv1];

        return B;
    }

    public static int search(int myArray[], int[] myInt, int pos) {
        int flag = 0;

        if (myArray[pos] >= myInt.length)
            flag = 1;

        for (int cv = 0; cv < pos; cv++) {
            if (myArray[cv] == myArray[pos]) {
                flag = 1;
                break;
            }
        }

        return flag;
    }

    public static void displayOrder(int A[]) throws java.io.IOException {
        int cv = 0;
        System.out.println(A[cv] + " " + A[cv + 1] + " " + A[cv + 2] + " " + A[cv + 3] + " " + A[cv + 4] + " " + A[cv + 5] + " " + A[cv + 6] + " " + A[cv + 7] + " " + A[cv + 8] + " " + A[cv + 9] + " " + A[cv + 10] + " " + A[cv + 11] + " " + A[cv + 12]);
    }


    public static void maxQ3(Person singleResident, int myDay) {

            if (myDay + 2 < singleResident.getCallType().length) {
                singleResident.setgorEligible(myDay + 2, false);
                singleResident.setobEligible(myDay + 2, false);
            }

            if (myDay + 1 < singleResident.getCallType().length) {
                singleResident.setgorEligible(myDay + 1, false);
                singleResident.setobEligible(myDay + 1, false);
            }
            // Make ineligible for previous 2 days
            if (myDay - 1 >= 0) {
                singleResident.setgorEligible(myDay - 1, false);
                singleResident.setobEligible(myDay - 1, false);
            }

             if (myDay - 2 >= 0) {
                 singleResident.setgorEligible(myDay - 2, false);
                singleResident.setobEligible(myDay - 2, false);
            }


    }

    public static String[] mergeArrays(String[] a1, String[] a2, String[] a3) {
        int cv;
        String[] myString = new String[a1.length + a2.length + a3.length];
        for (cv = 0; cv < a1.length; cv++)
            myString[cv] = a1[cv];

        for (cv = 0; cv < a2.length; cv++)
            myString[cv + a1.length] = a2[cv];

        for (cv = 0; cv < a3.length; cv++)
            myString[cv + a1.length + a2.length] = a3[cv];

        return myString;
    }

    public static boolean[] mergeArrays(boolean[] a1, boolean[] a2, boolean[] a3) {
        int cv;
        boolean[] myBoolean = new boolean[a1.length + a2.length + a3.length];
        for (cv = 0; cv < a1.length; cv++)
            myBoolean[cv] = a1[cv];

        for (cv = 0; cv < a2.length; cv++)
            myBoolean[cv + a1.length] = a2[cv];

        for (cv = 0; cv < a3.length; cv++)
            myBoolean[cv + a1.length + a2.length] = a3[cv];

        return myBoolean;
    }

    public static int[] mergeArrays(int[] a1, int[] a2, int[] a3) {
        int cv;
        int[] myInt = new int[a1.length + a2.length + a3.length];
        for (cv = 0; cv < a1.length; cv++)
            myInt[cv] = a1[cv];

        for (cv = 0; cv < a2.length; cv++)
            myInt[cv + a1.length] = a2[cv];

        for (cv = 0; cv < a3.length; cv++)
            myInt[cv + a1.length + a2.length] = a3[cv];

        return myInt;
    }


    public static void displayCall(String[][] myCall, int[] myCalendar) throws java.io.IOException {
        System.out.println("\n");
        System.out.printf("%-15s %-12s %-12s %-12s %-12s %n", "Day", "OB Call", "OR 1", "OR 2", "OR 3");
        System.out.println("---------------------------------------------------------------------");

        for (int day = 1; day < myCall.length; day++) {
            System.out.printf("%-15s %-12s %-12s %-12s %-12s %n", textDayOfWeek(myCalendar[day]) + " " + day, myCall[day][0], myCall[day][1], myCall[day][2], myCall[day][3]);
        }
    }

    public static String textDayOfWeek(int d) {
        String[] myWeek = {"**Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "**Friday", "**Saturday"};
        return myWeek[d - 1];
    }


    public static void displayMyCall(String myName, Person[] myResident, double avgCall) throws java.io.IOException {
        boolean found = false;

        for (int cv = 0; cv < myResident.length; cv++) {
            if (myResident[cv].getName() == myName) {
                myResident[cv].displayMyCall(avgCall);
                found = true;
            }
        }

        if (found == false)
            System.out.println("No resident name: '" + myName + "' found");
    }


    public static void displayGOREligible(Person[] myResidents, int day) throws java.io.IOException {
        System.out.println("Eligible for GOR call on day " + day);
        for (int cv = 0; cv < myResidents.length; cv++)
            System.out.println(myResidents[cv].getName() + " " + myResidents[cv].getGOREligible(day));
    }

    public static void displayOBEligible(Person[] myResidents, int day) throws java.io.IOException {
        System.out.println("Eligible for OB call on day " + day);
        for (int cv = 0; cv < myResidents.length; cv++)
            System.out.println(myResidents[cv].getName() + " " + myResidents[cv].getOBEligible(day));
    }

    public static void displayStats(Person[] myResidents, int[] myCalendar, double avgCall) throws java.io.IOException {
        System.out.println("\n*************Call Statistics***************\n");

        System.out.printf("%-10s %-8s %-8s %-8s %n", "Frequency", "Total", "OB", "Weekend");
        System.out.println("-------------------------------------------");

        for (int calls = 0; calls <= 6; calls++) {
            System.out.printf("%-10s %-8s %-8s %-8s %n", calls + " calls", freqCall(myResidents, calls, 1), freqCall(myResidents, calls, 0), freqCall(myResidents, calls, 4));
        }

        System.out.println("Average call: " + avgCall(myResidents));
        System.out.println("Average call factor: " + getAverageCallFactor(myResidents, myCalendar, getAllCallFactor(myResidents, myCalendar)));
       /*
        System.out.println("Q3 Calls: " + getCallInterval(myCalendar,3));
        System.out.println("Q4 Calls: " + getCallInterval(myCalendar,4));
        System.out.println("Q5 Calls: " + getCallInterval(myCalendar,5));
       */
        System.out.println("Total Deviation: " + deviationTotalCall(myResidents, myCalendar));
        System.out.println("Total Number of Q1 Calls: " + totalNumQCallInterval(myResidents, 1));
        System.out.println("Total Number of Q2 Calls: " + totalNumQCallInterval(myResidents, 2));
        System.out.println("Total Number of Q3 Calls: " + totalNumQCallInterval(myResidents, 3));
        System.out.println("Total Number of Q4 Calls: " + totalNumQCallInterval(myResidents, 4));
        System.out.println("Total Number of Q5 Calls: " + totalNumQCallInterval(myResidents, 5));
        //distWeekendCall(myResidents);
        //distTotalCall(myResidents);
        //distOBCall(myResidents);

    }

    public static int freqCall(Person[] myResidents, int freq, int toggle) {
    /* *
     * Toggle 0: OB Call
     * Toggle 1: Total Call
     * Toggle 4: Weekend Call
     * */

        int count = 0;
        for (int cv = 0; cv < myResidents.length; cv++) {
            if (myResidents[cv].getTotalCall() == freq && toggle == 1)
                count++;

            else if (myResidents[cv].getOBCall() == freq && toggle == 0)
                count++;

            else if (myResidents[cv].getWeekendCall() == freq && toggle == 4)
                count++;
        }

        return count;
    }


    public static int minCall(Person[] myResidents) {
        int min = 10;
        for (int cv = 0; cv < myResidents.length; cv++)
            if (myResidents[cv].getTotalCall() < min && myResidents[cv].getTotalCall() > 0)
                min = myResidents[cv].getTotalCall();

        return min;
    }

    public static int maxCall(Person[] myResidents) {
        int max = 0;
        for (int cv = 0; cv < myResidents.length; cv++)
            if (myResidents[cv].getTotalCall() > max)
                max = myResidents[cv].getTotalCall();

        return max;
    }

    public static double avgCall(Person[] myResidents) {
        double sum = 0.0;
        double offService = 0.0;
        for (int cv = 0; cv < myResidents.length; cv++) {
            sum = sum + myResidents[cv].getTotalCall();

            if (myResidents[cv].getTotalCall() == 0)
                offService++;
        }

        return Math.round(sum / (myResidents.length - offService));
    }

    public static double getAverageCallFactor(Person[] myResidents, int[] myCalendar, double[] myAllCallFactor) {
        double totalCallFactor = 0.0;
        int ignore = 0;

        for (int cv = 0; cv < myResidents.length; cv++) {
            totalCallFactor += myAllCallFactor[cv];

            if (myResidents[cv].isOffService() || myResidents[cv].isSatOnlyRotation())
                ignore++;
        }

        return totalCallFactor / (double) (myResidents.length - ignore);
    }


    public static double[] getAllCallFactor(Person[] myResidents, int[] myCalendar) {
        double currentCallFactor = 0.0;
        double totalCallFactor = 0.0;
        int ignore = 0;
        double[] callFactor = new double[myResidents.length];

        for (int cv = 0; cv < myResidents.length; cv++) {
            callFactor[cv] = myResidents[cv].getCallFactor();
        }

        return callFactor;
    }


    public static double deviationTotalCall(Person[] myResidents, int[] myCalendar) {
        double deviation = 0.0;
        double totalCallFactor = 0.0, averageCallFactor = 0.0;
        double[] allCallFactor = new double[myResidents.length];

        allCallFactor = getAllCallFactor(myResidents, myCalendar);
        averageCallFactor = getAverageCallFactor(myResidents, myCalendar, allCallFactor);

        for (int cv = 0; cv < myResidents.length; cv++) {
            if (myResidents[cv].isSatOnlyRotation() == false && myResidents[cv].isOffService() == false)
                deviation = deviation + Math.abs(allCallFactor[cv] - averageCallFactor) * 1.0;
        }

        return deviation;
    }


    public static void distWeekendCall(Person[] A) {
        System.out.println("\nDistribution of weekend calls");
        for (int cv = 0; cv < A.length; cv += 12) {
            System.out.println(A[cv + 0].getWeekendCall() + " " +
                    A[cv + 1].getWeekendCall() + " " +
                    A[cv + 2].getWeekendCall() + " " +
                    A[cv + 3].getWeekendCall() + " " +
                    A[cv + 4].getWeekendCall() + " " +
                    A[cv + 5].getWeekendCall() + " " +
                    A[cv + 6].getWeekendCall() + " " +
                    A[cv + 7].getWeekendCall() + " " +
                    A[cv + 8].getWeekendCall() + " " +
                    A[cv + 9].getWeekendCall() + " " +
                    A[cv + 10].getWeekendCall() + " " +
                    A[cv + 11].getWeekendCall() + " ");
        }
    }

    public static void distTotalCall(Person[] A) {
        System.out.println("\nDistribution of total calls");
        for (int cv = 0; cv < A.length; cv += 12) {
            System.out.println(A[cv + 0].getTotalCall() + " " +
                    A[cv + 1].getTotalCall() + " " +
                    A[cv + 2].getTotalCall() + " " +
                    A[cv + 3].getTotalCall() + " " +
                    A[cv + 4].getTotalCall() + " " +
                    A[cv + 5].getTotalCall() + " " +
                    A[cv + 6].getTotalCall() + " " +
                    A[cv + 7].getTotalCall() + " " +
                    A[cv + 8].getTotalCall() + " " +
                    A[cv + 9].getTotalCall() + " " +
                    A[cv + 10].getTotalCall() + " " +
                    A[cv + 11].getTotalCall() + " ");
        }
    }

    public static void distOBCall(Person[] A) {
        System.out.println("\nDistribution of OB calls");
        for (int cv = 0; cv < A.length; cv += 12) {
            System.out.println(A[cv + 0].getOBCall() + " " +
                    A[cv + 1].getOBCall() + " " +
                    A[cv + 2].getOBCall() + " " +
                    A[cv + 3].getOBCall() + " " +
                    A[cv + 4].getOBCall() + " " +
                    A[cv + 5].getOBCall() + " " +
                    A[cv + 6].getOBCall() + " " +
                    A[cv + 7].getOBCall() + " " +
                    A[cv + 8].getOBCall() + " " +
                    A[cv + 9].getOBCall() + " " +
                    A[cv + 10].getOBCall() + " " +
                    A[cv + 11].getOBCall() + " ");
        }
    }

    public static Boolean isScheduleFilled(String[][] myCallSchedule, int daysInMonth) {
        Boolean isFilled = true;

        outerLoop:
        for (int cv = 1; cv <= daysInMonth; cv++) {
            for (int cv2 = 0; cv2 <= 3; cv2++) {
                if (myCallSchedule[cv][cv2] == "**NoName**") {
                    //System.out.println("Not filled on Day: " + cv + " , call: " + cv2 + " by name: " +myCallSchedule[cv][cv2]);
                    isFilled = false;
                    break outerLoop;
                }
            }
        }
        return isFilled;
    }

    public static String[][] copy(String[][] originalSchedule) {
        String[][] newSchedule = new String[originalSchedule.length][originalSchedule[0].length];

        for (int cv = 1; cv < originalSchedule.length; cv++) {
            for (int cv2 = 0; cv2 <= 3; cv2++) {
                newSchedule[cv][cv2] = originalSchedule[cv][cv2];
            }
        }

        return newSchedule;
    }

    public static void displayAllCall(Person[] myResidents, double avgCall) throws java.io.IOException {
        System.out.println("\n*********************************************");
        System.out.printf("%-12s %-11s %-3s %-4s %-8s %-8s %-8s %-8s %-6s %-12s %-3s %n", "Name", "Rotation", "OB", "GOR", "Weekday", "Weekend", "Friday", "Sat/Sun", "Total", "Call Factor", "Q3 Calls");

        for (int cv = 0; cv < myResidents.length; cv++) {
            System.out.printf("%-12s %-11s %-3s %-4s %-8s %-8s %-8s %-8s %-6s %-12s %-10s %n",
                    myResidents[cv].getName(),
                    myResidents[cv].getRotation(),
                    myResidents[cv].getOBCall(),
                    myResidents[cv].getGORCall(),
                    myResidents[cv].getWeekdayCall(),
                    myResidents[cv].getWeekendCall(),
                    myResidents[cv].getFriCall(),
                    myResidents[cv].getSatSunCall(),
                    myResidents[cv].getTotalCall(),
                    myResidents[cv].getCallFactor(),
                    myResidents[cv].getQCallInterval(3)
            );
        }
    }

    public static int totalNumQCallInterval(Person[] myResidents, int qcall) {
        int k = 0;
        int count = 0;
        for (int i = 0; i < myResidents.length; i++) {
            List<Integer> calldays = myResidents[i].getCallDayList();
            for (int j = 0; j < calldays.size() - 1; ) {
                j++;
                k++;
                //If the difference between two adjacent calls are = to qcall then count
                if (calldays.get(j) - calldays.get(j-1) == qcall)
                    count++;
            }


        }
        return count;
    }

}
