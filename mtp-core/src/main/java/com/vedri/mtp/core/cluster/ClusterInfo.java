package com.vedri.mtp.core.cluster;

import lombok.Getter;
import lombok.ToString;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vedri.mtp.core.MtpConstants;

@Component
@Getter
@ToString
public class ClusterInfo {

	@Value(MtpConstants.NODE_NAME)
	private String nodeName;

}
