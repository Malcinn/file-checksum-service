package com.company.filechecksumservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
 public class File {

    @Id
    private Long id;

    private String name;

    private Long size;

    private String checksum;

}
