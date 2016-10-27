package com.gnice.radar.util;


import android.graphics.Color;

public class Constant {
    public static final String dateFormat = "yyyy-MM-dd HH:mm:ss";   // hh 为12小时制

    public static final int RADAR_GREEN = Color.parseColor("#1fa625");
    public static final int FRIEND_BLUE = Color.parseColor("#03A9F4");
    public static final int ENEMY_RED = Color.parseColor("#FF5252");
    public static final int SETTING_ORANGE = Color.parseColor("#FF9800");

    public static final int[] palette = {
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#03A9F4"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#009688"),
            Color.parseColor("#4CAF50"),  // green
            Color.parseColor("#8BC34A"),
            Color.parseColor("#CDDC39"),
            Color.parseColor("#FFEB3B"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#795548"),
            Color.parseColor("#9E9E9E"),
            Color.parseColor("#607D8B"),
            Color.parseColor("#D32F2F"),
            Color.parseColor("#FF5252"),
            Color.parseColor("#FF4081"),
            Color.parseColor("#7C4DFF"),
            Color.parseColor("#536DFE"),
            Color.parseColor("#FF5252")
    };

    public static int overLayColor(int type) {
        if (type == PersonItem.FRIEND) {
            return FRIEND_BLUE;
        } else if (type == PersonItem.ENEMY) {
            return ENEMY_RED;
        } else {
            return RADAR_GREEN;
        }
    }

}
