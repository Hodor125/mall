package com.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.mall.search.pojo.SkuInfo;
import com.mall.search.service.EsSearchService;
import io.netty.util.internal.StringUtil;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：hodor007
 * @date ：Created in 2020/12/31
 * @description ：
 * @version: 1.0
 */
@Service
public class EsSearchServiceImpl implements EsSearchService {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map search(Map<String, String> searchMap) {
        Map result = new HashMap();
        //判断参数
        if(searchMap == null){
            return result;
        }

        //构建综合搜索条件类，可以进行模糊搜索、精确搜索、范围搜索(搜索方式)
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //需求1：根据搜索关键词进行搜索（模糊搜索）
        //must相当于and should相当于or mustNot相当于not，后面还要指定AND
        if(!StringUtil.isNullOrEmpty(searchMap.get("keywords"))){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",searchMap.get("keywords")).operator(Operator.AND));
        }

        //需求5 根据品牌精确查询 类似于mysql的select * from tb_sku where brand_name=""
        if(!StringUtil.isNullOrEmpty(searchMap.get("brand"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName",searchMap.get("brand")));
        }

        //需求6 根据分类进行精确搜索    相当于mysql的select * from tb_sku where categoryName = ""
        if(!StringUtil.isNullOrEmpty(searchMap.get("categoryName"))){
            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName",searchMap.get("categoryName")));
        }

        //需求7 根据规格spec进行搜索，注意不能使用text类型否则搜索不到 类似于mysql的select * from tb_sku where spec_color='颜色名'
        //可以加多个过滤器的，如果是以spec_开头的，表明是规则条件
        for (String s : searchMap.keySet()) {
            if(s.startsWith("spec_")){
                String specMapKey = s.substring(5);
                String specMapValue = searchMap.get(s);
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + specMapKey + ".keyword", specMapValue));
            }
        }

        //需求8 价格区间搜索
        String prices = searchMap.get("price");
        if(!StringUtil.isNullOrEmpty(prices)){
            String[] split = prices.split("-");
            if(split.length == 2){
                String bottom = split[0];
                String top = split[1];
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(bottom).lte(top));
            }
        }


        //构建顶级查询对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        //对搜索结果的处理
        //需求2.1 根据品牌进行分组，设置分组    类似于select brandName from tb_sku group by brandName
        //terms(分组名).field(根据什么字段进行分组)
        String brandGroup = "brandGroup";
        TermsAggregationBuilder brandGroupBuilder = AggregationBuilders.terms(brandGroup).field("brandName");
        nativeSearchQueryBuilder.addAggregation(brandGroupBuilder);

        //需求3.1 根据分类进行分组
        String categoryGroup = "categoryName";
        TermsAggregationBuilder categoryGroupBuilder = AggregationBuilders.terms(categoryGroup).field("categoryName");
        nativeSearchQueryBuilder.addAggregation(categoryGroupBuilder);

        //需求4.1 根据规格进行分类    类似于mysql的select spec from tb_sku group by spec
        String specGroup = "specGroup";
        TermsAggregationBuilder specGroupBuilder = AggregationBuilders.terms(specGroup).field("spec.keyword");
        nativeSearchQueryBuilder.addAggregation(specGroupBuilder);

        //需求9 对结果进行分页    类似于mysql中的select * from tb_sku limit n,m
        int  pageNum= 1;
        int pageSize = 10;
        if(!StringUtil.isNullOrEmpty(searchMap.get("pageNum"))){
            pageNum = Integer.valueOf(searchMap.get("pageNum"));
        }
        if(!StringUtil.isNullOrEmpty(searchMap.get("pageSize"))){
            pageSize = Integer.valueOf(searchMap.get("pageSize"));
        }
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, pageSize));

        //需求10 对搜索的结果进行排序    类似于select * from tb_sku order by price desc|asc
        if(!StringUtil.isNullOrEmpty(searchMap.get("sortField")) && !StringUtil.isNullOrEmpty(searchMap.get("sortRule"))){
            String sortField = searchMap.get("sortField");
            String sortRule = searchMap.get("sortRule");
            if("DESC".equalsIgnoreCase(sortRule)){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.DESC));
            } else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.ASC));
            }
        }

        //需求11 对搜索的结果进行高亮显示
        HighlightBuilder.Field highLightField = new HighlightBuilder.Field("name").preTags("<span style='color:red'>").postTags("</span>");
        nativeSearchQueryBuilder.withHighlightFields(highLightField);

        //执行搜索
//        List<SkuInfo> skuInfos = elasticsearchTemplate.queryForList(nativeSearchQueryBuilder.build(), SkuInfo.class);
        AggregatedPage<SkuInfo> searchResult = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHit[] hits = searchResponse.getHits().getHits();    //搜索命中的结果集
                List<T> skuList = new ArrayList<>();
                long total = searchResponse.getHits().getTotalHits();    //搜索命中的结果集的条数
                if (total > 0) {
                    for (SearchHit hit : hits) {
                        //获取搜索命中的每一条商品的JSON数据
                        String skuInfoJson = hit.getSourceAsString();
                        //将搜索商品的字符串转为指定的类型
                        SkuInfo skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);

                        //需求11.2 对搜索结果的商品名称进行显示，获取高亮的名称，覆盖原本没有高亮的名称
                        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                        if(highlightFields != null && highlightFields.size() > 0){
                            HighlightField field = highlightFields.get("name");
                            String highName = field.getFragments()[0].toString();
                            skuInfo.setName(highName);
                        }

                        skuList.add((T) skuInfo);
                    }
                }
                return new AggregatedPageImpl<>(skuList, pageable, total, searchResponse.getAggregations());
            }
        });

        //需求2.2 取出品牌分组的结果
        StringTerms brandTerms = (StringTerms) searchResult.getAggregation(brandGroup);
        List<StringTerms.Bucket> buckets = brandTerms.getBuckets();
        ArrayList<String> brandList = new ArrayList<>();
        if(buckets != null && buckets.size() > 0){
            for (StringTerms.Bucket bucket : buckets) {
                //代表去重的brandName
                String brandName = bucket.getKeyAsString();
                brandList.add(brandName);
            }
        }

        //需求3.2 取出分类分组的结果
        StringTerms categoryTerms = (StringTerms) searchResult.getAggregation(categoryGroup);
        List<String> cateList = categoryTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

        //4.2 取出规格分组的结果
        StringTerms specTerms = (StringTerms) searchResult.getAggregation(specGroup);
        List<StringTerms.Bucket> specTermsBuckets = specTerms.getBuckets();
        ArrayList<String> list = new ArrayList<>();
        Map<String, Set<String>> specMap = new HashMap<>();
        if(specTermsBuckets != null && specTermsBuckets.size() > 0){
            //循环每一个sku的spec
            for (StringTerms.Bucket specTermsBucket : specTermsBuckets) {
                String specName = specTermsBucket.getKeyAsString();
                Map<String, String> map = JSON.parseObject(specName, Map.class);
                for (String s : map.keySet()) {
                    Set<String> set = null;
                    if(specMap.containsKey(s)){
                        set = specMap.get(s);
                    } else {
                        set = new HashSet<>();
                    }
                    set.add(map.get(s));
                    specMap.put(s, set);
                }
            }
        }

        result.put("rows", searchResult.getContent());
        result.put("total", searchResult.getTotalElements());
        result.put("totealPage", searchResult.getTotalPages());
        result.put("brandList", brandList);    //返回格式 ['','','']
        result.put("cateList", cateList);
        result.put("specList",specMap);
        result.put("pageNum",pageNum);
        return result;
    }
}
