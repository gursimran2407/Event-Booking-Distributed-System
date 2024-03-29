package Sequencer;

import CommonUtils.CommonUtils;
import Model.MessageData;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CreateSequence {

	//private static Logger log;
	private static int sequenceCounter = 0;

	
	public static void main(String[] args) throws Exception{

		Runnable task = () -> {
			frontEndToSequencer();
		};
		
		Thread thread = new Thread(task);
		thread.start();
		}

	private static void frontEndToSequencer() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(CommonUtils.SEQUNECER_PORT); //???????????????PORT NUMBERS
//			log.info("SEQUENCER RECEIVED REQUEST.....");
			while (true) {
				System.out.println("started");
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				System.out.println("came");
				ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(request.getData()));
				MessageData messageData = (MessageData) inputStream.readObject();
				inputStream.close();
				System.out.println(messageData);
				sequencerToReplicas(messageData);
//				DatagramPacket reply = new DatagramPacket(response.getBytes(), response.length(), request.getAddress(),
//						request.getPort());
//				aSocket.send(reply);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}
	
	public static String sequencerToReplicas(MessageData messageData) {
		//log.info("Sending request from sequencer to Replica Managers");
		//int serverPort = CommonUtils.REPLICA1_PORT; //*************?????????????????????
		String response = null;
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			messageData.setSequenceCounter(sequenceCounter);
			InetAddress addr = InetAddress.getByName(CommonUtils.INET_ADDR);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			ObjectOutput objectOutput = new ObjectOutputStream(byteStream);
			objectOutput.writeObject(messageData);
			objectOutput.close();
			DatagramPacket sendPacket = new DatagramPacket(byteStream.toByteArray(), byteStream.toByteArray().length, addr, CommonUtils.MULTICAST_PORT);
			aSocket .send(sendPacket);
			sequenceCounter++;
			//log.info("Request send " + sendPacket.getData());
//			byte [] receiveBuffer = new byte[1500];
//			DatagramPacket recievedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
//			aSocket.receive(recievedPacket);
//			response = new String(recievedPacket.getData());
			//log.info("Reply received" + response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (aSocket != null)
				aSocket.close();
		}
		return response;
	}
}
