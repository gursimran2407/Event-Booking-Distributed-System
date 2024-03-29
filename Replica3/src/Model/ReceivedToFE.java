package Model;

import java.io.Serializable;

public class ReceivedToFE implements Serializable{
	
	private String fromMessage;
	private String message;
	private String sequencerCounter;
	private int errorCount;
	
	public int getErrorCount() {
		return errorCount;
	}
	public String getFromMessage() {
		return fromMessage.trim();
	}
	public void setFromMessage(String fromMessage) {
		this.fromMessage = fromMessage;
	}
	public String getMessage() {
		return message.trim();
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSequencerCounter() {
		return sequencerCounter.trim();
	}
	public void setSequencerCounter(String sequencerCounter) {
		this.sequencerCounter = sequencerCounter;
	}

    @Override
    public String toString() {
        return "ReceivedToFE{" + "fromMessage=" + fromMessage + ", message=" + message + ", sequencerCounter=" + sequencerCounter + ", errorCount=" + errorCount + '}';
    }
        
        
}
