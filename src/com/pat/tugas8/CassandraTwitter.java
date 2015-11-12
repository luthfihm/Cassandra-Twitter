package com.pat.tugas8;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.*;

/**
 * Created by luthfi on 11/12/2015.
 */
public class CassandraTwitter {
    private String node;
    private String keyspace;
    private Cluster cluster;
    private Session session;

    public CassandraTwitter(String node, String keyspace) {
        this.node = node;
        this.keyspace = keyspace;
        cluster = Cluster.builder()
                .addContactPoint(node)
                .build();
        session = cluster.connect(keyspace);
    }

    public void registerUser(User user) {
        session.execute("INSERT INTO users (username,password) VALUES('"+user.getUsername()+"','"+user.getPassword()+"')");
    }

    public User getUser(String username) {
        ResultSet result = session.execute("SELECT * from users WHERE username='"+username+"'");
        User user = null;
        for (Row row : result){
            user = new User();
            user.setUsername(row.getString("username"));
            user.setPassword(row.getString("password"));
        }
        return user;
    }

    public List<User> getAllUser() {
        List<User> users = new ArrayList<User>();
        ResultSet result = session.execute("SELECT * from users");
        for (Row row : result){
            User user = new User(row.getString("username"), row.getString("password"));
            users.add(user);
        }
        return users;
    }

    public void followFriend(User follower, User friend) {
        session.execute("INSERT INTO followers (username, follower, since) VALUES ('"+friend.getUsername()+"', '"+follower.getPassword()+"', dateof(now()))");
        session.execute("INSERT INTO friends (username, friend, since) VALUES ('"+follower.getUsername()+"', '"+friend.getPassword()+"', dateof(now()))");
    }

    public List<User> getFollowers(String username) {
        List<User> followers = new ArrayList<User>();
        ResultSet result = session.execute("SELECT * from followers WHERE username='"+username+"'");
        for (Row row : result){
            User follower = this.getUser(row.getString("follower"));
            followers.add(follower);
        }
        return followers;
    }

    public void postTweet(Tweet tweet) {
        session.execute("INSERT INTO tweets (tweet_id, username, body) VALUES ("+tweet.getTweet_id().toString()+", '"+tweet.getUsername()+"', '"+tweet.getBody()+"')");
        session.execute("INSERT INTO userline (username, time, tweet_id) VALUES ('"+tweet.getUsername()+"', now(), "+tweet.getTweet_id()+")");
        session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('"+tweet.getUsername()+"', now(), "+tweet.getTweet_id()+")");
        List<User> followers = this.getFollowers(tweet.getUsername());
        for (User follower : followers) {
            session.execute("INSERT INTO timeline (username, time, tweet_id) VALUES ('" + follower.getUsername() + "', now(), " + tweet.getTweet_id() + ")");
        }
    }

    public Tweet getTweet(UUID tweet_id) {
        ResultSet result = session.execute("SELECT * from tweets WHERE tweet_id="+tweet_id.toString());
        Tweet tweet = null;
        for (Row row : result){
            tweet = new Tweet();
            tweet.setTweet_id(row.getUUID("tweet_id"));
            tweet.setUsername(row.getString("username"));
            tweet.setBody(row.getString("body"));
        }
        return tweet;
    }

    public List<Tweet> getTweets(String username) {
        List<Tweet> tweets = new ArrayList<Tweet>();
        ResultSet result = session.execute("SELECT * from userline WHERE username='"+username+"'");
        for (Row row : result){
            Tweet tweet = getTweet(row.getUUID("tweet_id"));
//            tweet.setTweet_id(row.getUUID("tweet_id"));
//            tweet.setUsername(row.getString("username"));
//            tweet.setBody(row.getString("body"));
            tweets.add(tweet);
        }
        return tweets;
    }

    public List<Timeline> getUserTimeline(String username) {
        List<Timeline> timelineList = new ArrayList<Timeline>();
        ResultSet result = session.execute("SELECT * from timeline WHERE username='"+username+"'");

        for (Row row : result) {
            Timeline timeline = new Timeline();
            timeline.setUsername(username);
            timeline.setTime(new Date((row.getUUID("time").timestamp() / 10000) -12219292800000l).toString());
            timeline.setTweet(this.getTweet(row.getUUID("tweet_id")));
            timelineList.add(timeline);
        }
        return timelineList;
    }
}
