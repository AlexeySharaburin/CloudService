//package ru.netology.cloud_service.controller;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.netology.cloud_service.model.AuthToken;
//import ru.netology.cloud_service.model.PairLoginPass;
////import ru.netology.cloud_service.service.CloudServiceService;
//
//@RestController
//@RequestMapping("/")
//public class CloudServiceController {
//
////    private final CloudServiceService cloudServiceService;
////
////    public CloudServiceController(CloudServiceService cloudServiceService) {
////        this.cloudServiceService = cloudServiceService;
////    }
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthToken> authorizeClient(@RequestBody PairLoginPass pairLoginPass) {
//        System.out.println("Controller");
//
//        System.out.println("Authorization success");
//        return new ResponseEntity<>(new AuthToken("Hallo!"), HttpStatus.OK);
//    }
//
//
//}
