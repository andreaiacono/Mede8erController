package org.aitek.movies.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import org.aitek.movies.utils.Constants;
import org.aitek.movies.utils.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: andrea
 * Date: 8/26/13
 * Time: 11:12 AM
 */
public class Mede8erConnector {

    private final TcpClient tcpClient;
    private Activity activity;
    private String inetAddress;

    public Mede8erConnector(Activity activity) throws Exception {

        this.activity = activity;
        this.inetAddress = getMede8erAddressFromPreferences();

        if (inetAddress == null) {

            inetAddress = getMede8erIpAddress();
            saveMede8erIpAddress(inetAddress);
        }

        tcpClient = new TcpClient(inetAddress, Constants.TCP_PORT);
    }


    private void saveMede8erIpAddress(String inetAddress) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PREFERENCES_MEDE8ER_IPADDRESS, inetAddress);
        editor.commit();
    }

    private String getMede8erAddressFromPreferences() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(Constants.PREFERENCES_MEDE8ER_IPADDRESS, null);
    }

    @SuppressWarnings("deprecation")
    private InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi.getDhcpInfo();
        if (dhcpInfo == null) {
            // TODO handle this!
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
        DatagramPacket packet = new DatagramPacket(helloCommand.getBytes(), helloCommand.length(), getBroadcastAddress(), Constants.UDP_PORT);
        socket.send(packet);

        byte[] buf = new byte[1024];
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        Logger.toast("IP: " + packet.getAddress().getHostAddress(), activity.getApplicationContext());
        Logger.log("IP: " + packet.getAddress().getHostAddress());
        return packet.getAddress().getHostAddress();
    }


    public Response send(Command command, String content) throws Exception {

        String request = command.toString().toLowerCase() + " " + content;
        return getResponseFromMessage(tcpClient.sendMessage(request));
    }

    private Response getResponseFromMessage(String message) {

        Response.Value value = Response.Value.OK;
        if (message.startsWith("err_")) {
            value = Response.Value.valueOf(message.toUpperCase());
        }

        return new Response(value, message);
    }
}
