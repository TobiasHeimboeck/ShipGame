package com.codeoftheweb.salvo;

import java.text.MessageFormat;
import java.util.Random;

public class Tests {

    public static void main(String[] args) {

        String index = MessageFormat.format("www.{0}.com:{1}", "globalinc", "8080");
        String admin = MessageFormat.format(index + "/{0}", "admin");
        String router = MessageFormat.format(admin + "?gpid={0}", new Random().nextInt(100));

        System.out.println("Index Page: " + index);
        System.out.println("Admin Page: " + admin);
        System.out.println("Router Page: " + router);

    }
}
