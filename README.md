[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monospark/remix/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monospark/remix)
[![javadoc](https://javadoc.io/badge2/org.monospark/remix/javadoc.svg)](https://javadoc.io/doc/org.monospark/remix)
[![MIT license](https://img.shields.io/badge/License-MIT-blue.svg)](https://lbesson.mit-license.org/)
[![Build Status](https://travis-ci.com/Monospark/remix.svg?token=SUawnpzswy23eTDodnmo&branch=master)](https://travis-ci.org/monospark/remix)

# Remix

Remix is a new lightweight Java library that provides useful features for the
newly introduced [record classes](https://openjdk.java.net/jeps/395), which are planned to release with JDK 16.

## Installation

To use Remix, you must use one of the following:
- An early-access build of [JDK 16](https://jdk.java.net/16/)
- [JDK 15](https://jdk.java.net/15/) with [preview features](https://docs.oracle.com/en/java/javase/15/language/preview-language-and-vm-features.html) enabled

Maven:

    <dependency>
      <groupId>org.monospark</groupId>
      <artifactId>remix</artifactId>
      <version>0.1</version>
    </dependency>
    
Gradle:

    implementation group: 'org.monospark', name: 'remix', version: '0.1'

## Motivation

According to the Java Language Specification, a record class is a shallowly immutable and
transparent carrier for a fixed set of values, called the record components.
In their basic form however, records are severely limited in their capabilities and applications.
While there already exist libraries that effectively provide the same features as record classes and Remix, like
[Auto](https://github.com/google/auto/), there are two significant differences:

- Remix requires no annotation processor and therefore works out
of the box when adding it as a dependency to your project
- Remix focuses exclusively on records and is therefore able to exploit every
single aspect of records better than a library with a more general focus.
Many features that remix provides are only possible because of the strict requirements for record classes 

## Record builders

With Remix, records can be constructed using the well-established Builder pattern.
Let's look at the following example record:

    public record Car(String manufacturer, String model, int price, boolean available) {}

Remix allows you to create a Car instance as follows:

    Car car = Records.builder(Car.class)
            .set(Car::manufacturer).to(() -> "RemixCars")
            .set(Car::model).to(() -> "The Budget car")
            .set(Car::price).to(() -> 10000)
            .set(Car::available).to(() -> true)
            .build();
            
            
## Record blanks

In addition, these builders also enable you to create record blanks,
which are basically blueprints to create new instances of some record.
These blank records can also be reused to effectively implement default values for record components:

    // We only sell cars manufactured by us, so let's predefine the manufacturer
    RecordBlank<Car> carBlank = Records.builder(Car.class)
            .set(Car::manufacturer).to(() -> "RemixCars")
            .blank();
            
    // No need to specify the manufacturer since the builder takes already set values from the blank
    Car c2 = Records.builder(carBlank)
            .set(Car::model).to(() -> "The luxurious car")
            .set(Car::price).to(() -> 60000)
            .set(Car::available).to(() -> true)
            .build();
    
## Wrapped components

One limitation of records is that there is no possibility to override the
component accessors or to validate some inputs without defining a complete canonical constructor.
When working with the following record:

    public record CarStorage(List<Car> cars) {
        public void addCar(Car car) {
            Objects.requireNonNull(car);
            
            // Do some database stuff ...
        
            // If we succeed, add the car to the storage
            cars.get().add(car);
        }
    }
    
which represents the car data stored in some database, a big problem is that the
list itself is still mutable and can be accessed and modified from the outside.
If we want to achieve complete immutability from the outside, then we can use wrapped components provided by Remix.
This is done by wrapping record components and defining a Remixer class,
that is able to alter the standard record behaviour:

    @Remix(CarStorage.Remixer.class)
    public record CarStorage(Wrapped<List<Car>> cars) {   
        public void addCar(Car car) {
            cars.get().add(Objects.requireNonNull(car));
        }
        
        static class Remixer implements RecordRemixer<CarStorage> {
            @Override
            public void create(RecordRemix<CarStorage> r) {
                // Return an unmodifiable list view to prevent tampering
                // with the database from outside this instance
                r.get(o -> o.add(CarStorage::cars, Collections::unmodifiableList));
    
                // Check for null and make a defensive copy of the list when constructing an instance.
                r.assign(o -> o
                        .notNull(CarStorage::cars)
                        .check(CarStorage::cars, c -> !c.contains(null))
                        .add(CarStorage::cars, ArrayList::new)
                );
            }
        }
    }
    
Now, the CarStorage class is truly immutable from the outside and
does input validation while still being a simple value storage:

    List<Car> cars = new ArrayList<>();
    cars.add(c1);
    cars.add(c2);
    CarStorage store = Records.create(CarStorage.class, cars);

    // Doesn't alter the database since we made a defensive copy
    cars.clear();

    List<Car> databaseContent = Records.get(store::cars);
    // Throws an exception, since the returned view is unmodifiable
    databaseContent.clear();
    
A Remixer class therefore allows you to add custom
behaviour to record components only when needed.
    
    
## Mutable components

While records are only designed to be shallowly immutable value stores.
This allows Remix to provide Mutable wrappers that enable you to modify record
components that would otherwise completely be immutable without violating the basic concepts of records.
For example, we can modify the Car record to make the price and availability mutable:

    record Car(String manufacturer, String model, MutableInt price, MutableBoolean available) {}
    
We can then modify car instances as follows:

    Car car = Records.builder(Car.class)
            .set(Car::manufacturer).to(() -> "RemixCars")
            .set(Car::model).to(() -> "The Budget car")
            .set(Car::price).to(() -> 10000)
            .set(Car::available).to(() -> true)
            .build();
    Records.set(car::available, false);
    Records.set(car::price, 12000);
    
The builder semantics however stay the same.


## Copies and deep copies

In most cases, using the `Records.copy(...)` method works out of the box.
For example, instances of the Car record can easily be copied:

    Car copy = Records.copy(car);
    // Does not change the availability of the original car instance
    Records.set(copy::available, false);

However, this does not work if you want to perform copies of records
that have a mutable component, like a mutable set or list.
In this case, deep copies have to be performed to completely decouple copies of original instances.

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
                    b.set(Bibliography::entries=.to(() -> new ArrayList<>());
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

## Structural copies

Lets take the following simple color record that defines default values and does input validation:

    @Remix(Color.Remixer.class)
    public record Color(WrappedInt red, WrappedInt green, WrappedInt blue) {
        public static class Remixer implements RecordRemixer<Color> {
            @Override
            public void create(RecordRemix<Color> r) {
                r.blank(b -> b
                        .set(Color::red).to(() -> 0)
                        .set(Color::green).to(() -> 0)
                        .set(Color::blue).to(() -> 0));
                Predicate<Integer> range = v -> v >= 0 && v <= Short.MAX_VALUE;
                r.assign(o -> o.check(o.all(), range));
            }
        }
    }
    
When working with already existing records, it is sometimes necessary to create structural copies of records.
For example, if we have to work with the following record:

    public record OtherColor(int red, int green, int blue) {}
    
If you are not easily able to modify this record, you can easily
convert records from one type into another as long as the types are
the same and assignment constraints are not violated:

    Color c = Records.create(Color.class, 500, 2032, 2034);
    OtherColor other = Records.structuralCopy(OtherColor.class, c);
    Color fromOther = Records.structuralCopy(Color.class, other);
    
However, if assignment constraints are violated, an exception will be thrown:

    OtherColor oc = Records.create(OtherColor.class, -1, -1, -1);
    // Throws an illegal argument exception
    Color c = Records.structuralCopy(Color.class, oc);
    
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
Of course, if the unwrapped type of a component changes, i.e. `int` to ``BigInteger``,
the order of components changes or components are added/removed,
then this will make the two versions incompatible.

## Local records

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

## More

- Samples are available [here](https://github.com/crschnick/remix/tree/master/samples/src/main/java/org/monospark/remix/samples)
- The javadocs are available at [javadoc.io](https://javadoc.io/doc/org.monospark/remix )
- This is a new library, so there will probably be some bugs.
If you stumble upon one of them, please report them.
- If you would like to contribute to this project, feel free to do so!
Formal contribution guidelines will be coming soon.