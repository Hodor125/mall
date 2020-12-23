package com.mall.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/23
 * @description ：
 * @version: 1.0
 */
@Table(name = "tb_album")
public class Album implements Serializable {
    @Id
    private Integer id;
    private String title;
    private String image;
    private String imageItems;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageItems() {
        return imageItems;
    }

    public void setImageItems(String imageItems) {
        this.imageItems = imageItems;
    }
}
