package com.shawon.awsimageupload.profile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.cloudsearchdomain.model.ContentType;
import com.shawon.awsimageupload.bucket.BucketName;
import com.shawon.awsimageupload.filestore.FileStore;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {
    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore){
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

     void uploadUserProfileImage(UUID userProfileId, MultipartFile file){
        // 1. check if image is not empty
        if (file.isEmpty()){
            throw new IllegalStateException("cannot upload empty file" + file.getSize());
        }
        // 2. if file is an image
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("file must be an image " + file.getContentType());
        }
        // 3. the user exists in our database
        UserProfile user = getUserProfileOrThrow(userProfileId);
        // 5. store the image in s3 and update database (userProfileImageLink) with s3 image link


        Map<String, String> metadata = extractMetadata(file);
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

        try{
            fileStore.save(path, filename, Optional.of(metadata),file.getInputStream() );
            user.setUserProfileImageLink(filename);
        }
        catch (IOException e){
            throw new IllegalStateException(e);
        }
       
    }

     byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImageLink()
        .map(key -> fileStore.download(path, key))
        .orElse(new byte[0]);
       
       
    }

    //grab some metadata from file
    private Map<String, String> extractMetadata(MultipartFile file){ Map<String, String> metadata = new HashMap<>();
        metadata.put("Content_Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {return userProfileDataAccessService.getUserProfiles().stream()
        .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));}
        
      

   
}
