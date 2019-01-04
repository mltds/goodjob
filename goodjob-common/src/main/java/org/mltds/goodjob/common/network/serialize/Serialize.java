package org.mltds.goodjob.common.network.serialize;

import java.io.IOException;

/**
 * @author sunyi
 */
public interface Serialize {

    Object read(byte[] bytes) throws IOException;

    <T> T read(byte[] bytes, Class<T> cl) throws IOException;

    byte[] write(Object object) throws IOException;

}
