package ru.javawebinar.topjava.util.format;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class LocalDateFormatterFactory implements AnnotationFormatterFactory<LocalDateFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return new HashSet<Class<?>>(asList(new Class<?>[]{
                LocalDate.class}));
    }

    @Override
    public Printer<?> getPrinter(LocalDateFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    @Override
    public Parser<?> getParser(LocalDateFormat annotation, Class<?> fieldType) {
        return getFormatter();
    }

    private LocalDateFormatter getFormatter() {
        return new LocalDateFormatter();
    }
}
