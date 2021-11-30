package ru.javawebinar.topjava.util.format;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class LocalTimeFormatterFactory implements AnnotationFormatterFactory<LocalTimeFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return new HashSet<Class<?>>(asList(new Class<?>[]{
                LocalTime.class}));
    }

    @Override
    public Printer<?> getPrinter(LocalTimeFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    @Override
    public Parser<?> getParser(LocalTimeFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    private LocalTimeFormatter getFormatter() {
        return new LocalTimeFormatter();
    }
}
