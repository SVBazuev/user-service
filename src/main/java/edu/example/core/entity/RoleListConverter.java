package edu.example.core.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)
public class RoleListConverter
implements AttributeConverter<List<UserRole>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UserRole> roles) {
        if (roles == null || roles.isEmpty()) {
            return "[]";
        }
        try {
            return mapper.writeValueAsString(roles);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<UserRole> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return mapper.readValue(
                dbData,
                new TypeReference<List<UserRole>>() {}
            );
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
