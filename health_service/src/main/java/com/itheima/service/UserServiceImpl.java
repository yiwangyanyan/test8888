package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @Description:
 * @Author: yp
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PermissionDao permissionDao;

    /**
     * 根据用户名查询
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        //方式一: 调用一次Dao, 直接使用MyBatis的映射文件进行关联
        //方式二: 调用三次Dao (调用UserDao根据用户名查询User, 调用RoleDao查询当前用户的角色, 调用PermissionDao查询当前角色拥有的权限)
        //1.调用UserDao根据用户名查询User
        User user = userDao.findByUsername(username);
        //2.调用RoleDao查询当前用户的角色(当前用户的id作为条件)
        if(user != null){
            Integer userId = user.getId();
            Set<Role> roles = roleDao.findByUserId(userId);
            //3.调用PermissionDao查询当前角色拥有的权限
            if(roles != null && roles.size()>0){
                for (Role role : roles) {
                    //SELECT * FROM t_permission WHERE id in(SELECT permission_id FROM t_role_permission WHERE role_id = ?)
                    Integer roleId = role.getId();
                    Set<Permission> permissions = permissionDao.findByRoleId(roleId);
                    role.setPermissions(permissions);
                }
            }
            user.setRoles(roles);
        }


        return user;
    }


}
