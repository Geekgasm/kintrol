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

    private final TelnetConnection telnetConnection;
    private final int probeCycleInMillis;
    private boolean stopRequested;

    public PeriodicConnectionProber(TelnetConnection telnetConnection, int probeCycleInMillis) {
        this.telnetConnection = telnetConnection;
        this.probeCycleInMillis = probeCycleInMillis;
    }

    @Override
    public void run() {
        Log.i(TAG, "Starting PeriodicConnectionProber thread");
        try {
            while (!stopRequested) {
                Thread.sleep(probeCycleInMillis);
                telnetConnection.probeConnection();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "PeriodicConnectionProber thread interrupted, stopping");
        }
        Log.i(TAG, "Stopping PeriodicConnectionProber thread");
    }

    public synchronized void stop() {
        Log.d(TAG, "Stop requested for PeriodicConnectionProber thread");
        stopRequested = true;
    }
}
