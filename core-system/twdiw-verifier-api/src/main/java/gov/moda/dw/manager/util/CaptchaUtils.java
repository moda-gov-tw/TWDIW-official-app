package gov.moda.dw.manager.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
// import javax.servlet.ServletOutputStream;
// import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.MediaType;

@Slf4j
public class CaptchaUtils {

    public static String verifyCode() {
        return new Builder().verifyCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(HttpServletResponse response) {
        return new Builder(response);
    }

    /**
     * 圖形驗證碼 產生器
     */
    public static class Builder {

        private HttpServletResponse response;
        private int width;
        private int height;
        private String code;
        private Boolean randomSizeCode;
        private Boolean colorCode;
        private double density;
        private Boolean colorNoise;
        private Color backGroundColor;

        public Builder() {
            init();
        }

        public Builder(HttpServletResponse response) {
            init();
            this.response = response;
        }

        private void init() {
            this.width = 256;
            this.height = 64;
            this.code = this.verifyCode();
            this.colorCode = false;
            this.randomSizeCode = false;
            this.density = 1.0;
            this.colorNoise = false;
            this.backGroundColor = new Color(this.randomNum(200, 240), this.randomNum(200, 240), this.randomNum(200, 240));
        }

        private Integer randomNum(int begin, int end) {
            SecureRandom random = new SecureRandom();
            random.setSeed(new Date().getTime());
            return (int) Math.floor(random.nextFloat() * (end - begin) + begin);
        }

        public String verifyCode() {
            String baseCode = "ABCEFGHJKLMNPQRTUWY123456789123456789";
            StringBuilder result = new StringBuilder();
            while (result.length() < 4) {
                int randomNum = randomNum(0, baseCode.length());
                result.append(baseCode.substring(randomNum, randomNum + 1));
            }
            return result.toString();
        }

        private Color randomColorCode() {
            return new Color(this.randomNum(50, 160), this.randomNum(50, 160), this.randomNum(50, 160));
        }

        private Font randomFontSize() {
            int fontSizeRange = this.randomNum((this.height / 10) * 9, (this.height / 10) * 6);
            return new Font(this.getFontByOs(), Font.BOLD, fontSizeRange);
        }

        private Color randomColorNoise() {
            return new Color(this.randomNum(0, 255), this.randomNum(0, 255), this.randomNum(0, 255));
        }

        private String getFontByOs() {
            final String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("win") >= 0) {
                return "Consolas";
            }
            return "Times New Roman";
        }

        private String toFullwidth(String halfwidth) {
            String fullwidth = "";
            char[] chars = halfwidth.toCharArray();
            int tranTemp = 0;
            for (int i = 0; i < chars.length; i++) {
                tranTemp = (int) chars[i];
                if (tranTemp != 45) tranTemp += 65248; //ASCII碼:45 是減號 - //此數字是 Unicode編碼轉為十進位 和 ASCII碼的 差
                fullwidth += (char) tranTemp;
            }

            return fullwidth;
        }

        /**
         * 設定 圖形寬高 預設 128*32
         * @param width
         * @param height
         * @return
         */
        public Builder captchaSize(int width, int height) {
            this.width = Optional.ofNullable(width).orElse(256);
            this.height = Optional.ofNullable(height).orElse(64);
            return this;
        }

        /**
         * 設定 顯示字串
         * @param code
         * @return
         */
        public Builder code(String code) {
            this.code = Optional.ofNullable(code).orElse("");
            return this;
        }

        /**
         * 設定 彩色字串
         */
        public Builder colorCode() {
            this.colorCode = true;
            return this;
        }

        /**
         * 設定 隨機大小字串
         */
        public Builder randomSizeCode() {
            this.randomSizeCode = true;
            return this;
        }

        /**
         * 設定 噪點密度 預設 1.0
         * @param density
         * @return
         */
        public Builder noiseDensity(double density) {
            this.density = Optional.ofNullable(density).orElse(1.0);
            return this;
        }

        /**
         * 設定 彩色噪點
         */
        public Builder colorNoise() {
            this.colorNoise = true;
            return this;
        }

        /**
         * 設定 設背影顏色 (rgb:0~255)
         * @param r
         * @param g
         * @param b
         * @return
         */
        public Builder backGroundColor(int r, int g, int b) {
            if (r < 0 || g < 0 || b < 0 || r > 255 || g > 255 || b > 255) {
                return this;
            }

            this.backGroundColor = new Color(r, g, b);
            return this;
        }

        /**
         * 設定 Servlet Response
         * @param response
         * @return
         */
        public Builder setResponse(HttpServletResponse response) {
            this.response = response;
            return this;
        }

        public String build() {
            return this.impl();
        }

        private String impl() {
            this.response.setHeader("Cache-Control", "no-store");
            this.response.setHeader("Pragma", "no-cache");
            this.response.setDateHeader("Expires", 0);
            this.response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            this.response.setStatus(HttpServletResponse.SC_OK);
            // HttpSession session = request.getSession();

            // 在記憶體中建立圖象
            BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

            // 獲取圖形上下文
            Graphics g = image.getGraphics();
            // 設定背景色
            g.setColor(this.backGroundColor);
            g.fillRect(0, 0, this.width, this.height);

            String rand = this.code;
            // String fullwidth = this.toFullwidth(rand);
            String fullwidth = rand;
            // 將認證碼存入SESSION
            // session.setAttribute("rand", rand);
            g.setFont(new Font(this.getFontByOs(), Font.BOLD, (this.height / 10) * 8));
            // 將認證碼顯示到圖象中
            g.setColor(new Color(this.randomNum(50, 160)));
            int xAxis = this.width / (rand.length() + 1);
            for (int i = 0; i < rand.length(); i++) {
                if (this.randomSizeCode) g.setFont(randomFontSize());
                if (this.colorCode) g.setColor(randomColorCode());
                String Str = fullwidth.substring(i, i + 1);
                int x = this.randomNum((xAxis / 10) * 2, (xAxis / 10) * 6) + (i * xAxis);
                int y = this.randomNum((this.height / 10) * 7, (this.height / 10) * 9);
                g.drawString(Str, x, y);
            }

            // 隨機產生  干擾點,使圖象中的認證碼不易被其它程式探測到
            // Random random = new Random();
            SecureRandom random = new SecureRandom();
            random.setSeed(new Date().getTime());
            for (int i = 0; i < ((this.width * this.height) / 32) * this.density; i++) {
                if (this.colorNoise) g.setColor(randomColorNoise());
                else g.setColor(new Color(this.randomNum(30, 180)));

                int x = random.nextInt(this.width);
                int y = random.nextInt(this.height);
                g.drawOval(x, y, random.nextInt(4), random.nextInt(3));
            }

            // 圖象生效
            g.dispose();
            try (ServletOutputStream responseOutputStream = this.response.getOutputStream();) {
                // 輸出圖象到頁面
                ImageIO.write(image, "jpeg", responseOutputStream);
                // 以下關閉輸入流!
                responseOutputStream.flush();
            } catch (Exception e) {
                log.error("captcha圖形 輸出時出現錯誤: {}\n{}", e.getMessage(), ExceptionUtils.getStackTrace(e));
                return null;
            } finally {
                image = null;
            }

            return rand;
        }
    }
}
