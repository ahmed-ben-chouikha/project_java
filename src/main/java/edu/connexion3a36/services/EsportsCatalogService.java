package edu.connexion3a36.services;

import java.util.ArrayList;
import java.util.List;

public class EsportsCatalogService {

    public static class StatCard {
        public String value, label, detail;
        public StatCard(String value, String label, String detail) {
            this.value = value;
            this.label = label;
            this.detail = detail;
        }
    }

    public static class MatchCard {
        public String game, teams, stage, status, time;
        public MatchCard(String game, String teams, String stage, String status, String time) {
            this.game = game;
            this.teams = teams;
            this.stage = stage;
            this.status = status;
            this.time = time;
        }
    }

    public static class TournamentCard {
        public String name, game, date, prizePool, slots;
        public boolean openForJoin;
        public TournamentCard(String name, String game, String date, String prizePool, String slots, boolean openForJoin) {
            this.name = name;
            this.game = game;
            this.date = date;
            this.prizePool = prizePool;
            this.slots = slots;
            this.openForJoin = openForJoin;
        }
    }

    public static class TeamCard {
        public String name, roster, region, record;
        public TeamCard(String name, String roster, String region, String record) {
            this.name = name;
            this.roster = roster;
            this.region = region;
            this.record = record;
        }
    }

    public List<StatCard> getOverviewStats() {
        List<StatCard> result = new ArrayList<>();
        result.add(new StatCard("12", "Live matches", "Streams and brackets in motion"));
        result.add(new StatCard("8", "Open slots", "Teams can still lock in their place"));
        result.add(new StatCard("24", "Featured teams", "Regional and academy squads"));
        result.add(new StatCard("5", "Admin actions", "Approvals, scheduling, and moderation"));
        return result;
    }

    public List<MatchCard> getFeaturedMatches() {
        List<MatchCard> result = new ArrayList<>();
        result.add(new MatchCard("Valorant", "Nova Crew vs Eclipse", "Quarterfinal", "LIVE", "19:30 GMT"));
        result.add(new MatchCard("League of Legends", "Titan Forge vs Pixel Pulse", "Semifinal", "UP NEXT", "20:15 GMT"));
        result.add(new MatchCard("CS2", "Shadow Unit vs Apex Drift", "Showmatch", "OPEN", "22:00 GMT"));
        return result;
    }

    public List<TournamentCard> getUpcomingTournaments() {
        List<TournamentCard> result = new ArrayList<>();
        result.add(new TournamentCard("Summer Clash", "Valorant", "12 May 2026", "$15,000 prize pool", "8/16 teams", true));
        result.add(new TournamentCard("Midnight Circuit", "CS2", "18 May 2026", "$10,000 prize pool", "12/16 teams", true));
        result.add(new TournamentCard("Neon Championship", "Rocket League", "25 May 2026", "$20,000 prize pool", "16/16 teams", false));
        return result;
    }

    public List<TeamCard> getFeaturedTeams() {
        List<TeamCard> result = new ArrayList<>();
        result.add(new TeamCard("Eclipse", "Rex • Nova • Byte • Kaze • Lynx", "EU West", "14W / 3L"));
        result.add(new TeamCard("Apex Drift", "Milo • Vex • Raze • Sol • Flux", "NA Central", "11W / 5L"));
        result.add(new TeamCard("Shadow Unit", "Kai • Ember • Drift • Zen • Orion", "APAC", "17W / 2L"));
        return result;
    }
}



