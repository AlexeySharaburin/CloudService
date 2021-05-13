package ru.netology.cloud_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloud_service.model.UserData;


@Repository
public interface UserCrudRepository extends CrudRepository<UserData, String> {

}
