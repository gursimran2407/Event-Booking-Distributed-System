package Model;

import java.io.Serializable;

public class MessageData implements Serializable {

	private String customerIDName;
	private String managerIDName;
	private String eventTypeName;
	private String eventIDName;
	private String bookingCap;
	private  String new_EventID;
	private  String old_EventID;
        String action;
	
	
	public String getOld_EventID() {
		return old_EventID;
	}

	public MessageData setOld_EventID(String old_EventID) {
		this.old_EventID = old_EventID;
		return this;
	}

	public String getOld_EventType() {
		return old_EventType;
	}

	public MessageData setOld_EventType(String old_EventType) {
		this.old_EventType = old_EventType;
		return this;
	}

	private  String old_EventType;
    private String new_EventType;
	private String methodName;
	private boolean isFromOtherServer;
	private int sequenceCounter;
	private int errorCounter;
	
	public int getErrorCounter() {
		return errorCounter;
	}

	public void setErrorCounter(int errorCounter) {
		this.errorCounter = errorCounter;
	}

	public String getNewEventId() {
		return new_EventID;
	}

	public MessageData setNewEventId(String newItemId) {
		this.new_EventID = newItemId;
		return this;
	}
	public String getNewEventType() {
		return new_EventType;
	}

	public MessageData setNewEventType(String newItemId) {
		this.new_EventType = newItemId;
		return this;
	}

	public boolean isFromOtherServer() {
		return isFromOtherServer;
	}

	public MessageData setFromOtherServer(boolean isFromOtherServer) {
		this.isFromOtherServer = isFromOtherServer;
		return this;
	}

	public String getCustomerId() {
		return customerIDName;
	}
	public String getManagerId() {
		return managerIDName;
	}

	public String getEventId() {
		return eventIDName;
	}

	public String getEventType() {
		return eventTypeName;
	}


	
	public String getBookingCap() {
		return bookingCap;
	}


	public String getMethodName() {
		return methodName;
	}

	public int getSequenceCounter() {
		return sequenceCounter;
	}

	public void setSequenceCounter(int sequenceCounter) {
		this.sequenceCounter = sequenceCounter;
	}

	public MessageData setCustomerId(String userId) {
		this.customerIDName = userId;
		return this;
	}

	public MessageData setManagerId(String userId) {
		this.managerIDName = userId;
		return this;
	}

	public MessageData setEventId(String itemId) {
		this.eventIDName = itemId;
		return this;
	}

	public MessageData setEventType(String itemName) {
		this.eventTypeName = itemName;
		return this;
	}

	public MessageData setBookingCap(String quantity) {
		this.bookingCap = quantity;
		return this;
	}

	public MessageData setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}

    public String getAction() {
        return action;
    }

public MessageData setAction(String action) {
        this.action = action;
        return this;
    }
        

	@Override
	public String toString() {
		return "MessageData{" +
				"customerIDName='" + customerIDName + '\'' +
				", managerIDName='" + managerIDName + '\'' +
				", eventTypeName='" + eventTypeName + '\'' +
				", eventIDName='" + eventIDName + '\'' +
				", bookingCap='" + bookingCap + '\'' +
				", new_EventID='" + new_EventID + '\'' +
				", old_EventID='" + old_EventID + '\'' +
				", old_EventType='" + old_EventType + '\'' +
				", new_EventType='" + new_EventType + '\'' +
				", methodName='" + methodName + '\'' +
				", isFromOtherServer=" + isFromOtherServer +
				", sequenceCounter=" + sequenceCounter +
				", errorCounter=" + errorCounter +
				'}';
	}
}
