package com.vedri.mtp.core.support.scala;

import java.util.Map;

import scala.Predef$;
import scala.Tuple2;
import scala.collection.JavaConverters$;
import scala.reflect.ClassTag;

public class ScalaConverts {

	public static <K, V> scala.collection.immutable.Map<K, V> mapToImmutableMap(Map<K, V> map) {
		return JavaConverters$.MODULE$.mapAsScalaMapConverter(map).asScala().toMap(
				Predef$.MODULE$.<Tuple2<K, V>> conforms());
	}

    public static <T> ClassTag<T> toClassTag(Class<T> clazz) {
        return scala.reflect.ClassTag$.MODULE$.apply(clazz);
    }
}
