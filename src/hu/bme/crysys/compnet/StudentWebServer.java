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
            int post_data_length = -1;   //if method is POST the length of the POST data should also set

            int responseCode = 200;

            //read
            while (true) {
                String line = in.readLine();
                if (line.startsWith("GET ") || line.startsWith("POST ") || line.startsWith("HEAD ")) {
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

            // handling main page
            if (page.equals("") || page.equals("/"))
                page = "/index.html";

            //files are stored in the "www" directory
            String directory = "www";
            File file = new File(directory + page);

            //Set responseCode
            if (method.equals("")) {
                responseCode = 501; // not implemented status code
            } else if (method.equals("POST") && post_data_length == -1){
                responseCode = 400;  //bad request
            } else if (!file.exists()) {
                responseCode = 404; //resource cannot be found
                file = new File(directory + "/404.jpg"); //change the file
            }

            // collect POST data to process in a further implementation
            String post_data = "";
            for (int i = 0; i < post_data_length; i++) {
                int character = in.read();
                post_data += (char)character;
                //System.out.print((char)character);
            }

            System.out.println();
            System.out.println("Sending response to: " + page);

            // send header
            out.println("HTTP/1.0 " + responseCode);
            out.println("Server: Olcso Apache server");

            //if method is GET or POST, send something back
            if (method.equals("GET") || method.equals("POST")) {
                //separate the header and the content
                out.println("");


                FileInputStream fr = null;
                OutputStream os = null;
                try {
                    fr = new FileInputStream(file);
                    os = socket.getOutputStream();  //PrintWriter wont be god in we want to send binary f.e.: pictures
                    int c;

                    while ((c = fr.read()) != -1) {
                        os.write(c);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //close streams if they have been created
                    if (fr != null)
                        fr.close();
                    if (os != null)
                        os.close();
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
