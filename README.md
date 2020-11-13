## Basic level

The basic level does not require you to make any changes to your used record classes at all.

- Builder
- + Default value annotation

### Record builders

With Remix, records can be constructed using the well-established Builder pattern.
Let's look at the following example record:

    public record Car(String manufacturer, String model, int price, boolean available) {}

Remix allows you to create a Car instance as follows:

    Car c1 = Records.builder(Car.class)
            .set(Car::manufacturer, () -> "RemixCars")
            .set(Car::model, () -> "The Budget car")
            .set(Car::price, () -> 10000)
            .set(Car::available, () -> true)
            .build();
            
            
### Record blanks

In addition, these builders also enable you to create record blanks,
which are basically blueprints to create new instances of some record.
These blank records can also be reused to effectively implement default values for records:

    // We only sell cars manufactured by us, so let's predefine the manufacturer
    RecordBlank<Car> carBlank = Records.builder(Car.class)
            .set(Car::manufacturer, () -> "RemixCars")
            .blank();
            
    // No need to specify the manufacturer
    Car c2 = Records.builder(carBlank)
            .set(Car::model, () -> "The luxurious car")
            .set(Car::price, () -> 60000)
            .set(Car::available, () -> true)
            .build();
    
### Constructor and accessor operations

One 

    class CarRemix extends RecordRemix<Car> {
        @Override
        protected void blank(RecordBuilder<Car> builder) {
            builder.set(Car::manufacturer, () -> "RemixCars");
        }

        @Override
        protected void assign(RecordOperations<Car> operations) {
            operations.notNull(Car::manufacturer)
                      .notNull(Car::model)
                      .check(Car::price, p -> p > 0);
        }
    }

    @Remix(CarRemix.class)
    record Car(Wrapped<String> manufacturer, Wrapped<String> model, MutableInt price, MutableBoolean available) {}

Lets take this further:

    record CarDatabase(List<Car> cars) {}
    
Improved:

    class CarDatabaseRemix extends RecordRemix<CarDatabase> {
        @Override
        protected void blank(RecordBuilder<CarDatabase> builder) {
            // The default value for the car list should be an empty array list
            builder.set(CarDatabase::cars, () -> new ArrayList<>());
        }

        @Override
        protected void get(RecordOperations<CarDatabase> ops) {
            // Return an unmodifiable list view to prevent tampering with the database from outside this instance
            ops.add(CarDatabase::cars, Collections::unmodifiableList);
        }

        @Override
        protected void assign(RecordOperations<CarDatabase> ops) {
            // Check for null and make a defensive copy of the list when constructing an instance.
            ops.notNull(CarDatabase::cars)
               .check(CarDatabase::cars, c -> !c.contains(null))
               .add(CarDatabase::cars, ArrayList::new);
        }
    }

    @Remix(CarDatabaseRemix.class)
    record CarDatabase(Wrapped<List<Car>> cars) {}
    
- Wrapped values provide value assignment checks and actions
- Getter annotation


        List<Car> cars = new ArrayList<>();
        cars.add(c1);
        cars.add(c2);
        CarDatabase d = Records.create(CarDatabase.class, cars);

        // Doesn't alter the database
        cars.clear();

        List<Car> databaseContent = Records.get(d::cars);
        // Throws an exception, since the returned view is unmodifiable
        databaseContent.clear();
        
## Value storage

- Support for setter operations for all record components with input checks and various actions

