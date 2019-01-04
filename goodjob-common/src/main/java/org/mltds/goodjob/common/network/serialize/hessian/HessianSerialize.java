package org.mltds.goodjob.common.network.serialize.hessian;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.mltds.goodjob.common.network.serialize.Serialize;


/**
 * Hessian 序列化服务
 * 
 * @author sunyi
 */
public class HessianSerialize implements Serialize {

    @Override
    public Object read(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bais);
        try {
            return input.readObject();
        } finally {
            input.close();
        }

    }

    @Override
    public <T> T read(byte[] bytes, Class<T> cl) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bais);
        try {
            return (T) input.readObject();
        } finally {
            input.close();
        }
    }

    @Override
    public byte[] write(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(baos);
        try {
            output.writeObject(object);
            output.flush();
            return baos.toByteArray();
        } finally {
            output.close();
        }
    }

}
