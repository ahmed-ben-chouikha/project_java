package edu.connexion3a36.services;

import java.util.List;

public class EsportsCatalogService {

    public record StatCard(String value, String label, String detail) {}

    public record MatchCard(String game, String teams, String stage, String status, String time) {}

    public record TournamentCard(String name, String game, String date, String prizePool, String slots, boolean openForJoin) {}

    public record TeamCard(String name, String roster, String region, String record) {}

    public List<StatCard> getOverviewStats() {
        return List.of(
                new StatCard("12", "Live matches", "Streams and brackets in motion"),
                new StatCard("8", "Open slots", "Teams can still lock in their place"),
                new StatCard("24", "Featured teams", "Regional and academy squads"),
                new StatCard("5", "Admin actions", "Approvals, scheduling, and moderation")
        );
    }

    public List<MatchCard> getFeaturedMatches() {
        return List.of(
                new MatchCard("Valorant", "Nova Crew vs Eclipse", "Quarterfinal", "LIVE", "19:30 GMT"),
                new MatchCard("League of Legends", "Titan Forge vs Pixel Pulse", "Semifinal", "UP NEXT", "20:15 GMT"),
                new MatchCard("CS2", "Shadow Unit vs Apex Drift", "Showmatch", "OPEN", "22:00 GMT")
        );
    }

    public List<TournamentCard> getUpcomingTournaments() {
        return List.of(
                new TournamentCard("Summer Clash", "Valorant", "12 May 2026", "$15,000 prize pool", "8/16 teams", true),
                new TournamentCard("Midnight Circuit", "CS2", "18 May 2026", "$10,000 prize pool", "12/16 teams", true),
                new TournamentCard("Neon Championship", "Rocket League", "25 May 2026", "$20,000 prize pool", "16/16 teams", false)
        );
    }

    public List<TeamCard> getFeaturedTeams() {
        return List.of(
                new TeamCard("Eclipse", "Rex • Nova • Byte • Kaze • Lynx", "EU West", "14W / 3L"),
                new TeamCard("Apex Drift", "Milo • Vex • Raze • Sol • Flux", "NA Central", "11W / 5L"),
                new TeamCard("Shadow Unit", "Kai • Ember • Drift • Zen • Orion", "APAC", "17W / 2L")
        );
    }
}

