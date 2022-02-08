package com.entor.config;

import com.entor.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis数据操作类
 */
@Component
public class RedisOperation {

    @Autowired(required = false)
    @Qualifier("redisTemplate")
    private RedisTemplate template;

    /**
     * 设置缓存有效时间
     *
     * @return
     */
    public boolean setExpire(final String key, Long expireTime, TimeUnit unit) {
        boolean result = false;
        try {
            //key判空
            if (template.hasKey(key)) {
                //设置时间
                template.expire(key, expireTime, unit);
                result = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取缓存的有效时间
     *
     * @param key
     * @param unit
     * @return -1永久有效
     */
    public long getExpire(final String key, TimeUnit unit) {
        return template.getExpire(key, unit);
    }

    public boolean hashKey(final String key) {
        return template.hasKey(key);
    }

    //-------------------------string---------------------------

    /**
     * 存入字符串
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setString(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = template.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public Object getString(final String key) {
        boolean b = template.hasKey(key);
        return b ? template.opsForValue().get(key) : null;
    }

    //-------------------------list----------------------------

    /**
     * 在开始位置存入字符串列表
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setListForStart(final String key, Object value) {
        boolean result = false;
        try {
            template.opsForList().leftPush(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 在尾部位置存入字符串列表
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setListForEnd(final String key, Object value) {
        boolean result = false;
        try {
            template.opsForList().rightPush(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean setListForList(final String key, List<User> list) {
        try {
            Long num = template.opsForList().rightPushAll(key, list);
            return num > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据下标修改一个list中的某一个值
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public boolean updateListIndex(final String key, long index, Object value) {
        if (template.hasKey(key)) {
            template.opsForList().set(key, index, value);
            return true;
        }
        return false;
    }

    /**
     * 得到list对象
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<Object> getList(final String key, int start, int end) {
        try {
            if (template.hasKey(key)) {
                if (end > 0 && start > end) {
                    start = start + end;
                    end = start - end;
                    start = start - end;
                }
                return template.opsForList().range(key, start, end);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 存放set字符串
     *
     * @param key
     * @param value
     * @return
     */
    public long setSet(final String key, Object... value) {
        try {
            long result = template.opsForSet().add(key, value);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取set的值
     *
     * @param key
     * @return
     */
    public Set<Object> getSet(final String key) {
        try {
            if (template.hasKey(key)) {
                Set<Object> sets = template.opsForSet().members(key);
                return sets;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合拼集合的值
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<Object> unionSet(final String key1, final String key2) {
        try {
            if (template.hasKey(key1) && template.hasKey(key2)) {
                Set sets = template.opsForSet().union(key1, key2);
                return sets;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //---------------------------zset-----------------------------

    /**
     * 设置有序集合
     *
     * @param key
     * @param sort
     * @param value
     * @return
     */
    public boolean setZset(final String key, Double sort, Object value) {
        try {
            return template.opsForZSet().add(key, value, sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取有序集合
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set getZSet(final String key, int start, int end) {
        if (template.hasKey(key)) {
            if (end > 0 && start > end) {
                start = start + end;
                end = start - end;
                start = start - end;
            }
            return template.opsForZSet().range(key, start, end);
        }
        return null;
    }

    //--------------------------hash-----------------------------

    /**
     * 设置哈希值
     *
     * @param key
     * @param maps
     * @return
     */
    public boolean setHash(final String key, Map<String, Object> maps) {
        try {
            template.opsForHash().putAll(key, maps);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取哈希值
     *
     * @param key
     * @return
     */
    public Map<String, Object> getHash(final String key) {
        return template.opsForHash().entries(key);
    }
}

