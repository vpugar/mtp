package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import com.vedri.mtp.frontend.transaction.aggregation.dao.SparkAggregationByUserDao;
import com.vedri.mtp.frontend.web.websocket.transaction.WebsocketSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PtByUserActor extends AggregationByUserActor {

    public static final String NAME = "ptByUserActor";

    @Autowired
    public PtByUserActor(WebsocketSender websocketSender, SparkAggregationByUserDao sparkAggregationByUserDao) {
        super(websocketSender, sparkAggregationByUserDao);
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
