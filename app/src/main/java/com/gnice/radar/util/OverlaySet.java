package com.gnice.radar.util;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.Text;

// 用于存放一个人的所有覆盖物
public class OverlaySet {
    private Marker marker = null;
    private Polyline polyline = null;
    private Text textInfo = null;
    private Text textdDistance = null;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Text getTextInfo() {
        return textInfo;
    }

    public void setTextInfo(Text textInfo) {
        this.textInfo = textInfo;
    }

    public Text getTextdDistance() {
        return this.textdDistance;
    }

    public void setTextdDistance(Text textdDistance) {
        this.textdDistance = textdDistance;
    }
}
