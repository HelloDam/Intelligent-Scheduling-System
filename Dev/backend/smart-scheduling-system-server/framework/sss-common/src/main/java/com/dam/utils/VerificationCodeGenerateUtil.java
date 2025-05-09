package com.dam.utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 随机验证码生成
 */
public class VerificationCodeGenerateUtil {

    private int width;
    private int height;
    private String code;
    private BufferedImage image;

    private static final Random random = new Random();
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 4;

    public VerificationCodeGenerateUtil(int width, int height) {
        this.width = width;
        this.height = height;
        this.code = generateCode();
        this.image = generateImage();
    }

    public String getCode() {
        return code;
    }

    public BufferedImage getImage() {
        return image;
    }

    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private BufferedImage generateImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(210, 223, 237));
        g.fillRect(0, 0, width, height);
        switch (random.nextInt(5)){
            case 0:
                g.setFont(new Font("Times New Roman", Font.BOLD, 30));
                break;
            case 1:
                g.setFont(new Font("Arial", Font.BOLD, 30));
                break;
            case 2:
                g.setFont(new Font("Comic Sans MS", Font.BOLD + Font.ITALIC, 30));
                break;
            case 3:
                g.setFont(new Font("Impact", Font.PLAIN, 30));
                break;
            case 4:
                g.setFont(new Font("Courier New", Font.BOLD + Font.ITALIC, 30));
                break;
        }

        int fontSize = g.getFont().getSize();
        for (int i = 0; i < code.length(); i++) {
            int x = width / (CODE_LENGTH + 1) * (i + 1);
            int y = height / 2 + fontSize / 2 + random.nextInt(height / 10) * (random.nextInt(3) - 1);
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }

        // 添加干扰线
        g.setColor(Color.GRAY);
        Graphics2D g2d = (Graphics2D) g;
        // 设置线条粗细为1
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 10; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 添加噪点
        float noiseDensity = 0.005f;
        int numNoisePixels = (int) (noiseDensity * width * height);
        for (int i = 0; i < numNoisePixels; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = random.nextInt(0xFFFFFF);
            image.setRGB(x, y, rgb);
        }

        // 字符串扭曲
   /*     double distortionFactor = 0.2; // 扭曲因子
        int wobbleDistance = 5; // 扭曲距离
        BufferedImage distortedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D distortedGraphics = (Graphics2D) distortedImage.getGraphics();
        AffineTransform transform = new AffineTransform();
        transform.setToIdentity();

        for (int i = 0; i < code.length(); i++) {
            int x = width / (CODE_LENGTH + 1) * (i + 1);
            int y = height / 2 + fontSize / 2;
            transform.translate(random.nextInt(wobbleDistance * 2) - wobbleDistance,
                    random.nextInt(wobbleDistance * 2) - wobbleDistance);
            transform.rotate((random.nextDouble() - 0.5) * distortionFactor, x, y);
            distortedGraphics.setTransform(transform);
            distortedGraphics.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            distortedGraphics.drawString(String.valueOf(code.charAt(i)), x, y);
        }
        distortedGraphics.dispose();
        //合并原图和扭曲后的图
        Graphics2D finalGraphics = (Graphics2D) image.getGraphics();
        finalGraphics.drawImage(distortedImage, 0, 0, null);
        finalGraphics.dispose();*/

        g.dispose();
        return image;
    }

    /**
     * 转化格式
     *
     * @param image
     * @param type
     * @return
     * @throws IOException
     */
    public String convertToBase64(BufferedImage image, String type) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, type, baos);
        byte[] bytes = baos.toByteArray();
        String base64String = Base64.getEncoder().encodeToString(bytes);
        return base64String;
    }

}
