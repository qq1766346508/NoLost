package com.example.vivic.nolost.bean;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

public class Goods {
    public static final Integer TYPE_LOST = 0;
    public static final Integer TYPE_FOUND = 1;

    public String name;
    public String detail;
    public List<BmobFile> photoList;
    public String location;
    public String time;
    public String createor;
    public Integer type;
}
