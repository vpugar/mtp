package com.vedri.mtp.processor.country;

import java.util.Set;

import com.google.common.base.Objects;
import com.vedri.mtp.core.support.cassandra.ColumnUtils;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Country {

	private String cca2;
	private String cca3;
	private String commonName;
	private String officialName;
	private Set<String> currencies;

    public enum Fields {
        cca2, cca3, commonName, officialName, currencies;

        public final ColumnUtils.Field<Fields> F = ColumnUtils.createField(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equal(cca2, country.cca2);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cca2);
    }
}
