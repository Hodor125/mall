package com.mall.goods.controller;

import com.github.pagehelper.Page;
import com.mall.entity.PageResult;
import com.mall.entity.Result;
import com.mall.entity.StatusCode;
import com.mall.goods.service.AlbumService;
import com.mall.pojo.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/23
 * @description ：
 * @version: 1.0
 */
@RestController
@RequestMapping("/album")
public class AlbumController {
    @Autowired
    private AlbumService albumService;

    @GetMapping("/findAll")
    public Result findAll(){
        List<Album> albumList = albumService.findAll();
        return new Result(true, StatusCode.OK, "查询相册成功", albumList);
    }

    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Album album = albumService.findById(id);
        return new Result(true, StatusCode.OK, "查找相册成功（单个）", album);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Album album){
        albumService.add(album);
        return new Result(true, StatusCode.OK, "添加相册成功");
    }

    //删除相册
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        albumService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    //修改相册
    @PutMapping("/update")
    public Result update(@RequestBody Album album){
        albumService.update(album);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    @GetMapping("/searchAlbums/{pageNo}/{pageSize}")
    public Result searchAlbum(@RequestParam String name, @PathVariable Integer pageNo, @PathVariable Integer pageSize){
        Page page = albumService.searchAlbum(name, pageNo, pageSize);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return new Result(true, StatusCode.OK, "分页查询相册成功", pageResult);
    }
}
