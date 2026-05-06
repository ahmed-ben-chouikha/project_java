package edu.connexion3a36.rankup.services;

import edu.connexion3a36.rankup.entities.DemandeRecompense;
import edu.connexion3a36.rankup.entities.Recompense;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardPdfService {

    private static final int MAX_LINES_PER_PAGE = 42;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private String lastErrorMessage = "";

    public boolean exportRecompenses(List<Recompense> recompenses, Map<Integer, String> tournamentNames, File file) {
        List<String> lines = new ArrayList<>();
        lines.add("Export des récompenses");
        lines.add("Généré le: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        lines.add("");
        lines.add("Nombre total: " + (recompenses == null ? 0 : recompenses.size()));
        lines.add("");

        if (recompenses != null) {
            for (Recompense recompense : recompenses) {
                String tournamentName = tournamentNames == null ? "" : tournamentNames.getOrDefault(recompense.getTournamentId(), "Tournoi #" + recompense.getTournamentId());
                lines.add("#" + recompense.getId() + " - " + safe(recompense.getRecompense()) + " | Type: " + safe(recompense.getType()) + " | Classement: " + recompense.getClassement());
                lines.add("Tournoi: " + tournamentName);
                addWrapped(lines, "Description: " + safe(recompense.getDescription()), 96, "  ");
                lines.add("");
            }
        }

        return writeSimplePdf("recompenses", lines, file);
    }

    public boolean exportDemandes(List<DemandeRecompense> demandes, Map<Integer, String> recompenseNames, File file) {
        List<String> lines = new ArrayList<>();
        lines.add("Export des demandes de récompense");
        lines.add("Généré le: " + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        lines.add("");
        lines.add("Nombre total: " + (demandes == null ? 0 : demandes.size()));
        lines.add("");

        if (demandes != null) {
            for (DemandeRecompense demande : demandes) {
                String recompenseName = "Aucune";
                if (demande.getRecompenseId() != null && recompenseNames != null) {
                    recompenseName = recompenseNames.getOrDefault(demande.getRecompenseId(), "Récompense #" + demande.getRecompenseId());
                }
                lines.add("#" + demande.getId() + " - " + safe(demande.getNomDemandeur()) + " | " + safe(demande.getEmail()) + " | Statut: " + safe(demande.getStatut()));
                lines.add("Récompense: " + recompenseName);
                lines.add("Date: " + (demande.getDateDemande() == null ? "-" : demande.getDateDemande().format(DATE_TIME_FORMATTER)));
                addWrapped(lines, "Motif: " + safe(demande.getMotif()), 96, "  ");
                lines.add("");
            }
        }

        return writeSimplePdf("demandes_recompense", lines, file);
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private boolean writeSimplePdf(String title, List<String> rawLines, File file) {
        lastErrorMessage = "";
        if (file == null) {
            lastErrorMessage = "Aucun fichier de destination n'a été sélectionné.";
            return false;
        }

        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            List<List<String>> pages = paginate(rawLines, MAX_LINES_PER_PAGE);
            byte[] pdfBytes = buildPdfBytes(title, pages);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
            }
            return true;
        } catch (IOException e) {
            lastErrorMessage = e.getMessage();
            return false;
        }
    }

    private byte[] buildPdfBytes(String title, List<List<String>> pages) throws IOException {
        List<String> objects = new ArrayList<>();
        objects.add(null); // index 0 unused
        objects.add("<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder("[");
        int contentStartId = 4;
        int pageStartId = 5;
        for (int i = 0; i < pages.size(); i++) {
            if (i > 0) {
                kids.append(' ');
            }
            kids.append(pageStartId + (i * 2)).append(" 0 R");
        }
        kids.append(']');
        objects.add("<< /Type /Pages /Kids " + kids + " /Count " + pages.size() + " >>");
        objects.add("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");

        for (int i = 0; i < pages.size(); i++) {
            String content = buildPageContent(title, pages.get(i), i + 1, pages.size());
            byte[] contentBytes = content.getBytes(StandardCharsets.ISO_8859_1);
            objects.add("<< /Length " + contentBytes.length + " >>\nstream\n" + content + "\nendstream");
            int contentId = contentStartId + (i * 2);
            int pageId = pageStartId + (i * 2);
            objects.add("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 3 0 R >> >> /Contents " + contentId + " 0 R >>");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("%PDF-1.4\n%âãÏÓ\n".getBytes(StandardCharsets.ISO_8859_1));

        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);

        for (int i = 1; i < objects.size(); i++) {
            offsets.add(out.size());
            out.write((i + " 0 obj\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write(objects.get(i).getBytes(StandardCharsets.ISO_8859_1));
            out.write("\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
        }

        int xrefStart = out.size();
        out.write(("xref\n0 " + objects.size() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        out.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        for (int i = 1; i < offsets.size(); i++) {
            out.write(String.format("%010d 00000 n \n", offsets.get(i)).getBytes(StandardCharsets.ISO_8859_1));
        }
        out.write(("trailer\n<< /Size " + objects.size() + " /Root 1 0 R >>\nstartxref\n" + xrefStart + "\n%%EOF").getBytes(StandardCharsets.ISO_8859_1));
        return out.toByteArray();
    }

    private String buildPageContent(String title, List<String> lines, int pageNumber, int totalPages) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n");
        sb.append("/F1 14 Tf\n");
        sb.append("50 800 Td\n");
        sb.append("14 TL\n");
        sb.append("(").append(escapePdfText(sanitizePdfText(title + " - Page " + pageNumber + "/" + totalPages))).append(") Tj\n");
        sb.append("/F1 10 Tf\n");
        sb.append("T*\n");
        sb.append("(").append(escapePdfText(sanitizePdfText("------------------------------------------------------------"))).append(") Tj\n");
        sb.append("T*\n");
        for (String line : lines) {
            sb.append("(").append(escapePdfText(sanitizePdfText(line))).append(") Tj\n");
            sb.append("T*\n");
        }
        sb.append("ET");
        return sb.toString();
    }

    private List<List<String>> paginate(List<String> lines, int linesPerPage) {
        List<List<String>> pages = new ArrayList<>();
        if (lines == null || lines.isEmpty()) {
            pages.add(List.of("Aucune donnée à exporter."));
            return pages;
        }
        for (int i = 0; i < lines.size(); i += linesPerPage) {
            pages.add(new ArrayList<>(lines.subList(i, Math.min(lines.size(), i + linesPerPage))));
        }
        return pages;
    }

    private void addWrapped(List<String> target, String text, int maxWidth, String indent) {
        String current = safe(text);
        if (current.isEmpty()) {
            target.add(indent + "-");
            return;
        }
        StringBuilder line = new StringBuilder();
        for (String word : current.split("\\s+")) {
            if (line.length() == 0) {
                line.append(word);
                continue;
            }
            if (line.length() + 1 + word.length() > maxWidth) {
                target.add(indent + line);
                line.setLength(0);
                line.append(word);
            } else {
                line.append(' ').append(word);
            }
        }
        if (line.length() > 0) {
            target.add(indent + line);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private String sanitizePdfText(String text) {
        String sanitized = safe(text)
                .replace('’', '\'')
                .replace('“', '"')
                .replace('”', '"')
                .replace('–', '-')
                .replace('—', '-')
                .replace("€", "EUR")
                .replace("œ", "oe")
                .replace("Œ", "OE");
        return sanitized;
    }

    private String escapePdfText(String text) {
        return text.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}

