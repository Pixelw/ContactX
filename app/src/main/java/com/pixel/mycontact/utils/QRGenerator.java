package com.pixel.mycontact.utils;

import android.graphics.Bitmap;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.background.BlendBackground;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;

/**
 * @author Carl Su
 * @date 2019/12/14
 */
public class QRGenerator {

    public static Bitmap generateQR(String string, Bitmap background, Bitmap avatar) {

        BlendBackground blendBackground = new BlendBackground();
        blendBackground.setBitmap(background);
//        blendBackground.setClippingRect(new Rect(100, 100, 1400, 1400));
        blendBackground.setAlpha(1.0f);
        blendBackground.setBorderRadius(10);

        Color color = new Color();
        color.setLight(0xfff4f7f3);
        color.setDark(0xff2e403a);
        color.setAuto(false);

        Logo logo = new Logo();
        logo.setBitmap(avatar);
        logo.setBorderRadius(10); // radius for logo's corners
        logo.setBorderWidth(10); // width of the border to be added around the logo
        logo.setScale(0.2f); // scale for the logo in the QR code
//        logo.setClippingRect(new Rect(0, 0, 200, 200)); // crop the logo image before applying it to the QR code

        RenderOption renderOption = new RenderOption();
        renderOption.setContent(string);
        renderOption.setSize(800);
        renderOption.setBorderWidth(20);
        renderOption.setRoundedPatterns(true);
        renderOption.setPatternScale(0.5f);
        renderOption.setColor(color);
        renderOption.setBackground(blendBackground);
        renderOption.setClearBorder(false);
        renderOption.setLogo(logo);
        RenderResult renderResult = null;
        Bitmap bitmap = null;
        try {
            renderResult = AwesomeQrRenderer.render(renderOption);
            bitmap = renderResult.getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
