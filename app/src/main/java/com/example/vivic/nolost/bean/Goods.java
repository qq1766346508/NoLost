package com.example.vivic.nolost.bean;

import java.util.List;

public class Goods {
    public static final int TYPE_LOST = 0;
    public static final int TYPE_FOUND = 1;

    public String goodsName;
    public String detail;
    public List<String> photoList;
    public String place;
    public String time;
    public String createor;
    public int goodsType;
}
