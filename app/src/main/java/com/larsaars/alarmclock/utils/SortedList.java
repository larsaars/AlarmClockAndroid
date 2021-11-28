/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 28.11.21, 02:04
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class SortedList<E> extends AbstractList<E> {
    private final ArrayList<E> internalList;

    public SortedList(Collection<E> collection) {
        internalList = new ArrayList<>(collection);
    }

    public SortedList(E[] array) {
        internalList = new ArrayList<>(Arrays.asList(array));
    }

    public SortedList() {
        internalList = new ArrayList<>();
    }

    @Override
    public E get(int index) {
        return internalList.get(index);
    }

    @Override
    public void add(int position, E e) {
        internalList.add(position, e);
        Collections.sort(internalList, null);
    }

    @Override
    public void clear() {
        internalList.clear();
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        boolean res = internalList.addAll(c);
        Collections.sort(internalList, null);
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(@NonNull Consumer<? super E> action) {
        internalList.forEach(action);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Spliterator<E> spliterator() {
        return internalList.spliterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Stream<E> stream() {
        return internalList.stream();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Stream<E> parallelStream() {
        return internalList.parallelStream();
    }

    @Override
    public int size() {
        return internalList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean removeIf(@NonNull Predicate<? super E> filter) {
        return internalList.removeIf(filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void replaceAll(@NonNull UnaryOperator<E> operator) {
        internalList.replaceAll(operator);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sort(@Nullable Comparator<? super E> c) {
        internalList.sort(c);
    }
}
