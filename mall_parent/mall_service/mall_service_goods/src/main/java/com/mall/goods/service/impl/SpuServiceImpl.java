package com.mall.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mall.goods.dao.*;
import com.mall.goods.pojo.*;
import com.mall.goods.service.SpuService;
import com.mall.util.IdWorker;
import io.netty.util.internal.StringUtil;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private RabbitMessagingTemplate messagingTemplate;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Spu findById(String id){
        return  spuMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param spu
     */
    @Override
    public void add(Spu spu){
        spuMapper.insert(spu);
    }


    /**
     * 修改
     * @param spu
     */
    @Override
    public void update(Spu spu){
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(String id){
        spuMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Spu> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Spu> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Spu>)spuMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Spu> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spu>)spuMapper.selectByExample(example);
    }

    /**
     * 添加商品
     * @param goods
     */
    @Transactional
    @Override
    public void addGoods(Goods goods) {
        //1 保存spu到表中
        goods.getSpu().setId(String.valueOf(idWorker.nextId()));    //使用雪花算法生成id
        spuMapper.insertSelective(goods.getSpu());    //写入数据库

        //2 保存sku到表中
        //需要使用循环保存，对比传入的数据和书库表的数据，补全缺失的字段
        saveSkuList(goods);
    }

    private void saveSkuList(Goods goods) {
        Spu spu = goods.getSpu();
        Integer category3Id = spu.getCategory3Id();
        Category category = categoryMapper.selectByPrimaryKey(category3Id);
        Integer brandId = spu.getBrandId();
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        List<Sku> skuList = goods.getSkuList();

        //处理分类与品牌表的关联关系，如果中间表存在则忽略，不存在则添加
        if(category != null && brand != null){
            CategoryBrand categoryBrand = new CategoryBrand();
            categoryBrand.setBrandId(brand.getId());
            categoryBrand.setCategoryId(category.getId());
            int count = categoryBrandMapper.selectCount(categoryBrand);
            if(count == 0){
                categoryBrandMapper.insertSelective(categoryBrand);
            }
        }

        //循环之前最好进行判断
        if(skuList != null && skuList.size() > 0){
            for (Sku sku : skuList) {
                sku.setId(String.valueOf(idWorker.nextId()));
                //如果调用方穿的spec为空或者空串则需要赋值
                if(StringUtil.isNullOrEmpty(sku.getSpec())){
                    sku.setSpec("{}");
                }
                String skuName = spu.getName();
                String spec = sku.getSpec();
                Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                if(specMap != null && specMap.size() > 0){
                    for (String key : specMap.keySet()) {
                        skuName += " " + specMap.get(key);
                    }
                }
                sku.setName(skuName);    //参考淘宝京东等，由spu名称和规格组成，空格进行分割，规格来自spec
                sku.setCreateTime(new Date());
                sku.setUpdateTime(new Date());
                sku.setSpuId(spu.getId());
                sku.setCategoryId(spu.getCategory3Id());    //赋值spu的第三级category id
                if(category != null){
                    sku.setCategoryName(category.getName());    //需要查询数据库了
                }
                if(brand != null){
                    sku.setBrandName(brand.getName());    //需要查数据库
                }
                skuMapper.insertSelective(sku);
            }
        }
    }

    /*
    更新商品
     */
    @Transactional
    @Override
    public void updateGoods(Goods goods) {
        //1 更新spu
        spuMapper.updateByPrimaryKey(goods.getSpu());

        //2 删除sku
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",goods.getSpu().getId());
        skuMapper.deleteByExample(example);

        //3 添加sku
        saveSkuList(goods);
    }

    /**
     * 审核商品
     * @param spuId
     */
    @Override
    public void auditGoods(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查找spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu != null){
                spu.setStatus("1");
                //2 修改status
                spuMapper.updateByPrimaryKeySelective(spu);
            } else {
                throw  new RuntimeException("没有这个商品");
            }
        } else {
            throw  new RuntimeException("spuId为空");
        }
    }

    /**
     * 上架商品
     * @param spuId
     */
    @Override
    public void upGoods(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查询spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu == null){
                throw new RuntimeException("不存在这个商品");
            }
            //2 判断审核状态
            if("0".equals(spu.getStatus())){
                throw new RuntimeException("商品还未审核通过");
            }

            //3 修改状态并保存
            spu.setIsMarketable("1");
            spuMapper.updateByPrimaryKeySelective(spu);

            //4 发送消息给rabbitmq
            messagingTemplate.convertAndSend("goods_up_exchange","",spuId);
        } else {
            throw new RuntimeException("spuId为空");
        }
    }

    /**
     * 下架商品
     * @param spuId
     */
    @Override
    public void downGoods(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查询spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu == null){
                throw new RuntimeException("不存在这个商品");
            }
            //2 判断是否为上架状态
            if("0".equals(spu.getIsMarketable())){
                throw new RuntimeException("商品已下架");
            }
            //3 修改状态写入数据库
            spu.setIsMarketable("0");
            spuMapper.updateByPrimaryKeySelective(spu);
            //4 消息发送到mq中
            messagingTemplate.convertAndSend("goods_down_exchange","",spuId);

        } else {
            throw new RuntimeException("spuId为空");
        }
    }

    /**
     * 逻辑删除商品
     * @param spuId
     */
    @Override
    public void logicDelete(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查询spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu == null){
                throw new RuntimeException("不存在这个商品");
            }
            //2 检查上架状态
            if("1".equals(spu.getIsMarketable())){
                throw new RuntimeException("已上架的商品不能删除");
            }
            //3 修改删除状态和上架状态，写入数据库
            spu.setIsMarketable("0");
            spu.setIsDelete("1");
            spuMapper.updateByPrimaryKeySelective(spu);
        } else {
            throw new RuntimeException("spuId为空");
        }
    }

    /**
     * 恢复商品
     * @param spuId
     */
    @Override
    public void restore(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查询spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu == null){
                throw new RuntimeException("不存在这个商品");
            }
            //2 检查删除状态
            if("0".equals(spu.getIsDelete())){
                throw new RuntimeException("商品已经恢复");
            }

            //3 修改删除状态，存入数据库
            spu.setIsDelete("0");
            spuMapper.updateByPrimaryKeySelective(spu);
        } else {
            throw new RuntimeException("spuId为空");
        }
    }


    /**
     * 物理删除
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteReal(String spuId) {
        if(!StringUtil.isNullOrEmpty(spuId)){
            //1 查询spu
            Spu spu = spuMapper.selectByPrimaryKey(spuId);
            if(spu == null){
                throw new RuntimeException("不存在这个商品");
            }
            //2 检查商品的上架状态
            if("1".equals(spu.getIsMarketable())){
                throw new RuntimeException("已上架的商品不能删除");
            }
            //3 删除spu表和sku表的记录
            spuMapper.deleteByPrimaryKey(spuId);
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId",spu.getId());
            skuMapper.deleteByExample(example);
        } else {
            throw new RuntimeException("spuId为空");
        }
    }


    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
           	}
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
           	}
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
           	}
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
           	}
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
           	}
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
           	}
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
           	}
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andEqualTo("isMarketable",searchMap.get("isMarketable"));
           	}
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andEqualTo("isEnableSpec", searchMap.get("isEnableSpec"));
           	}
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andEqualTo("isDelete",searchMap.get("isDelete"));
           	}
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
