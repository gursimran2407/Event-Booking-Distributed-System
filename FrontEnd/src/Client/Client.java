/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Client;

import CommonUtils.CommonUtils;
import FrontEndIdl.FrontEnd;
import FrontEndIdl.FrontEndHelper;
import ServerImpl.MontrealServerImpl;
import ServerImpl.OttawaServerImpl;
import ServerImpl.TorontoServerImpl;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static CommonUtils.CommonUtils.*;
import Model.MessageData;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Natheepan Ganeshamoorthy, Gursimran Singh
 */
public class Client {
    public static String demoOption = "";
    private static Logger LOGGER;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            String id = enterValidID(InputType.CLIENT_ID);
            startMenu(id.substring(0, 3), id.substring(4, 8), id.substring(3, 4), ncRef, id);


        } catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }

    }
    
    static void startMenu(String serverId, String clientID, String clientType, NamingContextExt ncRef, String id){
        
        
        String itemNum = "";
        while (!itemNum.equals("0"))
        {
            System.out.println("============================");
            System.out.println(" Menu");
            System.out.println("1: Fault Tolerance");
            System.out.println("2: Highly Available");
            System.out.println("3: Normal");
            System.out.println("============================");

            itemNum = scanner.next().trim();
            
          
            if (itemNum.matches("^[1-3]$"))
            {
                switch (itemNum)
                {
                    case "1":
                        MessageData messageData = new MessageData().setAction("FT").setMethodName(CommonUtils.FT);
                                
                         if (clientType.equals(CUSTOMER_ClientType))
                        {
                             messageData.setCustomerId(id);
                        }
                        if (clientType.equals(EVENT_MANAGER_ClientType))
                        {
                             messageData.setManagerId(id);
                        }
                        sendMessageToSequencer(messageData);
                        clientService(serverId, clientID, clientType, ncRef);
                             
                        break;
                    case "2":
                         MessageData messageData2 = new MessageData().setAction("HA").setMethodName("HA");;
                                
                         if (clientType.equals(CUSTOMER_ClientType))
                        {
                             messageData2.setCustomerId(id);
                        }
                        if (clientType.equals(EVENT_MANAGER_ClientType))
                        {
                             messageData2.setManagerId(id);
                        }
                        sendMessageToSequencer(messageData2);
                        clientService(serverId, clientID, clientType, ncRef);
                        break;
                    case "3":
                          MessageData messageData3 = new MessageData().setAction("NORMAL").setMethodName("NORMAL");;
                                
                         if (clientType.equals(CUSTOMER_ClientType))
                        {
                             messageData3.setCustomerId(id);
                        }
                        if (clientType.equals(EVENT_MANAGER_ClientType))
                        {
                             messageData3.setManagerId(id);
                        }
                        sendMessageToSequencer(messageData3);
                        clientService(serverId, clientID, clientType, ncRef);
                        break;

                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }
            else
            {
                System.out.println("Please select a valid choice!");
            }
        }
        scanner.close();
    }
    
    //Method to send message to the sequencer
	private static void sendMessageToSequencer(MessageData messageData) {
		long startTime = System.currentTimeMillis();
		try(DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(CommonUtils.SEQUENCER_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
			objectOutput.writeObject(messageData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, host, CommonUtils.SEQUNECER_PORT);
			socket.send(sendPacket);
                        System.out.println("FrontEnd.FrontEndImpl.sendMessageToSequencer() from the front the FrontEnd.");
			//Waiting for the reply from the replicas after sending to the sequencers
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//return CommonUtils.EXCEPTION;
	}

    private static void clientService(String serverId, String clientID, String clientType, NamingContextExt ncRef)
    {
        FrontEnd server;
        try
        {
            String customerID = capitalize(serverId + clientType + clientID);
            LOGGER = Logger.getLogger(getServerClassName(serverId));
            addFileHandler(LOGGER, customerID);


            server = FrontEndHelper.narrow(ncRef.resolve_str(CommonUtils.FRONT_END));
            if (clientType.equals(CUSTOMER_ClientType))
            {
                System.out.println("Welcome Customer " + customerID);
                runCustomerMenu(server, customerID);
            }
            if (clientType.equals(EVENT_MANAGER_ClientType))
            {
                System.out.println("Welcome Manager " + customerID);
                runManagerMenu(server, customerID);
            }
        }
        catch (CannotProceed cannotProceed) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, cannotProceed);
            cannotProceed.printStackTrace();
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runCustomerMenu(FrontEnd server, String customerID)
    {
        String itemNum = "";
        while (!itemNum.equals("0"))
        {
            System.out.println("============================");
            System.out.println("Customer Menu");
            System.out.println("0: Quit");
            System.out.println("1: Book Event");
            System.out.println("2: Get Booking Schedule");
            System.out.println("3: Cancel Event");
            System.out.println("4: Swap Event");
            System.out.println("============================");

            itemNum = scanner.next().trim();

            if (itemNum.matches("^[0-4]$"))
            {
                switch (itemNum)
                {
                    case "0":
                        System.out.println("Good Bye !!!");
                        break;
                    case "1":
                        runBookEvent(server, customerID);
                        break;
                    case "2":
                        runBookingSchedule(server, customerID, null);
                        break;
                    case "3":
                        System.out.println("Enter Event Type of The Event to Cancel? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String eventType = getEventType();
//                        System.out.println("Enter Event ID to Cancel: ");
                        String eventID = enterValidID(InputType.EVENT_ID);
                        String response = server.cancelEvent(customerID, eventID, eventType);
                        System.out.println("Response from server: " + response);
                        LOGGER.log(Level.INFO, "Response of server: {0}", response);
                        break;
                    case "4":
                        System.out.println("Enter new Event Type of The Event to Replace? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String newEventType = getEventType();
                        String newEventID = enterValidID(InputType.EVENT_ID);
                        System.out.println("Enter old Event Type of The Event to Remove? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String oldEventType = getEventType();
                        String oldEventID = enterValidID(InputType.EVENT_ID);
                        String swap = server.swapEvent(customerID, newEventID, newEventType, oldEventID, oldEventType);
                        System.out.println("Response from server: " + swap);
                        LOGGER.log(Level.INFO, "Response of server: {0}", swap);
                        break;
                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }
            else
            {
                System.out.println("Please select a valid choice!");
            }
        }
        scanner.close();
    }

    private static void runManagerMenu(FrontEnd server, String managerID)
    {
        String itemNum = "";
        while (!itemNum.equals("0"))
        {
            System.out.println("============================");
            System.out.println("Manager Menu");
            System.out.println("0: Quit");
            System.out.println("1: Add Event");
            System.out.println("2: Remove Event");
            System.out.println("3: List Event Availability");
            System.out.println("4: Book Event");
            System.out.println("5: Get Booking Schedule");
            System.out.println("6: Cancel Event");
            System.out.println("7: Swap Event");

            System.out.println("============================");

            itemNum = scanner.next().trim();

            if (itemNum.matches("^[0-7]$"))
            {
                switch (itemNum)
                {
                    case "0":
                        System.out.println("Good Bye !!!");
                        break;
                    case "1":
                        System.out.println("What event do you wish to add?");
                        managerAddEvent(server, managerID);
                        break;
                    case "2":
                        System.out.println("What event do you wish to remove?");
                        managerRemoveEvent(server, managerID);
                        break;
                    case "3":
                        System.out.println("Which type of event you wish to list? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        managerListEvents(server, managerID);
                        break;
                    case "4":
                        runBookEvent(server, enterValidID(InputType.CLIENT_ID));
                        break;
                    case "5":
                        System.out.println("What customer do you wish to get Booking Schedule for?");
                        runBookingSchedule(server, enterValidID(InputType.CLIENT_ID), managerID);
                        break;
                    case "6":
                        System.out.println("What event do you wish to cancel?");
                        System.out.println("Enter Event Type of The Event to Cancel? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String eventType = getEventType();
//                        System.out.println("Enter Event ID to Cancel: ");
                        String eventID = enterValidID(InputType.EVENT_ID);
                        String customerID = enterValidID(InputType.CLIENT_ID);
                        String response = server.cancelEvent(customerID, eventID, eventType);
                        System.out.println("Response from server: " + response);
                        break;
                    case "7":
                        System.out.println("Enter new Event Type of The Event to Replace? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String newEventType = getEventType();
                        String newEventID = enterValidID(InputType.EVENT_ID);
                        System.out.println("Enter old Event Type of The Event to Remove? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
                        String oldEventType = getEventType();
                        String oldEventID = enterValidID(InputType.EVENT_ID);
                        String custID = enterValidID(InputType.CLIENT_ID);
                        String swap = server.swapEvent(custID, newEventID, newEventType, oldEventID, oldEventType);
                        System.out.println("Response from server: " + swap);
                        LOGGER.log(Level.INFO, "Response of server: {0}", swap);
                        break;
                    default:
                        System.out.println("Invalid Choice !!!");
                        break;
                }
            }
            else
            {
                System.out.println("Please select a valid choice!");
            }
        }
        scanner.close();
    }

    private static void managerAddEvent(FrontEnd server, String managerID)
    {
        try
        {
            String eventID;
            String eventType;
            String bookingCapacity;
            eventID = enterValidID(InputType.EVENT_ID);
            System.out.println();
            System.out.println("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = getEventType();
            System.out.println();
            System.out.print("Please enter Booking Capacity: ");
            bookingCapacity = getNumber();
            LOGGER.log(Level.INFO, "Manager: {0} adding a new Event with Event id: {1} ,Event Type: {2} and Booking Capacity: {3}", new Object[]
            {
                managerID, eventID, eventType, bookingCapacity
            });
            String string = server.addEvent(eventID, eventType, bookingCapacity, managerID);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
            System.out.println("Response of server: " + string);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void managerRemoveEvent(FrontEnd server, String managerID)
    {
        String eventID;
        String eventType;
        try
        {
            eventID = enterValidID(InputType.EVENT_ID);
            System.out.println();
            System.out.println("Please enter Event Type: (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR) ");
            eventType = getEventType();

            LOGGER.log(Level.INFO, "Manager {0} removing Event with Event ID {1} of type: {2}", new Object[]
            {
                managerID, eventID, eventType
            });
            String string = server.removeEvent(eventID, eventType, managerID);
            System.out.println("Response of the server: " + string);
            LOGGER.log(Level.INFO, "Response of server: {0}", string);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void managerListEvents(FrontEnd server, String customerID)
    {
        try
        {
            String eventType = getEventType();
            String str = server.listEventAvailability(eventType, customerID);
            System.out.println(str);
            LOGGER.log(Level.INFO, "Response of Server: {0}", str);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void runBookEvent(FrontEnd server, String customerID)
    {
        System.out.println("What type of event do you wish to book? (Available Options: A: CONFERENCE, B: TRADESHOW, C: SEMINAR)");
        String eventType = getEventType();

        String eventID = enterValidID(InputType.EVENT_ID);
//        System.out.println("Enter the number of people attending: ");
//        Integer booking = Integer.parseInt(getNumber());

        String msg = server.bookEvent(customerID, eventID, eventType, "1");
        LOGGER.info(msg);
        System.out.println(msg);
    }

    private static void runBookingSchedule(FrontEnd server, String customerID, String managerId)
    {
        LOGGER.log(Level.INFO, "Booking Schedule Requested by {0}", customerID);
        System.out.println(customerID + "'s Bookings Schedule");
        String booking = server.getBookingSchedule(customerID, managerId);
        System.out.println(booking);

        if (!booking.equalsIgnoreCase(OPERATIONFAILURE))
        {
            LOGGER.log(Level.INFO, "Operation Sucessful. Records for {0} have been found", customerID);
            LOGGER.info(booking);
        }
        else
        {
            LOGGER.log(Level.INFO, "Operation Failure. Records for {0} do not exist.", customerID);
        }
    }

    private static int getServerPort(String serverId)
    {
        switch (serverId)
        {
            case TORONTO: return TORONTO_SERVER_PORT;
            case MONTREAL: return MONTREAL_SERVER_PORT;
            case OTTAWA: return OTTAWA_SERVER_PORT;
            default: return -1;
        }
    }

    private static String getServerName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO:  return TORONTO_SERVER_NAME;
            case MONTREAL: return MONTREAL_SERVER_NAME;
            case OTTAWA:   return OTTAWA_SERVER_NAME;
            default:       return "Server Does Not Exist";
        }
    }

    private static String getServerClassName(String serverId)
    {
        switch (serverId)
        {
            case TORONTO:  return TorontoServerImpl.class.getName();
            case MONTREAL: return MontrealServerImpl.class.getName();
            case OTTAWA:   return OttawaServerImpl.class.getName();
            default:       return "Server Does Not Exist";
        }
    }

    public static String getNumber()
    {
        String num = scanner.next().trim();
        System.out.println();
        while (!num.matches("^[1-9]\\d*$"))
        {
            System.out.println("Invalid ID !!!\n");
            System.out.print("Please enter Valid Number");
            num = scanner.next().trim();
            System.out.println();
        }
        return num;
    }

    private static String enterValidID(InputType type)
    {
        String msg = "";
        if (InputType.CLIENT_ID == type)
        {
            msg = "Enter Customer ID Number: ";
        }
        else if (InputType.EVENT_ID == type)
        {
            msg = "Enter Event ID Number: ";
        }
        System.out.print(msg);
        String id = capitalize(scanner.next().trim());
        System.out.println();
        while (!isInputValid(id, type))
        {
            System.out.println("Invalid ID !!!\n");
            System.out.print(msg);
            id = capitalize(scanner.next().trim());
            System.out.println();
        }
        return id;
    }

    private static String getEventType()
    {
        String eventType = capitalize(scanner.next().trim());;
        while (!eventType.equals("A") && !eventType.equals("B") && !eventType.equals("C"))
        {
            System.out.println("Select an appropriate option!");
            eventType = capitalize(scanner.next().trim());
        }
        switch (eventType)
        {
            case "A": return CONFERENCE;
            case "B": return TRADESHOW;
            case "C": return SEMINAR;
            default:  return "";
        }
    }
}