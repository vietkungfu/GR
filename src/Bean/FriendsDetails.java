package Bean;

import java.util.Date;

public class FriendsDetails {
	String userScreenName;
	String friendName;
    String friendScreenName;
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
    public String getFriendName() {
        return friendName;
    }
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
    public String getFriendScreenName() {
        return friendScreenName;
    }
    public void setFriendScreenName(String friendScreenName) {
        this.friendScreenName = friendScreenName;
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
