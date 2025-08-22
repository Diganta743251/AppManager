// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.AppManager.compat;

import android.annotation.SuppressLint;
import android.annotation.UserIdInt;
import android.app.AppOpsManager;
import android.app.AppOpsManagerHidden;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.collection.SparseArrayCompat;
import androidx.core.os.ParcelCompat;

import com.android.internal.app.IAppOpsService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import aosp.libcore.util.EmptyArray;
import dev.rikka.tools.refine.Refine;
import io.github.muntashirakon.AppManager.ipc.ProxyBinder;
import io.github.muntashirakon.AppManager.logs.Log;
import io.github.muntashirakon.AppManager.utils.ExUtils;
import io.github.muntashirakon.AppManager.utils.MiuiUtils;

@SuppressLint("SoonBlockedPrivateApi")
public class AppOpsManagerCompat {
    @IntRange(from = -1, to = 5)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final int OP_FLAG_SELF;
    public static final int OP_FLAG_TRUSTED_PROXY;
    public static final int OP_FLAG_UNTRUSTED_PROXY;
    public static final int OP_FLAG_TRUSTED_PROXIED;
    public static final int OP_FLAG_UNTRUSTED_PROXIED;
    public static final int OP_FLAGS_ALL;
    public static final int OP_FLAGS_ALL_TRUSTED;

    static {
        // Initialize operation flags with fallback values
        int opFlagSelf = 0;
        int opFlagTrustedProxy = 0;
        int opFlagUntrustedProxy = 0;
        int opFlagTrustedProxied = 0;
        int opFlagUntrustedProxied = 0;
        int opFlagsAll = 0;
        int opFlagsAllTrusted = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                opFlagSelf = AppOpsManagerHidden.OP_FLAG_SELF;
                opFlagTrustedProxy = AppOpsManagerHidden.OP_FLAG_TRUSTED_PROXY;
                opFlagUntrustedProxy = AppOpsManagerHidden.OP_FLAG_UNTRUSTED_PROXY;
                opFlagTrustedProxied = AppOpsManagerHidden.OP_FLAG_TRUSTED_PROXIED;
                opFlagUntrustedProxied = AppOpsManagerHidden.OP_FLAG_UNTRUSTED_PROXIED;
                opFlagsAll = AppOpsManagerHidden.OP_FLAGS_ALL;
                opFlagsAllTrusted = AppOpsManagerHidden.OP_FLAGS_ALL_TRUSTED;
            } catch (NoSuchFieldError | NoClassDefFoundError e) {
                // Fallback to default values if hidden APIs are not available
                opFlagSelf = 0x1;
                opFlagTrustedProxy = 0x2;
                opFlagUntrustedProxy = 0x4;
                opFlagTrustedProxied = 0x8;
                opFlagUntrustedProxied = 0x10;
                opFlagsAll = 0x1F;
                opFlagsAllTrusted = 0xB;
            }
        }

        // Assign final values
        OP_FLAG_SELF = opFlagSelf;
        OP_FLAG_TRUSTED_PROXY = opFlagTrustedProxy;
        OP_FLAG_UNTRUSTED_PROXY = opFlagUntrustedProxy;
        OP_FLAG_TRUSTED_PROXIED = opFlagTrustedProxied;
        OP_FLAG_UNTRUSTED_PROXIED = opFlagUntrustedProxied;
        OP_FLAGS_ALL = opFlagsAll;
        OP_FLAGS_ALL_TRUSTED = opFlagsAllTrusted;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OpFlags {
    }

    public static final int UID_STATE_PERSISTENT;
    public static final int UID_STATE_TOP;
    public static final int UID_STATE_FOREGROUND_SERVICE_LOCATION;
    public static final int UID_STATE_FOREGROUND_SERVICE;
    public static final int UID_STATE_FOREGROUND;
    public static final int UID_STATE_BACKGROUND;
    public static final int UID_STATE_CACHED;

    public static final int MAX_PRIORITY_UID_STATE;
    public static final int MIN_PRIORITY_UID_STATE;

    static {
        // Initialize UID states with fallback values
        int uidStatePersistent = 0;
        int uidStateTop = 0;
        int uidStateForegroundService = 0;
        int uidStateForeground = 0;
        int uidStateBackground = 0;
        int uidStateCached = 0;
        int uidStateForegroundServiceLocation = 0;
        int maxPriorityUidState = 0;
        int minPriorityUidState = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                uidStatePersistent = AppOpsManagerHidden.UID_STATE_PERSISTENT;
                uidStateTop = AppOpsManagerHidden.UID_STATE_TOP;
                uidStateForegroundService = AppOpsManagerHidden.UID_STATE_FOREGROUND_SERVICE;
                uidStateForeground = AppOpsManagerHidden.UID_STATE_FOREGROUND;
                uidStateBackground = AppOpsManagerHidden.UID_STATE_BACKGROUND;
                uidStateCached = AppOpsManagerHidden.UID_STATE_CACHED;
            } catch (NoSuchFieldError | NoClassDefFoundError e) {
                // Fallback to default values if hidden APIs are not available
                uidStatePersistent = 100;
                uidStateTop = 200;
                uidStateForegroundService = 300;
                uidStateForeground = 400;
                uidStateBackground = 600;
                uidStateCached = 700;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                uidStateForegroundServiceLocation = AppOpsManagerHidden.UID_STATE_FOREGROUND_SERVICE_LOCATION;
                maxPriorityUidState = AppOpsManagerHidden.MAX_PRIORITY_UID_STATE;
                minPriorityUidState = AppOpsManagerHidden.MIN_PRIORITY_UID_STATE;
            } catch (NoSuchFieldError | NoClassDefFoundError e) {
                // Fallback values if hidden APIs are not available
                uidStateForegroundServiceLocation = 350;
                maxPriorityUidState = uidStatePersistent;
                minPriorityUidState = uidStateCached;
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            uidStateForegroundServiceLocation = 0;
            maxPriorityUidState = uidStatePersistent;
            minPriorityUidState = uidStateCached;
        } else {
            uidStateForegroundServiceLocation = 0;
            maxPriorityUidState = 0;
            minPriorityUidState = 0;
        }

        // Assign final values
        UID_STATE_PERSISTENT = uidStatePersistent;
        UID_STATE_TOP = uidStateTop;
        UID_STATE_FOREGROUND_SERVICE = uidStateForegroundService;
        UID_STATE_FOREGROUND = uidStateForeground;
        UID_STATE_BACKGROUND = uidStateBackground;
        UID_STATE_CACHED = uidStateCached;
        UID_STATE_FOREGROUND_SERVICE_LOCATION = uidStateForegroundServiceLocation;
        MAX_PRIORITY_UID_STATE = maxPriorityUidState;
        MIN_PRIORITY_UID_STATE = minPriorityUidState;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface UidState {
    }

    private static final SparseArrayCompat<String> sModes = new SparseArrayCompat<>();
    private static final String[] sOpToString;

    public static final int OP_NONE;
    
    static {
        int opNone = -1; // Default fallback value
        try {
            opNone = AppOpsManagerHidden.OP_NONE;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            // Use fallback value if hidden API is not available
            opNone = -1;
        }
        OP_NONE = opNone;
    }
    /**
     * Control whether an application is allowed to run in the background.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    public static final int OP_RUN_IN_BACKGROUND;
    /**
     * Run jobs when in background
     */
    @RequiresApi(Build.VERSION_CODES.P)
    public static final int OP_RUN_ANY_IN_BACKGROUND;
    public static final int _NUM_OP;

    static {
        // Initialize operation constants with fallback values
        int opRunInBackground = 0;
        int opRunAnyInBackground = 0;
        int numOp = 100; // Default fallback value

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                opRunInBackground = AppOpsManagerHidden.OP_RUN_IN_BACKGROUND;
            } catch (NoSuchFieldError | NoClassDefFoundError e) {
                opRunInBackground = 63; // Fallback value for OP_RUN_IN_BACKGROUND
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                opRunAnyInBackground = AppOpsManagerHidden.OP_RUN_ANY_IN_BACKGROUND;
            } catch (NoSuchFieldError | NoClassDefFoundError e) {
                opRunAnyInBackground = 70; // Fallback value for OP_RUN_ANY_IN_BACKGROUND
            }
        }

        try {
            numOp = AppOpsManagerHidden._NUM_OP;
        } catch (NoSuchFieldError | NoClassDefFoundError e) {
            numOp = 100; // Fallback value for _NUM_OP
        }

        // Assign final values
        OP_RUN_IN_BACKGROUND = opRunInBackground;
        OP_RUN_ANY_IN_BACKGROUND = opRunAnyInBackground;
        _NUM_OP = numOp;
    }

    /**
     * Mapping from a permission to the corresponding app op.
     */
    private static final HashMap<String, Integer> sPermToOp = new HashMap<>();
    /**
     * Some ops don't have any permissions associated with them and are enabled by default.
     * We are interested in the parents of these ops.
     */
    public static List<Integer> sOpWithoutPerms;

    static {
        String[] opToString = EmptyArray.STRING;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Only needed for API 22 and earlier
            try {
                Field sOpToStringField = AppOpsManagerHidden.class.getDeclaredField("sOpToString");
                sOpToStringField.setAccessible(true);
                opToString = (String[]) sOpToStringField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        sOpToString = opToString;

        for (Field field : AppOpsManager.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType() == int.class && field.getName().startsWith("MODE_")) {
                try {
                    sModes.put(field.getInt(null), field.getName());
                } catch (IllegalAccessException ignore) {
                }
            }
        }

        HashSet<Integer> opWithoutPerms = new HashSet<>();
        for (int i = 0; i < _NUM_OP; i++) {
            String permission = AppOpsManagerHidden.opToPermission(i);
            if (permission != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    // Only needed for API 22 and earlier
                    sPermToOp.put(permission, i);
                }
            } else {
                // No permission
                opWithoutPerms.add(AppOpsManagerHidden.opToSwitch(i));
            }
        }
        sOpWithoutPerms = new ArrayList<>(opWithoutPerms);
    }

    public static boolean isMiuiOp(int op) {
        try {
            return MiuiUtils.isMiui() && op > AppOpsManagerHidden.MIUI_OP_START;
        } catch (Throwable e) {
            return false;
        }
    }

    @NonNull
    public static List<Integer> getAllOps() {
        List<Integer> appOps = new ArrayList<>();
        for (int i = 0; i < _NUM_OP; ++i) {
            appOps.add(i);
        }
        if (MiuiUtils.isMiui()) {
            try {
                for (int op = AppOpsManagerHidden.MIUI_OP_START + 1; op < AppOpsManagerHidden.MIUI_OP_END; ++op) {
                    appOps.add(op);
                }
            } catch (Exception ignore) {
            }
        }
        return appOps;
    }

    @NonNull
    public static List<Integer> getOpsWithoutPermissions() {
        return sOpWithoutPerms;
    }

    @NonNull
    public static List<Integer> getModeConstants() {
        return new ArrayList<Integer>(sModes.size()) {{
            for (int i = 0; i < sModes.size(); ++i) {
                add(sModes.keyAt(i));
            }
        }};
    }

    @NonNull
    public static String modeToName(@IntRange(from = -1) int mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return AppOpsManagerHidden.modeToName(mode);
        }
        // Fallback for pre28
        String fieldName = sModes.get(mode);
        if (fieldName == null) {
            return "mode=" + mode;
        }
        switch (mode) {
            case AppOpsManager.MODE_ALLOWED:
                return "allow";
            case AppOpsManager.MODE_IGNORED:
                return "ignore";
            case AppOpsManager.MODE_ERRORED:
                return "deny";
            // Rests have the same name as the constant name in lower case minus the MODE_ prefix
        }
        return fieldName.substring(5).toLowerCase(Locale.ROOT);
    }

    /**
     * Retrieve the op switch that controls the given operation.
     */
    public static int opToSwitch(int op) {
        return AppOpsManagerHidden.opToSwitch(op);
    }

    @NonNull
    public static String opToName(int op) {
        return AppOpsManagerHidden.opToName(op);
    }

    /**
     * Retrieve the permission associated with an operation, or null if there is not one.
     */
    @Nullable
    public static String opToPermission(int op) {
        return AppOpsManagerHidden.opToPermission(op);
    }

    /**
     * Retrieve the default mode for the operation.
     */
    public static int opToDefaultMode(int op) {
        try {
            return AppOpsManagerHidden.opToDefaultMode(op);
        } catch (NoSuchMethodError e) {
            return AppOpsManagerHidden.opToDefaultMode(op, false);
        }
    }

    /**
     * Retrieve the app op code for a permission, or {@link #OP_NONE} if there is not one.
     * This API is intended to be used for mapping runtime or appop permissions
     * to the corresponding app op.
     */
    public static int permissionToOpCode(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AppOpsManagerHidden.permissionToOpCode(permission);
        }
        // Fallback for Lollipop
        Integer boxedOpCode = sPermToOp.get(permission);
        if (boxedOpCode == null || boxedOpCode >= _NUM_OP) {
            return OP_NONE;
        }
        return boxedOpCode;
    }

    /**
     * Gets the app op name associated with a given permission.
     * The app op name is one of the public constants defined
     * in this class such as {@code #OPSTR_COARSE_LOCATION}.
     * This API is intended to be used for mapping runtime
     * permissions to the corresponding app op.
     *
     * @param permission The permission.
     * @return The app op associated with the permission or null.
     */
    @Nullable
    public static String permissionToOp(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return AppOpsManagerHidden.permissionToOp(permission);
        }
        // Fallback for Lollipop
        final int opCode = permissionToOpCode(permission);
        if (opCode == OP_NONE) {
            return null;
        }
        return sOpToString[opCode];
    }

    public static class PackageOps implements Parcelable {
        private final String mPackageName;
        private final int mUid;
        private final List<OpEntry> mEntries;

        public PackageOps(String packageName, int uid, List<OpEntry> entries) {
            mPackageName = packageName;
            mUid = uid;
            mEntries = entries;
        }

        public String getPackageName() {
            return mPackageName;
        }

        public int getUid() {
            return mUid;
        }

        public List<OpEntry> getOps() {
            return mEntries;
        }

        @NonNull
        @Override
        public String toString() {
            return "PackageOps{" +
                    "mPackageName='" + mPackageName + '\'' +
                    ", mUid=" + mUid +
                    ", mEntries=" + mEntries +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(mPackageName);
            dest.writeInt(mUid);
            dest.writeTypedList(mEntries);
        }

        protected PackageOps(@NonNull Parcel in) {
            mPackageName = in.readString();
            mUid = in.readInt();
            mEntries = new ArrayList<>();
            in.readTypedList(mEntries, OpEntry.CREATOR);
        }

        public static final Parcelable.Creator<PackageOps> CREATOR = new Parcelable.Creator<PackageOps>() {
            @NonNull
            @Override
            public PackageOps createFromParcel(Parcel source) {
                return new PackageOps(source);
            }

            @NonNull
            @Override
            public PackageOps[] newArray(int size) {
                return new PackageOps[size];
            }
        };
    }

    public static class OpEntry implements Parcelable {
        private final AppOpsManagerHidden.OpEntry mOpEntry;

        public OpEntry(Parcelable opEntry) {
            mOpEntry = Refine.unsafeCast(opEntry);
        }

        protected OpEntry(Parcel in) {
            mOpEntry = ParcelCompat.readParcelable(in,
                    AppOpsManagerHidden.OpEntry.class.getClassLoader(),
                    AppOpsManagerHidden.OpEntry.class);
        }

        public static final Creator<OpEntry> CREATOR = new Creator<OpEntry>() {
            @NonNull
            @Override
            public OpEntry createFromParcel(Parcel in) {
                return new OpEntry(in);
            }

            @NonNull
            @Override
            public OpEntry[] newArray(int size) {
                return new OpEntry[size];
            }
        };

        public int getOp() {
            return mOpEntry.getOp();
        }

        @NonNull
        public String getName() {
            return opToName(getOp());
        }

        @Nullable
        public String getPermission() {
            return opToPermission(getOp());
        }

        @Mode
        public int getMode() {
            return mOpEntry.getMode();
        }

        @Mode
        public int getDefaultMode() {
            return opToDefaultMode(getOp());
        }

        public long getTime() {
            return getLastAccessTime(OP_FLAGS_ALL);
        }

        public long getLastAccessTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastAccessTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastAccessTime();
            }
            return mOpEntry.getTime();
        }

        public long getLastAccessForegroundTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastAccessForegroundTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastAccessForegroundTime();
            } else return mOpEntry.getTime();
        }

        public long getLastAccessBackgroundTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastAccessBackgroundTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastAccessBackgroundTime();
            } else return mOpEntry.getTime();
        }

        public long getLastAccessTime(@UidState int fromUidState,
                                      @UidState int toUidState,
                                      @OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastAccessTime(fromUidState, toUidState, flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastTimeFor(fromUidState);
            } else return mOpEntry.getTime();
        }

        public long getRejectTime() {
            return getLastRejectTime(OP_FLAGS_ALL);
        }

        public long getLastRejectTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastRejectTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastRejectTime();
            } else return mOpEntry.getRejectTime();
        }

        public long getLastRejectForegroundTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastRejectForegroundTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastRejectForegroundTime();
            } else return mOpEntry.getRejectTime();
        }

        public long getLastRejectBackgroundTime(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastRejectBackgroundTime(flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastRejectBackgroundTime();
            } else return mOpEntry.getRejectTime();
        }

        public long getLastRejectTime(@UidState int fromUidState,
                                      @UidState int toUidState,
                                      @OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastRejectTime(fromUidState, toUidState, flags);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
                return mOpEntry.getLastRejectTimeFor(fromUidState);
            } else return mOpEntry.getRejectTime();
        }

        public boolean isRunning() {
            return mOpEntry.isRunning();
        }

        public long getDuration() {
            return getLastDuration(MAX_PRIORITY_UID_STATE, MIN_PRIORITY_UID_STATE, OP_FLAGS_ALL);
        }

        @RequiresApi(Build.VERSION_CODES.R)
        public long getLastDuration(@OpFlags int flags) {
            return mOpEntry.getLastDuration(flags);
        }

        public long getLastForegroundDuration(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastForegroundDuration(flags);
            } else return mOpEntry.getDuration();
        }

        public long getLastBackgroundDuration(@OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastBackgroundDuration(flags);
            } else return mOpEntry.getDuration();
        }

        public long getLastDuration(@UidState int fromUidState,
                                    @UidState int toUidState,
                                    @OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getLastDuration(fromUidState, toUidState, flags);
            } else return mOpEntry.getDuration();
        }

        // Deprecated in R
        @Deprecated
        @RequiresApi(Build.VERSION_CODES.M)
        public int getProxyUid() {
            return mOpEntry.getProxyUid();
        }

        // Deprecated in R
        @Deprecated
        public int getProxyUid(@UidState int uidState, @OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getProxyUid(uidState, flags);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mOpEntry.getProxyUid();
            }
            return 0;
        }

        @Deprecated
        @RequiresApi(Build.VERSION_CODES.M)
        @Nullable
        public String getProxyPackageName() {
            return mOpEntry.getProxyPackageName();
        }

        // Deprecated in R
        @Deprecated
        @Nullable
        public String getProxyPackageName(@UidState int uidState, @OpFlags int flags) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                return mOpEntry.getProxyPackageName(uidState, flags);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mOpEntry.getProxyPackageName();
            }
            return null;
        }

        // TODO(24/12/20): Get proxy info (From API 30)


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeParcelable(mOpEntry, flags);
        }
    }

    public static int getModeFromOpEntriesOrDefault(int op, @Nullable List<OpEntry> opEntries) {
        if (op <= OP_NONE || op >= _NUM_OP || opEntries == null) {
            return AppOpsManager.MODE_IGNORED;
        }
        for (OpEntry opEntry : opEntries) {
            if (opEntry.getOp() == op) {
                return opEntry.getMode();
            }
        }
        return opToDefaultMode(op);
    }

    @NonNull
    public static List<OpEntry> getConfiguredOpsForPackage(@NonNull AppOpsManagerCompat appOpsManager,
                                                           @NonNull String packageName, int uid)
            throws RemoteException {
        List<PackageOps> packageOpsList = appOpsManager.getOpsForPackage(uid, packageName, null);
        if (packageOpsList.size() == 1) {
            return packageOpsList.get(0).getOps();
        }
        return Collections.emptyList();
    }

    private final IAppOpsService mAppOpsService;

    public AppOpsManagerCompat() {
        mAppOpsService = IAppOpsService.Stub.asInterface(ProxyBinder.getService(Context.APP_OPS_SERVICE));
    }

    /**
     * Get the mode of operation of the given package or uid. This denotes the actual working state which is not
     * necessarily the same mode set using {@link #setMode(int, int, String, int)}.
     *
     * @param op          One of the OP_*
     * @param uid         User ID for the package(s)
     * @param packageName Name of the package
     * @return One of the MODE_*
     */
    @AppOpsManagerCompat.Mode
    public int checkOperation(int op, int uid, String packageName) throws RemoteException {
        return mAppOpsService.checkOperation(op, uid, packageName);
    }

    /**
     * Same as {@link AppOpsManager#checkOpNoThrow(String, int, String)}. To be used with App Manager itself.
     */
    @AppOpsManagerCompat.Mode
    public int checkOpNoThrow(int op, int uid, String packageName) {
        try {
            int mode = mAppOpsService.checkOperation(op, uid, packageName);
            return mode == AppOpsManager.MODE_FOREGROUND ? AppOpsManager.MODE_ALLOWED : mode;
        } catch (RemoteException e) {
            return ExUtils.rethrowFromSystemServer(e);
        }
    }

    @RequiresPermission(ManifestCompat.permission.GET_APP_OPS_STATS)
    public List<AppOpsManagerCompat.PackageOps> getOpsForPackage(int uid, String packageName, @Nullable int[] ops)
            throws RemoteException {
        // Check using uid mode and package mode, override ops in package mode from uid mode
        List<AppOpsManagerCompat.OpEntry> opEntries = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                addAllRelevantOpEntriesWithNoOverride(opEntries, mAppOpsService.getUidOps(uid, ops));
            } catch (NullPointerException e) {
                Log.e("AppOpsManagerCompat", "Could not get app ops for UID %d", e, uid);
            }
        }
        addAllRelevantOpEntriesWithNoOverride(opEntries, mAppOpsService.getOpsForPackage(uid, packageName, ops));
        return Collections.singletonList(new AppOpsManagerCompat.PackageOps(packageName, uid, opEntries));
    }

    @RequiresPermission(ManifestCompat.permission.GET_APP_OPS_STATS)
    @NonNull
    public List<AppOpsManagerCompat.PackageOps> getPackagesForOps(int[] ops) throws RemoteException {
        List<Parcelable> opsForPackage = mAppOpsService.getPackagesForOps(ops);
        List<AppOpsManagerCompat.PackageOps> packageOpsList = new ArrayList<>();
        if (opsForPackage != null) {
            for (Parcelable o : opsForPackage) {
                AppOpsManagerCompat.PackageOps packageOps = opsConvert(Refine.unsafeCast(o));
                packageOpsList.add(packageOps);
            }
        }
        return packageOpsList;
    }

    @RequiresPermission("android.permission.MANAGE_APP_OPS_MODES")
    public void setMode(int op, int uid, String packageName, @AppOpsManagerCompat.Mode int mode)
            throws RemoteException {
        if (AppOpsManagerCompat.isMiuiOp(op) || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Only package mode works in MIUI-only app ops and before Android M
            mAppOpsService.setMode(op, uid, packageName, mode);
        } else {
            // Set UID mode
            mAppOpsService.setUidMode(op, uid, mode);
        }
    }

    @RequiresPermission("android.permission.MANAGE_APP_OPS_MODES")
    public void resetAllModes(@UserIdInt int reqUserId, @NonNull String reqPackageName) throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mAppOpsService.resetAllModes(reqUserId, reqPackageName);
        }
    }

    private static void addAllRelevantOpEntriesWithNoOverride(final List<AppOpsManagerCompat.OpEntry> opEntries,
                                                              @Nullable final List<Parcelable> opsForPackage) {
        if (opsForPackage != null) {
            for (Parcelable o : opsForPackage) {
                AppOpsManagerCompat.PackageOps packageOps = opsConvert(Refine.unsafeCast(o));
                for (AppOpsManagerCompat.OpEntry opEntry : packageOps.getOps()) {
                    if (!opEntries.contains(opEntry)) {
                        opEntries.add(opEntry);
                    }
                }
            }
        }
    }

    @NonNull
    private static AppOpsManagerCompat.PackageOps opsConvert(@NonNull AppOpsManagerHidden.PackageOps packageOps) {
        String packageName = packageOps.getPackageName();
        int uid = packageOps.getUid();
        List<AppOpsManagerCompat.OpEntry> opEntries = new ArrayList<>();
        for (Parcelable opEntry : packageOps.getOps()) {
            opEntries.add(new AppOpsManagerCompat.OpEntry(opEntry));
        }
        return new AppOpsManagerCompat.PackageOps(packageName, uid, opEntries);
    }
}
