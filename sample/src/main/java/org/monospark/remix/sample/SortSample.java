package org.monospark.remix.sample;

import org.monospark.remix.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class SortSample {

    public static void checkOrdering() {
        var e1 = Records.create(Event.class, EventType.REGISTER, Instant.parse("2013-03-01T01:01:00Z"));
        var e2 = Records.create(Event.class, EventType.LOGOUT, Instant.parse("2013-03-01T07:05:00Z"));
        var e3 = Records.create(Event.class, EventType.LOGIN, Instant.parse("2013-03-01T02:15:00Z"));
        var e4 = Records.create(Event.class, EventType.LOGOUT);

        var history = Records.builder(EventHistory.class)
                .set(EventHistory::events, () -> List.of(e1, e2, e3, e4))
                .build();

        assert Records.get(history::events, 0).equals(e1);
        assert Records.get(history::events, 1).equals(e3);
        assert Records.get(history::events, 2).equals(e2);
        assert Records.get(history::events, 3).equals(e4);
    }

    public enum EventType {REGISTER, LOGIN, LOGOUT}

    public record Event(Wrapped<EventType> type, Wrapped<Instant> timestamp) implements Comparable<Event> {
        @Override
        public int compareTo(Event o) {
            return timestamp.get().compareTo(o.timestamp.get());
        }
    }

    @Remix(EventHistory.Remixer.class)
    public record EventHistory(Wrapped<List<Event>> events) {
        static class Remixer implements RecordRemixer<EventHistory> {
            @Override
            public void create(RecordRemix<EventHistory> r) {
                r.blank(b -> b.set(EventHistory::events, () -> new ArrayList<>()));
                r.assign(o -> o
                        .notNull(EventHistory::events)
                        .notNull(EventHistory::events)
                        .check(EventHistory::events, e -> !e.contains(null))
                );
            }
        }
    }
}
