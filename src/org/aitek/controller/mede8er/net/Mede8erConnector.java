package org.aitek.controller.mede8er.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
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

import static org.aitek.controller.mede8er.Status.DOWN;

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


    public Mede8erConnector(Context context) {
        Logger.log("creating conenctor");
        this.context = context;
        this.inetAddress = getMede8erAddressFromPreferences();
        connectToMede8er(true);
        Logger.log("creating conenctor");
    }

    public void connectToMede8er(boolean asNewThread) {
        if (asNewThread) {
            Logger.log("new thread for connecting");
            NetworkConnectorTask task = new NetworkConnectorTask();
            task.execute(new String[]{});
        } else {
            try {
                Logger.log("direct connection");
                connect();
                Logger.log("direct connected!!");

            }
            catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    private void saveMede8erIpAddress(String inetAddress) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(Constants.PREFERENCES_MEDE8ER_IPADDRESS, inetAddress);
        preferences.edit().commit();
    }

    private String getMede8erAddressFromPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.PREFERENCES_MEDE8ER_IPADDRESS, inetAddress);
    }

    @SuppressWarnings("deprecation")
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi.getDhcpInfo();
        if (dhcpInfo == null) {
            // TODO handle this!
            Logger.log("Error from Dhcpinfo()");
        }

        int broadcast = (dhcpInfo.ipAddress & dhcpInfo.netmask) | ~dhcpInfo.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    public String retrieveMede8erIpAddress() throws IOException {

        String mede8erAddress = null;
        DatagramSocket socket = null;
        DatagramPacket packet;

        try {
            String helloCommand = Command.HELLO.toString().toLowerCase() + " thisis " + " v" + Constants.APP_VERSION;
            socket = new DatagramSocket(Constants.UDP_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(10000);
            packet = new DatagramPacket(helloCommand.getBytes(), helloCommand.length(), getBroadcastAddress(), Constants.UDP_PORT);
            Logger.log("sending [" + helloCommand + "] to broadcast");
            socket.send(packet);
            Logger.log("sent [" + helloCommand + "] ");
            try {
                while (true) {
                    byte[] buf = new byte[1024];
                    packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    mede8erAddress = ((InetSocketAddress) packet.getSocketAddress()).getAddress().getHostAddress();
                    String localAddress = getLocalIP();
                    Logger.log("local:" + localAddress + " mede:" + mede8erAddress);

                    if (!mede8erAddress.equals(localAddress)) {
                        Logger.log("Mede8er IP sock:" + packet.getSocketAddress());
                        isUp = true;
                        return mede8erAddress;
                    }
                }
            }
            catch (SocketTimeoutException ste) {
                Logger.log("Receive timed out");
                isUp = false;
                throw new IOException("Mede8er not found on the network.");
            }
        }
        finally {
            Logger.log("finally executed.");
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
        Response response = getResponseFromMessage(tcpClient.sendMessage(request));
        Logger.log("Request: [" + request + "]");
        Logger.log("Response: [" + response.getContent() + "]");
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

        Response.Value value = Response.Value.OK;
        if (message.startsWith("err_") || message.equals("empty") || message.startsWith("fail") || message.equals("ok")) {
            value = Response.Value.valueOf(message.toUpperCase());
        } else {
            message = getHttpResource(message);
        }
        return new Response(value, message);
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
                Logger.log("read " + length + " bytes.");
                content.append(new String(buffer).substring(0, length));
                try {
                    new JSONObject(content.toString());
                    completed = true;
                }
                catch (JSONException e) {

                }
            }
            Logger.log("Http content: [" + content.toString() + "]");
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

            Logger.log("getting IP address");
            inetAddress = retrieveMede8erIpAddress();
            Logger.log("Inet address=" + inetAddress);
            saveMede8erIpAddress(inetAddress);
        }
        Logger.log("alreay Inet address=" + inetAddress);

        tcpClient = new TcpClient(inetAddress, Constants.TCP_PORT);
        isConnected = true;
    }

    private class NetworkConnectorTask extends AsyncTask<String, Void, String> {

        private ProgressDialog searchingMede8erProgress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Logger.log("network connector pre execution");
        }

        @Override
        protected String doInBackground(String... params) {

            Logger.log("network connector starting..");
            Looper.prepare();
            //searchingMede8erProgress = ProgressDialog.show(context, "Network discovery", "Searching mede8er on wifi network..");
            Logger.log("shown dialog");
            try {
                Logger.log("connecting..");
                connect();
                //searchingMede8erProgress.dismiss();
                Logger.log("dialog dismissed..");
            }
            catch (Exception e) {
                //searchingMede8erProgress.dismiss();
                isConnected = false;
                Handler dialogHandler = ((MainActivity) context).getDialogHandler();
                dialogHandler.sendMessage(Message.obtain(dialogHandler, DOWN));
            }

            return "working..";
        }
    }
}
