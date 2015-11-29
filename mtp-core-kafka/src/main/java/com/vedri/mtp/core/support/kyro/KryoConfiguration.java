package com.vedri.mtp.core.support.kyro;

import de.javakaffee.kryoserializers.jodatime.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.esotericsoftware.kryo.Kryo;
import org.springframework.context.annotation.Scope;

@Configuration
public class KryoConfiguration {

	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@Bean
	public Kryo kryo() {
		final Kryo kryo = new Kryo();
        kryo.register( DateTime.class, new JodaDateTimeSerializer() );
        kryo.register( LocalDate.class, new JodaLocalDateSerializer() );
        kryo.register( LocalDateTime.class, new JodaLocalDateTimeSerializer() );
		return kryo;
	}

	@Bean
	public KryoThreadLocal kryoThreadLocal() {
		return new KryoThreadLocal();
	}

}
