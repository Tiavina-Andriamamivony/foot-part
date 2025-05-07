package org.prog3.foot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.prog3.foot.configuration.DataSource;
import org.prog3.foot.models.Club;
import org.prog3.foot.models.Coach;
import org.prog3.foot.models.Player;
import org.prog3.foot.models.PlayerPosition;
import org.prog3.foot.repository.implementation.ClubRepositoryImplementation;

import java.util.ArrayList;
import java.util.List;

public class ClubTest {
    private DataSource dataSource = new DataSource();
    private ClubRepositoryImplementation repo = new ClubRepositoryImplementation(dataSource);

    @Test
    void upCreateClubs() {
        // Create a new club
        Club newClub = new Club();
        newClub.setId("testclub");
        newClub.setName("Test Football Club");
        newClub.setAcronym("TFC");
        newClub.setYearCreation(2024);
        newClub.setStadium("Test Stadium");
        
        Coach coach = new Coach();
        coach.setName("Test Coach");
        coach.setNationality("French");
        newClub.setCoach(coach);

        List<Club> clubs = new ArrayList<>();
        clubs.add(newClub);

        // Get initial clubs count
        final List<Club> firstGet = repo.getClubs();
        
        // Execute upsert
        repo.upCreateClub(clubs);

        // Get updated clubs count
        final List<Club> secondGet = repo.getClubs();

        // Assert the change
        Assertions.assertFalse(firstGet.size() == secondGet.size());
    }

    @Test
    void getPlayersFromClub() {
        // Using existing club 'lyon' from data.sql
        List<Player> players = repo.getPlayersFromASpecificClub("lyon");
        Assertions.assertFalse(players.isEmpty());
    }

    @Test
    void addAndDropPlayers() {
        // Create a test player
        Player player = new Player();
        player.setId("testplayer");
        player.setName("Test Player");
        player.setNumber(99);
        player.setPlayerPosition(PlayerPosition.STRIKER);
        player.setNationality("Test Nation");
        player.setAge(25);

        List<Player> players = new ArrayList<>();
        players.add(player);

        // Add player to Lyon (existing club from data.sql)
        List<Player> addedPlayers = repo.addPlayer("lyon", players);
        Assertions.assertFalse(addedPlayers.isEmpty());

        // Drop the same player
        List<Player> droppedPlayers = repo.dropPlayer("lyon", players);
        Assertions.assertFalse(droppedPlayers.isEmpty());
    }

    @Test
    void getClubStatistics() {
        // Test statistics for the 2023 season (from data.sql)
        List<ClubStatistics> statistics = repo.getClubStatistics(2023);
        Assertions.assertFalse(statistics.isEmpty());
    }
}
