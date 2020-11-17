package org.monospark.remix.samples;

import org.monospark.remix.RecordRemix;
import org.monospark.remix.RecordRemixer;
import org.monospark.remix.Remix;
import org.monospark.remix.Wrapped;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Remix(EventHistory.Remixer.class)
public record EventHistory(Wrapped<List<Event>> events) {
    static class Remixer implements RecordRemixer<EventHistory> {
        @Override
        public void create(RecordRemix<EventHistory> r) {
            r.blank(b -> b.set(EventHistory::events).to(ArrayList::new));
            r.assign(o -> o
                    .notNull(EventHistory::events)
                    .check(EventHistory::events, e -> e.stream().noneMatch(Objects::isNull))
                    .add(EventHistory::events, ArrayList::new)
                    .add(EventHistory::events, e -> {
                        Collections.sort(e);
                        return e;
                    })
            );
        }
    }

    public enum EventType {REGISTER, LOGIN, LOGOUT}

    public record Event(Wrapped<EventType> type, Wrapped<Instant> timestamp) implements Comparable<Event> {
        @Override
        public int compareTo(Event o) {
            return timestamp.get().compareTo(o.timestamp.get());
        }
    }
}
