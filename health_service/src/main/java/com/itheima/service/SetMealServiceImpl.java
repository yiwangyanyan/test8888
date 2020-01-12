package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constants.RedisConstant;
import com.itheima.dao.SetMealDao;
import com.itheima.pojo.Setmeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = SetMealService.class)
@Transactional
public class SetMealServiceImpl implements SetMealService {

    @Autowired
    private SetMealDao setMealDao;


    @Autowired
    private JedisPool jedisPool;
    /**
     * 新增套餐
     * @param setmeal
     * @param checkgroupIds
     */
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        //向t_setmeal插入一条记录
        setMealDao.add(setmeal);
        // 向t_setmeal_checkgroup插入多条记录
        Integer setmealId = setmeal.getId();
        setSetMealAndCheckgroup(setmealId,checkgroupIds);

        //把图片存到Redis
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());

    }

    /**
     * 查询所有的套餐
     *
     * @return
     */
    @Override
    public List<Setmeal> getSetmeal() {
        return setMealDao.getSetmeal();
    }

    /**
     * 根据套餐id查询套餐(包含检查组,检查项)
     *
     * @param id
     * @return
     */
    @Override
    public Setmeal findById(Integer id) {
        //方式一: 1.调用dao根据id查询setmeal基本信息  2.调用Dao取出套餐id查询出检查组集合  3.遍历检查组集合 ,调用Dao查询出每一个检查组的检查项集合
        //方式二: 直接调用一次Dao(在MyBatis映射文件里面 直接关联查询, 使用ResulMap)
        Setmeal setmeal =  setMealDao.findById(id);
        return setmeal;
    }



    private void setSetMealAndCheckgroup(Integer setmealId, Integer[] checkgroupIds) {
        if(checkgroupIds != null && checkgroupIds.length>0){
            for (Integer checkgroupId : checkgroupIds) {
                Map map = new HashMap();
                map.put("setmealId",setmealId);
                map.put("checkgroupId",checkgroupId);
                setMealDao.setSetMealAndCheckgroup(map);
            }
        }
    }
}
