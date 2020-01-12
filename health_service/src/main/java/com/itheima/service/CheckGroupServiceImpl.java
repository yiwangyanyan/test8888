package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {


    @Autowired
    private CheckGroupDao checkGroupDao;

    /**
     * 新增检查组
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.调用dao 向t_checkgroup表插入一条数据
        checkGroupDao.add(checkGroup);
        //2.遍历checkitemIds, 向t_checkgroup_checkitem插入数据
        Integer checkGroupId = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    //调用Dao向中间表t_checkgroup_checkitem插入数据
    private void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkitemIds) {
        if(checkitemIds != null && checkitemIds.length>0){
            for (Integer checkitemId : checkitemIds) {
                Map map = new HashMap();
                map.put("checkGroupId",checkGroupId);
                map.put("checkitemId",checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

    /**
     * 分页查询
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        //1.调用分页插件的方法
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //2.调用Dao获得Page
        Page<CheckGroup> page =  checkGroupDao.findByConditions(queryPageBean.getQueryString());
        //3.封装成PageResult 返回
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    /**
     * 根据检查组的id查询出关联的检查项的id集合
     *
     * @param id
     * @return
     */
    @Override
    public List<Integer> findCheckItemIdsById(Integer id) {
        return checkGroupDao.findCheckItemIdsById(id);
    }

    /**
     * 更新
     *
     * @param checkGroup
     * @param checkitemIds
     */
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //1.调用Dao 更新t_checkgroup(基本信息)
        checkGroupDao.edit(checkGroup);
        //2.调用Dao 删除checkGroup之前关联的检查项(根据检查组id删除t_checkgroup_checkitem)
        checkGroupDao.deleteCheckItemsById(checkGroup.getId());
        //3.调用Dao 插入检查组关联的检查项(t_checkgroup_checkitem)
        setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }

    /**
     * 查询所有的检查组
     *
     * @return
     */
    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }
}
