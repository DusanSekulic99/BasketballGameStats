package rs.raf.edu.rs.basketballgamestats.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import rs.raf.edu.rs.basketballgamestats.model.Game;
import rs.raf.edu.rs.basketballgamestats.model.Player_Game;
import rs.raf.edu.rs.basketballgamestats.model.Team;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportUtil {

    private final static List<String> columnNames = List.of("Number", "Position", "Name", "Minutes played", "Points", "Assists", "Rebounds", "Fouls");

    public static ByteArrayOutputStream generatePdfReport(Map<Team, List<Player_Game>> teamPlayerMap, Game game, String homePts, String awayPts) throws DocumentException {
        Document document = new Document(PageSize.LETTER, 0.75F, 0.75F, 0.75F, 0.75F);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, bytes);
        document.open();

        Paragraph title = new Paragraph("Match Report");
        title.setSpacingAfter(10);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph matchInfo = new Paragraph(game.getHomeTeam().getName() + " " + homePts + ":" + awayPts + " " + game.getAwayTeam().getName());
        matchInfo.setSpacingAfter(10);
        matchInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(matchInfo);

        teamPlayerMap.keySet().stream().sorted((team1, team2) -> team1.getId().equals(game.getHomeTeam().getId()) ? -1 : 1).forEach(team -> {
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(95);

            Paragraph teamName = new Paragraph(team.getName());
            teamName.setSpacingAfter(20);

            columnNames.forEach(cellName -> {
                table.addCell(new Phrase(cellName));
            });
            teamName.setAlignment(Element.ALIGN_CENTER);
            teamPlayerMap.get(team).forEach(playerGame -> {
                table.addCell(new PdfPCell(new Phrase(playerGame.getPlayer().getJerseyNo())));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(playerGame.getPlayer().getPosition()))));
                table.addCell(new PdfPCell(new Phrase(playerGame.getPlayer().getFirstName() + " " + playerGame.getPlayer().getLastName())));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(Math.round(playerGame.getSecondsPlayed() / 60.0)))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(playerGame.getPoints()))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(playerGame.getAssists()))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(playerGame.getRebounds()))));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(playerGame.getFouls()))));
            });
            table.setSpacingAfter(60);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            try {
                Image badge = Image.getInstance(team.getBadge());
                badge.scaleAbsoluteWidth(84);
                badge.scaleAbsoluteHeight(84);
                badge.setAlignment(Element.ALIGN_CENTER);
                document.add(badge);
                document.add(teamName);
                document.add(table);
                document.newPage();
            } catch (DocumentException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        document.close();
        return bytes;
    }
}
