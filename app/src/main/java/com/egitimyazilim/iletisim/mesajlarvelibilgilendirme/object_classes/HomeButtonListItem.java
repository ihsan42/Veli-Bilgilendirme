package com.egitimyazilim.iletisim.mesajlarvelibilgilendirme.object_classes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;

public class HomeButtonListItem {
    private Integer icon;
    private String buttonName;

    public HomeButtonListItem(Integer icon, String buttonName) {
        this.icon = icon;
        this.buttonName = buttonName;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }
}
