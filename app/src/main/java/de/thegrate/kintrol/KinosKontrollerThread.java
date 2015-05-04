package de.thegrate.kintrol;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by d037698 on 5/1/15.
 */
public class KinosKontrollerThread extends HandlerThread implements KinosStatusChecker {

    private static final String TAG = KinosKontrollerThread.class.getSimpleName();

    private static final int PORT = 9004;
    private final String ipAddress;
    private final KinosNotificationListener notificationListener;
    private KinosKontroller kontroller;
    private Handler handler;

    public KinosKontrollerThread(String ipAddress, KinosNotificationListener notificationListener) {
        super("Kinos Kontroller Thread");
        this.ipAddress = ipAddress;
        this.notificationListener = notificationListener;
    }

    @Override
    protected void onLooperPrepared() {
        Log.i(TAG, "KinosKontrollerThread starts KinosKontroller");
        kontroller = new KinosKontroller(ipAddress, notificationListener, this);
        kontroller.start();
        Log.i(TAG, "KinosKontrollerThread entering the loop");
    }


    // This method is allowed to be called from any thread
    public synchronized void requestStop() {
        Log.i(TAG, "KinosKontrollerThread loop quitting by request");
        quit();
        Log.i(TAG, "Shutting down KinosKontroller");
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
    public void checkDeviceStatus() {
        checkDeviceStatus(0);
    }

    @Override
    public void checkForOperationStatus() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkForOperationStatus();
            }
        });
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

    public void toggleMute() {
        post(new Runnable() {
            @Override
            public void run() {
                kontroller.toggleMute();
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
