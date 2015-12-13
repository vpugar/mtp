package com.vedri.mtp.frontend.transaction.aggregation.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PeriodicTick {

	private boolean returnToSender = false;
}
