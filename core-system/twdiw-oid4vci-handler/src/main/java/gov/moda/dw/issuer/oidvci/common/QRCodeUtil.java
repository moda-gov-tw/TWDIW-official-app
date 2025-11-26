package gov.moda.dw.issuer.oidvci.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;


public class QRCodeUtil {

	public String genQRCodeb64(final String contentString, final int codeWidth, final int codeHeight) {

        String b64QRCode = "";
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 設置zxing qrcode參數
        final HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            final BitMatrix bitMatrix = qrCodeWriter.encode(contentString, BarcodeFormat.QR_CODE, codeWidth, codeHeight, hints);
            final BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ImageIO.write(bufferedImage, "png", outputStream);

            b64QRCode = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

    	    /* generate image */
//    	    File f = new File("C:/Users/chrisky/Desktop/temp/mydata/myQRCode.png");
//    	    String suffix = f.getName().substring(f.getName().indexOf(".") + 1, f.getName().length());
//    	    try {
//    	        ImageIO.write(bufferedImage, suffix, f); // "png"
//    	    } catch (IOException ioe) {
//    	        return "failed";
//    	    }
//    	    /******************/
        } catch (final WriterException e) {
        	e.printStackTrace();
            return "failed";
        } catch (final IOException e1) {
        	e1.printStackTrace();
            return "failed";
        }

        return b64QRCode;
    }
}
