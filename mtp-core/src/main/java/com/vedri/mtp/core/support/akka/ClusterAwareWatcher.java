package com.vedri.mtp.core.support.akka;

import org.springframework.beans.factory.annotation.Value;

import scala.PartialFunction;
import scala.runtime.BoxedUnit;
import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import com.vedri.mtp.core.MtpConstants;

public abstract class ClusterAwareWatcher extends AbstractActor {

	@Value(MtpConstants.AKKA_LOG_CLUSTER_METRICS)
	private boolean logClusterMetrics;

	protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	protected final Cluster cluster;

	protected final PartialFunction<Object, BoxedUnit> receive = ReceiveBuilder
			.match(ClusterEvent.MemberUp.class, message -> memberUp(message.member()))
			.match(ClusterEvent.UnreachableMember.class, message -> memberUnreachable(message.member()))
			.match(ClusterEvent.MemberRemoved.class,
					message -> memberRemoved(message.member(), message.previousStatus()))
			.match(ClusterEvent.MemberEvent.class, message -> log.debug("Member event {}", message))
			.match(ClusterEvent.ClusterMetricsChanged.class, message -> {
				if (logClusterMetrics) {
					log.debug("Cluster metric changed {}", message);
				}
			})
			.match(ClusterEvent.ClusterDomainEvent.class, message -> {
				if (logClusterMetrics) {
					log.debug("Cluster event {}", message);
				}
			})
			.matchAny(message -> log.error("Unhandled message in watcher {}", message))
			.build();

	public ClusterAwareWatcher(Cluster cluster) {
		this.cluster = cluster;
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		cluster.subscribe(self(), ClusterEvent.ClusterDomainEvent.class);
	}

	@Override
	public void postStop() throws Exception {
		cluster.unsubscribe(self());
		super.postStop();
	}

	protected void memberUp(Member member) {
		log.debug("Member {} joined cluster", member);
	}

	protected void memberUnreachable(Member member) {
		log.debug("Member {} is unreachable", member);
	}

	protected void memberRemoved(Member member, MemberStatus previuosMemberStatus) {
		log.debug("Member {} is removed after {}", member, previuosMemberStatus);
	}
}
