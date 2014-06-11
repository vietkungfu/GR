package Bean;

import java.util.LinkedList;
import java.util.List;

public class FriendList {
	
	String screenName;
	List<String> friendList = new LinkedList<String>();
	
	
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public List<String> getFriendList() {
		return friendList;
	}

	public void setFriendList(List<String> friendList) {
		this.friendList = friendList;
	}
}
