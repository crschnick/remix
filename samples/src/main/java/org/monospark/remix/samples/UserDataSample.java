package org.monospark.remix.samples;

import org.monospark.remix.Records;

import java.lang.invoke.MethodHandle;
import java.time.LocalDateTime;

public class UserDataSample {

    public static void onLogin() {
        UserData d = Records.builder(UserData.class).set(UserData::online).to(() -> false).build();
        Records.set(d::online, true);
        Records.set(d::lastLogin, LocalDateTime.now());
        MethodHandle h;
    }
}
