package com.muvs.inspection_system.fleet;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;
import java.math.RoundingMode;

/** Preserve the JSON number API while storing monetary amounts as decimals. */
@Converter
public class MoneyConverter implements AttributeConverter<Double, BigDecimal> {
    @Override public BigDecimal convertToDatabaseColumn(Double value) {
        if (value == null) return null;
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Money values must be finite");
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
    @Override public Double convertToEntityAttribute(BigDecimal value) {
        return value == null ? null : value.doubleValue();
    }
}
