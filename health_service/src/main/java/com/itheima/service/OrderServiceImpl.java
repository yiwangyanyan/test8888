package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constants.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    @Autowired
    private MemberDao  memberDao;

    @Autowired
    private OrderDao  orderDao;

    /**
     * 提交预约
     * @param map
     * @return
     */
    @Override
    public Result submit(Map<String, Object> map) throws Exception {
        String  orderDateStr = (String) map.get("orderDate");
        Date orderDate = DateUtils.parseString2Date(orderDateStr);
        //  1. 判断当前日期是否可以预约(根据orderDate查询t_ordersetting)
        OrderSetting orderSetting = orderSettingDao.findByOrderDate(orderDate);
        if(orderSetting == null){
            return new Result(false, MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        //  2. 判断当前日期是否预约已满(判断reservations是否等于number)
        if(orderSetting.getReservations() == orderSetting.getNumber()){
            return new Result(false, MessageConstant.ORDER_FULL);
        }

        //  3. 判断当前用户是否是会员(根据phoneNumber查询t_member)
        String  telephone = (String) map.get("telephone");
        Member member = memberDao.findByTelephone(telephone);
        if(member != null){
            //  3.1是会员, 避免重复预约(根据会员id, 预约时间orderDate, 套餐id查询t_order)
            String  setmealId = (String) map.get("setmealId");
            Order order = new Order(member.getId(), orderDate, null, null, Integer.parseInt(setmealId));
            List<Order> list = orderDao.findByCondition(order);
            if(list != null && list.size()>0){
                return new Result(false, MessageConstant.HAS_ORDERED);
            }
        }else{
            //  3.2不是会员, 自动的注册成会员(向t_member插入一条记录)
            member = new Member();
            member.setName((String) map.get("name"));
            member.setPhoneNumber(telephone);
            member.setIdCard((String) map.get("idCard"));
            member.setSex((String) map.get("sex"));
            member.setRegTime(new Date());
            memberDao.add(member);
        }

        //  4. 预约
        //  4.1 向预约表t_order插入一条记录
        Order order = new Order(member.getId(),
                orderDate,
                "微信预约",
                Order.ORDERSTATUS_NO,
                Integer.parseInt((String) map.get("setmealId")));
        orderDao.add(order);

        // 4.2 更新当前日期的已经预约人数(更新t_ordersetting表里面的reservations)
        orderSetting.setReservations(orderSetting.getReservations()+1);
        orderSettingDao.editReservationsByOrderDate(orderSetting);
        return new Result(true,MessageConstant.ORDER_SUCCESS,order);
    }

    /**
     * 根据预约id 查询预约成功详情
     *
     * @param id
     * @return
     */
    @Override
    public Map findById(Integer id) {
        return orderDao.findById4Detail(id);
    }

    /**
     * 预约套餐统计
     *
     * @return
     */
    @Override
    public List<Map> getSetmealReport() {
        return orderDao.findSetmealCount();
    }
}
