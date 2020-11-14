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