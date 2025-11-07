package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.CategoryLevelDTO;
import com.example.budgetkeeperspring.entity.CategoryLevel;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:04:53+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class CategoryLevelMapperImpl implements CategoryLevelMapper {

    @Override
    public CategoryLevel mapToEntity(CategoryLevelDTO categoryLevelDTO) {
        if ( categoryLevelDTO == null ) {
            return null;
        }

        CategoryLevel.CategoryLevelBuilder categoryLevel = CategoryLevel.builder();

        categoryLevel.id( categoryLevelDTO.getId() );
        if ( categoryLevelDTO.getLevel() != null ) {
            categoryLevel.level( Integer.parseInt( categoryLevelDTO.getLevel() ) );
        }
        categoryLevel.name( categoryLevelDTO.getName() );

        return categoryLevel.build();
    }

    @Override
    public CategoryLevelDTO mapToDto(CategoryLevel categoryLevel) {
        if ( categoryLevel == null ) {
            return null;
        }

        CategoryLevelDTO.CategoryLevelDTOBuilder categoryLevelDTO = CategoryLevelDTO.builder();

        categoryLevelDTO.id( categoryLevel.getId() );
        if ( categoryLevel.getLevel() != null ) {
            categoryLevelDTO.level( String.valueOf( categoryLevel.getLevel() ) );
        }
        categoryLevelDTO.name( categoryLevel.getName() );

        return categoryLevelDTO.build();
    }
}
