/**
 * CONCORDIA UNIVERSITY
 * DEPARTMENT OF COMPUTER SCIENCE AND SOFTWARE ENGINEERING
 * COMP 6231, Summer 2019 Instructor: Sukhjinder K. Narula
 * ASSIGNMENT 1
 * Issued: May 14, 2019 Due: Jun 3, 2019
 */
package Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import CommonUtils.CommonUtils;
import Model.EventData;
import Model.MessageData;
import ServerImpl.MontrealServerImpl;

/**
 *
 * @author Gursimran Singh
 */
public class MontrealServer {
	static int i = 0;
    public static boolean isFT2 = true;
    public static void main(String[] args)
    {
    	MontrealServerImpl montrealLibraryImpl = new MontrealServerImpl();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(montrealLibraryImpl);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable libraryServerRunnable = () ->{
			handlesRequestFromAnotherServers(montrealLibraryImpl);
		};
		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(montrealLibraryImpl,args);
		};

		new Thread(replicaManagerImpl).start();
		new Thread(libraryServerRunnable).start();
    }
    private static void handleReplicaRequests(MontrealServerImpl montrealLibraryImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(CommonUtils.REPLICA_MONTREAL_SERVER_PORT)) {
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				byte[] byteArray = replicaManagerImpl(messageData, montrealLibraryImpl);
				 byte[] byteArray2 = new String("FT").getBytes();
                                if (!byteArray.equals(byteArray2)) {
                                DatagramPacket reply = new DatagramPacket(byteArray, byteArray.length, recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
                            }
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static byte[] replicaManagerImpl(MessageData messageData, MontrealServerImpl montrealLibraryImpl) {
		String response = "";
                
            
                
		switch(messageData.getMethodName()) {

		case CommonUtils.ADD_EVENT:
                    if (CommonUtils.isFT == true) {
                    	
                        response = montrealLibraryImpl.addEventWrong(messageData.getEventId(), messageData.getEventType(), messageData.getBookingCap(), messageData.getManagerId());
                        i++;
                        if(i >= 3) {
                        	
                            CommonUtils.isFT = false;
                        }
                        
                    }else{
                    	
			response = montrealLibraryImpl.addEvent(messageData.getEventId(), messageData.getEventType(), messageData.getBookingCap(), messageData.getManagerId());
                    }
                        break;
		case CommonUtils.REMOVE_EVENT:
			response = montrealLibraryImpl.removeEvent(messageData.getEventId(), messageData.getEventType(), messageData.getManagerId());
			break;
		case CommonUtils.LIST_EVENT:
			response=montrealLibraryImpl.listEventAvailability(messageData.getEventType(), messageData.getManagerId());
			break;
		case CommonUtils.BOOK_EVENT:
			response=montrealLibraryImpl.bookEvent(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType(), messageData.getBookingCap());
			break;
		case CommonUtils.GET_BOOKING_SCHEDULE:
			response=montrealLibraryImpl.getBookingSchedule(messageData.getCustomerId(), messageData.getManagerId());
			break;
		case CommonUtils.GET_DATA:
			EventData eventData = montrealLibraryImpl.getEventData();
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
				ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
				objectOutput.writeObject(eventData);
				return byteStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
				break;
		case CommonUtils.CANCEL_EVENT:
			response = montrealLibraryImpl.cancelEvent(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType());
			break;
		case CommonUtils.NON_OriginCustomerBooking:
			response=montrealLibraryImpl.nonOriginCustomerBooking(messageData.getCustomerId(), messageData.getEventId());
			break;
		case CommonUtils.SWAP_EVENT:
			response = montrealLibraryImpl.swapEvent(messageData.getCustomerId(), messageData.getNewEventId(), messageData.getNewEventType(), messageData.getOld_EventID(), messageData.getOld_EventType());
			break;
		case CommonUtils.CRASHED:
			response = CommonUtils.ALIVE;
			break;
		case CommonUtils.eventAvailable:
			response = montrealLibraryImpl.eventAvailable(messageData.getEventId(), messageData.getEventType());
			break;
                case "FT":
                       CommonUtils.isFT = isFT2;
                       System.out.println("Server.MontrealServer.replicaManagerImpl() CommonUtils.isFT set to::"+CommonUtils.isFT);
			response = "FT";
			break; 
                        
                case "HA":
			response = "FT";
			break;   
                        
                case "NORMAL":
			response = "FT";
			break;    
                        
		case CommonUtils.validateBooking:
			response = montrealLibraryImpl.validateBooking(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType());
		default: 
			response=" Invalid Request.";
		}
		return response.getBytes();

	}

	private static void handlesRequestFromAnotherServers(MontrealServerImpl montrealLibraryImpl){
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(CommonUtils.MONTREAL_SERVER_PORT);
			System.out.println("Montreal Server started...");
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String response = montrealLibraryImpl.handleRequestFromOtherServer(new String(recievedDatagramPacket.getData()));
				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(socket != null)
				socket.close();
		}
	}

	private static void receiveDataConsistence(MontrealServerImpl montrealLibraryImpl) {
		try(DatagramSocket socket = new DatagramSocket(CommonUtils.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[1024];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			EventData eventData = (EventData) inputStream.readObject();
			montrealLibraryImpl.parseEventnfo(eventData);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
