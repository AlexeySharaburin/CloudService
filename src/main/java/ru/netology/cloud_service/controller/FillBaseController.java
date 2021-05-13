//package ru.netology.cloud_service.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.netology.cloud_service.model.UserData;
//import ru.netology.cloud_service.repository.UserCrudRepository;
//
//import java.security.SecureRandom;
//
//@RestController
//public class FillBaseController {
//
//    @Autowired
//    private UserCrudRepository userCrudRepository;
//
//    @GetMapping("/fill")
//    public void fillBase() {
//
//        UserData userAlexey = new UserData("Alexey", encodePassword("123"), "cloud_drive/cloud_drive_0000001", true);
//        UserData userIvan = new UserData("Ivan", encodePassword("321"), "cloud_drive/cloud_drive_0000002", true);
//
//
//        userAlexey = UserCrudRepository.save(userAlexey);
//        userIvan = UserCrudRepository.save(userIvan);
//        System.out.println(userAlexey.toString() + " " + userIvan.toString());
//
//    }
//
//public static String encodePassword(String password){
//        int strength=10; // work factor of bcrypt
//        BCryptPasswordEncoder bCryptPasswordEncoder=
//        new BCryptPasswordEncoder(strength,new SecureRandom());
//        return bCryptPasswordEncoder.encode(password);
//        }
//
//}