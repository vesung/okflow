package com.github.vesung.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/6/12
 */
public class MapBuilder<K, V> {

    private Map<K,V> map;

    public MapBuilder(){
        map = new HashMap<>();
    }

    public MapBuilder<K, V> put(K key, V value){
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return this.map;
    }
}
