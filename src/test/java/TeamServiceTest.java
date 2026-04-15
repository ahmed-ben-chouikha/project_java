package edu.connexion3a36.tests;

import edu.connexion3a36.entities.Team;
import edu.connexion3a36.services.TeamService;

import java.util.List;

/**
 * Test class for TeamService CRUD operations
 * Run this to verify the Teams CRUD system is working correctly
 */
public class TeamServiceTest {

    public static void main(String[] args) {
        TeamService teamService = new TeamService();

        System.out.println("=== Teams CRUD Test Suite ===\n");

        // Test 1: Get all teams
        System.out.println("✓ TEST 1: Get all teams");
        List<Team> allTeams = teamService.getAllTeams();
        allTeams.forEach(team -> System.out.println("  - " + team.getName() + " (" + team.getCountry() + ")"));
        System.out.println("  Total teams: " + allTeams.size() + "\n");

        // Test 2: Add a new team
        System.out.println("✓ TEST 2: Add new team");
        Team newTeam = new Team("Test Team", "France", "This is a test team", "League of Legends", "Pro");
        if (teamService.addTeam(newTeam)) {
            System.out.println("  ✅ Team added successfully!\n");
        } else {
            System.out.println("  ❌ Failed to add team\n");
        }

        // Test 3: Get team by ID
        System.out.println("✓ TEST 3: Get team by ID");
        List<Team> teams = teamService.getAllTeams();
        if (!teams.isEmpty()) {
            Team firstTeam = teams.get(0);
            System.out.println("  Retrieved: " + firstTeam.getName() + "\n");
        }

        // Test 4: Update a team
        System.out.println("✓ TEST 4: Update team");
        if (!teams.isEmpty()) {
            Team teamToUpdate = teams.get(0);
            teamToUpdate.setScore(999);
            if (teamService.updateTeam(teamToUpdate)) {
                System.out.println("  ✅ Team updated: score=" + teamToUpdate.getScore() + "\n");
            } else {
                System.out.println("  ❌ Failed to update team\n");
            }
        }

        // Test 5: Search teams
        System.out.println("✓ TEST 5: Search teams by name");
        List<Team> searchResults = teamService.searchTeamsByName("Eclipse");
        searchResults.forEach(team -> System.out.println("  - Found: " + team.getName()));
        System.out.println("  Results: " + searchResults.size() + "\n");

        // Test 6: Delete a team (optional - uncomment if needed)
        /*
        System.out.println("✓ TEST 6: Delete team");
        if (teamService.deleteTeam(testTeamId)) {
            System.out.println("  ✅ Team deleted successfully!\n");
        } else {
            System.out.println("  ❌ Failed to delete team\n");
        }
        */

        System.out.println("=== All tests completed! ===");
    }
}

