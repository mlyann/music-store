package la1;

import java.util.List;
import java.util.*;

public class TablePrinter {

    public static void printDynamicTable(String tableTitle, List<List<String>> rows) {
        if (rows == null || rows.isEmpty()) {
            System.out.println("No data to display.");
            return;
        }

        // Print a fancy title
        System.out.println("===================================================");
        System.out.println("           🎉 " + tableTitle + " 🎉              ");
        System.out.println("===================================================");

        // Number of columns
        int colCount = rows.get(0).size();

        // Compute max width of each column
        int[] colWidths = new int[colCount];
        for (List<String> row : rows) {
            for (int c = 0; c < colCount; c++) {
                String cell = (row.get(c) == null) ? "" : row.get(c);
                colWidths[c] = Math.max(colWidths[c], cell.length());
            }
        }

        // Build separator line
        String separator = buildSeparatorLine(colWidths);

        // Print header row (first row), then separator
        System.out.println(separator);
        printRow(rows.get(0), colWidths);
        System.out.println(separator);

        // Print data rows
        for (int r = 1; r < rows.size(); r++) {
            printRow(rows.get(r), colWidths);
        }

        // Bottom line
        System.out.println(separator);
    }

    private static String buildSeparatorLine(int[] colWidths) {
        StringBuilder sb = new StringBuilder();
        for (int width : colWidths) {
            sb.append("+-");
            for (int i = 0; i < width; i++) {
                sb.append("-");
            }
            sb.append("-");
        }
        sb.append("+");
        return sb.toString();
    }

    private static void printRow(List<String> row, int[] colWidths) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colWidths.length; i++) {
            String cell = (row.get(i) == null) ? "" : row.get(i);
            sb.append("| ").append(padRight(cell, colWidths[i])).append(" ");
        }
        sb.append("|");
        System.out.println(sb.toString());
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(" ");
        }
        return sb.toString();
    }
}