package com.vedri.mtp.core.transaction;

public interface TableName {

    String TRANSACTION = "transaction";

    String RT_AGGREGATION_BY_VALIDATION_STATUS = "rt_aggregation_by_validation_status";
    String RT_AGGREGATION_BY_ORIGINATING_COUNTRY = "rt_aggregation_by_originating_country";
    String RT_AGGREGATION_BY_USER = "rt_aggregation_by_user";
    String RT_AGGREGATION_BY_CURRENCY = "rt_aggregation_by_currency";

    String PT_AGGREGATION_BY_ORIGINATING_COUNTRY = "pt_aggregation_by_originating_country";
    String PT_AGGREGATION_BY_USER = "pt_aggregation_by_user";
    String PT_AGGREGATION_BY_CURRENCY = "pt_aggregation_by_currency";
}
