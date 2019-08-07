package ReplicaManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import CommonUtils.CommonUtils;
import Model.EventData;
import Model.MessageData;
import Model.ReceivedToFE;
import Server.MontrealServer;
import Server.OttawaServer;
import Server.TorontoServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReplicaManager {
	static int errorCounter = 0;

    public static void receiveFromSequencer() 
    {
        System.out.println("Getting the request from Sequencer");
        String responseFromServer = "";
        InetAddress address = null;

        try (MulticastSocket clientSocket = new MulticastSocket(CommonUtils.MULTICAST_PORT)) 
        {
            address = InetAddress.getByName(CommonUtils.INET_ADDR);
            clientSocket.joinGroup(address);
            while (true) 
            {
                ReceivedToFE receievdToFE = new ReceivedToFE();
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                clientSocket.receive(request);
                ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(request.getData()));
                
                MessageData messageData = (MessageData) inputStream.readObject();
                messageData.setErrorCounter(errorCounter);
                inputStream.close();
                
                System.out.println(messageData);
                String userID = "";
                try {
                    if (messageData.getCustomerId() != null) 
                    {
                        userID = messageData.getCustomerId().trim();
                    } 
                    else 
                    {
                        userID = messageData.getManagerId().trim();
                    }
                } 
                catch (Exception e) 
                {
                    
                }

                if (userID.substring(0, 3).equalsIgnoreCase(CommonUtils.MONTREAL)) 
                {
                    responseFromServer = new String(sendToServer(messageData, CommonUtils.MONTREAL));
                } 
                else if (userID.substring(0, 3).equalsIgnoreCase(CommonUtils.TORONTO)) 
                {
                    responseFromServer = new String(sendToServer(messageData, CommonUtils.TORONTO));
                } 
                else if (userID.substring(0, 3).equalsIgnoreCase(CommonUtils.OTTAWA)) 
                {
                    responseFromServer = new String(sendToServer(messageData, CommonUtils.OTTAWA));
                }
                
                System.out.println("Response " + responseFromServer.trim());
                if (!responseFromServer.trim().equals("Message Not received")) 
                {
                    receievdToFE.setMessage(responseFromServer);
                    receievdToFE.setSequencerCounter(Integer.toString(messageData.getSequenceCounter()));
                    receievdToFE.setFromMessage(CommonUtils.FRONT_END_HOSTNAME);
                    int portNumber = CommonUtils.FRONT_END_PORT;
                    sendToFrontEnd(receievdToFE, portNumber);
                }
            }

        } 
        catch (SocketException e) 
        {
            System.out.println("Socket: " + e.getMessage());
        } 
        catch (IOException e) 
        {
            System.out.println("IO: " + e.getMessage());
        } 
        catch (ClassNotFoundException ex) 
        {
            Logger.getLogger(ReplicaManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
	private static byte[] sendToServer(MessageData messageData, String serverCode) {
		String response = CommonUtils.EXCEPTION;
		try(DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(1000);
			InetAddress host = InetAddress.getByName(CommonUtils.FRONT_END_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream); 
			objectOutput.writeObject(messageData);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, host, getServerPort(serverCode));
			socket.send(sendPacket);
                           System.out.print("Replica "+CommonUtils.FRONT_END_HOSTNAME+" sending request "+messageData.getMethodName()+" to the local server "+ serverCode);
			byte [] buffer = new byte[1024];
			DatagramPacket receivedDatagram = new DatagramPacket(buffer, buffer.length);
			socket.receive(receivedDatagram);
			return receivedDatagram.getData();
		}catch(SocketTimeoutException exception) {
			response = "Message Not received";
                        System.out.println(exception.toString());
		}catch (IOException e) {
			e.printStackTrace();
		}
		return response.getBytes();
	}

	public static void sendToFrontEnd(ReceivedToFE receivedToFE,int portNumber) throws IOException {
		try(DatagramSocket socket = new DatagramSocket()) {
			InetAddress host = InetAddress.getByName(CommonUtils.FRONT_END_HOSTNAME);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream); 
			objectOutput.writeObject(receivedToFE);
			System.out.println(byteStream.toByteArray().length);
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length,
					host, CommonUtils.FRONT_END_PORT);
			socket.send(sendPacket);
                        System.out.println("The response from the servers is sent to the FE "+"Message: "+receivedToFE.getMessage()+"From: "+receivedToFE.getFromMessage());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private static void handleReplicaToReplicaCommunication() {
		try(DatagramSocket socket = new DatagramSocket(CommonUtils.REPLICA_TO_REPLICA_PORT)) {
			while(true) {
				byte [] message = new byte[3072];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String receivedString = new String(recievedDatagramPacket.getData());
				MessageData messageData = new MessageData();
				messageData.setMethodName(CommonUtils.GET_DATA);
				byte[] receivedObject = sendToServer(messageData, receivedString.trim());
				DatagramPacket returnPacket = new DatagramPacket(receivedObject, receivedObject.length,recievedDatagramPacket.getAddress(), recievedDatagramPacket.getPort());
				socket.send(returnPacket);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void handleStringMessages() {
		try(DatagramSocket socket = new DatagramSocket(CommonUtils.TO_REPLICA_STRING_PORT)) { 
			while(true) {
				byte [] message = new byte[1000];
				DatagramPacket recievedDatagramPacket = new DatagramPacket(message, message.length);
				socket.receive(recievedDatagramPacket);
				String receivedData = new String(recievedDatagramPacket.getData());
				if(receivedData.trim().equals(CommonUtils.RESULT_ERROR)) {
					errorCounter++;
					MontrealServer.isFT = true;
					OttawaServer.isFT = true;
					TorontoServer.isFT = true;
				}else if(receivedData.trim().contains(CommonUtils.CRASHED)){
					String [] crashedReplica = receivedData.split(",");
					String replicaName = crashedReplica[1].trim();
					if(replicaName.equals(CommonUtils.FRONT_END_HOSTNAME)) {//change according to replica
						checkAndStartTheReplicas();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private static void checkAndStartTheReplicas() 
    {
        String[] replicaPorts = {CommonUtils.MONTREAL, CommonUtils.TORONTO, CommonUtils.OTTAWA};
        for (String serverCode : replicaPorts) 
        {
            MessageData messageData = new MessageData();
            messageData.setMethodName(CommonUtils.CRASHED);
            String response = new String(sendToServer(messageData, serverCode));
            if (!response.equals(CommonUtils.ALIVE)) 
            {
                MessageData data = new MessageData();
                data.setMethodName(CommonUtils.GET_DATA);
                EventData eventData = sendEventDataToReplica(serverCode);
                startTheServer(serverCode);
                try (DatagramSocket socket = new DatagramSocket()) 
                {
                    InetAddress host = InetAddress.getByName(CommonUtils.FRONT_END_HOSTNAME); //Varies per Replica
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
                    objectOutput.writeObject(eventData);
                    DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, host, CommonUtils.RECEIVE_DATA_FROM_REPLICA_PORT);
                    socket.send(sendPacket);
                } catch (IOException e) 
                {

                } finally {
                    System.out.println("Send data to Replica");
                }
            }
        }
    }

    private static EventData sendEventDataToReplica(String serverCode) 
    {
        EventData messageData = null;
        try (DatagramSocket socket = new DatagramSocket()) 
        {
            InetAddress host = InetAddress.getByName(CommonUtils.FRONT_END_HOSTNAME); //Varies per Replica
            DatagramPacket sendPacket = new DatagramPacket(serverCode.getBytes(), serverCode.getBytes().length, host, CommonUtils.REPLICA_TO_REPLICA_PORT);
            socket.send(sendPacket);
            
            byte[] buffer = new byte[5000];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(receivePacket);
            
            try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData()))) 
            {
                messageData = (EventData) inputStream.readObject();
            }
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            
        }
        finally
        {
            return messageData;
        }
    }

    private static void startTheServer(String serverCode) 
    {
        String[] stringArray = {"CRASH_START"};
        switch (serverCode) 
        {
            case CommonUtils.MONTREAL: MontrealServer.main(stringArray);
            case CommonUtils.TORONTO: TorontoServer.main(stringArray);
            case CommonUtils.OTTAWA: OttawaServer.main(stringArray);
        }
    }
    
    private static int getServerPort(String serverCode) 
    {
        switch (serverCode) 
        {
            case CommonUtils.MONTREAL: return CommonUtils.REPLICA_MONTREAL_SERVER_PORT;
            case CommonUtils.OTTAWA: return CommonUtils.REPLICA_OTTAWA_SERVER_PORT;
            case CommonUtils.TORONTO: return CommonUtils.REPLICA_TORONTO_SERVER_PORT;
        }
        return 0;
    }
    
    public static void main(String[] args) 
    {
        Runnable receiveFromSequencer = () -> {
            receiveFromSequencer();
        };
        Runnable receiveStringMessages = () -> {
            handleStringMessages();
        };
        Runnable receiveMessageFromReplicas = () -> {
            handleReplicaToReplicaCommunication();
        };
        new Thread(receiveFromSequencer).start();
        new Thread(receiveStringMessages).start();
        new Thread(receiveMessageFromReplicas).start();
    }
}

