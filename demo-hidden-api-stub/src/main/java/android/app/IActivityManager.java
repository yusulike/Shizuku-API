
package android.app;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;


public interface IActivityManager extends IInterface {

    int stopUser(int userid, boolean force, IStopUserCallback callback)
            throws RemoteException;

    boolean stopProfile(int userId)
            throws RemoteException;

    abstract class Stub extends Binder implements android.app.IActivityManager {

        public static android.app.IActivityManager asInterface(IBinder obj) {
            throw new RuntimeException("STUB");
        }
    }
}
