package store;

public class CassandraInfo {
	static final String KEYSPACE = "GR";
	static final String NODE = "127.0.0.1";
	static final String PORT = "9160";
	static final String INSERT_USER_DETAILS = "insert into  user_details (user_name, screen_name , location , twitter_id , created_date , url ) values (?,?,?,?,?,?)";
	static final String INSERT_FRIEND_LIST = "insert into  friend_list (screen_name, friends ) values (?,?)";
	static final String INSERT_FOLLOWER_LIST = "insert into  follower_list (screen_name, followers) values (?,?)";
	static final String INSERT_FRIEND_DETAILS = "insert into  friend_details (user_screen_name,friend_screen_name, friend_user_name , location , twitter_id , created_date , url ) values (?,?,?,?,?,?,?)";
	static final String INSERT_FOLLOWER_DETAILS = "insert into  follower_details (user_screen_name,follower_screen_name, follower_user_name , location , twitter_id , created_date , url ) values (?,?,?,?,?,?,?)";
	static final String INSERT_TWEET = "insert into tweet(status_id, screen_name, published_date, created_date, title) values (?,?,?,dateOf(now()),?)";
}
