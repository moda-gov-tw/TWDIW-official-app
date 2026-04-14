package gov.moda.dw.manager.util;

import com.grapecity.documents.excel.*;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author AlexChang
 * @create 2024/01/23
 * @description
 */
@Slf4j
public class ExcelUtils {

    private static final IWorksheet sheet = new Workbook().getWorksheets().add();

    /**
     * 用絕對路徑來打開Excel
     *
     * @param path
     * @return
     */
    public static Optional<Workbook> getWorkBookFromPath(String path, ImportFlags... flags) {
        try {
            XlsxOpenOptions xlsxOpenOptions = new XlsxOpenOptions();
            if (flags.length > 0) {
                xlsxOpenOptions.setImportFlags(EnumSet.of(flags[0], flags));
            }
            Workbook workbook = new Workbook();
            workbook.open(path);

            return Optional.of(workbook);
        } catch (Exception e) {
            log.error("輸入路徑： {} 無法開啟Excel，原因： {}", path, ExceptionUtils.getRootCauseMessage(e));

            return Optional.empty();
        }
    }

    /**
     * 去計算在GcExcel的數字第幾欄，例如A＝0，B＝1
     *
     * @param columnId col
     * @return col
     */
    public static int getCol(String columnId) {
        String c = columnId.toUpperCase();
        int result = 0;
        for (int i = 0; i < c.length(); i++) {
            result = result * 26 + (c.charAt(i) - 'A' + 1);
        }
        return result - 1;
    }

    public static boolean isInRange(String target, String source) {
        IRange outer = getRangeFromString(source);
        IRange inner = getRangeFromString(target);

        int outerStartRow = outer.getRow();
        int outerEndRow = outer.getLastRow();
        int outerStartColumn = outer.getColumn();
        int outerEndColumn = outer.getLastColumn();

        int innerStartRow = inner.getRow();
        int innerEndRow = inner.getLastRow();
        int innerStartColumn = inner.getColumn();
        int innerEndColumn = inner.getLastColumn();

        return (
            innerStartRow >= outerStartRow &&
            innerEndRow <= outerEndRow &&
            innerStartColumn >= outerStartColumn &&
            innerEndColumn <= outerEndColumn
        );
    }

    private static IRange getRangeFromString(String rangeString) {
        return sheet.getRange(rangeString);
    }

    public static Comparator<String> getExcelColumnComparator() {
        return new ExcelColumnComparator();
    }

    public static String getColumnId(int col) {
        int columnNumber = col + 1;
        if (columnNumber <= 0) {
            throw new IllegalArgumentException("Column number must be greater than 0");
        }

        StringBuilder columnName = new StringBuilder();

        while (columnNumber > 0) {
            int remainder = (columnNumber - 1) % 26;
            char character = (char) ('A' + remainder);
            columnName.insert(0, character);
            columnNumber = (columnNumber - 1) / 26;
        }

        return columnName.toString();
    }

    private static class ExcelColumnComparator implements Comparator<String> {

        @Override
        public int compare(String s1, String s2) {
            String a = Optional.ofNullable(s1).orElse("");
            String b = Optional.ofNullable(s2).orElse("");
            int result = compareNumericPart(a, b);

            return result != 0 ? result : a.compareTo(b);
        }

        private int compareNumericPart(String s1, String s2) {
            String numericPart1 = getNumericPart(s1);
            String numericPart2 = getNumericPart(s2);
            if (numericPart1.isEmpty() || numericPart2.isEmpty()) return 0;

            long x = Long.parseLong(numericPart1);
            long y = Long.parseLong(numericPart2);

            return Long.compare(x, y);
        }

        private String getNumericPart(String s) {
            return s.replaceAll("\\D", "");
        }
    }
}
