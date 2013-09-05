package org.aitek.controller.mede8er.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import org.aitek.controller.activities.MainActivity;
import org.aitek.controller.mede8er.Command;
import org.aitek.controller.utils.Constants;
import org.aitek.controller.utils.Logger;

import java.io.*;
import java.net.*;

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

    public Mede8erConnector(Context context) {
        this.context = context;
        this.inetAddress = getMede8erAddressFromPreferences();
        Logger.toast("IP loaded from config: " + inetAddress, context);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        NetworkConnectorTask task = new NetworkConnectorTask();
        task.doInBackground(new String[] {});
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

    public String getMede8erIpAddress() throws Exception {

        String helloCommand = Command.HELLO.toString().toLowerCase() + " thisis " + Constants.APP;
        DatagramSocket socket = new DatagramSocket(Constants.UDP_PORT);
        socket.setBroadcast(true);
        DatagramPacket packet = new DatagramPacket(helloCommand.getBytes(), helloCommand.length(), InetAddress.getByName("192.168.1.4"), Constants.UDP_PORT);
        socket.send(packet);

        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        Logger.toast("IP: " + packet.getAddress().getHostAddress() + " response=" + new String(packet.getData()), context);
        return packet.getAddress().getHostAddress();
    }

    public Response send(Command command, String content) throws Exception {

        String request = command.toString().toLowerCase() + " " + content;
        return getResponseFromMessage(tcpClient.sendMessage(request));
    }

    private Response getResponseFromMessage(String message) throws Exception {

        Response.Value value = Response.Value.OK;
        if (message.startsWith("err_") || message.equals("empty")) {
            value = Response.Value.valueOf(message.toUpperCase());
        }
        else {
            message = getHttpResource(message);
        }
        return new Response(value, message);
    }

    private String getHttpResource(String uri) throws Exception {

        URL url = new URL("http://" + inetAddress + "/" + uri);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        }
        finally {
            urlConnection.disconnect();
        }
    }

    private String readStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder streamContent = new StringBuilder();

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((streamContent.append(bufferedReader.readLine())) != null) ;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return streamContent.toString();
    }

    private class NetworkConnectorTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                if (inetAddress == null) {

                    inetAddress = getMede8erIpAddress();
                    saveMede8erIpAddress(inetAddress);
                }

                tcpClient = new TcpClient(inetAddress, Constants.TCP_PORT);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return "working..";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
}
