package gov.moda.dw.manager.service.custom.common;

// 引入 ZXing 相關的類別，用於產生 QR Code
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 引入 Java 標準庫相關類別
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code 產生服務
 * 此服務類別負責將文字內容轉換為 QR Code 圖片，並以 Base64 格式回傳
 * 支援 non-destructive QR Code with logo space
 */
@Slf4j
@Service
public class QRCodeService {

    // QR Code 預設設定
    private static final int DEFAULT_SIZE = 400;
    private static final int DEFAULT_LOGO_SIZE = 60;
    private static final int DEFAULT_LOGO_MARGIN = 5;
    private static final int DEFAULT_LOGO_CORNER_RADIUS = 6;
    private static final int DEFAULT_MARGIN = 10;

    /**
     * 將文字內容轉換為 QR Code 圖片，並回傳 Base64 編碼的字串
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeBase64(String text) throws Exception {
        return generateQRCodeBase64(text, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * 將文字內容轉換為指定尺寸的 QR Code 圖片
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @param width QR Code 寬度
     * @param height QR Code 高度
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeBase64(String text, int width, int height) throws Exception {
        // 設定 QR Code 的編碼提示，使用 UTF-8 字元集
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

        // 使用 ZXing 產生 QR Code 的點陣圖
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // 建立位元組輸出串流，用於儲存 QR Code 圖片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 將點陣圖轉換為 PNG 格式的圖片，並寫入輸出串流
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

        // 將圖片資料轉換為 Base64 字串並回傳
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 產生帶有 logo 空間的 non-destructive QR Code
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @param logoImage Logo 圖片（可為 null）
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeWithLogoSpace(String text, BufferedImage logoImage) throws Exception {
        return generateQRCodeWithLogoSpace(text, logoImage, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_LOGO_SIZE, DEFAULT_MARGIN);
    }

    /**
     * 產生帶有 logo 空間的 non-destructive QR Code（自訂尺寸）
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @param logoImage Logo 圖片（可為 null）
     * @param qrWidth QR Code 寬度
     * @param qrHeight QR Code 高度
     * @param logoSize Logo 尺寸
     * @param qrMargin QR Code 邊框大小
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeWithLogoSpace(String text, BufferedImage logoImage, 
            int qrWidth, int qrHeight, int logoSize, int qrMargin) throws Exception {

        // 設定 QR Code 的編碼提示，使用 UTF-8 字元集
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

        // 使用 ZXing 產生 QR Code 的點陣圖
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);

        // 如果有 logo，在 BitMatrix 層面預留 logo 空間
        if (logoImage != null) {
            bitMatrix = createLogoSpaceInBitMatrix(bitMatrix, logoSize);
        }

        // 轉換 BitMatrix 為 BufferedImage
        BufferedImage qrImage = matrixToImage(bitMatrix, qrWidth, qrMargin);

        // 如果有 logo，則在預留的空間中放置 logo
        if (logoImage != null) {
            qrImage = addLogoToQRCodeNonDestructive(qrImage, logoImage, logoSize);
        }

        // 轉換為 Base64
        return imageToBase64(qrImage, "PNG");
    }

    /**
     * 產生帶有 logo 空間的 non-destructive QR Code（使用預設 logo）
     * 
     * @param text 要轉換成 QR Code 的文字內容
     * @return 回傳 QR Code 圖片的 Base64 編碼字串（PNG 格式）
     * @throws Exception 當 QR Code 產生過程發生錯誤時拋出例外
     */
    public String generateQRCodeWithLogoSpace(String text) throws Exception {
        return generateQRCodeWithLogoSpace(text, null);
    }

    /**
     * 將 BitMatrix 轉換為 BufferedImage
     */
    private BufferedImage matrixToImage(BitMatrix bitMatrix, int targetSize, int marginPx) {
        // 找出 QR code 實際黑點範圍
        int[] rect = bitMatrix.getEnclosingRectangle();
        int qrWidth = rect[2];
        int qrHeight = rect[3];

        // 建立一個只包含 QR 黑點的子矩陣
        BufferedImage qrOnly = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < qrWidth; x++) {
            for (int y = 0; y < qrHeight; y++) {
                boolean isBlack = bitMatrix.get(x + rect[0], y + rect[1]);
                qrOnly.setRGB(x, y, isBlack ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }

        // 再額外加上固定的 marginPx
        int finalSize = targetSize; // 你想要的輸出大小 (e.g. 300x300)
        BufferedImage output = new BufferedImage(finalSize, finalSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, finalSize, finalSize);

        // 計算 QRCode 實際繪製大小 (留 margin)
        int drawSize = finalSize - 2 * marginPx;
        g.drawImage(qrOnly, marginPx, marginPx, drawSize, drawSize, null);
        g.dispose();

        return output;
    }

    /**
     * 在 QR Code 中央添加 logo（非破壞性方法）
     * 真正的 non-destructive QR Code：在生成 QR Code 時就預留 logo 空間
     */
    private BufferedImage addLogoToQRCodeNonDestructive(BufferedImage qrImage, BufferedImage logoImage, int logoSize) {
        // 計算 logo 在 QR Code 中的位置（中央）
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int logoX = (qrWidth - logoSize) / 2;
        int logoY = (qrHeight - logoSize) / 2;

        // 創建新的圖片，包含 logo
        BufferedImage combinedImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // 設定高品質渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 繪製 QR Code，但在 logo 區域預留白色空間
        for (int x = 0; x < qrWidth; x++) {
            for (int y = 0; y < qrHeight; y++) {
                // 檢查是否在 logo 區域內
                boolean inLogoArea = (x >= logoX - DEFAULT_LOGO_MARGIN && x < logoX + logoSize + DEFAULT_LOGO_MARGIN
                        && y >= logoY - DEFAULT_LOGO_MARGIN && y < logoY + logoSize + DEFAULT_LOGO_MARGIN);

                if (inLogoArea) {
                    // 在 logo 區域內繪製白色
                    combinedImage.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    // 在 logo 區域外繪製原始 QR Code 內容
                    combinedImage.setRGB(x, y, qrImage.getRGB(x, y));
                }
            }
        }

        // 調整 logo 尺寸並繪製
        BufferedImage resizedLogo = resizeImage(logoImage, logoSize, logoSize);
        g2d.drawImage(resizedLogo, logoX, logoY, null);

        g2d.dispose();

        return combinedImage;
    }

    /**
     * 在 QR Code 中央添加 logo（舊方法，會覆蓋原始資料）
     * @deprecated 使用 addLogoToQRCodeNonDestructive 替代
     */
    @Deprecated
    private BufferedImage addLogoToQRCode(BufferedImage qrImage, BufferedImage logoImage, int logoSize) {
        // 計算 logo 在 QR Code 中的位置（中央）
        int qrWidth = qrImage.getWidth();
        int qrHeight = qrImage.getHeight();
        int logoX = (qrWidth - logoSize) / 2;
        int logoY = (qrHeight - logoSize) / 2;

        // 創建新的圖片，包含 logo
        BufferedImage combinedImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // 設定高品質渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 繪製 QR Code
        g2d.drawImage(qrImage, 0, 0, null);

        // 在 logo 位置繪製白色背景（預留空間）
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(logoX - DEFAULT_LOGO_MARGIN, logoY - DEFAULT_LOGO_MARGIN, logoSize + 2 * DEFAULT_LOGO_MARGIN,
                logoSize + 2 * DEFAULT_LOGO_MARGIN, DEFAULT_LOGO_CORNER_RADIUS, DEFAULT_LOGO_CORNER_RADIUS);

        // 調整 logo 尺寸並繪製
        BufferedImage resizedLogo = resizeImage(logoImage, logoSize, logoSize);
        g2d.drawImage(resizedLogo, logoX, logoY, null);

        g2d.dispose();

        return combinedImage;
    }

    /**
     * 調整圖片尺寸
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();

        // 設定高品質渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * 將 BufferedImage 轉換為 Base64 字串
     */
    private String imageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 從 Base64 字串載入圖片
     */
    public BufferedImage loadImageFromBase64(String base64String) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(imageBytes);
        return ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
    }

    /**
     * 在 BitMatrix 中創建 logo 空間（真正的 non-destructive 方法）
     * 將 QR Code 中央的資料點設為白色（false），預留 logo 空間
     */
    private BitMatrix createLogoSpaceInBitMatrix(BitMatrix bitMatrix, int logoSize) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        // 計算 logo 在 QR Code 中的位置（中央）
        int logoX = (width - logoSize) / 2;
        int logoY = (height - logoSize) / 2;

        // 創建新的 BitMatrix
        BitMatrix newBitMatrix = new BitMatrix(width, height);

        // 複製原始 BitMatrix，但在 logo 區域設為白色
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 檢查是否在 logo 區域內
                boolean inLogoArea = (x >= logoX - DEFAULT_LOGO_MARGIN && 
                                    x < logoX + logoSize + DEFAULT_LOGO_MARGIN &&
                                    y >= logoY - DEFAULT_LOGO_MARGIN && 
                                    y < logoY + logoSize + DEFAULT_LOGO_MARGIN);

                if (inLogoArea) {
                    // 在 logo 區域內設為白色（false）- 不設置任何值，預設為 false
                    // newBitMatrix 預設為 false，所以不需要額外設置
                } else {
                    // 在 logo 區域外保持原始值
                    if (bitMatrix.get(x, y)) {
                        newBitMatrix.set(x, y);
                    }
                }
            }
        }

        return newBitMatrix;
    }
} 