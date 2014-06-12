package store;

import java.util.Properties;

import Bean.FollowerList;
import Bean.FollowersDetails;
import Bean.FriendList;
import Bean.FriendsDetails;
import Bean.Tweet;
import Bean.UserDetails;

//import org.slf4j.LoggerFactory;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import store.CassandraInfo;

public class StoreToCassandra {
	
	private Cluster cluster;
    private Session session;
    public  Properties properties;
    
    public void connectToCassandra(){
    	System.out.println(CassandraInfo.NODE);
    	try{
	    	cluster = Cluster.builder().addContactPoint(CassandraInfo.NODE).withPort(CassandraInfo.PORT).build();
	    	Metadata metadata = cluster.getMetadata();
	        System.out.printf("Connected to cluster: %s\n", 
	              metadata.getClusterName());
	    	session = cluster.connect(CassandraInfo.KEYSPACE);
	    	System.out.println("CONNECTED TO " + CassandraInfo.KEYSPACE);
    	}catch (ExceptionInInitializerError e){
    		e.printStackTrace();
    	}
    }
    
    public void insertUserDetail(UserDetails userDetail){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null;
    	prepare_statement = session.prepare(CassandraInfo.INSERT_USER_DETAILS);
    	boundStatement = new BoundStatement(prepare_statement);
    	session.execute(boundStatement.bind(
    			userDetail.getUserName(),
    			userDetail.getScreenName(),
    			userDetail.getLocation(), 
    			userDetail.getTweetId(), 
    			userDetail.getCreatedDate(), 
    			userDetail.getUrl()
		));
    	
    	 /*prepare_statement = session.prepare(CassandraInfo.UPDATE_TWITTER_USERNAME);
         boundStatement = new BoundStatement(prepare_statement);
         session.execute(boundStatement.bind(userDetail.getUserName(), screenName));*/
    }
    
    public void insertFriendsList(FriendList friendList){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null; 
    	prepare_statement = session.prepare(CassandraInfo.INSERT_FRIEND_LIST);
         boundStatement = new BoundStatement(prepare_statement);
         session.execute(boundStatement.bind(
        		 friendList.getScreenName(),
                 friendList.getFriendList()));  
    }
    
    public void insertFollowersList(FollowerList followerList){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null;
    	prepare_statement = session.prepare(CassandraInfo.INSERT_FOLLOWER_LIST);
        boundStatement = new BoundStatement(prepare_statement);
        session.execute(boundStatement.bind(
        		followerList.getScreenName(),
                followerList.getFollowerList()));
    }
    
    public void insertFriendDetails(FriendsDetails friendDetail){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null;
    	prepare_statement = session.prepare(CassandraInfo.INSERT_FRIEND_DETAILS);
    	boundStatement = new BoundStatement(prepare_statement);
    	session.execute(boundStatement.bind(
    			friendDetail.getUserScreenName(),
    			friendDetail.getFriendScreenName(),
    			friendDetail.getFriendName(),
    			friendDetail.getLocation(), 
    			friendDetail.getTweetId(), 
    			friendDetail.getCreatedDate(), 
    			friendDetail.getUrl()
		));
    }

    public void insertFollowersDetails(FollowersDetails followerDetail){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null;
    	prepare_statement = session.prepare(CassandraInfo.INSERT_FOLLOWER_DETAILS);
    	boundStatement = new BoundStatement(prepare_statement);
    	session.execute(boundStatement.bind(
    			followerDetail.getUserScreenName(),
    			followerDetail.getFollowerScreenName(),
    			followerDetail.getFollowerName(),
    			followerDetail.getLocation(), 
    			followerDetail.getTweetId(), 
    			followerDetail.getCreatedDate(), 
    			followerDetail.getUrl()
		));
    }
    
    public void insertTweet(Tweet tweet){
    	BoundStatement boundStatement = null;
    	PreparedStatement prepare_statement = null;
    	prepare_statement = session.prepare(CassandraInfo.INSERT_TWEET);
    	boundStatement = new BoundStatement(prepare_statement);
    	session.execute(boundStatement.bind(
    			tweet.getStatusId(),
    			tweet.getScreenName(),
    			tweet.getPublishedDate(),
    			tweet.getTitle()
		));
    }
    
    public void disconnectFromCassandra(){
    	cluster.close();
    	System.out.println("Disconnected From Cassandra.");
    }
}
