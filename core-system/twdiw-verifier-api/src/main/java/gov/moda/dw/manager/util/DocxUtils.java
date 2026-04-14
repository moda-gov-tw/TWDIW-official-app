package gov.moda.dw.manager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.model.structure.PageSizePaper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * @author AlexChang
 * @create 2024/08/14
 * @description
 */
@SuppressWarnings("SpellCheckingInspection")
public class DocxUtils {

    public static WordprocessingMLPackage open(String path) throws Docx4JException {
        return WordprocessingMLPackage.load(new File(path));
    }

    public static void save(WordprocessingMLPackage wordprocessingMLPackage, String path) throws Docx4JException {
        wordprocessingMLPackage.save(new File(path));
    }

    public static WordprocessingMLPackage createWordprocessingMLPackage(String papersize, boolean landscape) throws InvalidFormatException {
        return WordprocessingMLPackage.createPackage(PageSizePaper.valueOf(papersize), landscape);
    }

    public static void toPDF(WordprocessingMLPackage wordprocessingMLPackage, String path) throws IOException, Docx4JException {
        Path output = Path.of(path);
        Path parent = output.getParent();
        if (Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        try(FileOutputStream outputStream = new FileOutputStream(path)) {
            Docx4J.toFO(new FOSettings(wordprocessingMLPackage), outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        if (wordprocessingMLPackage.getMainDocumentPart().getFontTablePart() != null) {
            wordprocessingMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
        }
    }
}
