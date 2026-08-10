package com.ashish.common.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

public interface FeignFallbackFactory<T> {
    T fallback(Throwable cause);
}
