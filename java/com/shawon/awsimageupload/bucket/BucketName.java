package com.shawon.awsimageupload.bucket;

public enum BucketName {
    PROFILE_IMAGE("shawons-image-upload");

    private final String bucketName;

    BucketName(String bucketName){
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
