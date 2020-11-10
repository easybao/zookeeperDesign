package com.zkdesign.config;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @version 1.0.0
 * @@menu <p>
 * @date 2020/11/9 11:24
 */
public class ZookeeperConfig implements Watcher {

    static CountDownLatch countDownLatch = new CountDownLatch(1);
    ZooKeeper zookeeper = null;
    Stat  stat = new Stat();
    String path;

    @Before
    public void init() throws Exception {
        path = "/username";
        zookeeper = new ZooKeeper("127.0.0.1:2181",5000,new ZookeeperConfig());
        countDownLatch.await();//等待zookeeper连接成功,才继续往下执行
        System.out.println("连接成功");
    }

    @Test
    public void test01() throws KeeperException, InterruptedException {
        byte[] data = zookeeper.getData(path, true, stat);
        System.out.println("读取到的数据是:"+new String(data));
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("这是添加监听");
        if(event.getState() == Event.KeeperState.SyncConnected){
            //连接成功后,监听 是否断开连接
            /**
             *  事件
             *  None (-1),
             *  NodeCreated (1),
             *  NodeDeleted (2),
             *  NodeDataChanged (3),
             *  NodeChildrenChanged (4),
             *  DataWatchRemoved (5),
             *  ChildWatchRemoved (6);
             */
            if (event.getType() == Event.EventType.None || event.getPath() == null){
                countDownLatch.countDown();
            }else if(event.getType() == Event.EventType.NodeDataChanged){
                //监听 节点数据是否发生变化
                try {
                    /**
                     * 一个watcher实例 是一个回调函数,被回调一次就移除了,如果还要监控节点变化,还需要注册watcher
                     * 可以注册监听的方法有: getData,exists,getChildren
                     * 可以触发watcher方法: create, delete, setData, 连接断开情况下,触发的watcher会丢失
                     */
                    byte[] data = zookeeper.getData(path, true, stat);
                    System.out.println("节点数据已经修改: 为" + new String(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
