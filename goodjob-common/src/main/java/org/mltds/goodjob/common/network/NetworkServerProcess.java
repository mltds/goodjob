package org.mltds.goodjob.common.network;

import org.mltds.goodjob.common.network.model.NetworkRequest;
import org.mltds.goodjob.common.network.model.NetworkResponse;

/**
 *
 * @author sunyi 2018/11/6.
 */
public interface NetworkServerProcess {

    NetworkResponse process(NetworkRequest request);

}
