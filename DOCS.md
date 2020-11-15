## Serialization

If your record class does not have any wrapped components, then there is nothing to take care of.
Otherwise, there are two ways of making records serializable when working with wrapped record components.
The first way is just implementing `Serializable` as normal, i.e.

    public record Car(Wrapped<String> manufacturer, Wrapped<String> model, WrappedInt price,
                             MutableBoolean available) implements Serializable {
    }
    
This works as long you don't change any types of record components.
If, later on, you want to make `model` mutable as well and convert `available` into a normal boolean with

    public record Car(Wrapped<String> manufacturer, Mutable<String> model, WrappedInt price,
                             boolean available) implements Serializable {
    }

then this would break serialization compatibility with previous versions.

The alternative is to use `SerializableRecord`, which discards any wrappers
when serializing and dynamically wraps component values when deserializing.
If we declare our record class as follows:

    public record Car(String manufacturer, Wrapped<String> model, WrappedInt price,
                             MutableBoolean available) implements SerializableRecord {
        @Override
        public Object writeReplace() {
            return Records.serialized(this);
        }
    }
    
and later on change the record class to this:

    public record Car(Wrapped<String> manufacturer, Mutable<String> model, WrappedInt price,
                               boolean available) implements SerializableRecord {
        @Override
        public Object writeReplace() {
            return Records.serialized(this);
        }
    }
    
then serialization between both versions will work fine.
Of course, if the unwrapped type of a component changes, i.e. `int` to ``BigInteger``, or components are added/removed,
then this will make the two versions incompatible.

## Inline records

Records are also designed to be used locally in methods.
This is useful if you need some kind of custom value storage for only one method.
If you want to add some custom behaviour to that local record as well, use can do it like this:

    void doStuff() {
        record TripleEntry(Mutable<String> stringId, MutableInt intId, Wrapped<Object> value) {}
        Records.remix(TripleEntry.class, r -> r.assign(o -> o
                .notNull(o.all())
                .check(TripleEntry::stringId, s -> s.length() >= 5)
                .check(TripleEntry::intId, i -> i >= 0)));
        List<TripleEntry> list = new ArrayList<>();

        // Do some stuff ...
    }
    
This is also shorter than explicitly defining a Remixer class and annotating the record class.

## Performing copies and deep copies

In most cases, using the `Records.copy(...)` method works out of the box.
For example, instances of the Car record can easily be copied:

    Car car = Records.builder(Car.class)
            .set(Car::manufacturer, () -> "RemixCars")
            .set(Car::model, () -> "The Budget car")
            .set(Car::price, () -> 10000)
            .set(Car::available, () -> true)
            .build();
    Car copy = Records.copy(car);
    // Does not change the availability of the original car instance
    Records.set(copy::available, false);

However, this does not work if you want to perform copies of records
that have a mutable component, like a mutable set or list.
In this case, deep copies have to be performed to completely decouple copies of original instances
.
Lets take the following example of managing a storage of in-progress publications
where the authors and the title can still change.
An entry of this store looks like this:

    @Remix(Entry.Remixer.class)
    record Entry(Mutable<List<String>> authors,
                 Mutable<String> title,
                 Wrapped<UUID> id) {

        static class Remixer implements RecordRemixer<Entry> {
            @Override
            public void create(RecordRemix<Bibliography.Entry> r) {
                r.get(o -> o.add(Bibliography.Entry::authors, Collections::unmodifiableList));
                r.assign(o -> o
                        .notNull(o.all())
                        .check(Bibliography.Entry::authors, c -> !c.contains(null))
                        .add(Bibliography.Entry::authors, ArrayList::new)
                );
            }
        }
    }
    
The storage record could look like this:
    
    @Remix(Bibliography.Remixer.class)
    public record Bibliography(Wrapped<List<Bibliography.Entry>> entries) {
    
        static class Remixer implements RecordRemixer<Bibliography> {
            @Override
            public void create(RecordRemix<Bibliography> r) {
                // The default value should be an empty array list
                r.blank(b -> {
                    b.set(Bibliography::entries, () -> new ArrayList<>());
                });
    
                // Return an unmodifiable list view to prevent tampering from outside
                r.get(o -> o.add(Bibliography::entries, Collections::unmodifiableList));
    
                // Check for null and make a defensive copy of the list when constructing an instance.
                r.assign(o -> o
                        .notNull(Bibliography::entries)
                        .check(Bibliography::entries, c -> !c.contains(null))
                        .add(Bibliography::entries, ArrayList::new)
                );
    
                // Perform a deep copy. Otherwise, operations working on the copied bibliography entries
                // will change the entries of this one as well!
                r.copy(o -> o
                        .add(Bibliography::entries, e -> e.stream()
                        .map(Records::copy)
                        .collect(Collectors.toCollection(ArrayList::new))));
            }
        }
    }

If we want to support copying instances of the storage record using `Records.copy(...)`
and want to perform a deep copy, we have to explicitly specify the copy operations as shown above.
This allows us to work on copied storages like this:

    var entry = Records.create(BibliographyStore.Entry.class,
            List.of("James Gosling"),
            "The Java Programming Language",
            UUID.randomUUID());
    var store = Records.create(BibliographyStore.class, List.of(entry));

    var storeCopy = Records.copy(store);
    var entryInStoreCopy = Records.get(storeCopy::entries).get(0);

    // This does not change the entry title in the original store because we are working on a deep copy!
    Records.set(entryInStoreCopy::authors, List.of("Ken Arnold", "James Gosling"));

