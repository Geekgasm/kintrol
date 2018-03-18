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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TelnetCommunicator {
    private static final String TAG = TelnetCommunicator.class.getSimpleName();
    private static final int DEFAULT_PORT = 9004;
    private static final int SEND_RETRIES = 3;

    private String deviceIp;
    private int devicePort;
    private int probeCycleInMillis;

    private TelnetConnection telnetConnection;
    private PeriodicConnectionProber connectionProber;

    public TelnetCommunicator(DeviceInfo deviceInfo) {
        this.deviceIp = deviceInfo.getIpAddress();
        this.devicePort = decodeDevicePort(deviceInfo.getPort());
        this.probeCycleInMillis = decodeProbeCycleInMillis(deviceInfo.getProbeCycleMillis());
    }

    private int decodeProbeCycleInMillis(String probeCycleMillisString) {
        if (probeCycleMillisString != null) {
            try {
                return Integer.valueOf(probeCycleMillisString);
            } catch (NumberFormatException ex) {
                // Fall back to default (0: switched off)
            }
        }
        return 0;
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
        telnetConnection.disconnect();
    }

    synchronized boolean connect() throws IOException {
        ensureTelnetConnection();
        return telnetConnection.probeConnection();
    }

    private void ensureTelnetConnection() throws IOException {
        if (telnetConnection == null) {
            telnetConnection = new TelnetConnection(deviceIp, devicePort);
            if (probeCycleInMillis > 0) {
                createConnectionProberThread();
            }
        }
    }

    private void ensureConnection() throws IOException {
        if (!telnetConnection.probeConnection()) {
            throw new IOException("Unable to connect to " + deviceIp + ":" + devicePort);
        }
    }

    private void createConnectionProberThread() {
        connectionProber = new PeriodicConnectionProber(telnetConnection, probeCycleInMillis);
        Thread connectionProberThread = new Thread(connectionProber, "Periodic connection prober thread");
        connectionProberThread.start();
    }

    synchronized void sendLine(String stringToSend) throws IOException {
        ensureTelnetConnection();
        for (int i = 0; i < SEND_RETRIES; i++) {
            if (telnetConnection.sendData(stringToSend)) {
                return;
            }
        }
        Log.w(TAG, "Unable to send command, no connection");
        throw new IOException("Unable to send command, no connection");
    }

    synchronized BufferedReader getInputReader() throws IOException {
        ensureConnection();
        InputStream instr = telnetConnection.getInputStream();
        return new BufferedReader(new InputStreamReader(instr));
    }

    synchronized void shutdown() {
        Log.i(TAG, "Shutting down TelnetCommunicator");
        disconnect();
    }
}
