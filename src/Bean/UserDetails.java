package Bean;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class UserDetails {
	String userName;
	String screenName;
	String location;
	Long tweetId;
	Date createdDate;
	String url;
	List<String> friendList = new LinkedList<String>();
	List<FriendsDetails> friendsList = new LinkedList<FriendsDetails>();
	List<String> followerList = new LinkedList<String>();
	List<FriendsDetails> followersList = new LinkedList<FriendsDetails>();

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	/*public void setScreen_name(String screenName) {
		this.screenName = screenName;
	}*/

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

	public List<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<String> friendList) {
		this.friendList = friendList;
	}

	public List<FriendsDetails> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<FriendsDetails> friendsList) {
		this.friendsList = friendsList;
	}

	public List<String> getFollowerList() {
		return followerList;
	}

	public void setFollowerList(List<String> followerList) {
		this.followerList = followerList;
	}

	public List<FriendsDetails> getFollowersList() {
		return followersList;
	}

	public void setFollowersList(List<FriendsDetails> followersList) {
		this.followersList = followersList;
	}
}
