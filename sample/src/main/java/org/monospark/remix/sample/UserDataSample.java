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
        UserData d = Records.builder(UserData.class).set(UserData::online).to(() -> false).build();
        Records.set(d::online, true);
        Records.set(d::lastLogin, LocalDateTime.now());
        MethodHandle h;
    }
}
