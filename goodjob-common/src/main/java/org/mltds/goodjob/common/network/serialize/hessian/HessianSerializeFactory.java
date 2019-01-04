package org.mltds.goodjob.common.network.serialize.hessian;

import org.mltds.goodjob.common.network.serialize.Serialize;
import org.mltds.goodjob.common.network.serialize.SerializeFactory;

/**
 * Hessian 序列化工厂
 */
public class HessianSerializeFactory implements SerializeFactory {

	private HessianSerialize hessianSerialize;


	@Override
	public Serialize getSerialize() {
		if (hessianSerialize == null) {
			synchronized (HessianSerializeFactory.class) {
				if (hessianSerialize == null) {
					hessianSerialize = new HessianSerialize();
				}
			}
		}

		return hessianSerialize;
	}
}
