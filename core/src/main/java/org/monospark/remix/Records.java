package org.monospark.remix;

import org.monospark.remix.internal.RecordCache;

import java.util.function.Function;
import java.util.function.Supplier;

public class Records {

    public static <R extends Record> R create(Class<R> clazz, Object... args) {
        return (R) RecordCache.getOrAdd(clazz).create(args);
    }

    public static <R extends Record> RecordBuilder<R> builder(Class<R> clazz) {
        return new RecordBuilder<>(clazz);
    }

    public static <T> T get(Wrapped<T> entry) {
        return entry.get();
    }

    public static <T> T get(Supplier<Wrapped<T>> entry) {
        return entry.get().get();
    }

    public static <R extends Record, T> T get(Function<R, Wrapped<T>> entry, R instance) {
        return entry.apply(instance).get();
    }

    public static <T> void set(Mutable<T> entry, T value) {
        entry.set(value);
    }

    public static <T> void set(Supplier<Mutable<T>> entry, T value) {
        entry.get().set(value);
    }

    public static void set(MutableBoolean entry, boolean value) {
        entry.set(value);
    }

    public static void set(Supplier<MutableBoolean> entry, boolean value) {
        entry.get().set(value);
    }
}
