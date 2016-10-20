package SecondAssignment;

import java.io.*;
import java.net.*;
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
    private String recivedData = null; // A string to store the recived data (not neccessery to use).

    /**
     * Initializes the SecondAssignment.Client object.
     */
    public void initialise() {
        recivedData = "";
        try {
            destinationAddress = InetAddress.getByName("152.66.249.135");
            socket = new DatagramSocket();

            dp = new DatagramPacket(new byte[]{}, 0);
            dp.setAddress(destinationAddress);
            dp.setPort(destinationPort);

            inBuf = new byte[1024];

        } catch (SocketException e) {
            e.printStackTrace();
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
            LOGGER.log(Level.WARNING, "ObjectInputStreem error: {0}", e.getLocalizedMessage());
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


    }

    @Override
    protected void finalize() throws Throwable {
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
    }

    /**
     * Terminates the virtual TCP connection with the server.
     */

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
    }

    /**
     * Sends back the previously received data via a TCP connection.
     */
    private void sendDataBackWithTCP() {
        try {
            Socket socket = new Socket(destinationAddress, destinationPort);
            new ObjectOutputStream(socket.getOutputStream()).writeObject(recivedData);

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
        client.initialise();
        client.performHandShake();//done
        client.reciveData();
        client.terminateConnection();
        client.sendDataBackWithTCP();

    }
}
