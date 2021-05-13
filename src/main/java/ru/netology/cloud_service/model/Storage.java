package ru.netology.cloud_service.model;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idFile;

    @Column
    private String fileName;

    @Column
    private Boolean isExist;

    @Column
    private Date date;

    @Column
    private String username;

    @Column
    private Integer fileSize;

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
