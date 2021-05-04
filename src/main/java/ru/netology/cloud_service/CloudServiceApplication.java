package ru.netology.cloud_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
public class CloudServiceApplication {

    public static final String time = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date());

    public static void main(String[] args) {
        SpringApplication.run(CloudServiceApplication.class, args);
        System.out.println("Welcome!");
    }

}
