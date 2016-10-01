package hu.bme.crysys.compnet;

import java.io.*;
import java.net.Socket;

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
            String page = "";   //filename of the requested content
            String method = ""; //POST or HEAD or GET or something else, but that is not supported
            int post_data_length = 0;   //if method is POST the length of the POST data should also set

            int responseCode = 200;

            //read
            while (true) {
                String line = in.readLine();
                if (line.startsWith("GET") || line.startsWith("POST") || line.startsWith("HEAD")) {
                    String[] line_split = line.split(" ");
                    page = line_split[1];  // get the page file from the header
                    page = page.split("\\?")[0];  // get rid of GET components
                    method = line_split[0]; // get http method
                }
                if (line.startsWith("Content-Length:") && method.equals("POST")){
                    String[] line_split = line.split(" ");
                    post_data_length = Integer.parseInt(line_split[1]);
                }

                // if there is an empty line then break
                if (line.equals("")) {
                    break;
                }
            }

            // collect POST data to process in a further implementation
            String post_data = "";
            for (int i = 0; i < post_data_length; i++) {
                int character = in.read();
                post_data += (char)character;
            }

            // handling main page
            if (page.equals("") || page.equals("/"))
                page = "/index.html";

            //files are stored in the "www" directory
            String directory = "www";
            File file = new File(directory + page);

            if (method.equals("")) {
                responseCode = 501; // not implemented status code
            } else if (!file.exists()) {
                responseCode = 404; //resource cannot be found
                file = new File(directory + "/404.html"); //change the file
            }

            System.out.println("Sending response to: " + page);

            // send header
            out.println("HTTP/1.0 " + responseCode);
            out.println("Server: Olcso Apache server");

            //if method is GETor POST, send something back
            if (method.equals("GET") && method.equals("POST")) {
                //separate the header and the content
                out.println("");

                //Read the file and send it
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
