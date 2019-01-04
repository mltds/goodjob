package org.mltds.goodjob.trigger.utils;


import java.io.Serializable;

/**
 * 推荐只在远程调用的接口层使用, 即对外提供服务的最外层.<br>
 * 例如:<br>
 * <li>controller - service - dao 这种, 不建议 service 层的返回结果使用 Result, 异常统一在最外层处理.</li>
 * <li>facade - service - dao 这种, 请在 facade 层使用 Result,并且无论任何情况, result 都不能为
 * null.</li>
 *
 * @param <V>
 */
public class Result<V> implements Serializable {

	private static final long serialVersionUID = 6781030660269943247L;

	private boolean success = false;

	private V data;

	/**
	 * 错误信息
	 */
	private String errorMsg;
	/**
	 * 业务错误信息代码
	 */
	private String errorCode;
	/**
	 * 一般是 e.getMessage();
	 */
	private String exceptionMsg;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public V getData() {
		return data;
	}

	public void setData(V data) {
		this.data = data;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	/**
	 * 将 {@link #success} , {@link #errorMsg} , {@link #errorCode},
	 * {@link #exceptionMsg} 拼接成String 返回, 方便调用方记录Log.
	 *
	 * @return
	 */
	public String getErrorString() {
		return "ResultErrorString [success=" + success + ", errorCode=" + errorCode + ", errorMsg=" + errorMsg + ", exceptionMsg="
				+ exceptionMsg + "]";
	}

	public static <T> Result<T> buildFail(String errorCode) {
		return buildFail(errorCode, null);
	}

	public static <T> Result<T> buildFail(String errorCode, String errorMsg) {
		return buildFail(errorCode, errorMsg, null);
	}

	public static <T> Result<T> buildFail(String errorCode, String errorMsg, String exceptionMsg) {
		Result<T> result = new Result<T>();
		result.setSuccess(false);
		result.setErrorCode(errorCode);
		result.setErrorMsg(errorMsg);
		result.setExceptionMsg(exceptionMsg);
		return result;
	}

	public static <T> Result<T> buildSucc(T data) {
		Result<T> result = new Result<T>();
		result.setSuccess(true);
		result.setData(data);
		return result;
	}

}
