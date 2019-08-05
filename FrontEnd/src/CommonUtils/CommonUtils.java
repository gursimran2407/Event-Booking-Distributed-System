package CommonUtils;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Natheepan Ganeshamoorthy
 */
public class CommonUtils {

    public static final String FRONT_END_HOSTNAME = "BRYNNER";
    public static final String REPLICA3_HOSTNAME = "CAGE";
    public static final String SEQUENCER_HOSTNAME = "BRODY";

    public static final String FRONT_END_IPADDRESS = "132.205.64.80";
    public static final String SEQUENCER_IPADDRESS = "132.205.64.60";
    public static final String REPLICA3IPADDRESS = "132.205.4.152";

    public static final int TO_REPLICA_STRING_PORT = 5746;
    public static final int SEQUNECER_PORT = 1564;
    public static final int MULTICAST_PORT = 8945;
    public static final int FRONT_END_PORT = 3647;
    public static final int RECEIVE_DATA_FROM_REPLICA_PORT = 1547;
    public static final int REPLICA_OTTAWA_SERVER_PORT = 6547;
    public static final int REPLICA_TORONTO_SERVER_PORT = 7575;
    public static final int REPLICA_MONTREAL_SERVER_PORT = 1452;
    public static final int REPLICA_TO_REPLICA_PORT = 1425;

    public final static String INET_ADDR = "224.0.0.3";
    public final static int PORT = 8888;

    public static final String ALIVE = "Yes, I'm Alive";
    public static final String EXCEPTION = "Unknown Error. Please try again!";
    public static final String CRASHED = "CRASHED";
    public static final String FRONT_END = "FRONT END";
    public static final String THREE_CONSECUTIVE_ERRORS = "Three Consecutive Errors";
    public static final String RESULT_ERROR = "Result Error";
    
    public static final String ADD_EVENT = "ADD_EVENT";
    public static final String REMOVE_EVENT = "REMOVE_EVENT";
    public static final String LIST_EVENT = "LIST_EVENT";
    public static final String eventAvailable = "eventAvailable";
    public static final String validateBooking = "validateBooking";
    public static final String BOOK_EVENT = "BOOK_EVENT";
    public static final String CANCEL_EVENT = "CANCEL_EVENT";
    public static final String SWAP_EVENT = "SWAP_EVENT";
    public static final String FT = "FT";
    public static final String NON_OriginCustomerBooking = "NON_ORIGIN_CUSTOMER_BOOKING";
    public static final String GET_DATA = "GET_DATA";
    public static final String GET_BOOKING_SCHEDULE = "GET_BOOKING_SCHEDULE";
    
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

    public static final int TORONTO_SERVER_PORT = 7845;
    public static final int MONTREAL_SERVER_PORT = 3254;
    public static final int OTTAWA_SERVER_PORT = 6587;

    public static final String CONFERENCE = "Conferences";
    public static final String SEMINAR = "Seminars";
    public static final String TRADESHOW = "TradeShows";

    public static final String OPERATIONFAILURE = "Operation Failure";

    public static enum InputType {
        CLIENT_ID, EVENT_ID
    };

    public static void addFileHandler(Logger log, String fileName) throws SecurityException, IOException {
        log.setUseParentHandlers(false);
        FileHandler fileHandler = new FileHandler(System.getProperty("user.dir") + "/Records/" + fileName + ".log", true);
        log.addHandler(fileHandler);
        fileHandler.setFormatter(new SimpleFormatter());
    }

    public static boolean isInputValid(String id, CommonUtils.InputType type) {
        if (type == CommonUtils.InputType.CLIENT_ID) {
            String serverId = id.substring(0, 3);
            String clientType = id.substring(3, 4);
            String clientID = id.substring(4, 8);

            return id.length() == 8 && (clientType.equals(CUSTOMER_ClientType) || clientType.equals(EVENT_MANAGER_ClientType))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (clientID.matches("^[0-9]+$"));
        }

        if (type == CommonUtils.InputType.EVENT_ID) {
            String serverId = id.substring(0, 3);
            String eventType = id.substring(3, 4);
            String day = id.substring(4, 6);
            String month = id.substring(6, 8);
            String year = id.substring(8, 10);

            return id.length() == 10 && (eventType.equals(MORNING) || eventType.equals(EVENING) || eventType.equals(AFTERNOON))
                    && (serverId.equals(TORONTO) || serverId.equals(MONTREAL) || serverId.equals(OTTAWA))
                    && (day.length() == 2 && day.matches("0[1-9]|[1-2][0-9]|3[0-1]"))
                    && (month.length() == 2 && month.matches("0[1-9]|1[0-2]"))
                    && (year.length() == 2 && year.matches("19|[2-9][0-9]"));
        }
        return false;
    }

    public static String capitalize(String input) {
        return input.toUpperCase();
    }

}
