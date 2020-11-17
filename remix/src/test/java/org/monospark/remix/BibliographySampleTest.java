package org.monospark.remix;

import org.junit.Test;
import org.monospark.remix.samples.BibliographyStore;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BibliographySampleTest {

    @Test(expected = NullPointerException.class)
    public void testNull() {
        var entry = Records.create(BibliographyStore.Entry.class,
                List.of("James Gosling"),
                "The Java Programming Language",
                null);
    }

    @Test
    public void testCopy() {
        var entry = Records.create(BibliographyStore.Entry.class,
                List.of("James Gosling"),
                "The Java Programming Language",
                UUID.randomUUID());
        var store = Records.create(BibliographyStore.class, List.of(entry));

        var storeCopy = Records.copy(store);
        var entryInStoreCopy = Records.get(storeCopy::entries).get(0);

        // This should not change the entry title in the original store because we are working on a copy!
        Records.set(entryInStoreCopy::authors, List.of("Ken Arnold", "James Gosling"));

        assertThat(store.entries().get().get(0).authors().get().size(), equalTo(1));
        assertThat(storeCopy.entries().get().get(0).authors().get().size(), equalTo(2));
    }
}
