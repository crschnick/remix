package org.monospark.remix;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.monospark.remix.internal.DefaultImpl.NULL;

public class Test {

    public static void main(String[] args) {
        Function<TestRecord, Integer> s = TestRecord::a;
        TestRecord r = Records.create(TestRecord.class, (int) 5, 2, null);
        System.out.println(r.s.get());
        Records.set(r.s, "test");
        r.get(TestRecord::s);
        System.out.println(Records.get(TestRecord::s, r));
        //RecordCache.addToCache(TestRecord.class);
    }

    public interface TestInt {
    }

    public record BibliographyEntry(List<String> authors, String title, Optional<String> citedSections) {

        public BibliographyEntry(List<String> authors, String title, Optional<String> citedSections) {
            this.authors = Objects.requireNonNull(authors);
            if (authors.size() == 0) {
                throw new IllegalArgumentException("Author set cannot be empty");
            }
            this.title = Objects.requireNonNull(title);
            this.citedSections = Objects.requireNonNull(citedSections);
        }

        public BibliographyEntry(List<String> authors, String title, String citedSections) {
            this(authors, title, Optional.of(citedSections));
        }
    }

    ;

    public record TestRecord(@Default(NULL)
                             Integer a,

                             @Action({Action.NOT_NULL})
                             WrappedInt b,

                             @Default(NULL)
                             Mutable<String> s) implements Enhanced {
    }
}
