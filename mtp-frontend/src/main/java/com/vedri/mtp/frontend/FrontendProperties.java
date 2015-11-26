package com.vedri.mtp.frontend;

import javax.validation.constraints.NotNull;

import com.vedri.mtp.core.CoreProperties;
import com.vedri.mtp.processor.support.spark.CoreSparkProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = MtpFrontendConstants.CONFIG_PREFIX, ignoreUnknownFields = true)
@Getter
public class FrontendProperties {

	private final Http http = new Http();
	private final Cache cache = new Cache();
	private final Security security = new Security();
	private final Swagger swagger = new Swagger();
	private final Metrics metrics = new Metrics();
	private final CoreProperties.Akka akka = new CoreProperties.Akka();
	private final CoreProperties.Cluster cluster = new CoreProperties.Cluster();
	private final CoreSparkProperties.Spark spark = new CoreSparkProperties.Spark();
	private final CoreProperties.Cassandra cassandra = new CoreProperties.Cassandra();

	@Getter
	public static class Http {

		private final Cache cache = new Cache();

		@Getter
		@Setter
		public static class Cache {
			private int timeToLiveInDays = 31;
		}
	}

	@Getter
	@Setter
	public static class Cache {
		private int timeToLiveSeconds = 3600;
	}

	@Getter
	public static class Security {

		private final Rememberme rememberme = new Rememberme();
		private final Authentication authentication = new Authentication();

		@Getter
		public static class Authentication {

			private final Oauth oauth = new Oauth();

			@Getter
			@Setter
			public static class Oauth {
				private String clientid;
				private String secret;
				private int tokenValidityInSeconds = 1800;
			}
		}

		public static class Rememberme {

			@NotNull
			private String key;

			public String getKey() {
				return key;
			}

			public void setKey(String key) {
				this.key = key;
			}
		}
	}

	@Getter
	@Setter
	public static class Swagger {
		private String title = "MTP API";
		private String description = "MTP API documentation";
		private String version = "0.3.0";
		private String termsOfServiceUrl;
		private String contact;
		private String license;
		private String licenseUrl;
	}

	@Getter
	public static class Metrics {

		private final Jmx jmx = new Jmx();
		private final Spark spark = new Spark();
		private final Graphite graphite = new Graphite();

		@Getter
		@Setter
		public static class Jmx {
			private boolean enabled = true;
		}

		@Getter
		@Setter
		public static class Spark {
			private boolean enabled = false;
			private String host = "localhost";
			private int port = 9999;
		}

		@Getter
		@Setter
		public static class Graphite {
			private boolean enabled = false;
			private String host = "localhost";
			private int port = 2003;
			private String prefix = "mtp";
		}
	}
}
