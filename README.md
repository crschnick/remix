[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.monospark/remix/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.monospark/remix)
[![javadoc](https://javadoc.io/badge2/org.monospark/remix/javadoc.svg)](https://javadoc.io/doc/org.monospark/remix)
[![Build Status](https://travis-ci.org/Monospark/remix.svg?branch=master)](https://travis-ci.org/monospark/remix)

# Remix

Remix is a new lightweight Java library that provides useful features for the
newly introduced [record classes](https://openjdk.java.net/jeps/395), which are planned to release with JDK 16.
These features currently include:

- [Record builders](#record-builders)
- [Record blanks](#record-blanks)
- [Mutable components](#mutable-components)
- [Copies and deep copies](#copies-and-deep-copies)
- [Structural copies](#structural-copies)
- [Pattern binding](#pattern-binding)

Note that this library is still in early development and will change quite drastically.
The goal is to release version 1.0 of this library when JDK 16 is in its [final phase](https://openjdk.java.net/projects/jdk/16/).


### Why a new library?

While there already exist libraries that provide many of the same features as record classes and Remix, like
[Auto](https://github.com/google/auto/) or [Lombok](https://projectlombok.org/),
there are two significant differences:

- Remix requires no annotation processor and therefore works out
of the box when adding it as a dependency to your project

- Remix focuses exclusively on records and is therefore able to exploit every
single aspect of records better than a library with a more general focus.
Many features that remix provides are only possible because of the strict requirements for record classes.


### Installation

To use Remix and record classes, you must use a build of [JDK 15](https://jdk.java.net/15/) with
[preview features](https://docs.oracle.com/en/java/javase/15/language/preview-language-and-vm-features.html) enabled.
To use Remix with Maven you have to add it as a dependency:

    <dependency>
      <groupId>org.monospark</groupId>
      <artifactId>remix</artifactId>
      <version>0.2</version>
    </dependency>
    
You also have to enable preview features:
    
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
            <release>15</release>
            <compilerArgs>--enable-preview</compilerArgs>
        </configuration>
    </plugin>

For gradle, add the following entries to your build.gradle file:

    dependencies {
        implementation group: 'org.monospark', name: 'remix', version: '0.2'
    }

    tasks.withType(JavaCompile) {
        options.compilerArgs += "--enable-preview"
    }

## Record builders

With Remix, record instances can be constructed using the well-established Builder pattern.
Let's define the following example record class:

    public record Car(String manufacturer, String model, int price, boolean available) {}

You can then create a Car instance as follows:

    Car car = Records.builder(Car.class)
            .set(Car::manufacturer).to(() -> "RemixCars")
            .set(Car::model).to(() -> "The Budget car")
            .set(Car::price).to(() -> 10000)
            .set(Car::available).to(() -> true)
            .build();
                    
By default, every component is assigned its default value,
i.e. null for objects and 0, 0.0, false, etc. for primitives.

    // model and manufacturer are null, price is 0, available is false
    Car defaultCar = Records.builder(Car.class).build();
    
You can modify this behaviour by specifying a global default value blank as shown next.
    
## Record blanks

In addition, these builders also enable you to create record blanks,
which are basically blueprints to create new instances of some record class.
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
         
By creating a Remix object, we are able to define a global default blank,
that will be used by every created builder as follows:
            
    @Remix
    public record Car(String manufacturer, String model, int price, boolean available) {
        private static void createRemix(RecordRemix<Car> r) {
            r.blank(b -> b.set(Car::manufacturer).to(() -> "RemixCars"));
        }
    }
    
Now, every builder create with `Records.builder()` automatically sets the manufacturer.
Next, we will see how Remix objects can be used for input validation and more.


## Mutable components (Experimental)

Remix also provides mutable wrappers that enable you to modify record
components that would otherwise be immutable without violating the basic concepts of records.
For example, we can modify the Car record to make the price and availability mutable and add input validation:

    @Remix
    public record Car(String manufacturer, String model, MutableInt price,
                             MutableBoolean available) {
        private static void createRemix(RecordRemix<Car> r) {
            r.assign(o -> o
                    .notNull(o.all())
                    .check(Car::price, p -> p > 0)
            );
        }
    }
    
We can then modify car instances as follows:

    Car car = Records.builder(Car.class)
            .set(Car::manufacturer).to(() -> "RemixCars")
            .set(Car::model).to(() -> "The Budget car")
            .set(Car::price).to(() -> 10000)
            .set(Car::available).to(() -> true)
            .build();
    Records.set(car::available, false);
    int currentPrice = Records.get(car::price);
    Records.set(car::price, currentPrice - 1000);
    
    // Throws an illegal argument exception
    Records.set(car::price, -5);
    
    
The builder usage stays the same with wrapped components.


## Copies and deep copies

One commonly needed feature is copying or cloning record instances.
In most cases, using the `Records.copy(...)` method works out of the box.
For example, instances of the Car record can easily be copied:

    Car copy = Records.copy(car);
    // Does not change the availability of the original car instance
    Records.set(copy::available, false);

However, this does not work if you want to perform copies of records
that have for example have a collection as a record component,
since the collection contents are not copied.
In this case, deep copies have to be performed to completely decouple copies of original instances.
Lets take the following example of copying the car storage:

    CarStorage copy = Records.copy(store);
    var c = Records.get(copy::cars).get(0);
    
    // This changes the availability of the car in the original storage as well!
    Records.set(c::available, false);

If we want to support copying instances of the storage record using `Records.copy(...)`
and want to perform a deep copy, we have to explicitly specify the copy operations.:

    @Remix
    public record CarStorage(List<Car> cars, int capacity) {    
        private static void createRemix(RecordRemix<CarStorage> r) {
            r.assign(o -> o
                    .check(CarStorage::capacity, c -> c > 0)
                    .notNull(CarStorage::cars)
                    .check(CarStorage::cars, c -> c.stream().noneMatch(Objects::isNull))
                    .add(CarStorage::cars, ArrayList::new)
            );
            
            // Perform a deep copy!
            r.copy(o -> o.add(CarStorage::cars, e -> e.stream()
                    .map(Records::copy)
                    .collect(Collectors.toCollection(ArrayList::new))));
        }
    
        public void addCar(Car car) {
            if (car == null || cars.size() == capacity) {
                return;
            }
    
            cars.add(Objects.requireNonNull(car));
        }
    }

This allows us to work on copied storages like this:

    CarStorage copy = Records.copy(store);
    var c = Records.get(copy::cars).get(0);
    
    // This does not change the availability of the car in the original storage!
    Records.set(c::available, false);

## Structural copies

Lets take the following simple color record that defines default values and does input validation:

    @Remix(Color.Remixer.class)
    public record Color(int red, int green, int blue) {
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
    
## Local records

Records are also designed to be used locally in methods.
This is useful if you need some kind of custom value storage for only one method.
If you want to add some custom behaviour to that local record as well, use can do it like this:

    void doStuff() {
        record TripleEntry(Mutable<String> stringId, MutableInt intId, Object value) {}
        Records.remix(TripleEntry.class, r -> r.assign(o -> o
                .notNull(o.all())
                .check(TripleEntry::stringId, s -> s.length() >= 5)
                .check(TripleEntry::intId, i -> i >= 0)));
        List<TripleEntry> list = new ArrayList<>();

        // Do some stuff ...
    }
    

## Pattern binding

Inspired by [pattern matching](https://cr.openjdk.java.net/~briangoetz/amber/pattern-match.html),
Remix supports a form a pattern binding.
The method `Records.bind()` can construct bindings as follows:

    List<Car> cars = List.of( ... );

    Function<Car,String> nameFunc = Records.bind(Car::manufacturer).and(Car::model)
            .toFunction((s1, s2) -> String.join(" ", s1, s2));
    
    Map<Car,String> names = cars.stream().collect(Collectors.toMap(c -> c, nameFunc));
    
This works for consumers, functions and predicates.
Record components can also be permuted for bindings.


## Miscellaneous

- Some samples are available [here](https://github.com/crschnick/remix/tree/master/samples/src/main/java/org/monospark/remix/samples)
- The javadocs are available at [javadoc.io](https://javadoc.io/doc/org.monospark/remix )
- This is a new library, so there will probably be some bugs.
If you stumble upon one of them, please report them.
- If you would like to contribute to this project, feel free to do so!
Formal contribution guidelines will be coming soon.
