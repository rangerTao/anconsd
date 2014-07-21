/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */
package com.ranger.bmaterials.net;

public interface IHttpInterface {
	
	public abstract int sendRequest(String url, int requestTag, String bodydata, INetListener listener);
	public abstract	int	sendDownLoadRequest(String url, String filepath, INetListener listener);
	public abstract void cancelRequestById(int requestId);
	public abstract void cancelAllRequest();
}
