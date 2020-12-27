package com.mall.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mall.system.dao.AdminMapper;
import com.mall.system.pojo.Admin;
import com.mall.system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 查询全部列表
     * @return
     */
    @Override
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @Override
    public Admin findById(Integer id){
        return  adminMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     * @param admin
     */
    @Override
    public void add(Admin admin){
        String oriPass = admin.getPassword();
        String hashpw = BCrypt.hashpw(oriPass,BCrypt.gensalt());
        admin.setPassword(hashpw);
        admin.setStatus("1");
        adminMapper.insertSelective(admin);
    }


    /**
     * 修改
     * @param admin
     */
    @Override
    public void update(Admin admin){
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void delete(Integer id){
        adminMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @Override
    public List<Admin> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Admin> findPage(int page, int size){
        PageHelper.startPage(page,size);
        return (Page<Admin>)adminMapper.selectAll();
    }

    /**
     * 条件+分页查询
     * @param searchMap 查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public Page<Admin> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Admin>)adminMapper.selectByExample(example);
    }


    /**
     * 构建查询对象
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andLike("loginName","%"+searchMap.get("loginName")+"%");
           	}
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
           	}
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

    @Override
    public boolean login(Admin admin) {
        //根据用户名查询数据库
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName", admin.getLoginName());

        List<Admin> admins = adminMapper.selectByExample(example);
        Admin dbAdmin = admins.get(0);
        //如果查不到数据则返回失败
        if(dbAdmin == null){
            return false;
        }

        //对比密码
        if(!StringUtils.isEmpty(dbAdmin.getPassword())){
            return BCrypt.checkpw(admin.getPassword(), dbAdmin.getPassword());
        } else {
            return false;
        }
    }
}
