package com.ppdai.framework.raptor.common;

/**
 * Id 生成接口.
 * 
 */
public interface IdGenerator {
    
    /**
     * 生成Id.
     * 
     * @return 返回生成的Id,返回值应为@{@link Number}对象或者为@{@link String}对象
     */
    long generateId();
}
