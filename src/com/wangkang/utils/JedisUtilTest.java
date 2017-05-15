package com.wangkang.utils;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: JedisUtilTest.java</p>
 * <p>Description: information management software platform</p>
 * <p>Copyright: Copyright (c) 2011-2012 JinRui Information Technology Co., Ltd.</p>
 * <p>Company: JinRui Information Technology Co., Ltd.</p>
 * @author wangkang
 * @version 1.0 creation time��2017-5-15 ����3:44:50
 */

public class JedisUtilTest {
	
	
	JedisPool pool;
    Jedis jedis;
    @Before
    public void setUp() {
    	
    	JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config, "192.168.0.9");
        
        jedis = pool.getResource();
     //   jedis.auth("password");
    }


    @Test
    public void testGet(){
        System.out.println(jedis.get("lu"));
    }

    /**
     * Redis�洢�������ַ���
     * CRUD
     */
    @Test
    public void testBasicString(){
        //-----�������----------
        jedis.set("name","wangkang");//��key-->name�з�����value-->minxr
        System.out.println(jedis.get("name"));//ִ�н����minxr

        //-----�޸�����-----------
        //1����ԭ���������޸�
        jedis.append("name","bluemonkey");   //��ֱ�ۣ�����map ��jarorwar append���Ѿ��е�value֮��
        System.out.println(jedis.get("name"));//ִ�н��:minxrjarorwar

        //2��ֱ�Ӹ���ԭ��������
        jedis.set("name","zhouwei");
        System.out.println(jedis.get("name"));//ִ�н����������

        //ɾ��key��Ӧ�ļ�¼
        jedis.del("name");
        System.out.println(jedis.get("name"));//ִ�н����null

        /**
         * mset�൱��
         * jedis.set("name","wangkang");
         * jedis.set("bluemonkey","zhouwei");
         */
        jedis.mset("name","wangkang","bluemonkey","zhouwei");
        System.out.println(jedis.mget("name","bluemonkey"));

    }

    /**
     * jedis����Map
     */
    @Test
    public void testMap(){
        Map<String,String> user=new HashMap<String,String>();
        user.put("name","wangkang");
        user.put("pwd","password");
        jedis.hmset("user",user);
        //ȡ��user�е�name��ִ�н��:[minxr]-->ע������һ�����͵�List
        //��һ�������Ǵ���redis��map�����key����������Ƿ���map�еĶ����key�������key���Ը�������ǿɱ����
        List<String> rsmap = jedis.hmget("user", "name");
        System.out.println(rsmap);

        //ɾ��map�е�ĳ����ֵ
//    jedis.hdel("user","pwd");
        System.out.println(jedis.hmget("user", "pwd")); //��Ϊɾ���ˣ����Է��ص���null
        System.out.println(jedis.hlen("user")); //����keyΪuser�ļ��д�ŵ�ֵ�ĸ���1
        System.out.println(jedis.exists("user"));//�Ƿ����keyΪuser�ļ�¼ ����true
        System.out.println(jedis.hkeys("user"));//����map�����е�����key  [pwd, name]
        System.out.println(jedis.hvals("user"));//����map�����е�����value  [minxr, password]

        Iterator<String> iter=jedis.hkeys("user").iterator();
        while (iter.hasNext()){
            String key = iter.next();
            System.out.println(key+":"+jedis.hmget("user",key));
        }

    }

    /**
     * jedis����List
     */
    @Test
    public void testList(){
        //��ʼǰ�����Ƴ����е�����
        jedis.del("java framework");
        System.out.println(jedis.lrange("java framework",0,-1));
        //����key java framework�д����������
        jedis.lpush("java framework","spring");
        jedis.lpush("java framework","struts");
        jedis.lpush("java framework","hibernate");
        //��ȡ����������jedis.lrange�ǰ���Χȡ����
        // ��һ����key���ڶ�������ʼλ�ã��������ǽ���λ�ã�jedis.llen��ȡ���� -1��ʾȡ������
        System.out.println(jedis.lrange("java framework",0,-1));
    }

    /**
     * jedis����Set
     */
    @Test
    public void testSet(){
        //���
        jedis.sadd("sname","wangkang");
        jedis.sadd("sname","blue");
        jedis.sadd("sname","monkey");
        jedis.sadd("sanme","noname");
        //�Ƴ�noname
        jedis.srem("sname","noname");
        System.out.println(jedis.smembers("sname"));//��ȡ���м����value
        System.out.println(jedis.sismember("sname", "monkey"));//�ж� minxr �Ƿ���sname���ϵ�Ԫ��
        System.out.println(jedis.srandmember("sname"));
        System.out.println(jedis.scard("sname"));//���ؼ��ϵ�Ԫ�ظ���
    }

    @Test
    public void test() throws InterruptedException {
        //keys�д���Ŀ�����ͨ���
        System.out.println(jedis.keys("*")); //���ص�ǰ�������е�key  [sose, sanme, name, jarorwar, foo, sname, java framework, user, braand]
        System.out.println(jedis.keys("*name"));//���ص�sname   [sname, name]
        System.out.println(jedis.del("sanmdde"));//ɾ��keyΪsanmdde�Ķ���  ɾ���ɹ�����1 ɾ��ʧ�ܣ����߲����ڣ����� 0
        System.out.println(jedis.ttl("sname"));//���ظ���key����Чʱ�䣬�����-1���ʾ��Զ��Ч
        jedis.setex("timekey", 10, "min");//ͨ���˷���������ָ��key�Ĵ���Чʱ�䣩 ʱ��Ϊ��
        Thread.sleep(5000);//˯��5���ʣ��ʱ�佫Ϊ<=5
        System.out.println(jedis.ttl("timekey"));   //������Ϊ5
        jedis.setex("timekey", 1, "min");        //��Ϊ1�������ٿ�ʣ��ʱ�����1��
        System.out.println(jedis.ttl("timekey"));  //������Ϊ1
        System.out.println(jedis.exists("key"));//���key�Ƿ����
        System.out.println(jedis.rename("timekey","time"));
        System.out.println(jedis.get("timekey"));//��Ϊ�Ƴ�������Ϊnull
        System.out.println(jedis.get("time")); //��Ϊ��timekey ������Ϊtime ���Կ���ȡ��ֵ min

        //jedis ����
        //ע�⣬�˴���rpush��lpush��List�Ĳ�������һ��˫���������ӱ��������ģ�
        jedis.del("a");//��������ݣ��ټ������ݽ��в���
        jedis.rpush("a", "1");
        jedis.lpush("a","6");
        jedis.lpush("a","3");
        jedis.lpush("a","9");
        System.out.println(jedis.lrange("a",0,-1));// [9, 3, 6, 1]
        System.out.println(jedis.sort("a")); //[1, 3, 6, 9]  //�����������
        System.out.println(jedis.lrange("a",0,-1));

    }
}
