package com.zero.support.common.widget.recycler.annotation;

import com.zero.support.common.widget.recycler.ItemViewBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecyclerViewBind {
    Class<? extends ItemViewBinder> value() default ItemViewBinder.class;

    int layout() default -1;

    int br() default -1;
}
