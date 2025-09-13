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
        Map<Integer, List<Double>> teamScores = new HashMap<>();

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
                        double score = cell.getNumericCellValue();
                        teamScores.computeIfAbsent(teamId, k -> new ArrayList<>()).add(score);
                    }
                }
            }
        }

        return teamScores.entrySet().stream()
                .map(e -> {
                    double avg = e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    return new TeamScore(e.getKey(), avg);
                })
                .sorted(Comparator.comparingDouble(TeamScore::getAverage).reversed())
                .collect(Collectors.toList());
    }
}
