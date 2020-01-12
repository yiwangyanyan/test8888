package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberDao memberDao;


    /**
     * 根据手机号码查询Member
     *
     * @param telephone
     * @return
     */
    @Override
    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    /**
     * 增加会员
     *
     * @param member
     */
    @Override
    public void add(Member member) {
        memberDao.add(member);
    }

    /**
     * 根据月份查询会员数量
     * @param monthsList
     * @return
     */
    @Override
    public List<Integer> getMemberReport(List<String> monthsList) {
        List<Integer> list = new ArrayList<Integer>();
        //SELECT COUNT(*) FROM t_member WHERE regTime <= '2019-01-31'
        //遍历月, 拼接成带日的
        for (String month : monthsList) { //2019-08
            String date = month+"-31"; //2019-08-31
            Integer count = memberDao.findMemberCountBeforeDate(date);
            list.add(count);
        }
        return list;
    }
}
