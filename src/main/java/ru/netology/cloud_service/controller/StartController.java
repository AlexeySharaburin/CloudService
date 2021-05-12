package ru.netology.cloud_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloud_service.repository.UserDaoRepository;

import java.security.SecureRandom;

@RestController
public class StartController {

    @Autowired
    private UserDaoRepository userDaoRepository;

    @GetMapping("/fill")
    public void fillBase() {

//        User userAlexey = new User("Alexey", encodePassword("123"), "cloud_drive/cloud_drive_0000001", true);
//        User userIvan = new User("Ivan", encodePassword("321"), "cloud_drive/cloud_drive_0000002", true);
//
//
//        userAlexey = userDaoRepository.save(userAlexey);
//        userIvan = userDaoRepository.save(userIvan);
//        System.out.println(userAlexey.toString() + " " + userIvan.toString());

    }

    public static String encodePassword(String password) {
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder(strength, new SecureRandom());
        return bCryptPasswordEncoder.encode(password);
    }

}