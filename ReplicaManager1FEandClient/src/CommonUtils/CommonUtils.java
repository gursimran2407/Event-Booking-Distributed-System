package CommonUtils;

/**
 * @author gursimransingh
 */
/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class CommonUtils {

    //CONSTANTS
    public static final String REPLICA1_HOSTNAME="SENEGAL";//
    public static final String REPLICA2_HOSTNAME="GHANA"; //
    public static final String FRONT_END_HOSTNAME="GHANA"; //
    public static final String REPLICA3_HOSTNAME="GUINEA";//
    public static final String SEQUENCER_HOSTNAME="GUINEA";//


//	public static final String REPLICA1_IPADDRESS="132.205.4.148";
//	public static final String REPLICA2_IPADDRESS="132.205.4.149";
//	public static final String REPLICA3_IPADDRESS="132.205.4.152";
//	public static final String SEQUENCER_IPADDRESS="132.205.4.152";
//	public static final String REPLICA4_IPADDRESS="";

    public static final int TO_REPLICA_STRING_PORT = 1212;
    public static final int SEQUNECER_PORT=1218;
    public static final int MULTICAST_PORT = 1219;
    public static final int FRONT_END_PORT = 1220;
    public static final int RECEIVE_DATA_FROM_REPLICA_PORT = 2010;

    public final static String INET_ADDR = "224.0.0.3";
    public final static int PORT = 8888;


    public static final String ADD_EVENT = "ADD_EVENT";
    public static final String REMOVE_EVENT = "REMOVE_EVENT";
    public static final String LIST_EVENT = "LIST_EVENT";
    public static final String eventAvailable = "eventAvailable";
    public static final String validateBooking = "validateBooking";
    public static final String BOOK_EVENT = "BOOK_EVENT";
    public static final String CANCEL_EVENT = "CANCEL_EVENT";
    public static final String SWAP_EVENT = "SWAP_EVENT";
    public static final String NON_OriginCustomerBooking = "NON_ORIGIN_CUSTOMER_BOOKING";
    public static final String GET_DATA = "GET_BOOKING_SCHEDULE";


    public static final String SOME_THING_WENT_WRONG = "Something went wrong. Please try again!";


    public static final String CRASHED = "CRASHED";
    public static final String FRONT_END = "FRONT_END";
    public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
    public static final String RESULT_ERROR = "Result Error";
    public static final int REPLICA_OTTAWA_SERVER_PORT = 2000;
    public static final int REPLICA_TORONTO_SERVER_PORT = 2002;
    public static final int REPLICA_MONTREAL_SERVER_PORT = 2004;
    public static final int REPLICA_TO_REPLICA_PORT = 2006;

    public static final String I_AM_ALIVE = "I am Alive";
    public static String USER_CODE = "U";
    public static String MANAGER_CODE = "M";
    public static String LOGGER_FOLDER="/logs";
    public static String CONCORDIA_INITAL_LOAD_FILE = "/resources/ConcordiaLibrary.txt";
    public static String MCGGILL_INITAL_LOAD_FILE = "/resources/McgillLibrary.txt";
    public static String MONTREAL_INITAL_LOAD_FILE = "/resources/MontrealLibrary.txt";
    public static String CONCORDIA_SERVER_LOG_FILE = "/log/concordia_server_log.log";
    public static String MCGILL_SERVER_LOG_FILE = "/log/mcgill_server_log.log";
    public static String MONTREAL_SERVER_LOG_FILE = "/log/montreal_server_log.log";
    public static String LOG_FOLDER = "/log/";
    public static String CONCORDIA_SERVER = "CONCORDIA_SERVER";
    public static String MCGILL_SERVER = "MCGILL_SERVER";
    public static String MONTREAL_SERVER = "MONTREAL_SERVER";
    public static String WAITING_LIST_MESSAGE="Book not available. Do you want to be added in waiting list?(y/n)";
    public static int CONCORDIA_SERVER_PORT = 5555;
    public static int MCGILL_SERVER_PORT = 6666;
    public static String TRUE = "true";
    public static String FALSE = "false";
    ///////////////////////////////////////////////

    public static final String CUSTOMER_ClientType = "C";
    public static final String EVENT_MANAGER_ClientType = "M";

    public static final String MORNING = "M";
    public static final String EVENING = "E";
    public static final String AFTERNOON = "A";

    public static final String TORONTO = "TOR";
    public static final String MONTREAL = "MTL";
    public static final String OTTAWA = "OTW";

    public static final String TORONTO_SERVER_NAME = "TORONTO";
    public static final String MONTREAL_SERVER_NAME = "MONTREAL";
    public static final String OTTAWA_SERVER_NAME = "OTTAWA";

    public static final int TORONTO_SERVER_PORT = 1111;
    public static final int MONTREAL_SERVER_PORT = 2222;
    public static final int OTTAWA_SERVER_PORT = 3333;

    public static final String CONFERENCE = "Conferences";
    public static final String SEMINAR = "Seminars";
    public static final String TRADESHOW = "TradeShows";

    public static final String OPERATIONFAILURE = "Operation Failure";

    public static enum InputType
    {
        CLIENT_ID, EVENT_ID
    };

    public static void addFileHandler(Logger log, String fileName) throws SecurityException, IOException
    {
        log.setUseParentHandlers(false);
        FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/CORBA/Records/" + fileName + ".log", true);
        log.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

    public static boolean isInputValid(String id, CommonUtils.CommonUtils.InputType type)
    {
        if(type == CommonUtils.CommonUtils.InputType.CLIENT_ID)
        {
            String serverId   = id.substring(0, 3);
            String clientType = id.substring(3, 4);
            String clientID   = id.substring(4, 8);

            return id.length() == 8 && (clientType.equals(CUSTOMER_ClientType) || clientType.equals(EVENT_MANAGER_ClientType))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (clientID.matches("^[0-9]+$"));
        }

        if(type == CommonUtils.CommonUtils.InputType.EVENT_ID)
        {
            String serverId  = id.substring(0, 3);
            String eventType = id.substring(3, 4);
            String day       = id.substring(4, 6);
            String month     = id.substring(6, 8);
            String year      = id.substring(8, 10);

            return id.length() == 10 && (eventType.equals(MORNING) || eventType.equals(EVENING) || eventType.equals(AFTERNOON))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (day.length() == 2 && day.matches("0[1-9]|[1-2][0-9]|3[0-1]"))
                    && (month.length() == 2 && month.matches("0[1-9]|1[0-2]"))
                    && (year.length() == 2 && year.matches("19|[2-9][0-9]"));
        }
        return false;
    }

    public static String capitalize(String input)
    {
        return input.toUpperCase();
    }

}

