package com.codeoftheweb.salvo;

import org.springframework.boot.test.context.SpringBootTest;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Consumer;

@SpringBootTest
public class Tests {

    public static void main(String[] args) {}

    public static void main2(String[] args) throws InterruptedException {
        String prefix = "[Log] (12:30) ";
        Scanner scanner = new Scanner(System.in);
        String out;

        startCountdown("Starting console...","Console successfully enabled!", o -> {});

        while (scanner.hasNext()) {
            out = scanner.nextLine();
            if (out.startsWith("/clear")) {
                startCountdown("Starting clearing console.", "Cleared console successfully!", o -> {
                    for (int i = 0; i < 100; i++) {
                        System.out.println(" ");
                    }
                    System.out.println(prefix + "Console was cleared.");
                });
            } else if (out.startsWith("/help")) {
                System.out.println("All commands: (2)");
                System.out.println("- /help | Shows all commands");
                System.out.println("- /clear | Clear the console");
            } else {
                System.out.println(prefix + out);
            }
        }
    }

    private static void startCountdown(String msgStart, String msgEnd, Consumer<Object> a) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println(msgStart);
        Thread.sleep(1000);
        System.out.println(3);
        Thread.sleep(1000);
        System.out.println(2);
        Thread.sleep(1000);
        System.out.println(1);
        Thread.sleep(1000);
        System.out.println(msgEnd);
        a.accept(new Object());
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    public static void main1(String[] args) {
        String index = MessageFormat.format("www.{0}.com:{1}", "globalinc", "8080");
        String admin = MessageFormat.format(index + "/{0}", "admin");
        String router = MessageFormat.format(admin + "?gpid={0}", new Random().nextInt(100));

        System.out.println("Index Page: " + index);
        System.out.println("Admin Page: " + admin);
        System.out.println("Router Page: " + router);

        fireException(ExceptionType.NOT_SUPPORTED_OPERATION_EXCEPTION, exception -> System.out.println("[!] Error: " + exception.getClass().getName()));
        fireException(ExceptionType.NO_SUCH_METHOD_EXCEPTION, exception -> System.out.println("[!] Error: " + exception.getClass().getName()));
        fireException(ExceptionType.NULL_POINTER_EXCEPTION, exception -> System.out.println("[!] Error: " + exception.getClass().getName()));

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
        NULL_POINTER_EXCEPTION(NullPointerException.class),
        NOT_SUPPORTED_OPERATION_EXCEPTION(UnsupportedOperationException.class),
        NO_SUCH_METHOD_EXCEPTION(NoSuchMethodException.class);

        Class<? extends Exception> exception;

        ExceptionType(Class<? extends Exception> exception) {
            this.exception = exception;
        }

        public Class<? extends Exception> getException() {
            return this.exception;
        }
    }
}
