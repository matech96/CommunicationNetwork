package SecondAssignment;

import java.io.*;
import java.net.*;
<<<<<<< HEAD
import java.util.concurrent.TimeoutException;
=======
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
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
<<<<<<< HEAD
    private String receivedData = null; // A string to store the received data (not necessary to use).

    private int SEQ = 0;
    private int ACK = 0;

    /**
     * Reads a TCPSegment from socket
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

    private TCPSegment receiveCorrectSegment() throws SocketTimeoutException {
        int receivedSeq = -1;
        TCPSegment received = null;
        while (receivedSeq != ACK) {
            received = receiveSegment();
            receivedSeq = received.getSequenceNumber();
            if (receivedSeq != ACK) {
                System.out.println("Invalid segment received!");
                System.out.println(ACK);
                System.out.println(receivedSeq);
                TCPSegment emptySegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, true, false, false, false, "");
                sendSegment(emptySegment);
            }
        }
        ACK = received.getSequenceNumber() + 1;
        return received;
    }

    /**
     * Serialize TCPSegment and send it through socket
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

    private TCPSegment receiveSegmentAndSendAck() throws SocketTimeoutException {
        TCPSegment received = receiveCorrectSegment();
        System.out.println(SEQ);
        System.out.println(ACK);
        TCPSegment ackSegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK,
                false, true, false, false, "");
        sendSegment(ackSegment);

        return received;
    }

    private TCPSegment sendSegmentAndWaitForAck(TCPSegment segment) throws SocketTimeoutException {
        sendSegment(segment);
        int receivedAck = -1;
        return receiveCorrectSegment();
    }
=======
    private String recivedData = null; // A string to store the recived data (not neccessery to use).
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5

    /**
     * Initializes the SecondAssignment.Client object.
     */
    public void initialise() {
<<<<<<< HEAD
        receivedData = "";
=======
        recivedData = "";
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
        try {
            destinationAddress = InetAddress.getByName("152.66.249.135");
            socket = new DatagramSocket();

            dp = new DatagramPacket(new byte[]{}, 0);
            dp.setAddress(destinationAddress);
            dp.setPort(destinationPort);

            inBuf = new byte[1024];
<<<<<<< HEAD
=======

        } catch (SocketException e) {
            e.printStackTrace();
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
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
<<<<<<< HEAD
            LOGGER.log(Level.WARNING, "ObjectInputStream error: {0}", e.getLocalizedMessage());
=======
            LOGGER.log(Level.WARNING, "ObjectInputStreem error: {0}", e.getLocalizedMessage());
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
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
<<<<<<< HEAD
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
        } while (!synack.isSYNflag() || !synack.isACKflag());
        ACK = synack.getSequenceNumber() + 1;
        TCPSegment ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, false, true, false, false, "");
        sendSegment(ackPacked);
=======
    private void performHandShake() {

        TCPSegment synPacked = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK, true, false, false, false, "");

        outBuf = serializeSegment(synPacked);
        dp.setData(outBuf);

        try {
            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // SYNACK
        TCPSegment synack = null;
        try {
            DatagramPacket packet = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(packet);

            synack = deserializeSegment(packet.getData());
            ACK = synack.getSequenceNumber(); //updating ack

            System.out.println("synack");
            System.out.println(synack);

        } catch (IOException e) {
            e.printStackTrace();
        }


        // ACK
        TCPSegment ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), ++SEQ, ++ACK,
                false, true, false, false, "");

        outBuf = serializeSegment(ackPacked);
        dp.setData(outBuf);

        try {
            socket.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ack");


>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
    }

    @Override
    protected void finalize() throws Throwable {
<<<<<<< HEAD
        super.finalize();
        socket.close();
    }

    /**
     * Receives data from the server. (20 times 10 characters.)
     */
    public void reciveData() throws SocketTimeoutException {
        while (receivedData.length() < 200) {
            TCPSegment received = receiveSegmentAndSendAck();
            receivedData += received.getData();
        }

        System.out.println(receivedData);
=======
        socket.close();
    }

    int SEQ = 0;
    int ACK = 0;

    /**
     * Recives data from the server. (20 times 10 characters.)
     */
    public void reciveData() {
        while (recivedData.length() < 200) {
            TCPSegment tcpSegment = null;
            try {
                // receive
                DatagramPacket packet = new DatagramPacket(inBuf, inBuf.length);
                socket.receive(packet);

                tcpSegment = deserializeSegment(packet.getData());
                int seq = tcpSegment.getSequenceNumber();
                if (seq == ACK) {
                    ACK++;
                    recivedData += tcpSegment.getData();
                }
                //send ack

                TCPSegment ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), ++SEQ, ACK,
                        false, true, false, false, "");

                outBuf = serializeSegment(ackPacked);
                dp.setData(outBuf);

                socket.send(dp);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        System.out.println(recivedData);
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
    }

    /**
     * Terminates the virtual TCP connection with the server.
     */

<<<<<<< HEAD
    private void terminateConnection() throws SocketTimeoutException {
        TCPSegment finSegment = new TCPSegment(sender, String.valueOf(destinationPort), SEQ, ACK,
                false, false, true, false, "");
        TCPSegment received = null;
        do {
            sendSegment(finSegment);
            received = receiveSegment();
        } while (!received.isACKflag());
        System.out.println("fin ack");
        do {
            received = receiveSegment();
        } while (!received.isFINflag());
        TCPSegment ackSegment = new TCPSegment(sender, String.valueOf(destinationPort), received.getAcknowledgeNumber(),
                received.getSequenceNumber()+1,
                false, true, false, false, "");
        sendSegment(ackSegment);

=======
    private void terminateConnection() {

        TCPSegment tcpSegment = null;
        try {

            //send fin

            TCPSegment ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), ++SEQ, ACK,
                    false, false, true, false, "");

            outBuf = serializeSegment(ackPacked);
            dp.setData(outBuf);

            socket.send(dp);


            // receive
            DatagramPacket packet = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(packet);

            tcpSegment = deserializeSegment(packet.getData());
            System.out.println("(FIN)ACK");
            System.out.println(tcpSegment);
            int seq = tcpSegment.getSequenceNumber();
            if (seq == ACK) {
                ACK++;
                recivedData += tcpSegment.getData();
            }


            // wait for server fin
            packet = new DatagramPacket(inBuf, inBuf.length);
            socket.receive(packet);

            tcpSegment = deserializeSegment(packet.getData());
            System.out.println("FIN received");
            System.out.println(tcpSegment);
            seq = tcpSegment.getSequenceNumber();
            if (seq == ACK) {
                ACK++;
                recivedData += tcpSegment.getData();
            }

            // ACK send

            ackPacked = new TCPSegment(sender, String.valueOf(destinationPort), ++SEQ, ACK,
                    false, true, false, false, "");

            outBuf = serializeSegment(ackPacked);
            dp.setData(outBuf);

            socket.send(dp);



        } catch (IOException e) {
            e.printStackTrace();
        }
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5
    }

    /**
     * Sends back the previously received data via a TCP connection.
     */
    private void sendDataBackWithTCP() {
        try {
            Socket socket = new Socket(destinationAddress, destinationPort);
<<<<<<< HEAD
            new ObjectOutputStream(socket.getOutputStream()).writeObject(receivedData);
=======
            new ObjectOutputStream(socket.getOutputStream()).writeObject(recivedData);
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5

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
<<<<<<< HEAD
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
=======

        Client client = new Client();
        client.initialise();
        client.performHandShake();//done
        client.reciveData();
        client.terminateConnection();
        client.sendDataBackWithTCP();
>>>>>>> d5ff66a55cab02737b88edb078e97d392a52eba5

    }
}
