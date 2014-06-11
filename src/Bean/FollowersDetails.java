package Bean;

import java.util.Date;

public class FollowersDetails {
	String userScreenName;
	String followerName;
    String followerScreenName;
    String location;
    Long tweetId;
    Date createdDate;
    String url;
    public String getUserScreenName() {
        return userScreenName;
    }
    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }
    public String getFollowerName() {
        return followerName;
    }
    public void setFollowerName(String FollowerName) {
        this.followerName = FollowerName;
    }
    public String getFollowerScreenName() {
        return followerScreenName;
    }
    public void setFollowerScreenName(String FollowerScreenName) {
        this.followerScreenName = FollowerScreenName;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Long getTweetId() {
        return tweetId;
    }
    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
