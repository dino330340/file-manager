package com.example.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileManagerController {

    @Autowired
    private FileStorageService fileStorageService;
    private static final Logger log = Logger.getLogger(FileManagerController.class.getName());

    @PostMapping("/upload-file")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileStorageService.saveFile(file);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during upload", e);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam Map<String, String> allParams) {
        log.info("Received parameters: " + allParams);

        if (!allParams.containsKey("fileName")) {
            log.warning("Missing 'fileName' parameter in request");
            return ResponseEntity.badRequest().body(null);
        }

        String filename = allParams.get("filename");

        try {
            var fileToDownload = fileStorageService.getDownloadFile(filename);
            return ResponseEntity.ok()
                    .contentLength(fileToDownload.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(Files.newInputStream(fileToDownload.toPath())));
        } catch (FileNotFoundException e) {
            log.warning("File not found: " + filename);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.severe("Error processing request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }
}

