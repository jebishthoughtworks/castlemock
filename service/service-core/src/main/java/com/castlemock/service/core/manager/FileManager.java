/*
 * Copyright 2015 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castlemock.service.core.manager;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The FileManager provides functionality to upload a file to the server.
 * @author Karl Dahlgren
 * @since 1.0
 */
@Component
public class FileManager {

    @Value(value = "${temp.file.directory}")
    private String tempFilesFolder;
    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    public File uploadFile(final MultipartFile file) throws IOException {
        final File fileDirectory = new File(tempFilesFolder);

        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        final String fileName = "Upload-" + formatter.format(new Date());

        LOGGER.debug("Uploading file: " + fileName);
        final File serverFile = new File(fileDirectory, fileName);
        final byte[] bytes = file.getBytes();
        final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        try{
            stream.write(bytes);
        }finally {
            stream.close();
        }

        return serverFile;
    }

    public List<File> uploadFiles(final String downloadURL) throws IOException {
        final File fileDirectory = new File(tempFilesFolder);

        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }

        final URL url = new URL(downloadURL);
        final String fileName = generateNewFileName();
        final File file = new File(fileDirectory.getAbsolutePath() + File.separator + fileName);

        if(!file.exists()){
            file.createNewFile();
        }

        final Path targetPath = file.toPath();
        Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        final List<File> files = new ArrayList<>();
        files.add(file);
        return files;
    }

    /**
     * The method takes a file that has been uploaded to the server and deletes it
     * @param file The file of the file that will be deleted
     */
    public void deleteUploadedFile(final File file){
        Preconditions.checkNotNull(file, "Uploaded file cannot be null");
        LOGGER.debug("Deleting: " + file.getName());
        file.delete();
    }

    /**
     * The method takes a filename for a file that has been uploaded to the server and deletes them
     * @param filename The filename of the file that will be deleted
     */
    public void deleteUploadedFile(final String filename){
        Preconditions.checkNotNull(filename, "The filename cannot be null");
        final File file = new File(filename);
        if(file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * The method provides the functionality to delete a file from the file system.
     * @param file The file that will be deleted.
     * @return The result of the deletion.
     */
    public boolean deleteFile(File file){
        return file.delete();
    }

    /**
     * The method provides the functionality to rename a directory from one name to another.
     * @param oldFolderPath The path to the directory that should receive the new name. Note that the path contains
     *                      the old directory name.
     * @param newFolderPath The new path to the directory that should be renamed. Note that the path contains
     *                      the new directory name.
     * @return Returns the result of the rename operation.
     */
    public boolean renameDirectory(final String oldFolderPath, final String newFolderPath){
        File oldFileDirectory = new File(oldFolderPath);
        if (!oldFileDirectory.exists() || !oldFileDirectory.isDirectory()) {
            // The directory was not found and therefore no rename operation was accomplished.
            return false;
        }

        File newFileDirectory = new File(newFolderPath);
        return oldFileDirectory.renameTo(newFileDirectory);
    }



    private String generateNewFileName(){
        return "UploadedFile-" + RandomStringUtils.random(6, true, true);
    }




}
