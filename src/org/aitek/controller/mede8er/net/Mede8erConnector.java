package org.aitek.controller.mede8er.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.mede8er.Command;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Enumeration;

import static org.aitek.controller.mede8er.Mede8erStatus.DOWN;
import static org.aitek.controller.mede8er.Mede8erStatus.FULLY_OPERATIONAL;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/26/13
 * Time: 11:12 AM
 */
public class Mede8erConnector {

    private TcpClient tcpClient;
    private String inetAddress;
    private Context context;
    private boolean isUp;
    private boolean isConnected;

    public Mede8erConnector(Context context, boolean launch) {
        this.context = context;
        if (launch) {
            connectToMede8er(true);
        }
    }

    public void launchConnector() {
        connectToMede8er(true);
    }

    public void connectToMede8er(boolean asNewThread) {
        if (asNewThread) {
            NetworkConnectorTask task = new NetworkConnectorTask();
            task.execute(new String[]{});
        }
        else {
            try {
                connect();
            }
            catch (Exception e) {
                Logger.both("Error connecting to mede8er: " + e.getMessage(), context);
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi.getDhcpInfo();
        if (dhcpInfo == null) {
            // TODO handle this!
            Logger.both("Error from Dhcpinfo()!", context);
        }

        int broadcast = (dhcpInfo.ipAddress & dhcpInfo.netmask) | ~dhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    public String retrieveMede8erIpAddress() throws IOException {

        String mede8erAddress;
        DatagramSocket socket = null;
        DatagramPacket packet;

        try {
            String helloCommand = Command.HELLO.toString().toLowerCase() + " thisis " + " v" + Constants.APP_VERSION;
            socket = new DatagramSocket(Constants.UDP_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(10000);
            packet = new DatagramPacket(helloCommand.getBytes(), helloCommand.length(), getBroadcastAddress(), Constants.UDP_PORT);
            socket.send(packet);
            try {
                while (true) {
                    byte[] buf = new byte[1024];
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    mede8erAddress = ((InetSocketAddress) packet.getSocketAddress()).getAddress().getHostAddress();
                    String localAddress = getLocalIP();

                    if (!mede8erAddress.equals(localAddress)) {
                        Logger.log("Mede8er IP:" + mede8erAddress);
                        isUp = true;
                        return mede8erAddress;
                    }
                }
            }
            catch (SocketTimeoutException ste) {
                Logger.both("Receive timed out connecting to mede8er.", context);
                isUp = false;
                throw new IOException("Mede8er not found on the network.");
            }
        }
        finally {
            if (socket != null) {
                socket.disconnect();
                socket.close();
            }
        }
    }

    public String getInetAddress() {
        return inetAddress;
    }

    public Response send(String command) throws IOException {

        String request = command;
        if (tcpClient == null) {
            tcpClient = new TcpClient(inetAddress, Constants.TCP_PORT);
        }
        Logger.log("Request: [" + (request.length() > Constants.LOG_MAX_LENGTH ? request.substring(0, Constants.LOG_MAX_LENGTH).replaceAll("\n", "") + "..." : request) + "]");
        Response response = getResponseFromMessage(tcpClient.sendMessage(request));
        Logger.log("Response: [" + (response.getContent().length() > Constants.LOG_MAX_LENGTH ? response.getContent().substring(0, Constants.LOG_MAX_LENGTH).replaceAll("\n", "") + "..." : response.getContent()) + "]");
        return response;
    }

    private String getLocalIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> address = networkInterface.getInetAddresses(); address.hasMoreElements(); ) {
                    InetAddress inetAddress = address.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().length() <= 15) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException ex) {
            Logger.log(ex.toString());
        }
        return "";

    }

    private Response getResponseFromMessage(String message) throws IOException {

        Response.Code code = Response.Code.OK;
        try {
            Long.parseLong(message.replace("/", ""));
            code = Response.Code.STATUS;
            return new Response(code, message);
        }
        catch (NumberFormatException nfe) {

            if (message.startsWith("err_") || message.equals("empty") || message.startsWith("fail") || message.equals("ok")) {
                code = Response.Code.valueOf(message.toUpperCase());
            }
            else {
                message = getHttpResource(message);
            }
            return new Response(code, message);
        }
    }

    private String getHttpResource(String uri) throws IOException {

        URL url = new URL("http://" + inetAddress + "/" + uri);
        Logger.log("Getting URL " + "http://" + inetAddress + "/" + uri);
        StringBuffer content = new StringBuffer();
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
            boolean completed = false;
            while (!completed) {
                char[] buffer = new char[10 * 1024];
                int length = inputStreamReader.read(buffer);
                if (length > 0) {
                    content.append(new String(buffer).substring(0, length));
                    try {
                        new JSONObject(content.toString());
                        completed = true;
                    }
                    catch (JSONException e) {

                    }
                }
                else {
                    completed = true;
                }
            }
            return content.toString();
        }
        finally {
            urlConnection.disconnect();
        }
    }

    public boolean isUp() {
        return isUp;
    }

    public boolean isConnected() {
        return isConnected;
    }

//    private String readStream(InputStream inputStream) {
//        BufferedReader bufferedReader = null;
//        StringBuilder streamContent = new StringBuilder();
//
//        try {
//            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            while ((streamContent.append(bufferedReader.readLine())) != null) ;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (bufferedReader != null) {
//                try {
//                    bufferedReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return streamContent.toString();
//    }

    private void connect() throws Exception {
        if (inetAddress == null) {

            inetAddress = retrieveMede8erIpAddress();
        }
        tcpClient = new TcpClient(inetAddress, Constants.TCP_PORT);
        isConnected = true;
    }

    private class NetworkConnectorTask extends AsyncTask<String, Void, String> {

        private ProgressDialog initialProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            initialProgressDialog = new ProgressDialog(context);
            initialProgressDialog.setTitle("Searching for mede8er on local network");
            initialProgressDialog.setMessage("Please wait...");
            initialProgressDialog.setCancelable(false);
            initialProgressDialog.setIndeterminate(true);
            initialProgressDialog.show();
            Logger.log("Showed ProgressDialog");
        }

        @Override
        protected String doInBackground(String... params) {

            Looper.prepare();
            //searchingMede8erProgress = ProgressDialog.show(context, "Network discovery", "Searching mede8er on wifi network..");
            try {
                connect();
            }
            catch (Exception e) {
                isConnected = false;
                Handler dialogHandler = ((MainActivity) context).getDialogHandler();
                dialogHandler.sendMessage(Message.obtain(dialogHandler, DOWN));
            }
            initialProgressDialog.dismiss();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {

            if (isConnected) {
                Handler dialogHandler = ((MainActivity) context).getDialogHandler();
                dialogHandler.sendMessage(Message.obtain(dialogHandler, FULLY_OPERATIONAL));
            }
        }
    }
}
