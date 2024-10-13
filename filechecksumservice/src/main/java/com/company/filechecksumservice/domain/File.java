package com.company.filechecksumservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private String checksum;

}
