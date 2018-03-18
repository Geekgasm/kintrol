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

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class KontrollerThread extends HandlerThread implements StatusChecker {

    private static final String TAG = KontrollerThread.class.getSimpleName();

    private DeviceInfo deviceInfo;
    private final NotificationListener notificationListener;
    private Kontroller kontroller;
    private Handler handler;

    public KontrollerThread(DeviceInfo deviceInfo, NotificationListener notificationListener) {
        super("Kontroller Thread");
        this.deviceInfo = deviceInfo;
        this.notificationListener = notificationListener;
    }

    @Override
    protected void onLooperPrepared() {
        Log.i(TAG, "KontrollerThread starts Kontroller");
        Device device = DeviceDirectory.getDevice(deviceInfo.deviceType);
        kontroller = new Kontroller(deviceInfo, notificationListener, this);
        kontroller.start();
        Log.i(TAG, "KontrollerThread entering the loop");
    }


    // This method is allowed to be called from any thread
    public synchronized void requestStop() {
        Log.i(TAG, "KontrollerThread loop quitting by request");
        quit();
        Log.i(TAG, "Shutting down Kontroller");
        if (kontroller != null) {
            kontroller.stop();
            kontroller = null;
        }
    }

    public synchronized void decreaseVolume() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.decreaseVolume();
            }
        });
    }

    public synchronized void increaseVolume() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.increaseVolume();
            }
        });
    }

    public synchronized void switchOn() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.switchOn();
            }
        });
    }

    public synchronized void switchOff() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.switchOff();
            }
        });
    }

    public synchronized void previousInputProfile() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.previousInputProfile();
            }
        });
    }

    public synchronized void nextInputProfile() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.nextInputProfile();
            }
        });
    }

    @Override
    public void checkDeviceStatus(long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                kontroller.checkDeviceStatus();
            }
        }, delayMillis);
    }

    @Override
    public void checkVolume() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkVolume();
            }
        });
    }

    @Override
    public void checkInputProfile() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkInputProfile();
            }
        });
    }

    @Override
    public void checkUnityGain(final String currentInputProfileId) {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkUnityGain(currentInputProfileId);
            }
        });
    }

    @Override
    public void checkInputName(final String currentInputProfileId) {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkInputName(currentInputProfileId);
            }
        });
    }

    @Override
    public void checkSurroundMode() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkSurroundMode();
            }
        });
    }

    public void toggleMute() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.toggleMute();
            }
        });
    }

    public void setVolume(final int volume) {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.setVolume(volume);
            }
        });
    }

    public void previousSurroundMode() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.previousSurroundMode();
            }
        });
    }

    public void nextSurroundMode() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.nextSurroundMode();
            }
        });
    }

    private void post(Runnable runnable) {
        postDelayed(runnable, 0);
    }

    private void postDelayed(Runnable runnable, long delayMillis) {
        Handler handler = getHandler();
        if (handler != null) {
            handler.postDelayed(runnable, delayMillis);
        } else {
            Log.w(TAG, "Kontroller thread not active, command ignored");
        }
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler(getLooper());
        }
        return handler;
    }

}
