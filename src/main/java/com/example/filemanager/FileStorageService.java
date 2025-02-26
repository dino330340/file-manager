package com.example.filemanager;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private static final String STORAGE_DIRECTORY = "D:\\Storage";

    public void saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null){
            throw new NullPointerException("fileToSave is null");
        }
        var targetFile = new File(STORAGE_DIRECTORY + File.separator + fileToSave.getOriginalFilename());
        if (!Objects.equals(targetFile.getParent(),STORAGE_DIRECTORY)) {
            throw new SecurityException("Unsupported file name");
        }
        Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String fileName) throws Exception {
        if(fileName == null) {
            throw new NullPointerException("FilName is null");
        }
        var fileToDownload = new File(STORAGE_DIRECTORY + File.separator + fileName);
        if (!Objects.equals(fileToDownload.getParent(),STORAGE_DIRECTORY)) {
            throw new SecurityException("Unsupported file name");
        }
        if (!fileToDownload.exists()) {
            throw new FileNotFoundException("no filed named " + fileName);
        }
        return fileToDownload;
    }
}
