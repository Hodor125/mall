package com.mall.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/27
 * @description ：
 * @version: 1.0
 */
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
