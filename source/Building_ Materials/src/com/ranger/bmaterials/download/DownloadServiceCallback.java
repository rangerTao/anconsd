/**
 * Copyright (c) 2012 Baidu Inc.
 * 
 * @author 		Qingbiao Liu <liuqingbiao@baidu.com>
 * 
 * @date 2012-3-6
 */
package com.ranger.bmaterials.download;

/**
 * DownloadService����������ʱ����ø÷������Ӷ�ʵ��һЩ�������ط���ĳ�ʼ������,DownloadService.
 * java���ڵ�Ӧ�õ�ApplicationӦ��ʵ�ָýӿڡ�
 */

public interface DownloadServiceCallback {
    /**
     * DownloadService����������ʱ����ø÷������Ӷ�ʵ��һЩ�������ط���ĳ�ʼ��������
     */
    void onDownloadServiceCreate();
}
