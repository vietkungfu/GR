package REST;

import support.APIType;
import support.InfoType;
import support.OAuthTokenSecret;
import OpenAuthentication.OpenAuthentication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Locale;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import store.StoreToCassandra;
import Bean.FollowerList;
import Bean.FollowersDetails;
import Bean.FriendList;
import Bean.FriendsDetails;
import Bean.Tweet;
import Bean.UserDetails;

public class RESTApi {
	//file handlers to store the collected user information
	BufferedWriter OutFileWriter;
	OAuthTokenSecret OAuthTokens;
	/**
	 * name of the file containing a list of users
	 */
	final String DEF_FILENAME = "users.txt";
	//final String DEF_OUTFILENAME = "restapiresults.json";
	ArrayList<String> Usernames = new ArrayList<String>();
	OAuthConsumer Consumer;

	/**
	 * Creates a OAuthConsumer with the current consumer & user access tokens and secrets
	 * @return consumer
	 */
	public OAuthConsumer GetConsumer()
	{
		OAuthConsumer consumer = new DefaultOAuthConsumer(utils.OAuthUtils.CONSUMER_KEY,utils.OAuthUtils.CONSUMER_SECRET);
		consumer.setTokenWithSecret(OAuthTokens.getAccessToken(),OAuthTokens.getAccessSecret());
		return consumer;
	}

	/**
	 * Reads the file and loads the users in the file to be crawled
	 * @param filename
	 */
	public void ReadUsers(String filename)
	{
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			String temp = "";
			while((temp = br.readLine())!=null)
			{
				if(!temp.isEmpty())
				{
					Usernames.add(temp);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		finally{
			try {
				br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Load the User Access Token, and the User Access Secret
	 */
	public void LoadTwitterToken()
	{
		OAuthTokens = OpenAuthentication.DEBUGUserAccessSecret();
	}

	public static void main(String[] args) throws JSONException
	{
		//System.out.println("aaaaa");
		RESTApi rae = new RESTApi();
		rae.LoadTwitterToken();
		rae.Consumer = rae.GetConsumer();
		int apicode = InfoType.PROFILE_INFO;
		String infilename = rae.DEF_FILENAME;
		if(args!=null)
		{
			if(args.length>1)
			{	
				apicode = Integer.parseInt(args[1]);
				infilename = args[0];
			}
			else
				if(args.length>0)
				{
					infilename = args[0];
				}
		}
		rae.ReadUsers(infilename);
		if(apicode!=InfoType.PROFILE_INFO&&apicode!=InfoType.FOLLOWER_INFO&&apicode!=InfoType.FRIEND_INFO&&apicode!=InfoType.STATUSES_INFO)
		{
			System.out.println("Invalid API type: Use 0 for Profile, 1 for Followers, 2 for Friends, and 3 for Statuses");
			System.exit(0);
		}
		if(rae.Usernames.size()>0)
		{
			rae.LoadTwitterToken();
			for(String user:rae.Usernames)
			{
				if(apicode==InfoType.PROFILE_INFO)
				{
					
					JSONObject jobj = rae.GetProfile(user);
					if(jobj != null) 
					{
						StoreToCassandra storeToCassandra = new StoreToCassandra();
						rae.ReadUserDetails(jobj, storeToCassandra);
					}
				}
				else if(apicode==InfoType.FRIEND_INFO)
				{
					JSONArray friends = rae.GetFriends(user);
					if(friends.length()>0)
					{
						StoreToCassandra storeToCassandra = new StoreToCassandra();
						rae.ReadFriendsDetails(friends,user,storeToCassandra);
					}
				}
				else if(apicode == InfoType.FOLLOWER_INFO)
				{
					JSONArray followers = rae.GetFollowers(user);
					if(followers.length()>0)
					{
						StoreToCassandra storeToCassandra = new StoreToCassandra();
						rae.ReadFollowersDetails(followers, user, storeToCassandra);
					}
				}
				else if(apicode == InfoType.STATUSES_INFO)
				{
					JSONArray statusarr = rae.GetStatuses(user);
					if(statusarr.length()>0)
					{
						StoreToCassandra storeToCassandra = new StoreToCassandra();
						rae.ReadTweet(statusarr,user, storeToCassandra);
					}
				}
			}
		}
	}

	public JSONObject GetRateLimitStatus()
	{
		try{
			URL url = new URL("https://api.twitter.com/1.1/application/rate_limit_status.json");
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setReadTimeout(5000);           
			Consumer.sign(huc);
			huc.connect();
			BufferedReader bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
			StringBuffer page = new StringBuffer();
			String temp= "";
			while((temp = bRead.readLine())!=null)
			{
				page.append(temp);
			}
			bRead.close();
			return (new JSONObject(page.toString()));
		} catch (JSONException ex) {
			Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
		} catch (OAuthCommunicationException ex) {
			Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
		}  catch (OAuthMessageSignerException ex) {
			Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
		} catch (OAuthExpectationFailedException ex) {
			Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
		}catch(IOException ex)
		{
			Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public JSONObject GetProfile(String username)
	{
		BufferedReader bRead = null;
		JSONObject profile = null;
		try {
			System.out.println("Processing profile of "+username);
			boolean flag = true;
			URL url = new URL("https://api.twitter.com/1.1/users/show.json?screen_name="+username);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setReadTimeout(5000);
			Consumer.sign(huc);
			huc.connect();
			if(huc.getResponseCode()==404||huc.getResponseCode()==401){
				System.out.println(huc.getResponseMessage());
			}           
			else if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
			{
				try {
					huc.disconnect();
					System.out.println(huc.getResponseMessage());
					Thread.sleep(3000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			else if(huc.getResponseCode()==429)
			{
				try {
					huc.disconnect();
					Thread.sleep(this.GetWaitTime("/users/show/:id"));
					flag = false;
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}

			if(!flag)
			{
				huc.connect();
			}
			StringBuilder content=new StringBuilder();
			if(flag)
			{
				bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
				String temp= "";
				while((temp = bRead.readLine())!=null)
				{
					content.append(temp);
				}
			}
			huc.disconnect();
			try {
				profile = new JSONObject(content.toString());
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		} catch (OAuthCommunicationException ex) {
			ex.printStackTrace();
		} catch (OAuthMessageSignerException ex) {
			ex.printStackTrace();
		} catch (OAuthExpectationFailedException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		//System.out.(profile);
		return profile;
	}

	public JSONArray GetFollowers(String username)
	{
		BufferedReader bRead = null;
		int j=0;
		JSONArray followers = new JSONArray();
		try {
			System.out.println(" followers user =fff "+username);
			long cursor = -1;
			System.out.println(" followers user = "+username +(++j));
			while(true)
			{
				if(cursor==0)
				{
					break;
				}
				URL url = new URL("https://api.twitter.com/1.1/followers/list.json?screen_name="+username+"&cursor=" + cursor);
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setReadTimeout(5000);
				Consumer.sign(huc);
				huc.connect();
				if(huc.getResponseCode()==400||huc.getResponseCode()==404)
				{
					System.out.println(huc.getResponseMessage());
					break;
				}
				else
					if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503||huc.getResponseCode()==504)
					{
						try{
							System.out.println(huc.getResponseMessage());
							huc.disconnect();
							Thread.sleep(3000);
							continue;
						} catch (InterruptedException ex) {
							Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					else
						if(huc.getResponseCode()==429)
						{
							try {
								huc.disconnect();
								Thread.sleep(this.GetWaitTime("/followers/list"));
								continue;
							} catch (InterruptedException ex) {
								Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
				bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
				StringBuilder content = new StringBuilder();
				String temp = "";
				while((temp = bRead.readLine())!=null)
				{
					content.append(temp);
				}
				try {
					JSONObject jobj = new JSONObject(content.toString());
					cursor = jobj.getLong("next_cursor");
					JSONArray idlist = jobj.getJSONArray("users");
					if(idlist.length()==0)
					{
						break;
					}
					for(int i=0;i<idlist.length();i++)
					{
						followers.put(idlist.getJSONObject(i));
					}
				} catch (JSONException ex) {
					Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (OAuthCommunicationException ex) {
			ex.printStackTrace();
		} catch (OAuthMessageSignerException ex) {
			ex.printStackTrace();
		} catch (OAuthExpectationFailedException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {              
			ex.printStackTrace();
		}
		return followers;
	}

	public JSONArray GetStatuses(String username)
	{
		BufferedReader bRead = null;
		//Get the maximum number of tweets possible in a single page 200
		int tweetcount = 200;
		//Include include_rts because it is counted towards the limit anyway.
		boolean include_rts = true;
		JSONArray statuses = new JSONArray();
		try {
			System.out.println("Processing status messages of "+username);
			long maxid = 0;
			while(true)
			{
				URL url = null;                    
				if(maxid==0)
				{
					url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + username+"&include_rts="+include_rts+"&count="+tweetcount);
				}
				else
				{
					//use max_id to get the tweets in the next page. Use max_id-1 to avoid getting redundant tweets.
					url = new URL("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + username+"&include_rts="+include_rts+"&count="+tweetcount+"&max_id="+(maxid-1));
				}
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setReadTimeout(5000);
				Consumer.sign(huc);
				huc.connect();
				if(huc.getResponseCode()==400||huc.getResponseCode()==404)
				{
					System.out.println(huc.getResponseCode());
					break;
				}
				else
					if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
					{
						try {System.out.println(huc.getResponseCode());
						Thread.sleep(3000);
						} catch (InterruptedException ex) {
							Logger.getLogger(RESTApi.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
					else
						if(huc.getResponseCode()==429)
						{
							try {
								huc.disconnect();
								Thread.sleep(this.GetWaitTime("/statuses/user_timeline"));
								continue;
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
				bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getInputStream()));
				StringBuilder content = new StringBuilder();
				String temp = "";
				while((temp = bRead.readLine())!=null)
				{
					content.append(temp);
				}
				try {
					JSONArray statusarr = new JSONArray(content.toString());
					if(statusarr.length()==0)
					{
						break;
					}
					for(int i=0;i<statusarr.length();i++)
					{
						JSONObject jobj = statusarr.getJSONObject(i);
						statuses.put(jobj);
						//Get the max_id to get the next batch of tweets
						if(!jobj.isNull("id"))
						{
							maxid = jobj.getLong("id");
						}
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			}
			System.out.println(statuses);
		} catch (OAuthCommunicationException ex) {
			ex.printStackTrace();
		} catch (OAuthMessageSignerException ex) {
			ex.printStackTrace();
		} catch (OAuthExpectationFailedException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return statuses;
	}

	public JSONArray GetFriends(String username)
	{
		BufferedReader bRead = null;
		JSONArray friends = new JSONArray();
		try {
			//System.out.println("Processing friends of "+username);
			long cursor = -1;
			while(true)
			{
				if(cursor==0)
				{
					break;
				}
				URL url = new URL("https://api.twitter.com/1.1/friends/list.json?screen_name="+username+"&cursor="+cursor);
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setReadTimeout(5000);
				Consumer.sign(huc);
				huc.connect();
				if(huc.getResponseCode()==400||huc.getResponseCode()==401)
				{
					System.out.println(huc.getResponseMessage());
					break;
				}
				else
					if(huc.getResponseCode()==500||huc.getResponseCode()==502||huc.getResponseCode()==503)
					{
						try {
							System.out.println(huc.getResponseMessage());
							Thread.sleep(3000);
							continue;
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}
					else
						if(huc.getResponseCode()==429)
						{
							try {
								huc.disconnect();
								Thread.sleep(this.GetWaitTime("/friends/list"));
								continue;
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}
				bRead = new BufferedReader(new InputStreamReader((InputStream) huc.getContent()));
				StringBuilder content = new StringBuilder();
				String temp = "";
				while((temp = bRead.readLine())!=null)
				{
					content.append(temp);
				}
				try {
					JSONObject jobj = new JSONObject(content.toString());
					cursor = jobj.getLong("next_cursor");
					JSONArray userlist = jobj.getJSONArray("users");
					if(userlist.length()==0)
					{
						break;
					}
					for(int i=0;i<userlist.length();i++)
					{
						friends.put(userlist.get(i));
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}               
				huc.disconnect();
			}
		} catch (OAuthCommunicationException ex) {
			ex.printStackTrace();
		} catch (OAuthMessageSignerException ex) {
			ex.printStackTrace();
		} catch (OAuthExpectationFailedException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return friends;
	}

	public long GetWaitTime(String api)
	{
		JSONObject jobj = this.GetRateLimitStatus();
		if(jobj!=null)
		{
			try {                
				if(!jobj.isNull("resources"))
				{
					JSONObject resourcesobj = jobj.getJSONObject("resources");
					JSONObject apilimit = null;
					if(api.equals(APIType.USER_TIMELINE))
					{
						JSONObject statusobj = resourcesobj.getJSONObject("statuses");
						apilimit = statusobj.getJSONObject(api);
					}
					else if(api.equals(APIType.FOLLOWERS))
						{
							JSONObject followersobj = resourcesobj.getJSONObject("followers");
							apilimit = followersobj.getJSONObject(api);
						}
						else if(api.equals(APIType.FRIENDS))
							{
								JSONObject friendsobj = resourcesobj.getJSONObject("friends");
								apilimit = friendsobj.getJSONObject(api);
							}
							else if(api.equals(APIType.USER_PROFILE))
								{
									JSONObject userobj = resourcesobj.getJSONObject("users");
									apilimit = userobj.getJSONObject(api);
								}
					int numremhits = apilimit.getInt("remaining");
					if(numremhits<=1)
					{
						long resettime = apilimit.getInt("reset");
						resettime = resettime*1000; //convert to milliseconds
						return resettime;
					}
				}
			} catch (JSONException ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}

	private void ReadUserDetails(JSONObject jobj, StoreToCassandra storeToCassandra){
		UserDetails userDetails = new UserDetails();
		try{
			userDetails.setScreenName(jobj.getString("screen_name"));
			userDetails.setTweetId(jobj.getLong("id"));
			userDetails.setUserName(jobj.getString("name"));
			userDetails.setLocation(jobj.getString("location"));
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(jobj.getString("created_at"));
			userDetails.setCreatedDate(date);
			System.out.println(date);
			userDetails.setUrl((String) jobj.get("url"));
			//System.out.println("==========FOLLOWING " + jobj);
			System.out.println(jobj.getString("name")+ "is inserted.");
		}catch (Exception e){
			e.printStackTrace();
		}
		storeToCassandra.connectToCassandra();
		System.out.println("Day la sau connect");
		storeToCassandra.insertUserDetail(userDetails);
		System.out.println("Day la sau insert");
		storeToCassandra.disconnectFromCassandra();
		//return userDetails;
	}

	private void ReadFriendsDetails(JSONArray friends, String user, StoreToCassandra storeToCassandra){
		FriendList friendsList = new FriendList(); 
		List<String> friendsName = new LinkedList<String>();
		storeToCassandra.connectToCassandra();
		for (int i=0;i < friends.length();i++) {
			try{
				JSONObject friend = friends.getJSONObject(i); 
	            FriendsDetails friendsDetail = new FriendsDetails();
	            friendsName.add(friend.getString("name"));
	            friendsDetail.setFriendName(friend.getString("name"));
	            if (friend.getString("url") == null) friendsDetail.setUrl("");
	            	else friendsDetail.setUrl(friend.getString("url"));
	            friendsDetail.setUserScreenName(user);
	            friendsDetail.setCreatedDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(friend.getString("created_at")));
	            friendsDetail.setFriendScreenName(friend.getString("screen_name"));
	            friendsDetail.setLocation(friend.getString("location"));
	            friendsDetail.setTweetId(friend.getLong("id"));
	            System.out.println(i);
	            
	    		storeToCassandra.insertFriendDetails(friendsDetail);	    		
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		friendsList.setScreenName(user);
		friendsList.setFriendList(friendsName);
		
		storeToCassandra.insertFriendsList(friendsList);
		storeToCassandra.disconnectFromCassandra();
	}
	
	private void ReadFollowersDetails(JSONArray followers, String user, StoreToCassandra storeToCassandra){
		List<String> followersName = new LinkedList<String>();
		FollowerList followerList =new FollowerList();
		storeToCassandra.connectToCassandra();
		for (int i=0;i < followers.length();i++) {
			try{
				JSONObject follower = followers.getJSONObject(i); 
	            FollowersDetails followersDetail = new FollowersDetails();
	            followersName.add(follower.getString("name"));
	            followersDetail.setFollowerName(follower.getString("name"));
	            if (follower.getString("url") == null) followersDetail.setUrl("");
	            	else followersDetail.setUrl(follower.getString("url"));
	            followersDetail.setUserScreenName(user);
	            followersDetail.setCreatedDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(follower.getString("created_at")));
	            followersDetail.setFollowerScreenName(follower.getString("screen_name"));
	            followersDetail.setLocation(follower.getString("location"));
	            followersDetail.setTweetId(follower.getLong("id"));
	            System.out.println(i);
	            
	    		storeToCassandra.insertFollowersDetails(followersDetail);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		//StoreToCassandra.connectToCassandra();
		followerList.setScreenName(user);
		followerList.setFollowerList(followersName);
		
		storeToCassandra.insertFollowersList(followerList);
		storeToCassandra.disconnectFromCassandra();
	}
	
	private void ReadTweet(JSONArray statusarr, String user, StoreToCassandra storeToCassandra){
		storeToCassandra.connectToCassandra();
		for (int i=0;i < statusarr.length();i++) {
			try{
				JSONObject status = statusarr.getJSONObject(i); 
	            Tweet tweet = new Tweet();
	            tweet.setScreenName(user);
	            tweet.setStatusId(status.getLong("id"));
	            tweet.setTitle(status.getString("text"));
	            tweet.setPublishedDate(new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(status.getString("created_at")));
	            System.out.println(user + status.getLong("id")+ status.getString("text") + new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH).parse(status.getString("created_at")));
	            storeToCassandra.insertTweet(tweet);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		storeToCassandra.disconnectFromCassandra();
	}
}
