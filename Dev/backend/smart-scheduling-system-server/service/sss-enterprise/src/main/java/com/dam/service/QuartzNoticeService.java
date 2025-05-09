package com.dam.service;

/**
 * 定时通知
 */
public interface QuartzNoticeService {
    void sendWorkNotice(Long storeId, Integer workNoticeType);

    void sendRestNotice(Long storeId, Integer workNoticeType);
}
