package org.mltds.goodjob.common.network.serialize;

import org.mltds.goodjob.common.Extensible;

/**
 * 序列化工厂
 */
@Extensible
public interface SerializeFactory {

    Serialize getSerialize();

}
