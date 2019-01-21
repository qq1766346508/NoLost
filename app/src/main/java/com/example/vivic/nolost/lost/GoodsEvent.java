package com.example.vivic.nolost.lost;

import com.example.vivic.nolost.bean.Goods;

public class GoodsEvent {
    public Goods goods;
    public operate operate;

    public GoodsEvent(Goods goods, GoodsEvent.operate operate) {
        this.goods = goods;
        this.operate = operate;
    }

    public enum operate {
        save,
        update,
        delete
    }
}
