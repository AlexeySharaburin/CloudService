package ru.netology.cloud_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication

public class CloudServiceApplication {

//    public static final String date = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date());

    public static void main(String[] args) {

        SpringApplication.run(CloudServiceApplication.class, args);
        System.out.println("Welcome!");

    }

}
