package gov.moda.dw.verifier.vc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtils.class);

    /**
     * using gzip to compress byte array, then encoding to base64
     *
     * @param input byte array
     * @return encoded result
     */
    public static String gzipCompressThenBase64(byte[] input) {

        String output = null;

        if (input != null && input.length > 0) {

            // try-catch-resource
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GZIPOutputStream gzos = new GZIPOutputStream(baos)) {

                // gzip compress
                gzos.write(input);
                gzos.flush();
                gzos.close();

                byte[] bytes = baos.toByteArray();

                // base64 encode
                if (bytes.length > 0) {
                    output = Base64.getEncoder().encodeToString(bytes);
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }

        }

        return output;
    }

    /**
     * base64 decode firstly, then using gzip to uncompress
     * @param input encoded data
     * @return uncompress byte array
     */
    public static byte[] gzipUncompress(String input) {

        byte[] output = null;

        if (input != null && !input.trim().isEmpty()) {

            // base64 decode
            byte[] compressed = Base64.getDecoder().decode(input);

            // check input is compressed or not
            boolean isCompressed =
                (compressed[0] == (byte)(GZIPInputStream.GZIP_MAGIC)) &&
                    (compressed[1] == (byte)(GZIPInputStream.GZIP_MAGIC >> 8));

            if (isCompressed) {

                // try-catch-resource
                try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                     GZIPInputStream gzis = new GZIPInputStream(bais);
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    // gzip uncompress
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = gzis.read(buffer)) > 0) {
                        baos.write(buffer, 0, count);
                    }
                    output = baos.toByteArray();

                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                output = compressed;
            }
        }

        return output;
    }
}
