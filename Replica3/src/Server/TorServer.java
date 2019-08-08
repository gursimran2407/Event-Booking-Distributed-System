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

import Constants.Constants;
import Model.EventData;
import Model.MessageData;
import ServerImplementation.TorServerImpl;

/**
 *
 * @author Gursimran Singh
 */
public class TorServer {

    public static void main(String[] args)
    {
    	TorServerImpl torontoServerImpl = new TorServerImpl();

		if(args.length > 0) {
			Runnable dataConsistentImpl = () ->{
				receiveDataConsistence(torontoServerImpl);
			};
			new Thread(dataConsistentImpl).start();
		}

		Runnable libraryServerRunnable = () ->{
			handlesRequestFromAnotherServers(torontoServerImpl);
		};
		Runnable replicaManagerImpl = () ->{
			handleReplicaRequests(torontoServerImpl,args);
		};

		new Thread(replicaManagerImpl).start();
		new Thread(libraryServerRunnable).start();
    }
    

    private static void handleReplicaRequests(TorServerImpl montrealLibraryImpl, String[] args) {
		try(DatagramSocket socket = new DatagramSocket(Constants.REPLICA_TORONTO_SERVER_PORT)) {
			System.out.println("Toronto Server started...");
			while(true) {
				byte [] message = new byte[1024];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				byte[] byteArray = replicaManagerImpl(messageData, montrealLibraryImpl);
				DatagramPacket reply = new DatagramPacket(byteArray, byteArray.length, recievedDatagramPacket.getAddress(),
						recievedDatagramPacket.getPort());
				socket.send(reply);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static byte[] replicaManagerImpl(MessageData messageData, TorServerImpl montrealLibraryImpl) {
		String response = "";

		switch(messageData.getMethodName()) {

		case Constants.ADD_EVENT:
			response = montrealLibraryImpl.addEvent(messageData.getEventId(), messageData.getEventType(), messageData.getBookingCap(), messageData.getManagerId());
			break;
		case Constants.REMOVE_EVENT:
			response = montrealLibraryImpl.removeEvent(messageData.getEventId(), messageData.getEventType(), messageData.getManagerId());
			break;
		case Constants.LIST_EVENT:
			response=montrealLibraryImpl.listEventAvailability(messageData.getEventType(), messageData.getManagerId());
			break;
		case Constants.BOOK_EVENT:
			response=montrealLibraryImpl.bookEvent(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType(), messageData.getBookingCap());
			break;
		case Constants.GET_BOOKING_SCHEDULE:
				response=montrealLibraryImpl.getBookingSchedule(messageData.getCustomerId(), messageData.getManagerId());
				break;
		case Constants.GET_DATA:
//				EventData eventData = montrealLibraryImpl.getEventData();
//				try {
//					ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//					ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
//					objectOutput.writeObject(eventData);
//					return byteStream.toByteArray();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
				break;
		case Constants.CANCEL_EVENT:
			response = montrealLibraryImpl.cancelEvent(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType());
			break;
		case Constants.NON_OriginCustomerBooking:
			response=montrealLibraryImpl.nonOriginCustomerBooking(messageData.getCustomerId(), messageData.getEventId());
			break;
		case Constants.SWAP_EVENT:
			response = montrealLibraryImpl.swapEvent(messageData.getCustomerId(), messageData.getNewEventId(), messageData.getNewEventType(), messageData.getOld_EventID(), messageData.getOld_EventType());
			break;
		case Constants.CRASHED:
			response = Constants.ALIVE;
			break;
		case Constants.eventAvailable:
			response = montrealLibraryImpl.eventAvailable(messageData.getEventId(), messageData.getEventType());
			break;
		case Constants.validateBooking:
			response = montrealLibraryImpl.validateBooking(messageData.getCustomerId(), messageData.getEventId(), messageData.getEventType());
		default: 
			response="Invalid request!!!";
		}
		return response.getBytes();


	}

	private static void handlesRequestFromAnotherServers(TorServerImpl montrealLibraryImpl){
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(Constants.TORONTO_SERVER_PORT);
			System.out.println("Toronto Server started...");
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
//			e.printStackTrace();
		}finally {
			if(socket != null)
				socket.close();
		}
	}

	private static void receiveDataConsistence(TorServerImpl montrealLibraryImpl) {
		try(DatagramSocket socket = new DatagramSocket(Constants.RECEIVE_DATA_FROM_REPLICA_PORT)){
			byte [] message = new byte[1024];
			DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
			socket.receive(recievedDatagramPacket);
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(recievedDatagramPacket.getData()));
			EventData eventData = (EventData) inputStream.readObject();
//			montrealLibraryImpl.parseEventnfo(eventData);
			inputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
}
