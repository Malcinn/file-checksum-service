package com.company.filechecksumservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class File {

    @Id
    private Long id;

    private String name;

    private Long size;

    private char[] checksum;
    
}
