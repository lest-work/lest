package com.lest.gateway.config;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.util.Configurable;

/**
 * 自定义简单干扰线背景生成器
 * 生成类似 FastAPI 风格的简洁干扰线背景
 *
 * @author lest
 */
public class SimpleLineNoise extends Configurable implements BackgroundProducer
{
    private static final Random RANDOM = new Random();

    @Override
    public BufferedImage addBackground(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = background.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 白色背景
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        // 绘制少量斜线干扰（类似 FastAPI 风格）
        int lineCount = 3 + RANDOM.nextInt(3);
        g2.setStroke(new BasicStroke(1.5f));

        for (int i = 0; i < lineCount; i++)
        {
            // 灰色系干扰线
            int gray = 160 + RANDOM.nextInt(80);
            g2.setColor(new Color(gray, gray, gray, 120));

            // 随机斜线
            if (RANDOM.nextBoolean())
            {
                // 左上到右下
                int startX = RANDOM.nextInt(width / 2);
                int startY = RANDOM.nextInt(height);
                int endX = startX + width / 2 + RANDOM.nextInt(width / 2);
                int endY = RANDOM.nextInt(height);
                g2.drawLine(startX, startY, endX, endY);
            }
            else
            {
                // 右上到左下
                int startX = width / 2 + RANDOM.nextInt(width / 2);
                int startY = RANDOM.nextInt(height);
                int endX = RANDOM.nextInt(width / 2);
                int endY = RANDOM.nextInt(height);
                g2.drawLine(startX, startY, endX, endY);
            }
        }

        // 把验证码图片画到背景上
        g2.drawImage(image, 0, 0, null);
        g2.dispose();

        return background;
    }
}
