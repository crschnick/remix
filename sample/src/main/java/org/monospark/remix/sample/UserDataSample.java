package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.monospark.remix.Default.*;

public class UserDataSample {

    public static record UserData(String username, String password, boolean online, LocalDateTime lastLogin) {

    }

    public static void onLogin() {

    }

    public static final class Test implements DefaultCustom.Factory{

        @Override
        public Object create() {
            return null;
        }
    }

    public static record UserDataStorageRemix(
            @GetAction(int.class)
            Set<UserDataRemix> users) {

        public void add(UserData d) {

        }
    }

    public static record UserDataRemix(
            Wrapped<String> username,
            Mutable<String> password,
            @DefaultBoolean(true)
            MutableBoolean online,
            @Default(DefaultValues.NOW)
            Mutable<LocalDateTime> lastLogin) {

    }

    public static void onLoginRemix() {
        UserDataRemix d = null;
        Records.set(d::online, true);
        Records.set(d::lastLogin, LocalDateTime.now());
    }
}
