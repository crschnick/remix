package org.monospark.remix.sample;

import org.monospark.remix.*;
import org.monospark.remix.actions.Actions;
import org.monospark.remix.actions.Get;
import org.monospark.remix.defaults.*;

import java.time.LocalDateTime;
import java.util.Set;

public class UserDataSample {

    public static record UserData(String username, String password, boolean online, LocalDateTime lastLogin) {

    }

    public static void onLogin() {

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
            @DefaultBoolean(true)
            MutableBoolean online,
            @Default(Defaults.Now.class)
            Mutable<LocalDateTime> lastLogin) {

    }

    public static void onLoginRemix() {
        UserDataRemix d = Records.builder(UserDataRemix.class).set(UserDataRemix::online, false).build();
        Records.set(d::online, true);
        Records.set(d::lastLogin, LocalDateTime.now());
    }
}
