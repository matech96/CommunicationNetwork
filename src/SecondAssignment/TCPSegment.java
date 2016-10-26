package SecondAssignment;

import java.io.Serializable;
import java.util.Date;

/**
 * A TCP Segment class representing a simple TCP segment with minor modifications.
 * @author GAndris
 */
public class TCPSegment implements Serializable {
    
    private String destinationPort; // The destination port of the segment. Always should be CompNet2!
    private String sourcePort; // The source port of the segment. Should be the NEPTUN code of the sender!
    private int sequenceNumber; // The sequence number of the segment.
    private int acknowledgeNumber; // The acknowledgement number of the segment
    private boolean SYNflag; // The SYN flag
    private boolean ACKflag; // The ACK flag
    private boolean FINflag; // The FIN flag
    private boolean RSTflag; // The RST flag: resets the communication
    private String data; // A string that stores the segment data.
    private long timestamp; // A timestamp representing the time when the segment was created. (Should be updated uppon sending the segment.)

    /**
     * The constructor for the TCP segment object.
     * @param sourcePort the source port of the segment
     * @param destinationPort the destination port of the segment
     * @param sequenceNumber the sequence number of the segment
     * @param acknowledgeNumber the acknowledge number of the segment
     * @param SYNflag the SYN flag
     * @param ACKflag the ACK flag
     * @param FINflag the FIN flag
     * @param RSTflag the RST flag
     * @param data the data of the segment
     */
    public TCPSegment(String sourcePort, String destinationPort, int sequenceNumber, int acknowledgeNumber, 
            boolean SYNflag, boolean ACKflag, boolean FINflag, boolean RSTflag, String data) {
        this.destinationPort = destinationPort;
        this.sourcePort = sourcePort;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgeNumber = acknowledgeNumber;
        this.SYNflag = SYNflag;
        this.ACKflag = ACKflag;
        this.FINflag = FINflag;
        this.RSTflag = RSTflag;
        this.data = data;
        timestamp = new Date().getTime();
    }
    /**
     * @return The current value of the ACK field.
     */
    public int getAcknowledgeNumber() {
        return acknowledgeNumber;
    }

    /**
     * @return The current value of the destination field.
     */
    public String getDestinationPort() {
        return destinationPort;
    }

    /**
     * @return The current value of the sequence number field.
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return The current value of the source port field.
     */
    public String getSourcePort() {
        return sourcePort;
    }

    /**
     * @return The current value of the ACK flag.
     */
    public boolean isACKflag() {
        return ACKflag;
    }

    /**
     * @return The current value of the FIN flag.
     */
    public boolean isFINflag() {
        return FINflag;
    }

    /**
     * @return The current value of the SYN flag.
     */
    public boolean isSYNflag() {
        return SYNflag;
    }

    /**
     * @return The current value of the RST flag.
     */
    public boolean isRSTflag() {
        return RSTflag;
    }

    /**
     * @return The current value of the data field.
     */
    public String getData() {
        return data;
    }

    /**
     * @return The current value of the timestamp field.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp value of the segment.
     * @param timestamp The current timestamp.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the acknowledgeNumber field.
     * @param acknowledgeNumber The new value of the acknowledgeNumber.
     */
    public void setAcknowledgeNumber(int acknowledgeNumber) {
        this.acknowledgeNumber = acknowledgeNumber;
    }
    
    /**
     * An override of the toString() method.
     * @return A string containing all the information about the package.
     */
    @Override
    public String toString() {
        return "Packet from: " + sourcePort + " to: " + destinationPort + " seq: " + sequenceNumber + " ack: " + acknowledgeNumber + " flags: " + 
                SYNflag + ACKflag + FINflag + " data: " + data;
    }
}