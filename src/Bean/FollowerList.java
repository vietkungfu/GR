package Bean;

import java.util.LinkedList;
import java.util.List;

public class FollowerList {
	
	String screenName;
	List<String> followerList = new LinkedList<String>();
	
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public List<String> getFollowerList() {
		return followerList;
	}

	public void setFollowerList(List<String> followerList) {
		this.followerList = followerList;
	}
}
