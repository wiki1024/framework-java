package com.wiki.framework.mybatis.mybatis.listener;

public interface OrderedListener {
    default int getOrder() {
        return 0;
    }
}
