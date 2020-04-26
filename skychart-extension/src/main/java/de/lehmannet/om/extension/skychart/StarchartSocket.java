package de.lehmannet.om.extension.skychart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarchartSocket extends Socket {

        public static final String SERVER_RESPONSE_OK = "OK!";
        private static final String SERVER_RESPONSE_FAILED = "Failed!";
        private static final String SERVER_RESPONSE_NOTFOUND = "Not found!";
    
    
        private PrintWriter out = null;
        private BufferedReader in = null;

        private static final Logger LOGGER = LoggerFactory.getLogger(StarchartSocket.class);
    
        public StarchartSocket(String ip, int port) throws IOException {
    
            super(ip, port);
    
          
    
            this.out = new PrintWriter(super.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(super.getInputStream()));
    
            String response = this.in.readLine();
            
            LOGGER.debug("Socket creation response from Skychart: {}", response);
            
    
        }
    
        public String send(String command) throws IOException {
    
            // Add CR+LF (Byte 10 and 13) to end of command as PrintWriter.println()
            // uses system
            // line separator which is 13+10 on windows and e.g. only 10 on Linux.
            // Skycharts expects 13+10 so we've to make sure the CR+LF comes as
            // expected to Skychart
            byte[] b = command.getBytes();
            byte[] lfB = new byte[b.length + 2];
            System.arraycopy(b, 0, lfB, 0, b.length);
            lfB[lfB.length - 2] = 13;
            lfB[lfB.length - 1] = 10;
            command = new String(lfB);
            StringBuilder byteString = new StringBuilder();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Skychart command is: {}" , command);
                LOGGER.debug("Skychart command as byte array: ");
                for (byte value : lfB) {
                    byteString.append(" ").append(value);
                }
                LOGGER.debug(byteString.toString());
            }
    
            // Send the data
            this.out.print(command);
            this.out.flush();
    
            // Get the response
            String r = "";
            StringBuilder response = new StringBuilder();
    
            // Check the response and wait on OK or Failure message from Skychart
            int index = 0;
            do {
                r = this.in.readLine();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Skychart response: {}" , r);
                }
                response.append(r);
                index++;
            } while ((r != null && (!r.contains(StarchartSocket.SERVER_RESPONSE_OK))
                    && (!r.contains(StarchartSocket.SERVER_RESPONSE_NOTFOUND))
                    && (!r.contains(StarchartSocket.SERVER_RESPONSE_FAILED))) && (index <= 3) // Wait for 3 responses
                                                                                              // for a OK or Failure
                                                                                              // from
                                                                                              // Skychart
            );
    
            return response.toString();
    
        }
    
        @Override
        public void close() throws IOException {
    
            try {
                if (out != null) {
                    this.out.close();
                }
                if (in != null) {
                    this.in.close();
                }
            } catch (IllegalStateException ise) {
                // Readers and writers cannot be closed...can't do anything here
            }
    
            super.close();
    
        }
    
    

}