package org.aitek.movies.net;

import org.aitek.movies.utils.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/29/13
 * Time: 10:34 AM
 */
public class TcpClient {

    private InetAddress mede8erAddress;
    private Socket socket;
    private int port;
    private BufferedReader inputStream;
    private PrintWriter outputStream;


    public TcpClient(InetAddress mede8erAddress, int port) throws Exception {

        this.mede8erAddress = mede8erAddress;
        this.port = port;

        // creates the socket to the mede8er
        socket = new Socket(mede8erAddress.getHostAddress(), port);
        outputStream = new PrintWriter(socket.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * sends a request to the mede8er
     *
     * @param request the request to be sent ot the mede8er
     */
    public String sendMessage(String request) throws Exception {

        // sends the request to the mede8er
        outputStream.println(request);
        Logger.log("Client request: [" + request + "]");

        // reads the response
        String response = inputStream.readLine();
        Logger.log("Mede8er response: [" + response + "]");

        return response;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (socket != null) {
            socket.close();
        }
    }
}