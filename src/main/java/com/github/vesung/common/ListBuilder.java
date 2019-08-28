package com.github.vesung.common;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjing.dc@qq.com
 * @since 2019/6/13
 */
public class ListBuilder<T> {
    private List<T>  list = new ArrayList<>();

    public ListBuilder<T> add(T t){
        this.list.add(t);
        return this;
    }

    public List<T> build(){
        return this.list;
    }
}
