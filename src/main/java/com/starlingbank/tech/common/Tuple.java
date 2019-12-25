package com.starlingbank.tech.common;

import java.util.Objects;

public class Tuple<T, U> {
    public final T _1;
    public final U _2;

    public Tuple(T t, U u) {
        this._1 = Objects.requireNonNull(t);
        this._2 = Objects.requireNonNull(u);
    }

    public static <T, U> Tuple<T, U> of(T t, U u) {
        return new Tuple<>(t, u);
    }

    @Override
    public String toString() {
        return String.format("(%s,%s)", _1,  _2);
    }
}