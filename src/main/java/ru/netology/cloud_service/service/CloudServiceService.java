//package ru.netology.cloud_service.service;
//
//import org.springframework.data.util.Pair;
//import org.springframework.stereotype.Service;
//import ru.netology.cloud_service.CloudServiceApplication;
//import ru.netology.cloud_service.model.AuthToken;
//import ru.netology.cloud_service.model.PairLoginPass;
//import ru.netology.cloud_service.repository.CloudServiceRepository;
//
//import java.util.Map;
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class CloudServiceService {
//
//    private final CloudServiceRepository cloudServiceRepository;
//
//    public Map<AuthToken, Long> authTokensRepository = new ConcurrentHashMap<>();
//
//    public CloudServiceService(CloudServiceRepository cloudServiceRepository) {
//        this.cloudServiceRepository = cloudServiceRepository;
//    }
//
//    public AuthToken authorizeClient(PairLoginPass pairLoginPass) {
//        System.out.println("Service");
//        AuthToken authToken = null;
//        Long idClient = cloudServiceRepository.authorizeClient(pairLoginPass);
//        if (idClient != 0) {
//        authToken = new AuthToken(generateCode());
//        authTokensRepository.put(authToken, idClient);}
//        return authToken;
//    }
//
//    public boolean unauthorizeClient(AuthToken authToken) {
//        System.out.println("Service");
//        long id = authTokensRepository.get(authToken);
//        if (id != 0) {
//            return authTokensRepository.remove(authToken, id);
//        } else {
//            return false;
//        }
//
//    }
//
//    public static String generateCode() {
//        Random random = new Random();
//        int codeInt = random.nextInt(8999999) + 1000000;
//        return "AutoToken" + String.valueOf(codeInt) + String.valueOf(CloudServiceApplication.time);
//    }
//}
