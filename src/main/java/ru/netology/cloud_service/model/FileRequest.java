package ru.netology.cloud_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FileRequest implements Serializable, Comparable<FileRequest> {

    private String fileName;

    private Integer fileSize;


    @Override
    public int compareTo(FileRequest o) {
        return (this.getFileName().compareTo(o.getFileName()));
    }
}
