/**

 * @author huzexin@duoku.com

 * @version CreateData��2012-5-10 3:46:54 PM

 */
package com.andconsd.net;

public interface IHttpInterface {
	
	public abstract int sendRequest(int tag,String url, String bodydata, INetListener listener);
	public abstract	int	sendDownLoadRequest(String url, String filepath, INetListener listener);
	public abstract void cancelRequestById(int requestId);
	public abstract void cancelAllRequest();
}
