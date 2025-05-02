package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.ClubPlayer;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerPosition;
import org.prog3.foot.repository.implementation.PayerRepositoryImplementation;

import java.util.ArrayList;
import java.util.List;


public class PlayerRepositoryTest {
    private DataSource DataSource=new DataSource();
    private  PayerRepositoryImplementation repo = new PayerRepositoryImplementation(DataSource);

    @Test
    void upSertPlayers(){
        Player player1=new Player();
        player1.setPlayerPosition(PlayerPosition.MIDFIELDER);
        player1.setAge(19);
        player1.setName("Jarvis CHANG");
        player1.setNationality("China");
        player1.setId("JarvisChang");
        player1.setNumber(69);

        Player player2=new Player();
        player2.setPlayerPosition(PlayerPosition.STRIKER);
        player2.setAge(32);
        player2.setName("Alexandre Lacazette ");
        player2.setNationality("Français");
        player2.setId("lacazette");
        player2.setNumber(10);

        final List<ClubPlayer> firstGet= repo.getClubPlayers();

        List<Player> execute = new ArrayList<>();

        execute.add(player1);
        execute.add(player2);

        repo.upCreatePlayers(execute);

        final List<ClubPlayer> secondGet= repo.getClubPlayers();

        Assertions.assertFalse(firstGet.size() == secondGet.size());
    }

    @Test
    void getStatistics(){
        System.out.println(repo.getPlayerStatsitic("lacazette",2023));
    }

}
