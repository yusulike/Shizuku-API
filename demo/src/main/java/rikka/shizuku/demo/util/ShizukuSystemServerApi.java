package rikka.shizuku.demo.util;

import android.app.IActivityManager;
import android.app.IStopUserCallback;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.os.Build;
import android.os.IUserManager;
import android.os.RemoteException;
import android.os.UserHandle;

import java.util.List;

import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

public class ShizukuSystemServerApi {

    private static final Singleton<IPackageManager> PACKAGE_MANAGER = new Singleton<IPackageManager>() {
        @Override
        protected IPackageManager create() {
            return IPackageManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService("package")));
        }
    };

    private static final Singleton<IUserManager> USER_MANAGER = new Singleton<IUserManager>() {
        @Override
        protected IUserManager create() {
            return IUserManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.USER_SERVICE)));
        }
    };

    private static final Singleton<IActivityManager> ACTIVITY_MANAGER = new Singleton<IActivityManager>() {
        @Override
        protected IActivityManager create() {
            return IActivityManager.Stub.asInterface(new ShizukuBinderWrapper(SystemServiceHelper.getSystemService(Context.ACTIVITY_SERVICE)));
        }
    };


    public static IPackageInstaller PackageManager_getPackageInstaller() throws RemoteException {
        IPackageInstaller packageInstaller = PACKAGE_MANAGER.get().getPackageInstaller();
        return IPackageInstaller.Stub.asInterface(new ShizukuBinderWrapper(packageInstaller.asBinder()));
    }

    public static List<UserInfo> UserManager_getUsers(boolean excludePartial, boolean excludeDying, boolean excludePreCreated) throws RemoteException {
        if (Build.VERSION.SDK_INT >= 30) {
            return USER_MANAGER.get().getUsers(excludePartial, excludeDying, excludePreCreated);
        } else {
            try {
                return USER_MANAGER.get().getUsers(excludeDying);
            } catch (NoSuchFieldError e) {
                return USER_MANAGER.get().getUsers(excludePartial, excludeDying, excludePreCreated);
            }
        }
    }

    public static boolean UserManager_requestQuietModeEnabled(boolean enableQuietMode, UserHandle userHandle) throws RemoteException {
        return USER_MANAGER.get().requestQuietModeEnabled(enableQuietMode, userHandle);
    }

    public static boolean UserManager_requestQuietModeEnabled(String callingPackage, boolean enableQuietMode, int userId, IntentSender target, int flags) throws RemoteException {
        return USER_MANAGER.get().requestQuietModeEnabled(callingPackage, enableQuietMode, userId, target, flags);
    }

    public static boolean UserManager_isQuietModeEnabled(int userId) throws RemoteException {
            return USER_MANAGER.get().isQuietModeEnabled(userId);
    }

    public static int ActivityManager_stopUser(int userid, boolean force, IStopUserCallback callback) throws RemoteException {
        return ACTIVITY_MANAGER.get().stopUser(userid, force, callback);
    }

    public static boolean ActivityManager_stopProfile(int userid) throws RemoteException {
        return ACTIVITY_MANAGER.get().stopProfile(userid);
    }

    // method 2: use transactRemote directly
    /*public static List<UserInfo> UserManager_getUsers(boolean excludeDying) {
        Parcel data = SystemServiceHelper.obtainParcel(Context.USER_SERVICE, "android.os.IUserManager", "getUsers");
        Parcel reply = Parcel.obtain();
        data.writeInt(excludeDying ? 1 : 0);

        List<UserInfo> res = null;
        try {
            ShizukuService.transactRemote(data, reply, 0);
            reply.readException();
            res = reply.createTypedArrayList(UserInfo.CREATOR);
        } catch (RemoteException e) {
            Log.e("ShizukuSample", "UserManager#getUsers", e);
        } finally {
            data.recycle();
            reply.recycle();
        }
        return res;
    }*/
}
