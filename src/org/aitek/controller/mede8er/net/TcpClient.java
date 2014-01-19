package org.aitek.controller.mede8er.net;

import org.aitek.controller.utils.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/29/13
 * Time: 10:34 AM
 */
public class TcpClient {

    private Socket socket;
    private InputStreamReader inputStream;
    private PrintWriter outputStream;


    public TcpClient(String mede8erAddress, int port) throws IOException {

        // creates the socket to the mede8er and set the streams
        socket = new Socket(mede8erAddress, port);
        outputStream = new PrintWriter(socket.getOutputStream());
        inputStream = new InputStreamReader(socket.getInputStream());
    }

    /**
     * sends a request to the mede8er
     *
     * @param request the request to be sent ot the mede8er
     */
    public String sendMessage(String request) throws IOException {

        // sends the request to the mede8er
        outputStream.println(request);
        outputStream.flush();

        // reads the response
        char[] buffer = new char[1024];
        int length = inputStream.read(buffer);
        String response = "";
        if (length > 0) {
            response = new String(buffer).substring(0, length);
        }

        return response;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}