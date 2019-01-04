package org.mltds.goodjob.common.registry.zookeeper;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import org.mltds.goodjob.common.GoodjobException;

public class ZookeeperClient {

    /**
     * Zookeeper 数据的编码
     */
    public static final String ZK_DATA_CHARSET = "UTF-8";

    private final ZkClient client;

    private volatile KeeperState state = KeeperState.SyncConnected;

    public ZookeeperClient(String zkUrl) {
        client = new ZkClient(zkUrl, 60 * 60 * 1000, 5 * 1000);// sessionTimeout 1个小时, connectionTimeout 5秒钟

        client.setZkSerializer(new ZkSerializer() {

            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                if (data == null) {
                    return new byte[] {};
                } else {
                    try {
                        return data.toString().getBytes(ZK_DATA_CHARSET);
                    } catch (UnsupportedEncodingException e) {
                        throw new GoodjobException(e.getMessage(), e);
                    }
                }

            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                try {
                    if (bytes == null || bytes.length == 0) {
                        return null;
                    } else {
                        return new String(bytes, ZK_DATA_CHARSET);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new GoodjobException(e.getMessage(), e);
                }
            }
        });

    }

    public void createPersistent(String path) {
        try {
            client.createPersistent(path, "");
        } catch (ZkNodeExistsException e) {
        }
    }

    public void createEphemeral(String path) {
        try {
            client.createEphemeral(path, "");
        } catch (ZkNodeExistsException e) {
        }
    }

    public void delete(String path) {
        try {
            client.delete(path);
        } catch (ZkNoNodeException e) {
        }
    }

    public List<String> getChildren(String path) {
        return client.getChildren(path);
    }

    public boolean isConnected() {
        return state == KeeperState.SyncConnected;
    }

    public void doClose() {
        try {
            client.close();
        } catch (Exception e) {
        }
    }

    public String getData(String path) {
        return client.readData(path, true);
    }

    public void setData(String path, String data) {
        client.writeData(path, data);
    }

    public void registerChangeListener(String path, final ZookeeperListener listener) {

        client.subscribeDataChanges(path, new IZkDataListener() {

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                listener.handleDataDeleted(dataPath);
            }

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                listener.handleDataChange(dataPath, data);
            }
        });

        client.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                listener.handleChildChange(parentPath, currentChilds);
            }
        });
    }

}
