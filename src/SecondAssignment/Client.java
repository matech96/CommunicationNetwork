package SecondAssignment;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SecondAssignment.Client class representing the client side of the communication.
 *
 * @author dawars
 */
public class Client {

    private static final Logger LOGGER = Logger.getLogger("CompNet2"); // A logger object to perform logging.
    private DatagramSocket socket = null; // The UDP socket used for communication.
    private DatagramPacket dp = null; // A Datagram packet, that can be send via the DatagramSocket.
    private InetAddress destinationAddress = null; // The destination address of the communication.
    private int destinationPort = 60000; // The destination port of the communication
    private String sender = "CTE1MR"; // Should be NEPTUN code of the author
    private byte[] outBuf = null; // A byte buffer to store the outgoing objects.
    private byte[] inBuf = null; // A byte buffer to store the incoming objects.
    private String receivedData = null; // A string to store the received data (not necessary to use).

    private int SEQ = 0;
    private int ACK = 0;

    /**
     * Reads a TCPSegment from socket
     *
     * @return TCPSegment or null, if there was an error
     */
    private TCPSegment receiveSegment() throws SocketTimeoutException {
        TCPSegment segment = null;
        try {
            DatagramPacket packet = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(packet);

            segment = deserializeSegment(packet.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(segment);
        return segment;
    }

    /**
     * Waits for the segment which has the same sequence number as ACK. If it is different sends an empty segment
     * with an acknowledgement number.
     *
     * @return The received TCPSegment
     * @throws SocketTimeoutException
     */
    private TCPSegment receiveCorrectSegment() throws SocketTimeoutException {
        int receivedSeq = -1;
        TCPSegment received = null;
        while (receivedSeq != ACK) {
            received = receiveSegment();
            receivedSeq = received.getSequenceNumber();
            if (receivedSeq != ACK) {
                System.out.println("Invalid segment received!");
                System.out.println("Expected sequence number: " + ACK);
                System.out.println("Received sequence number: " + receivedSeq);
                //Resending ACK number
                TCPSegment emptySegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, true, false, false, false, "");
                sendSegment(emptySegment);
            }
        }
        if (received != null)
            ACK = received.getSequenceNumber() + 1;
        return received;
    }

    /**
     * Serialize TCPSegment and send it through the socket
     */
    private void sendSegment(TCPSegment segment) {
        System.out.println(segment);
        outBuf = serializeSegment(segment);
        dp.setData(outBuf);

        try {
            socket.send(dp);
            SEQ++;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Uses receiveCorrectSegment() and sends the ACK segment back.
     *
     * @return The received TCPSegment
     * @throws SocketTimeoutException
     */
    private TCPSegment receiveSegmentAndSendAck() throws SocketTimeoutException {
        TCPSegment received = receiveCorrectSegment();
        TCPSegment ackSegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK,
                false, true, false, false, "");
        sendSegment(ackSegment);

        return received;
    }

    /**
     * Initializes the SecondAssignment.Client object.
     */
    public void initialise() {
        receivedData = "";
        try {
            destinationAddress = InetAddress.getByName("152.66.249.135");
            socket = new DatagramSocket();

            dp = new DatagramPacket(new byte[]{}, 0);
            dp.setAddress(destinationAddress);
            dp.setPort(destinationPort);

            inBuf = new byte[1024];
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method serializes a SecondAssignment.TCPSegment object to byte array.
     *
     * @param packet The SecondAssignment.TCPSegment object, that will be serialized.
     * @return A byte array containing the serialized object.
     */
    public byte[] serializeSegment(TCPSegment packet) {
        try {
            // Serializing the packet
            ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(packet);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Serialization problem: {0}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * This method deserializes a byte array to a SecondAssignment.TCPSegment object.
     *
     * @param buf The byte array containing a serialized object.
     * @return A SecondAssignment.TCPSegment object.
     */
    public TCPSegment deserializeSegment(byte[] buf) {
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "ObjectInputStream error: {0}", e.getLocalizedMessage());
        }

        TCPSegment recivedPacket = null;
        try {
            recivedPacket = (TCPSegment) ois.readObject();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Read object error: {0}", e.getLocalizedMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Class not found: {0}", e.getLocalizedMessage());
        }

        return recivedPacket;
    }

    /**
     * Performs 3-Way Handshake with the server to establish a virtual TCP connection.
     */
    private void performHandShake() throws SocketTimeoutException {
        TCPSegment synPacked = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, true, false, false, false, "");
        TCPSegment synack = null;
        do {
            // ACK
            sendSegment(synPacked);
            // SYNACK - ACK
            synack = receiveSegment();
            System.out.println(synack.isACKflag());
            System.out.println(synack.isSYNflag());
        } while (!synack.isSYNflag() || !synack.isACKflag()); // while the response is not SYNACK
        ACK = synack.getSequenceNumber() + 1;
        TCPSegment ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, false, true, false, false, "");
        sendSegment(ackPacked);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        socket.close(); // at the end of the program close the socket
    }

    /**
     * Receives data from the server. (20 times 10 characters.)
     */
    public void reciveData() throws SocketTimeoutException {
        while (receivedData.length() < 200) { // check to see if we are waiting for more data
            TCPSegment received = receiveSegmentAndSendAck();
            receivedData += received.getData();
        }

        System.out.println(receivedData);
    }

    /**
     * Terminates the virtual TCP connection with the server.
     */
    private void terminateConnection() throws SocketTimeoutException {
        TCPSegment finSegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK,
                false, false, true, false, "");
        TCPSegment received = null;
        do { // send FIN until we receive an ACK
            sendSegment(finSegment);
            received = receiveSegment();
        } while (!received.isACKflag());
        System.out.println("fin ack");
        do {// wait for a FIN packet
            received = receiveSegment();
        } while (!received.isFINflag());
        TCPSegment ackSegment = new TCPSegment(sender, String.valueOf(destinationPort), received.getAcknowledgeNumber(),
                received.getSequenceNumber() + 1,
                false, true, false, false, "");
        sendSegment(ackSegment);

    }

    /**
     * Sends back the previously received data via a TCP connection.
     */
    private void sendDataBackWithTCP() {
        try {
            Socket socket = new Socket(destinationAddress, destinationPort);
            new ObjectOutputStream(socket.getOutputStream()).writeObject(receivedData); // serialize the String object

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main function of the assignment.
     *
     * @param args
     */
    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.initialise();
            client.performHandShake();
            client.reciveData();
            client.terminateConnection();
            client.sendDataBackWithTCP();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }

    }
}