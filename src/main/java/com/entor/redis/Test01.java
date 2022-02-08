package com.entor.redis;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 获取redis的存储
 */
public class Test01 {
    public static void main(String[] args) {
        //连接redis
        Jedis jedis = new Jedis("127.0.0.1");
        System.out.println("连接中...");
        System.out.println("服务器是否在运行:" + jedis.ping());
        //获取redis数据的值
        String myname = jedis.get("myname");
        System.out.println("ke=myname value=" + myname);
        //设置值
        jedis.set("myString", "mystring");

        //集合
        List<String> list = jedis.lrange("myList01", 0, 2);
        int total = 0;
        for (String str : list) {
            total++;
            if (total == list.size()) {
                System.out.println(str);
                break;
            }
            System.out.print(str + ",");
        }
        System.out.println("-------------------");

        //从头部插入 lpush
//        String[] strings = {"j1", "j2", "j3"};
//        jedis.lpush("mynames",strings);
        //从尾部插入 rpush
        jedis.rpush("myList01", "jj1", "jj2");

        //set
        Set<String> set = jedis.smembers("mySet");
        boolean b = false;
        for (String s : set) {
            System.out.print((b ? "," : "") + s);
            b = true;
        }

        System.out.println("");

        jedis.sadd("newSet", "123", "456");

        //判断集合中是否存在某个元素值
        //判断是否存在123值，存在返回1否则返回0
        Boolean i = jedis.sismember("mySet", "123");
        System.out.println(i ? "存在" : "不存在");

        //集合合并
        Set<String> ss = jedis.sunion("mySet", "newSet");
        b = false;
        for (String s : ss) {
            System.out.println((b ? "," : "") + s);
            b = true;
        }
        System.out.println("");
        //zset
        Set<String> myZset = jedis.zrange("myZset", 0, -1);
        b = false;
        for (String s : myZset) {
            System.out.print((b ? "," : "") + s);
            b = true;
        }
        System.out.println();
        //设置值 1、普通方式 2、map
        jedis.zadd("myZset02", 2, "456");
        Map<String, Double> map = new HashMap();
        map.put("753", 5D);
        map.put("75389", 5.5D);
        map.put("4753", 5.2D);
        jedis.zadd("myZset02", map);

        //修改数据库
        jedis.select(1);
        //修改生命时间,以秒为单位
        jedis.expire("myHash02", 10);

        //hash
        String v = jedis.hget("myHash", "li");
        System.out.println("key=myHash,filed=li,value=" + v);

        Map<String, String> maps = jedis.hgetAll("myHash");
        System.out.println(maps);

        jedis.hset("mset", "us", "美丽国");
        HashMap<String, String> smap = new HashMap<String, String>();
        smap.put("username", "li");
        smap.put("password", "1223");
        smap.put("age", "30");
        smap.put("sex", "1");
        jedis.hmset("myHash02", smap);
    }
}
