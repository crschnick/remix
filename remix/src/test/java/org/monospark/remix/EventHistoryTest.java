package org.monospark.remix;

import org.junit.Test;
import org.monospark.remix.samples.EventHistory;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class EventHistoryTest {

    @Test
    public void checkOrdering() {
        var e1 = Records.create(EventHistory.Event.class,
                EventHistory.EventType.REGISTER, Instant.parse("2013-03-01T01:01:00Z"));
        var e2 = Records.create(EventHistory.Event.class,
                EventHistory.EventType.LOGOUT, Instant.parse("2013-03-01T07:05:00Z"));
        var e3 = Records.create(EventHistory.Event.class,
                EventHistory.EventType.LOGIN, Instant.parse("2013-03-01T02:15:00Z"));
        var e4 = Records.create(EventHistory.Event.class,
                EventHistory.EventType.LOGOUT, Instant.parse("2013-05-02T02:15:00Z"));

        var history = Records.builder(EventHistory.class)
                .set(EventHistory::events).to(() -> List.of(e1, e2, e3, e4))
                .build();

        assertThat(Records.get(history::events).get(0), equalTo(e1));
        assertThat(Records.get(history::events).get(1), equalTo(e3));
        assertThat(Records.get(history::events).get(2), equalTo(e2));
        assertThat(Records.get(history::events).get(3), equalTo(e4));
    }
}
