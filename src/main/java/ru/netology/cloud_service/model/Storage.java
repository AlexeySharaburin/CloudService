package ru.netology.cloud_service.model;

import jdk.jfr.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Embeddable
@Entity

public class Storage implements Serializable{


    private static int flag;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column
    private String fileName;

    @Column
    private Boolean isExist;

    @Column
//    @Timestamp
    private Date date;

    @Column
    private long userId;


    @Column
    private long fileSize;

//    @Column
//    private String username;

//    @ManyToOne
//    @JoinColumn
//    private UserData userData;
//    private HashMap<String, Date> files;

//    public static boolean flag;
//
//    @Override
//    public int compareTo(Storage o) {
////        if (flag) {
//            return (this.getFileName().compareTo(o.getFileName()));
////        } else {
////            return (this.contact.getEmail().compareTo(o.contact.getEmail()));
////        }
//    }

}
