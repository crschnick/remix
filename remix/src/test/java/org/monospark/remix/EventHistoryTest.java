package org.monospark.remix;

import org.junit.jupiter.api.Test;
import org.monospark.remix.samples.EventHistory;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(Records.get(history::events).get(0), e1);
        assertEquals(Records.get(history::events).get(1), e3);
        assertEquals(Records.get(history::events).get(2), e2);
        assertEquals(Records.get(history::events).get(3), e4);
    }
}
