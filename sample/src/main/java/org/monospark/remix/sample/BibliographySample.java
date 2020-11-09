package org.monospark.remix.sample;

import org.monospark.remix.SetAction;
import org.monospark.remix.Default;
import org.monospark.remix.Records;
import org.monospark.remix.Wrapped;

import java.util.*;

import static org.monospark.remix.SetAction.*;
import static org.monospark.remix.DefaultValues.NULL;

public class BibliographySample {

    public static record BibliographyEntry(List<String> authors, String title, String sourceInformation, String citingInformation) {

        public BibliographyEntry(List<String> authors, String title) {
            this(authors, title, null);
        }

        public BibliographyEntry(List<String> authors, String title, String sourceInformation) {
            this(authors, title, sourceInformation, null);
        }

        public BibliographyEntry(List<String> authors, String title, String sourceInformation, String citingInformation) {
            Objects.requireNonNull(authors);
            if (authors.size() == 0) {
                throw new IllegalArgumentException("Authors cannot be empty");
            }
            this.authors = Collections.unmodifiableList(authors);
            this.title = Objects.requireNonNull(title);
            this.sourceInformation = sourceInformation;
            this.citingInformation = citingInformation;
        }
    }

    public static record Bibliography(List<BibliographyEntry> entries) {

        public Bibliography(List<BibliographyEntry> entries) {
            this.entries = Objects.requireNonNull(Collections.unmodifiableList(entries));
        }

        static class Builder {
            private List<BibliographyEntry> entries = new ArrayList<>();

            public Builder add(BibliographyEntry entry) {
                this.entries.add(entry);
                return this;
            }

            public Bibliography build() {
                return new Bibliography(entries);
            }
        }
    }

    public static record BibliographyEntryRemix(
            @SetAction({NOT_NULL, SIZE_GREATER_ZERO, UNMODIFIABLE_LIST})
            Wrapped<List<String>> authors,

            @SetAction({NOT_NULL})
            Wrapped<String> title,

            @Default(NULL)
            Wrapped<String> sourceInformation,

            @Default(NULL)
            Wrapped<String> citingInformation) {}

    public static record BibliographyRemix(
            @SetAction({NOT_NULL, UNMODIFIABLE_LIST})
            Wrapped<List<BibliographyEntryRemix>> entries) {}

    public static void main(String[] args) {
        var bib = new Bibliography.Builder().add(new BibliographyEntry(List.of("test"), "test")).build();
        System.out.println(bib);

        var remixBib = Records.builder(BibliographyRemix.class)
                .set(BibliographyRemix::entries, new ArrayList<>())
                .add(BibliographyRemix::entries, BibliographyEntryRemix.class, List.of("test"), "test")
                .build();
    }

}
