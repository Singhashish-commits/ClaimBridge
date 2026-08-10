package com.ashish.common.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import javax.naming.ServiceUnavailableException;


public class CommonFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if(response.status() == 503){
            return new ServiceUnavailableException();
        }
        return defaultDecoder.decode(methodKey, response);
    }
}
