package com.company.filechecksumservice.interfaces.facade;

import org.springframework.http.codec.multipart.FilePart;

public interface FileFacade {

    void load(FilePart part);
}
