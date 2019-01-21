package com.example.vivic.nolost.lost;

import com.example.vivic.nolost.bean.Goods;

public class GoodsEvent {
    public Goods goods;
    public String operate;

    public GoodsEvent(Goods goods, String operate) {
        this.goods = goods;
        this.operate = operate;
    }

    public static class Operate {
        public static String SAVE = "SAVE";
        public static String UPDATE = "UPDATE";
        public static String DELETE = "DELETE";
    }
}
