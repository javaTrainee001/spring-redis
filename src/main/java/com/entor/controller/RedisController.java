package com.entor.controller;

import com.alibaba.fastjson.JSON;
import com.entor.config.RedisOperation;
import com.entor.entity.User;
import com.entor.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class RedisController {

    @Autowired
    private RedisOperation ro;
    @Autowired(required = false)
    private UserMapper userMapper;

    @RequestMapping("/getString/{key}")
    public String getString(@PathVariable(value = "key") String key) {
        //绝对装换String 1.String.valueOf 2.+""
        String value = String.valueOf(ro.getString(key));
        return value;
    }

    @RequestMapping("/setString/{key}/{value}")
    public String setString(@PathVariable(value = "key") String key,
                            @PathVariable(value = "value") String value) {
        boolean b = ro.setString(key, value);
        return b ? "success" : "fail";
    }

    @RequestMapping("/setExpire/{key}/{time}")
    public String setExpire(@PathVariable(value = "key") String key,
                            @PathVariable(value = "time") long time) {
        boolean b = ro.setExpire(key, time, TimeUnit.SECONDS);
        return b ? "success" : "fail";
    }

    @RequestMapping("/findUser/{key}")
    public List<User> find(@PathVariable(value = "key") String key) {
        List<User> list = null;
        boolean hasKey = ro.hashKey(key);
        if (hasKey) {
            System.out.println("从Redis中获取");
            List<Object> objList = ro.getList(key, 0, -1);
            String obj = JSON.toJSONString(objList);
            list = JSON.parseArray(obj, User.class);
        } else {
            System.out.println("从数据库查询");
            list = userMapper.list();
            //存储到Redis数据库
            ro.setListForList(key, list);
        }
        return list;
    }

    @RequestMapping("/find/{key}/{username}")
    public User queryByUserName(@PathVariable(value = "key") String key,
                                @PathVariable(value = "username") String username) {
        User user = null;
        boolean hasKey = ro.hashKey(key);
        if (hasKey) {
            System.out.println("从Redis中获取");
            List<Object> objList = ro.getList(key, 0, -1);
            List<User> users = new LinkedList<>();
            for (Object o : objList) {
                users.add((User) o);
            }
            for (User user1 : users) {
                if (user1.getUsername().equals(username)) {
                    user = user1;
                    String str = JSON.toJSONString(user);
                    user = JSON.parseObject(str, User.class);
                    break;
                }
            }
        } else {
            System.out.println("从数据库查询");
            user = userMapper.queryByUserName(username);
            //存储到Redis数据库
            ro.setListForEnd(key, user);
        }
        return user;
    }
}
