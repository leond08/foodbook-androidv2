package falcon.assassin.ph.foodblog.model;

import android.graphics.Bitmap;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    public String userId;
    public String originalImageUri;
    public String thumbnailUri;
    public String description;
    public Long timeStamp;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Post(String userId, String originalImageUri, String thumbnailUri, String description, Long timeStamp) {
        this.userId = userId;
        this.originalImageUri = originalImageUri;
        this.thumbnailUri = thumbnailUri;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginalImageUri() {
        return originalImageUri;
    }

    public void setOriginalImageUri(String originalImageUri) {
        this.originalImageUri = originalImageUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = this.timeStamp;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("originalImageUri", originalImageUri);
        result.put("thumbnailUri", thumbnailUri);
        result.put("description", description);

        return result;
    }

}
