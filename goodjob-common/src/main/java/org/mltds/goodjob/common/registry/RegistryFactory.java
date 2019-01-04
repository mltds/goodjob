package org.mltds.goodjob.common.registry;

import org.mltds.goodjob.common.Extensible;

/**
 * @author sunyi 2018/11/23.
 */
@Extensible
public interface RegistryFactory {

    Registry getRegistry(String registryServerUrl);

}
