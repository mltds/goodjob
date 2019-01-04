package org.mltds.goodjob.worker.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * 是否允许访问这个 http 接口
 * @author sunyi
 */
public class AccessUtils {

	private static final String HEADER_X_REAL_IP = "x-real-ip"; // 如果是通过 nginx 反向代理进来的， nginx 负责填充

	private static final String ALLOW_PARAM = "allow"; // 允许接收请求的ip范围，例如  192.168.1.1/16

	private static final String ALLOW_DEFAULT = "192.168.1.1/16";  // 为了防止外网攻击，默认在这个范围内的IP才能发起任务，

	/**
	 * 根据请求IP进行判断，使用 IPv4，对 IPv6 支持的不好
	 * @param request
	 * @return
	 */
	public static boolean isAllow(HttpServletRequest request) {

		String allow = request.getSession().getServletContext().getInitParameter(ALLOW_PARAM);
		if (!hasText(allow)) {
			// 是否设置默认allow范围犹豫很久，基于约定大于配置的原则，还是给一个默认 allow 范围吧
			allow = ALLOW_DEFAULT;
		}

		String realIp = extractIp(request);

		if ("127.0.0.1".equals(realIp) || "0:0:0:0:0:0:0:1".equals(realIp)) {
			return true;
		}

		IPRange ipRange = new IPRange(allow);

		return ipRange.isIPAddressInRange(new IPAddress(realIp));

	}

	/**
	 * 从 Request 里提提取请求者 IP
	 * @param request
	 * @return
	 */
	public static String extractIp(HttpServletRequest request) {

		String realIp;
		// 没有使用 x-forwarded-for，主要是因为现在所有外网访问都会走反向代理并使用nginx
		String x_real_ip = request.getHeader(HEADER_X_REAL_IP);

		if (hasText(x_real_ip)) {
			// 通过 nginx 反向代理进来的, 获取 nginx 填充的真实IP
			realIp = x_real_ip;
		} else {
			// 内网直接请求的
			realIp = request.getRemoteHost();
		}

		return realIp;
	}

	private static boolean hasText(String str) {
		return str != null && str.length() > 0;
	}
}
