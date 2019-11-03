package com.fut.chatbot.util;

import java.io.File;
import java.io.IOException;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

public class ImageSaver {

    private static ImageSaver instance;

    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "portable",
            "api_key", "426127912636286",
            "api_secret", "bktbnyjN0SCjNcR68v7vKtZP-TQ"));

    private ImageSaver() {

    }

    public static ImageSaver getInstance() {
        if (instance == null) {
            instance = new ImageSaver();
        }

        return instance;
    }

    public String saveImage(File f) throws IOException {
        return (cloudinary.uploader().upload(f, ObjectUtils.emptyMap())).get("url").toString();
    }

}
