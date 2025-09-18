package gr.open.hackathon.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ScoreService {

    public List<TeamScore> calculateScores(MultipartFile file) throws Exception {
        return calculateScores(file, false);
    }

    public List<TeamScore> calculateScores(MultipartFile file, boolean verifyResults) throws Exception {
        Map<Integer, List<Double>> teamScores = new HashMap<>();
        Map<Integer, Map<String, List<Double>>> teamCategoryScores = new HashMap<>();
        Map<Integer, Integer> teamVoteCounts = new HashMap<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            int colCount = header.getLastCellNum();

            Pattern teamPattern = Pattern.compile("ID[#\\s]*(\\d+)");

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                for (int c = 1; c < colCount; c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) continue;

                    String colName = header.getCell(c).getStringCellValue();
                    Matcher matcher = teamPattern.matcher(colName);
                    if (matcher.find()) {
                        int teamId = Integer.parseInt(matcher.group(1));

                        // Extract category from column name (everything before " (ID X)")
                        String category = colName.replaceAll("\\s*\\(ID[#\\s]*\\d+\\)\\s*$", "").trim();

                        double score = cell.getNumericCellValue();
                        teamScores.computeIfAbsent(teamId, k -> new ArrayList<>()).add(score);

                        if (verifyResults) {
                            teamCategoryScores.computeIfAbsent(teamId, k -> new HashMap<>())
                                    .computeIfAbsent(category, k -> new ArrayList<>()).add(score);
                            teamVoteCounts.put(teamId, teamVoteCounts.getOrDefault(teamId, 0) + 1);
                        }
                    }
                }
            }
        }

        if (verifyResults) {
            return teamScores.entrySet().stream()
                    .map(e -> {
                        int teamId = e.getKey();
                        double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);

                        // Calculate category averages
                        Map<String, Double> categoryAverages = new HashMap<>();
                        if (teamCategoryScores.containsKey(teamId)) {
                            teamCategoryScores.get(teamId).forEach((category, scores) -> {
                                double categoryAvg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                                categoryAverages.put(category, categoryAvg);
                            });
                        }

                        int totalVotes = teamVoteCounts.getOrDefault(teamId, 0) / 5; // Divide by 5 categories per team per judge

                        return new TeamScore(teamId, avg, categoryAverages, totalVotes);
                    })
                    .sorted(Comparator.comparingDouble(TeamScore::getAverage).reversed())
                    .collect(Collectors.toList());
        } else {
            return teamScores.entrySet().stream()
                    .map(e -> {
                        double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                        return new TeamScore(e.getKey(), avg);
                    })
                    .sorted(Comparator.comparingDouble(TeamScore::getAverage).reversed())
                    .collect(Collectors.toList());
        }
    }
}
