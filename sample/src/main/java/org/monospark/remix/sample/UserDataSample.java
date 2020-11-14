package org.monospark.remix.sample;

import org.monospark.remix.Mutable;
import org.monospark.remix.MutableBoolean;
import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

import java.lang.invoke.MethodHandle;
import java.time.LocalDateTime;
import java.util.Set;

public class UserDataSample {

    public static void onLogin() {

    }

    public static void onLoginRemix() {
        UserDataRemix d = Records.builder(UserDataRemix.class).set(UserDataRemix::online, () -> false).build();
        Records.set(d::online, true);
        Records.set(d::lastLogin, LocalDateTime.now());
        MethodHandle h;
    }

    public static record UserData(String username, String password, boolean online, LocalDateTime lastLogin) {

    }

    public static record UserDataStorageRemix(
            //@Get(Actions.Unmodifiable.class)
            Set<UserDataRemix> users) {

        public void add(UserData d) {

        }
    }

    public static record UserDataRemix(
            Wrapped<String> username,
            Mutable<String> password,
            MutableBoolean online,
            Mutable<LocalDateTime> lastLogin) {

    }
}
