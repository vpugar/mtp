package com.vedri.mtp.core.support.cassandra;

import com.google.common.base.CaseFormat;

public class ColumnUtils {

    public static String getColumnName(String cammelCaseName) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, cammelCaseName);
    }

    public static <E extends Enum<E>> Field<E> createField(Enum<E> val) {
        return new Field<>(val);
    }

    public static class Field<E extends Enum<E>> {

        private final Enum<E> enumVal;
        private final String underscore;

        public Field(Enum<E> enumVal) {
            this.enumVal = enumVal;
            this.underscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, enumVal.name());
        }

        public final String underscore() {
            return underscore;
        }

        public final String cammelCase() {
            return enumVal.name();
        }
    }
}
