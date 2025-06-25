package com.example.UserManagement.Service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.UserManagement.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;

    public String uploadImage(MultipartFile file){
        try{
            var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder","/upload",
                    "use_filename",true,
                    "unique_filename",true,
                    "resource_type","auto"
            ));
            return result.get("secure_url").toString();//lấy ra url của ảnh
        }catch (IOException e){
            throw new RuntimeException("Upload fail");
        }
    }
}
