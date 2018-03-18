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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TelnetCommunicator {
    private static final String TAG = TelnetCommunicator.class.getSimpleName();
    private static final int DEFAULT_PORT = 9004;
    private static final int SEND_RETRIES = 3;
    private static final int SEND_RETRY_INTERVAL_MILLIS = 200;

    private String deviceIp;
    private int devicePort;

    private TelnetClient telnetClient;
    private PeriodicConnectionProber connectionProber;

    public TelnetCommunicator(DeviceInfo deviceInfo) {
        this.deviceIp = deviceInfo.getIpAddress();
        this.devicePort = decodeDevicePort(deviceInfo.getPort());
    }

    private int decodeDevicePort(String devicePortString) {
        if (devicePortString != null) {
            try {
                return Integer.valueOf(devicePortString);
            } catch (NumberFormatException ex) {
                // Fall back to default
            }
        }
        return DEFAULT_PORT;
    }

    synchronized void disconnect() {
        if (connectionProber != null) {
            Log.i(TAG, "Stopping connection prober thread");
            connectionProber.stop();
        }
        if (telnetClient != null && telnetClient.isConnected()) {
            try {
                Log.i(TAG, "Disconnecting telnet connection to " + deviceIp + ":" + devicePort);
                telnetClient.disconnect();
            } catch (IOException e) {
                Log.w(TAG, "Error disconnecting Telnet client: ", e);
            }
        }
    }

    synchronized boolean connect() throws IOException {
        ensureTelnetClient();
        return connectionProber.probeConnection();
    }

    private void ensureTelnetClient() throws IOException {
        if (telnetClient == null) {
            try {
                createTelnetClient();
                createConnectionProberThread();
            } catch (InvalidTelnetOptionException e) {
                Log.e(TAG, "Unable to open create telnet client", e);
                throw new RuntimeException("Unable to create telnet client", e);
            }
        }
    }

    private void ensureConnection() throws IOException {
        if (!connectionProber.ensureConnection()) {
            throw new IOException("Unable to connect to " + deviceIp + ":" + devicePort);
        }
    }

    private void createTelnetClient() throws IOException, InvalidTelnetOptionException {
        Log.d(TAG, "Creating telnet client");
        telnetClient = new TelnetClient();

        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);

        telnetClient.addOptionHandler(ttopt);
        telnetClient.addOptionHandler(echoopt);
        telnetClient.addOptionHandler(gaopt);
    }

    private void createConnectionProberThread() {
        connectionProber = new PeriodicConnectionProber(telnetClient, deviceIp, devicePort);
        Thread connectionProberThread = new Thread(connectionProber, "Periodic connection prober thread");
        connectionProberThread.start();
    }

    synchronized void sendLine(String stringToSend) throws IOException {
        ensureTelnetClient();
        for (int i = 0; i < SEND_RETRIES; i++) {
            if (connectionProber.sendData(stringToSend)) {
                return;
            }
        }
        Log.w(TAG, "Unable to send command, no connection");
        throw new IOException("Unable to send command, no connection");
    }

    synchronized BufferedReader getInputReader() throws IOException {
        ensureConnection();
        InputStream instr = telnetClient.getInputStream();
        return new BufferedReader(new InputStreamReader(instr));
    }

    synchronized void shutdown() {
        Log.d(TAG, "Shutting down TelnetCommunicator");
        disconnect();
        telnetClient = null;
    }
}
