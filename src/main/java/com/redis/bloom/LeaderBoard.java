package com.redis.bloom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.github.javafaker.Faker;

import io.rebloom.client.Client;

public class LeaderBoard {

  public static void main(String[] args) {

    try (Client client = new Client("localhost", 6379)) {
      String topkFilter = "topk_rock_paper_scissors";
      client.topkCreateFilter("topk_rock_paper_scissors", 10, 2000, 7, 0.925);

      Faker faker = new Faker();
      Random rand = new Random();

      int noPlayers = 500;
      int games = 2500;
      
      List<String> players = new ArrayList<String>();
      IntStream.range(0, noPlayers).forEach(i -> players.add(faker.name().fullName()));
      System.out.println("created players...");

      for (int i = 0; i < games; i++) {
        String player1 = players.get(rand.nextInt(players.size()));
        String player2 = players.get(rand.nextInt(players.size()));

        if (rand.nextBoolean()) {
          client.topkIncrBy(topkFilter, player1, 1);
        } else {
          client.topkIncrBy(topkFilter, player2, 1);
        }
        System.out.println(String.format("game %d...", i));
      }
      
      List<String> topPlayers = client.topkList(topkFilter);
      for (int i = 0; i < topPlayers.size(); i++) {
        System.out.println(String.format("(%d) %s", i, topPlayers.get(i)));
      }
    }
  }
}
