/*
 Kintrol: Remote control app for LINN(R) KINOS(TM), KISTO(TM) and
 Klimax Kontrol(TM) system controllers.
 Copyright (C) 2015-2018 Oliver Götz

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 3.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.geekgasm.kintrol;

import android.util.Log;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TelnetConnection {
    private static final String TAG = TelnetConnection.class.getSimpleName();

    private TelnetClient telnetClient;
    private final String deviceIp;
    private final int devicePort;

    public TelnetConnection(String deviceIp, int devicePort) {
        this.deviceIp = deviceIp;
        this.devicePort = devicePort;
    }

    private void ensureTelnetClient() {
        if (telnetClient == null) {
            try {
                createTelnetClient();
            } catch (IOException | InvalidTelnetOptionException  e) {
                Log.w(TAG, "Unable to create telnet client", e);
                throw new RuntimeException("Unable to create telnet client", e);
            }
        }
    }

    private void createTelnetClient() throws IOException, InvalidTelnetOptionException {
        Log.i(TAG, "Creating telnet client");
        telnetClient = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        telnetClient.addOptionHandler(ttopt);
        telnetClient.addOptionHandler(echoopt);
        telnetClient.addOptionHandler(gaopt);
    }

    public synchronized boolean establishConnection() {
        ensureTelnetClient();
        boolean connectionEstablished = false;
        try {
            Log.d(TAG, "Trying to connect to " + deviceIp + ":" + devicePort);
            telnetClient.setConnectTimeout(200);
            telnetClient.connect(deviceIp, devicePort);
            connectionEstablished = true;
            Log.d(TAG, "Connected to " + deviceIp + ":" + devicePort);
        } catch (Exception e) {
            Log.d(TAG, "Unable to open telnet connection to " + deviceIp + ":" + devicePort + ": " + e.getMessage());
        }
        return connectionEstablished;
    }

    public synchronized void disconnect() {
        if (telnetClient != null && telnetClient.isConnected()) {
            try {
                Log.i(TAG, "Disconnecting telnet connection to " + deviceIp + ":" + devicePort);
                telnetClient.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error disconnecting Telnet client: ", e);
            }
        }
    }

    public synchronized boolean sendData(String data) {
        ensureTelnetClient();
        boolean connectionEstablished = telnetClient.isConnected() && telnetClient.isAvailable();
        Log.d(TAG, "Probing connection to " + deviceIp + ":" + devicePort + ": " + (connectionEstablished ? "connected" : "not connected"));
        if (connectionEstablished) {
            // Check by sending something
            OutputStream outputStream = telnetClient.getOutputStream();
            if (outputStream == null) {
                Log.w(TAG, "Couldn't get output stream, no connection!");
                connectionEstablished = false;
            } else {
                try {
                    outputStream.write((data + "\n").getBytes("UTF-8"));
                    outputStream.flush();
                } catch (IOException e) {
                    Log.w(TAG, "Couldn't send command, no connection!");
                    connectionEstablished = false;
                }
            }
        }
        return connectionEstablished;
    }

    public InputStream getInputStream() {
        return telnetClient.getInputStream();
    }
}
