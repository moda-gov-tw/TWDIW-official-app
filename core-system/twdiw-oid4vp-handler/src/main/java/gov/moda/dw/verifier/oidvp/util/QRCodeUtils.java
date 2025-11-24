package gov.moda.dw.verifier.oidvp.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QRCodeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeUtils.class);
    private static final Map<EncodeHintType, Object> hints = new HashMap<>();
    public static final int DEFAULT_WIDTH_OR_HEIGHT = 400;
    public static final String IMAGE_FORMAT = "PNG";

    static {
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    }

    /**
     * generate QRCode for input text using default size(400x400)
     *
     * @param text input text for QRCode
     * @return the Base64-URLEncoded QRCode
     */
    public static String getQRCodeBase64(String text) {
        return getQRCodeBase64(text, DEFAULT_WIDTH_OR_HEIGHT, DEFAULT_WIDTH_OR_HEIGHT);
    }

    /**
     * generate QRCode for input text
     *
     * @param text   input text for QRCode
     * @param width  QRCode width
     * @param height QRCode height
     * @return the Base64-URLEncoded QRCode
     */
    public static String getQRCodeBase64(String text, int width, int height) {
        byte[] qrCodeImage = createQRCodeImage(text, width, height);
        if (qrCodeImage == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(qrCodeImage);
    }

    public static byte[] createQRCodeImage(String text, int width, int height) {
        try {
            BitMatrix qrcodeBitMatrix = createQrcodeBitMatrix(text, width, height);
            return bitMatrixToByteArray(qrcodeBitMatrix);
        } catch (WriterException | IOException ex) {
            LOGGER.error("createQRCodeImage error", ex);
            return null;
        }
    }

    private static BitMatrix createQrcodeBitMatrix(
        String text, int width, int height) throws WriterException
    {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        return bitMatrix;
    }

    private static byte[] bitMatrixToByteArray(BitMatrix bitMatrix) throws IOException {
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}
