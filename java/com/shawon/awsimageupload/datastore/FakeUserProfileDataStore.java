package com.shawon.awsimageupload.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shawon.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();
    
    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("d743aa83-ff28-4d1d-8b5f-9bf1dd11aaff"), "janetjones", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("28f83d7b-bd72-4399-9309-67946141245a"), "antoniojunior", null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
