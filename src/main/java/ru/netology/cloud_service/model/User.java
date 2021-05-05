package ru.netology.cloud_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Boolean isEnable;

    public User(String username, String password, String filePath, Boolean isEnable) {
        this.username = username;
        this.password = password;
        this.filePath = filePath;
        this.isEnable = isEnable;
    }
}
