package tz.co.flex.flexsms.service;

import tz.co.flex.flexsms.model.Customer;

public interface SmsService {
    void sendSms(Customer customer) throws Exception;
}
