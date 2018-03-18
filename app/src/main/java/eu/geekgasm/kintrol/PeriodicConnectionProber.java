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

public class PeriodicConnectionProber implements Runnable {
    private static final String TAG = PeriodicConnectionProber.class.getSimpleName();
    private static final long PROBE_CYCLE_IN_MILLIS = 3000;

    private final TelnetConnection telnetConnection;
    private boolean stopRequested;

    public PeriodicConnectionProber(TelnetConnection telnetConnection, String deviceIp, int devicePort) {
        this.telnetConnection = telnetConnection;
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
        boolean connectionEstablished = telnetConnection.sendData("$STATUS$");
        if (!connectionEstablished) {
            connectionEstablished = telnetConnection.establishConnection();
        }
        return connectionEstablished;
    }

    public synchronized void stop() {
        Log.d(TAG, "Stop requested for PeriodicConnectionProber thread");
        stopRequested = true;
    }
}
