package FrontEnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.omg.CORBA.ORB;

import CommonUtils.CommonUtils;
import FrontEndIdl.FrontEndPOA;
import Model.MessageData;
import Model.ReceivedToFE;
import java.util.HashMap;
import java.util.Iterator;

public class FrontEndImpl extends FrontEndPOA {

	ORB orb;
	static private long replicaOneTimer = 0;
	static private long replicaTwoTimer = 0;
	static private long replicaThreeTimer = 0;
	static private int counter = 0;

	public void setOrb(ORB orb) {
		this.orb = orb;
	}

	@Override
	public String addEvent(String eventID, String eventType, String bookingCapacity, String managerID) {
		return sendMessageToSequencer(new MessageData().setManagerId(managerID).setEventId(eventID)
				.setEventType(eventType).setBookingCap(bookingCapacity).setMethodName(CommonUtils.ADD_EVENT));

	}

	@Override
	public String removeEvent(String eventID, String eventType, String managerID) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setEventId(eventID).setManagerId(managerID)
				.setMethodName(CommonUtils.REMOVE_EVENT).setEventType(eventType));
	}

	@Override
	public String listEventAvailability(String eventType, String managerID) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setEventType(eventType).setManagerId(managerID)
				.setMethodName(CommonUtils.LIST_EVENT));
	}

	@Override
	public String bookEvent(String customerID, String eventID, String eventType, String bookingAmount) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setEventType(eventType)
				.setBookingCap(bookingAmount).setMethodName(CommonUtils.BOOK_EVENT).setEventId(eventID));
	}

	@Override
	public String getBookingSchedule(String customerID, String managerID) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setManagerId(managerID)
				.setMethodName(CommonUtils.GET_BOOKING_SCHEDULE));
	}

	@Override
	public String cancelEvent(String customerID, String eventID, String eventType) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setEventType(eventType)
				.setMethodName(CommonUtils.CANCEL_EVENT).setEventId(eventID));
	}

	@Override
	public String nonOriginCustomerBooking(String customerID, String eventID) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setEventId(eventID)
				.setMethodName(CommonUtils.NON_OriginCustomerBooking));
	}

	@Override
	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setNewEventId(newEventID)
				.setNewEventType(newEventType).setOld_EventID(oldEventID).setOld_EventType(oldEventType)
				.setMethodName(CommonUtils.SWAP_EVENT));
	}

	@Override
	public String eventAvailable(String eventID, String eventType) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setEventId(eventID).setEventType(eventType)
				.setMethodName(CommonUtils.eventAvailable));
	}

	@Override
	public String validateBooking(String customerID, String eventID, String eventType) {
		// TODO Auto-generated method stub
		return sendMessageToSequencer(new MessageData().setCustomerId(customerID).setEventId(eventID)
				.setEventType(eventType).setMethodName(CommonUtils.validateBooking));
	}

	@Override
	public void shutdown() {
		orb.shutdown(false);
	}

	HashMap<String, Integer> hm = new HashMap<>();

	// Method to send message to the sequencer
	private String sendMessageToSequencer(MessageData messageData) {
		long startTime = System.currentTimeMillis();
		try (DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(CommonUtils.SEQUENCER_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
			objectOutput.writeObject(messageData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length,
					host, CommonUtils.SEQUNECER_PORT);
			socket.send(sendPacket);
			System.out.println("FrontEnd.FrontEndImpl.sendMessageToSequencer() from the front the FrontEnd.");
			// Waiting for the reply from the replicas after sending to the sequencers
			return waitForReplyFromReplicas(startTime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return CommonUtils.EXCEPTION;
	}

	private String waitForReplyFromReplicas(long startTime) {
		List<ReceivedToFE> dataReceived = new ArrayList<>();
		String messageToClient = CommonUtils.EXCEPTION;
		try (DatagramSocket socket = new DatagramSocket(CommonUtils.FRONT_END_PORT)) {
			while (true) {
				System.out
						.println("Timers : " + replicaOneTimer + " " + replicaTwoTimer + " " + replicaThreeTimer + " ");
				byte[] message = new byte[3072];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				if (dataReceived.size() >= 2) {
					socket.setSoTimeout(getTimeOutTimer());
				}
				socket.receive(recievedDatagramPacket);
				ObjectInputStream inputStream = new ObjectInputStream(
						new ByteArrayInputStream(recievedDatagramPacket.getData()));
				ReceivedToFE messageData = (ReceivedToFE) inputStream.readObject();
				inputStream.close();
				System.out.println("######MESSAGE RECEIVED FROM########: " + messageData.getFromMessage() + " "
						+ messageData.getMessage());
				checkTheTimer(messageData, startTime);
				// adding messages to the arraylist
				dataReceived.add(messageData);

				messageToClient = investigateMessagefromReplicas(dataReceived, startTime);
				// messageToClient = this.checkMessagesToSendToClient(dataReceived, startTime);
				System.out.println(
						"FrontEnd.FrontEndImpl.waitForReplyFromReplicas(): Message to Client: " + messageToClient);
				if (Objects.nonNull(messageToClient)) {
					return messageToClient;
				} else {
					messageToClient = CommonUtils.EXCEPTION;
				}
			}
		} catch (SocketTimeoutException exception) {
			messageToClient = this.checkMessagesToSendToClient(dataReceived, startTime);
			if (Objects.nonNull(messageToClient)) {
				return messageToClient;
			} else {
				messageToClient = CommonUtils.EXCEPTION;
			}
		} catch (IOException | ClassNotFoundException exception) {
			exception.printStackTrace();
		}
		return messageToClient;
	}

	private String investigateMessagefromReplicas(List<ReceivedToFE> dataRecieved, long startTime) {
		ArrayList<ReceivedToFE> arraylist = new ArrayList<>();
		String response= null;
		if (dataRecieved.size() >= 2) { // if we receive more at least 2 messages than add all unsuccseccfull messages
										// to the hashmap.
			for (Iterator<ReceivedToFE> iterator = dataRecieved.iterator(); iterator.hasNext();) {
				ReceivedToFE next = iterator.next();
				String sender = next.getFromMessage();
				if (!hm.containsKey(sender) && next.getMessage().equals("UNSUCCESSFULL")) {
					hm.put(sender, 0);
				}

				if (hm.containsKey(sender)) {
					Integer count = hm.get(sender);
					int new_count = count+1;
					hm.replace(sender, new_count);
				}
				// checking for success
				if (!next.getMessage().equals("UNSUCCESSFULL")) {
					arraylist.add(next);
				}
			}
			if (arraylist.size() >= 2) {// change it to 2 after adding the replica
				String message = null;
				int count = 0;
				message = arraylist.get(0).getMessage();
				for (Iterator<ReceivedToFE> iterator = arraylist.iterator(); iterator.hasNext();) {
					ReceivedToFE next = iterator.next();
					if (message.equals(next.getMessage())) {
						count++;
					}
					if (count >= 2) {
						response =  message;
					}
				}
			}

			for (Entry<String, Integer> entry : hm.entrySet()) {
				String key = entry.getKey();
				Integer value = entry.getValue();
				if (value == 3) {
					informSoftwareBug(key);
					hm.remove(key);
				}
			}
		}
		
		System.out.println("#################HM####################"+hm);
		return response;

	}
	private int getTimeOutTimer() {
		int timerToSend;
		long firstTimers = replicaOneTimer > replicaTwoTimer ? replicaOneTimer : replicaTwoTimer;
		long lastTimers = replicaTwoTimer > replicaThreeTimer ? replicaTwoTimer : replicaThreeTimer;
		long timer = firstTimers > lastTimers ? firstTimers : lastTimers;
		timerToSend = (int) (timer == 0 ? 11000 : 3 * timer);
		return timerToSend;
	}

	private void checkTheTimer(ReceivedToFE messageData, long startTime) {
		long endTime = System.currentTimeMillis() - startTime;
		System.out.println("Check Timer " + messageData.getFromMessage() + " " + counter++);
		switch (messageData.getFromMessage().toUpperCase()) {
		case CommonUtils.FRONT_END_HOSTNAME:
			if (endTime > replicaOneTimer)
				replicaOneTimer = endTime;
			break;
		case CommonUtils.SEQUENCER_HOSTNAME:
			if (endTime > replicaTwoTimer)
				replicaTwoTimer = endTime;
			break;
		case CommonUtils.REPLICA3_HOSTNAME:
			if (endTime > replicaThreeTimer)
				replicaThreeTimer = endTime;
			break;

		default:
			break;
		}
	}

	private void informSoftwareBug(String receivedToFE) {
		String sendMessage = CommonUtils.RESULT_ERROR;
		System.out.println("Informing software bug to " + receivedToFE);
		try (DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(receivedToFE.toUpperCase());
			DatagramPacket sendPacket = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, host,
					CommonUtils.TO_REPLICA_STRING_PORT);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Checking the message received by the FE that needs to be sent to the client
	private String checkMessagesToSendToClient(List<ReceivedToFE> dataRecieved, long startTime) {
		String response = null;
		String[] replicasNames = { CommonUtils.FRONT_END_HOSTNAME, CommonUtils.SEQUENCER_HOSTNAME,
				CommonUtils.REPLICA3_HOSTNAME };

		Map<String, ReceivedToFE> receivedMessages = dataRecieved.stream().map(data -> data)
				.collect(Collectors.toMap(ReceivedToFE::getFromMessage, Function.identity()));

		for (Entry<String, ReceivedToFE> entry : receivedMessages.entrySet()) {
			String key = entry.getKey();
			ReceivedToFE value = entry.getValue();

			System.out.println("FrontEnd.FrontEndImpl.checkMessagesToSendToClient()");
			System.out.println("Sender Name: " + key);
			System.out.println("Value: " + value);

		}
		boolean hasToWait = true;
		// if one crashed
		if (dataRecieved.size() >= 2) {
			boolean crashinform = false;
			for (int index = 0; index < replicasNames.length; index++) {

				// Basically checking if one of the repllica has not sent a message then
				if (!receivedMessages.containsKey(replicasNames[index])) {
					// start the waiting process and wait for a reply for some time
					hasToWait = checkWhetherToWaitForMessage(startTime, replicasNames[index]);
					if (!hasToWait) { // if you dont need to wait example a timeout has occured

						// inform the replica about the crash after waiting
						informReplicasAboutCrash(replicasNames[index]);
						crashinform = true;
					}
				}
			}
			// if one is crashed and other 2 gave messages or all gave messages after
			// waiting
			if (dataRecieved.size() >= 3 || (crashinform && dataRecieved.size() == 2)) {// replca count - 1
				Map<String, List<ReceivedToFE>> messagesReceived = dataRecieved.stream()
						.collect(Collectors.groupingBy(ReceivedToFE::getMessage));
				for (Entry<String, List<ReceivedToFE>> message : messagesReceived.entrySet()) {
					System.out.println(message.getKey().trim() + " " + message.getValue().size());
					if (message.getValue().size() >= 1) {// replicas count - 2
						response = message.getKey();
					} else {
						//(message.getValue().get(0));
					}
				}
				for (Entry<String, List<ReceivedToFE>> entry : messagesReceived.entrySet()) {
					String key = entry.getKey();
					List<ReceivedToFE> value = entry.getValue();
					System.out.println("FrontEnd.FrontEndImpl.checkMessagesToSendToClient()");
					System.out.println("key: " + key);
					System.out.println("Value: " + value);
				}
			}
			if (Objects.nonNull(response))
				response = response.trim();
		}
		return response;
	}

	private void informReplicasAboutCrash(String replicasName) {
		System.out.println("Informing crash to replica " + replicasName);
		String sendMessage = CommonUtils.CRASHED.concat(",").concat(replicasName);
		try (DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(replicasName);
			DatagramPacket sendPacket = new DatagramPacket(sendMessage.getBytes(), sendMessage.getBytes().length, host,
					CommonUtils.TO_REPLICA_STRING_PORT);
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean checkWhetherToWaitForMessage(long startTime, String replicasName) {
		long timeDifference = System.currentTimeMillis() - startTime;
		System.out.println(replicasName + " " + timeDifference);
		long timer = getReplicaTimer(replicasName);
		if ((timer != 0 && timeDifference > 2 * timer) || (timer == 0 && timeDifference > getTimeOutTimer()))
			return false;
		return true;
	}

	private long getReplicaTimer(String replicasName) {
		switch (replicasName) {
		case CommonUtils.FRONT_END_HOSTNAME:
			return replicaOneTimer;
		case CommonUtils.SEQUENCER_HOSTNAME:
			return replicaTwoTimer;
		case CommonUtils.REPLICA3_HOSTNAME:
			return replicaThreeTimer;
		default:
			break;
		}
		return 0;
	}

}
