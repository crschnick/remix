## Structural copies of records

Lets take the following simple color record that defines default values and does input validation:

    @Remix(Color.Remixer.class)
    public record Color(WrappedInt red, WrappedInt green, WrappedInt blue) {
        public static class Remixer implements RecordRemixer<Color> {
            @Override
            public void create(RecordRemix<Color> r) {
                r.blank(b -> b
                        .set(Color::red, () -> 0)
                        .set(Color::green, () -> 0)
                        .set(Color::blue, () -> 0));
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