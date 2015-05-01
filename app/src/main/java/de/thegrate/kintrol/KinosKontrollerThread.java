package de.thegrate.kintrol;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by d037698 on 5/1/15.
 */
public class KinosKontrollerThread extends Thread implements KinosStatusChecker {

    private static final String TAG = KinosKontrollerThread.class.getSimpleName();

    private static final int PORT = 9004;
    private final String ipAddress;
    private final KinosNotificationListener notificationListener;
    private Handler handler;
    private KinosKontroller
            kontroller;

    public KinosKontrollerThread(String ipAddress, KinosNotificationListener notificationListener) {
        this.ipAddress = ipAddress;
        this.notificationListener = notificationListener;
    }

    @Override
    public void run() {
        try {
            // preparing a looper on current thread
            // the current thread is being detected implicitly
            Looper.prepare();

            Log.i(TAG, "KinosKontrollerThread starts KinosKontroller");
            kontroller = new KinosKontroller(ipAddress, notificationListener, this);
            kontroller.start();

            Log.i(TAG, "KinosKontrollerThread entering the loop");

            // now, the handler will automatically bind to the
            // Looper that is attached to the current thread
            // You don't need to specify the Looper explicitly
            handler = new Handler();

            // After the following line the thread will start
            // running the message loop and will not normally
            // exit the loop unless a problem happens or you
            // quit() the looper (see below)
            Looper.loop();

            Log.i(TAG, "KinosKontrollerThread exiting gracefully");
        } catch (Throwable t) {
            Log.e(TAG, "KinosKontrollerThread halted due to an error", t);
        }
    }

    // This method is allowed to be called from any thread
    public synchronized void requestStop() {
        // using the handler, post a Runnable that will quit()
        // the Looper attached to our KinosKontrollerThread
        // obviously, all previously queued tasks will be executed
        // before the loop gets the quit Runnable
        handler.post(new Runnable() {
            @Override
            public void run() {
                // This is guaranteed to run on the DownloadThread
                // so we can use myLooper() to get its looper
                Log.i(TAG, "KinosKontrollerThread loop quitting by request");

                Looper.myLooper().quitSafely();

                Log.i(TAG, "Shutting down KinosKontroller");
//                kontroller.stop();
//                kontroller = null;
            }
        });
    }

    public synchronized void decreaseVolume() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.decreaseVolume();
            }
        });
    }

    public synchronized void increaseVolume() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.increaseVolume();
            }
        });
    }

    public synchronized void switchOn() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.switchOn();
            }
        });
    }

    public synchronized void switchOff() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.switchOff();
            }
        });
    }

    public synchronized void previousInputProfile() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.previousInputProfile();
            }
        });
    }

    public synchronized void nextInputProfile() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.nextInputProfile();
            }
        });
    }

    @Override
    public void checkDeviceStatus() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkDeviceStatus();
            }
        });
    }

    @Override
    public void checkForOperationStatus() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkForOperationStatus();
            }
        });
    }

    @Override
    public void checkVolume() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkVolume();
            }
        });
    }

    @Override
    public void checkInputProfile() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                kontroller.checkInputProfile();
            }
        });
    }
}
