package ru.netology.cloud_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.netology.cloud_service.model.User;
import ru.netology.cloud_service.repository.UserDaoRepository;
import ru.netology.cloud_service.repository.UserRepository;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootApplication
//@ComponentScan(basePackages = {"ru.netology"})
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class CloudServiceApplication {

    public static final String time = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(new Date());

    public static void main(String[] args) {

        SpringApplication.run(CloudServiceApplication.class, args);
        System.out.println("Welcome!");

        var context = new AnnotationConfigApplicationContext("ru.netology");

        var repo = context.getBean(UserDaoRepository.class);
        User userAlexey = new User("Alexey", encodePassword("123"), "cloud_drive/cloud_drive_0000001", true);
        User userIvan = new User("Ivan", encodePassword("321"), "cloud_drive/cloud_drive_0000002", true);
        userAlexey = repo.save(userAlexey);
        userIvan = repo.save(userIvan);

    }

    public static String encodePassword(String password) {
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder(strength, new SecureRandom());
        return bCryptPasswordEncoder.encode(password);
    }

}
