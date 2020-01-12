package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {


    @Autowired
    private OrderSettingDao orderSettingDao;

    /**
     * 导入orderSetting
     *
     * @param orderSettingList
     */
    @Override
    public void add(List<OrderSetting> orderSettingList) {

        if (orderSettingList != null && orderSettingList.size() > 0) {
            //1.遍历List<OrderSetting>
            for (OrderSetting orderSetting : orderSettingList) {
                //2.调用Dao,判断当前日期是否设置过(根据日期查询数量)
                long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
                if (count > 0) {
                    //2.1有设置过, 调用Dao 更新(根据日期更新数量)
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                } else {
                    //2.2 没有设置过,调用Dao 增加
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    /**
     * 查询当前月份的预约设置
     *
     * @param date
     * @return
     */
    @Override
    public List<OrderSetting> getOrderSettingByMonth(String date) {
        //1.拼接日期 封装到Map
        String dateBegin = date + "-01"; //2019-08-01
        String dateEnd = date + "-31"; //2019-08-31
        //2.调用Dao, 根据日期查询
        Map map = new HashMap();
        map.put("dateBegin", dateBegin);
        map.put("dateEnd", dateEnd);
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        return list;
    }

    /**
     * 更新预约设置
     * @param orderSetting
     */
    @Override
    public void editNumberByDate(OrderSetting orderSetting) {
        //判断当前日期是否设置过
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count > 0){
            // 设置过, 根据日期进行更新
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            // 没有设置过, 新增
            orderSettingDao.add(orderSetting);
        }
    }
}
