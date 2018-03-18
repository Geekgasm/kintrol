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

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class PeriodicConnectionProber implements Runnable {
    private static final String TAG = PeriodicConnectionProber.class.getSimpleName();
    private static final long PROBE_CYCLE_IN_MILLIS = 3000;
    private static final long MIN_PROBE_CYCLE_INTERVAL_MILLIS = 400;

    private final TelnetClient telnetClient;
    private final String deviceIp;
    private final int devicePort;
    private boolean stopRequested;
    private long lastProbeTimestamp;
    private boolean lastProbeResult;

    public PeriodicConnectionProber(TelnetClient telnetClient, String deviceIp, int devicePort) {
        this.telnetClient = telnetClient;
        this.deviceIp = deviceIp;
        this.devicePort = devicePort;
    }

    @Override
    public void run() {
        Log.d(TAG, "Starting PeriodicConnectionProber thread");
        try {
            while (!stopRequested) {
                Thread.sleep(PROBE_CYCLE_IN_MILLIS);
                probeConnection();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "PeriodicConnectionProber thread interrupted, stopping");
        }
        Log.d(TAG, "Stopping PeriodicConnectionProber thread");
    }

    public synchronized boolean probeConnection() {
        if (System.currentTimeMillis() - lastProbeTimestamp < MIN_PROBE_CYCLE_INTERVAL_MILLIS) {
            return lastProbeResult;
        }
        return ensureConnection();
    }

    public synchronized boolean ensureConnection() {
        boolean connectionEstablished = sendData("$STATUS$");
        if (!connectionEstablished) {
            connectionEstablished = establishConnection();
        }
        lastProbeTimestamp = System.currentTimeMillis();
        lastProbeResult = connectionEstablished;
        return connectionEstablished;
    }

    public synchronized boolean establishConnection() {
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

    public synchronized boolean sendData(String data) {
        boolean connectionEstablished = telnetClient.isConnected() && telnetClient.isAvailable();
        Log.d(TAG, "Probing connection to " + deviceIp + ":" + devicePort + ": " + (connectionEstablished ? "connected" : "not connected"));
        if (connectionEstablished) {
            // Check by sending something
            OutputStream outputStream = telnetClient.getOutputStream();
            if (outputStream == null) {
                Log.d(TAG, "Couldn't get output stream, no connection!");
                connectionEstablished = false;
            } else {
                try {
                    outputStream.write((data + "\n").getBytes("UTF-8"));
                    outputStream.flush();
                } catch (IOException e) {
                    Log.d(TAG, "Couldn't send command, no connection!");
                    connectionEstablished = false;
                }
            }
        }
        return connectionEstablished;
    }

    public synchronized void stop() {
        Log.d(TAG, "Stop requested for PeriodicConnectionProber thread");
        stopRequested = true;
    }
}
