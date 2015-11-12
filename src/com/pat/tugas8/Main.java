package com.pat.tugas8;

import javax.net.ssl.SSLContext;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
        String ipAddress;
        String keySpace = "fahziar";
        String username = "";
        String password;
        User user = null;
        CassandraTwitter twitter;
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter server ip address:");
        ipAddress = scanner.nextLine();
        twitter = new CassandraTwitter(ipAddress, keySpace);

        while (user == null) {
            System.out.println("Enter command (r:register, l:login):");
            String cmd = scanner.nextLine();
            if (cmd.equalsIgnoreCase("r")) {
                System.out.println("User Registration");
                System.out.println("Enter username");
                username = scanner.nextLine();
                System.out.println("Enter password");
                password = scanner.nextLine();

                twitter.registerUser(new User(username, password));
                user = twitter.getUser(username);
            } else {
                System.out.println("User Login");
                System.out.println("Enter username");
                username = scanner.nextLine();
                System.out.println("Enter password");
                password = scanner.nextLine();
                user = twitter.getUser(username);
                if (user == null) {
                    System.out.println("Invalid username or password");
                }
            }
        }

        //Show welcome message to user
        System.out.println("Welcome to Cassandra Tweeets");
        System.out.println("Command list:");
        System.out.println("/follow <username>: follow a user");
        System.out.println("/tweet <tweet>: post a tweet");
        System.out.println("/getTweet <username>: view all tweets from an user user");
        System.out.println("/timeline <username>: view timeline from an user");
        System.out.println("/exit : quit application");

        boolean quit = false;
        do {
            System.out.println();
            System.out.println("Enter your command:");
            String cmd = scanner.nextLine();
            //done
            if (cmd.startsWith("/exit")){
                quit = true;
             //done
            } else if (cmd.startsWith("/getTweet")){
                if (cmd.split(" ").length > 1) {
                    String target = cmd.split(" ")[1];
                    List<Tweet> tweets = twitter.getTweets(target);
                    for (Tweet tweet : tweets) {
                        System.out.println(tweet.getBody());
                    }
                } else {
                    System.out.println("Wrong syntax");
                }
            //done
            } else if (cmd.startsWith("/follow")){
                if (cmd.split(" ").length > 1) {
                    String target = cmd.split(" ")[1];
                    User friend = twitter.getUser(target);
                    if (friend == null) {
                        System.out.println("Invalid username");
                    } else {
                        twitter.followFriend(user, friend);
                    }
                } else {
                    System.out.println("Invalid syntax");
                }

            } else if (cmd.startsWith("/tweet")){
                if (cmd.split(" ").length > 0) {
                    StringBuilder builder = new StringBuilder();
                    String[] tweetArray = cmd.split(" ");
                    for (int i = 1; i < tweetArray.length; i++) {
                        builder.append(tweetArray[i]);
                        if (i + 1 < tweetArray.length) {
                            builder.append(" ");
                        }
                    }

                    String tweetString = builder.toString();
                    twitter.postTweet(new Tweet(username, tweetString));
                    System.out.println("Tweet posted");
                } else {
                    System.out.println("Invalid syntax");
                }

            } else if (cmd.startsWith("/timeline")){
                if (cmd.split(" ").length > 1) {
                    String target = cmd.split(" ")[1];
                    List<Timeline> tweets = twitter.getUserTimeline(target);
                    for (Timeline tweet : tweets) {
                        System.out.print(tweet.getTime() + " ");
                        System.out.println(tweet.getTweet().getBody());
                    }
                } else {
                    System.out.println("Invalid syntax");
                }
            }
        } while (!quit);

        System.exit(0);
    }
}
