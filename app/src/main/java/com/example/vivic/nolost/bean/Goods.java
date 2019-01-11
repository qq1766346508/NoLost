package com.example.vivic.nolost.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class Goods extends BmobObject {
    public static final String TYPE_LOST = "丢失";
    public static final String TYPE_FOUND = "拾获";

    public String name;
    public String detail;
    public List<BmobFile> photoList;
    public String location;
    public MyUser createor;
    public String type;
}
