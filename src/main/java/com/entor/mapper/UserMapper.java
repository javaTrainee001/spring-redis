package com.entor.mapper;

import com.entor.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    @Select("select * from user limit 0,100")
    List<User> list();

    @Select("select * from user where username=#{username}")
    User queryByUserName(String username);

    @Select("select * from user")
    List<User> queryAll();
}
