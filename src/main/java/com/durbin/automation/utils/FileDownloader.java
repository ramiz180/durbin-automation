package com.durbin.automation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    private static final Logger logger = LogManager.getLogger(FileDownloader.class);
    private static final String DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "src" +
            File.separator + "test" + File.separator + "resources" + File.separator + "testdata" + File.separator;

    public static String downloadFile(String fileUrl, String customer_name) {
        String downloadedFilePath = null;
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String safeCustomerName = customer_name.replaceAll("[^a-zA-Z0-9._-]", "_");
                String fileName = safeCustomerName + "_" + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

                File directory = new File(DOWNLOAD_DIR);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String saveFilePath = DOWNLOAD_DIR + fileName;
                File outputFile = new File(saveFilePath);

                InputStream inputStream = httpConn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                downloadedFilePath = outputFile.getAbsolutePath();
                logger.info("File downloaded successfully to: " + downloadedFilePath);
            } else {
                logger.error("No file to download. Server replied HTTP code: " + responseCode);
            }
            httpConn.disconnect();
        } catch (Exception e) {
            logger.error("Error downloading file from URL: " + fileUrl, e);
        }
        return downloadedFilePath;
    }
}

