package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.time.LocalDateTime;

@Remix(UserData.Remixer.class)
public record UserData(Wrapped<String> username,
                       Mutable<String> password,
                       MutableBoolean online,
                       Mutable<LocalDateTime> lastLogin) {
    static class Remixer implements RecordRemixer<UserData> {
        @Override
        public void create(RecordRemix<UserData> r) {
            // When a new user is created, the user is automatically logged in
            r.blank(b -> b
                    .set(UserData::online).to(() -> true)
                    .set(UserData::lastLogin).to(LocalDateTime::now));

            r.assign(o -> o
                    .notNull(o.all())
                    // Even though these conditions should also be checked on the client,
                    // you should also veriy the constraints on the server
                    .check(UserData::username, u -> u.length() > 5)
                    .check(UserData::password, p -> p.length() > 10));
        }
    }
}