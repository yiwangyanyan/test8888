package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    /**
     * 新增CheckItem
     * @param checkItem
     */
    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    /**
     * 分页查询CheckItem
     *
     * @param queryPageBean
     * @return
     */
    @Override
    public PageResult findPage(QueryPageBean queryPageBean) {
        /**
         * 【分页查询原理】: ThreadLocal 和本地线程进行了绑定
         * 1.业务层PageHelper.startPage() 把查询页码和一页查询的数量绑定到ThreadLocal
         * 2.在Dao里面 从ThreadLocal取出查询页码和一页查询的数量 进行调用limit a,b
         */
        //1.调用分页插件的方法(参数1:查询页码; 参数2:一页查询的数量)
        PageHelper.startPage(queryPageBean.getCurrentPage(),queryPageBean.getPageSize());
        //2.调用Dao Page对象是分页插件封装的对象,这里面包含了分页查询的数据(total,list...)
        Page<CheckItem> page =  checkItemDao.findByConditions(queryPageBean.getQueryString());
        PageResult pageResult =new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    /**
     * 删除CheckItem
     * @param checkItemId
     */
    @Override
    public void delete(Integer checkItemId) {
        //1.查询检查项是否被引用到了
        long count = checkItemDao.findByCheckItemId(checkItemId);
        //2.有, 不删除
        if(count > 0){
            throw new RuntimeException("该检查项被引用了不能删除");
        }
        //3.没有, 删除
        checkItemDao.delete(checkItemId);

    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @Override
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    /**
     * 更新检查项
     *
     * @param checkItem
     */
    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    /**
     * 查询所有的检查项
     *
     * @return
     */
    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }
}
