package hu.bme.crysys.compnet;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;

/**
 * This class has to be completed by the students as their first assignment.
 */
public class StudentWebServer extends Thread {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    public StudentWebServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException ioE) {
            System.out.println("Fatal error: " + ioE.getLocalizedMessage());
            return;
        }
        webServer();
    }

    /**
     * Reads the incoming data from the socket and replies accordingly
     */
    private void webServer() {
        try {
            String page = "";
            String method = "";
            boolean isHead = false;

            int responseCode = 200;


            while (true) {
                String line = in.readLine();
                if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("HEAD")) {
                    page = line.split(" ")[1];  // get the page file from the header
                    method = line.split(" ")[0];// get http method
                }

                // if there is an empty line then break
                if (line.equals(""))
                    break;
            }

            // handling main page
            if (page.equals("") || page.equals("/"))
                page = "/index.html";

            // handling different methods
            switch (method) {
                case "GET":
                    break;
                case "HEAD":
                    isHead = true;
                    break;
                case "POST":
                    // TODO: load some scripts better than PHP
                    break;
                default:
                    responseCode = 501; // not implemented status code
            }

            System.out.println("Sending response to: " + page);

            out.println("HTTP/1.0 " + responseCode);
            out.println("Server: Olcso Apache server");
            out.println("");


            if (responseCode != 200)
                return;

            if (!isHead) {
                File file = new File("www" + page);

                if (!file.exists()) {
                    out.println("404");
                    return;
                }

                BufferedReader fileReader = new BufferedReader(new FileReader(file));

                while (true) {
                    String line = fileReader.readLine();
                    if (line == null)
                        break;

                    out.println(line);
                }
            }

        } catch (IOException e) {
            // Handling exceptions ^^
            e.printStackTrace();
        } finally {

            // flush and close the socket
            out.flush();
            out.close();
        }
    }
}
