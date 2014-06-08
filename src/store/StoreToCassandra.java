package store;
 
import java.util.Properties;
 
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class StoreToCassandra {
    Cluster cluster = null;
    Session session = null;
    public  Properties properties;
 
    /**
     * Connecting with Cassandra
     */
    public void connect() {
        cluster = Cluster.builder().addContactPoints(properties.getProperty("NODE"))
                .build();
        session = cluster.connect(properties.getProperty("keyspaceName"));
        System.out.println("CONNECTED !! ! ");
    }
 
    /**
     * @param userDetails
     * @throws Exception
     *             Insert the details of user and friend list
     */
    public void insertUserDetails(UserDetails userDetails) throws Exception {
        BoundStatement boundStatement = null;
        PreparedStatement prepare_statement = null;
        String userName = userDetails.getScreenName();
        /**
         * insert user details
         */
        prepare_statement = session.prepare(CassandraInfo.INSERT_USER_DETAILS);
        boundStatement = new BoundStatement(prepare_statement);
        session.execute(boundStatement.bind(userDetails.getUserName(),
                userName, userDetails.getLocation(), userDetails.getTweetId(),
                userDetails.getCreatedDate(), userDetails.getUrl()));
 
        prepare_statement = session
                .prepare(CassandraInfo.UPDATE_TWITTER_USERNAME);
        boundStatement = new BoundStatement(prepare_statement);
        session.execute(boundStatement.bind(userDetails.getUserName(), userName));
        /**
         * insert friends names as list
         */
        prepare_statement = session.prepare(CassandraInfo.INSERT_FRIEND_LIST);
        boundStatement = new BoundStatement(prepare_statement);
        session.execute(boundStatement.bind(userName,
                userDetails.getFriendList()));
 
        insertFriendsDetails(userDetails);// to insert friends details
        /**
         * insert followers names as list
         */
 
        prepare_statement = session.prepare(CassandraInfo.INSERT_FOLLOWER_LIST);
        boundStatement = new BoundStatement(prepare_statement);
        session.execute(boundStatement.bind(userName,
                userDetails.getFollowerList()));
 
        System.out.println("Insert into  twitter_followers_list!!");
 
        insertFollowersDetails(userDetails);
    }
 
    /**
     * @param userDetails
     *            insert details of followers of a user
     */
    private void insertFollowersDetails(UserDetails userDetails) {
        BoundStatement boundStatement = null;
        PreparedStatement prepare_statement = null;
        String userName = userDetails.getScreenName();
        for (FriendsDetails fd : userDetails.getFollowersList()) {
            prepare_statement = session
                    .prepare(CassandraInfo.INSERT_FOLLOWER_DETAILS);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(userName, fd.getScreen_name(),
                    fd.getUserName(), fd.getLocation(), fd.getTweetId(),
                    fd.getCreatedDate(), fd.getUrl()));
            /**
             * update followers count
             */
            prepare_statement = session
                    .prepare(CassandraInfo.UPDATE_FOLLOWER_COUNT);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(userName));
            /**
             * insert user and screen name to the column family
             */
            prepare_statement = session
                    .prepare(CassandraInfo.UPDATE_TWITTER_USERNAME);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(fd.getUserName(),
                    fd.getScreen_name()));
        }
 
    }
 
    /**
     * @param userDetails
     *            insert details of friends of a user
     */
    private void insertFriendsDetails(UserDetails userDetails) {
        BoundStatement boundStatement = null;
        PreparedStatement prepare_statement = null;
        String userName = userDetails.getScreenName();
        for (FriendsDetails fd : userDetails.getFriendsList()) {
 
            prepare_statement = session
                    .prepare(CassandraInfo.INSERT_FRIEND_DETAILS);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(userName, fd.getScreen_name(),
                    fd.getUserName(), fd.getLocation(), fd.getTweetId(),
                    fd.getCreatedDate(), fd.getUrl()));
 
            /**
             * update friends count
             */
            prepare_statement = session
                    .prepare(CassandraInfo.UPDATE_FRIEND_COUNT);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(userName));
 
            /**
             * insert user and screen name to the column family
             */
            prepare_statement = session
                    .prepare(CassandraInfo.UPDATE_TWITTER_USERNAME);
            boundStatement = new BoundStatement(prepare_statement);
            session.execute(boundStatement.bind(fd.getUserName(),
                    fd.getScreen_name()));
        }
    }
 
/**
     * Disconnecting with Cassandra
     */
 
    public void disconnect() {
        cluster.shutdown();
        System.out.println("Disconnected!!");
 
    }
 
}