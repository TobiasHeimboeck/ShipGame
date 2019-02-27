package com.codeoftheweb.salvo;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class Tests {

    public static void main(String[] args) {
        String index = MessageFormat.format("www.{0}.com:{1}", "globalinc", "8080");
        String admin = MessageFormat.format(index + "/{0}", "admin");
        String router = MessageFormat.format(admin + "?gpid={0}", new Random().nextInt(100));

        System.out.println("Index Page: " + index);
        System.out.println("Admin Page: " + admin);
        System.out.println("Router Page: " + router);

        fireException(ExceptionType.NOT_SUPPORTED_OPPERATION, exception -> System.out.println("[!] Error: " + exception.getClass().getName()));
        fireException(ExceptionType.NO_SUCH_METHOD, exception -> System.out.println("[!] Error: " + exception.getClass().getName()));
    }

    private static void fireExceptions(Consumer<Exception> action, Exception... exceptions) {
        Arrays.asList(exceptions).forEach(current -> fireException(current, action));
    }

    private static void fireException(ExceptionType exceptionType, Consumer<Exception> action) {
        try {
            fireException(exceptionType.exception.newInstance(), action);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void fireException(Exception exception, Consumer<Exception> action) {
        try {
            action.accept(exception);
            throw exception;
        } catch (Exception ignored) {}
    }

    private enum ExceptionType {
        NULL_POINTER(NullPointerException.class),
        NOT_SUPPORTED_OPPERATION(UnsupportedOperationException.class),
        NO_SUCH_METHOD(NoSuchMethodException.class);

        Class<? extends Exception> exception;

        ExceptionType(Class<? extends Exception> exception) {
            this.exception = exception;
        }

        public Class<? extends Exception> getException() {
            return this.exception;
        }
    }
}
