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
            System.out.println("encode QRCode error:" + e.getMessage());
            return "failed";
        } catch (final IOException e1) {
        	e1.printStackTrace();
            System.out.println("write img error:" + e1.getMessage());
            return "failed";
        }

        return b64QRCode;
    }
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		QRCodeUtil qrObj = new QRCodeUtil();
//		System.out.println(qrObj.genQRCodeb64("T003FV01gk/GFiKitZpAO+04nn/D+Lg4J9tHbT29/40/0KajaH/pzAS3vCau5qTyOGkjsoEPO4EjjcmjwAmKLMPynNzNID2J8hTvvm17MSeBK+O0QyG7VfW9+NwKtUTccY54DOlNe/Ipt0vfjFFqi6MA8nKPbqSPWh268DVShw17b0IEAtf2hjULccYGowEQE8dGtySR", 161, 161));
//		System.out.println(qrObj.genQRCodeb64("mobilemoica://moica.moi.gov.tw/w2a/verifySign?sp_ticket=chrisky", 201, 201));
		System.out.println(qrObj.genQRCodeb64("credential_offer?credential_offer_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c96fd4b0bebd1bd19fb3eda99dd60ef6a8ea6e3b8ef308ea55de", 201, 201));
		System.out.println(qrObj.genQRCodeb64("modadigitalwallet://credential_offer?credential_offer_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c96fd4b0bebd1bd19fb3eda99dd60ef6a8ea6e3b8ef308ea55de", 201, 201));
		System.out.println(qrObj.genQRCodeb64("mobilemoica://credential_offer?credential_offer_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c96fd4b0bebd1bd19fb3eda99dd60ef6a8ea6e3b8ef308ea55de", 201, 201));
		System.out.println(qrObj.genQRCodeb64("modadigitalwallet://credential_offer?credential_offer_uri=http%3A%2F%2Fissuer-oidvci.service.modadw.dev.webe.hinet.services%2Fapi%2Fissuer%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c36cd2b8bcbd15d098b32f53a9993053a38a3841d5fcda2f28d5", 251, 251));
		
		// Gen qr-code & link
		String resp_json_str = "{\r\n"
				+ "    \"link\": \"credential_offer_uri=http%3A%2F%2F127.0.0.1%3A8080%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c96fd4b0bebd1bd19fb3eda99dd60ef6a8ea6e3b8ef308ea55de\"\r\n"
				+ "}";
		JSONObject resp_json = (JSONObject)new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(resp_json_str);
		String credential_offer_uri = resp_json.getAsString("link");
		String link = "mobilemoica://moica.moi.gov.tw/w2a/verifySign?" + credential_offer_uri;
		System.out.println(link);
		String[] token = credential_offer_uri.split("=");
		System.out.println(URLDecoder.decode(token[1], "UTF-8"));
		
		// Get credential_offer_uri from link
		link = null;
		link = "modadigitalwallet://credential_offer?credential_offer_uri=http%3A%2F%2Fissuer-oidvci.service.modadw.dev.webe.hinet.services%2Fapi%2Fissuer%2Fcredential-offer-object%3Fnonce%3D3531dcd0-8e21-44c5-8908-98f3b6fd5e9f%26sub%3D4f1e21bfcef6859a9d8bba75c36cd2b8bcbd15d098b32f53a9993053a38a3841d5fcda2f28d5";
		String[] link_token = link.split("=");
		System.out.println("credential_offer_uri: " + URLDecoder.decode(link_token[1], "UTF-8"));

	}
}
