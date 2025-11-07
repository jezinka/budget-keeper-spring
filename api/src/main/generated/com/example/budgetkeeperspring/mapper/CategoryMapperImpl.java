package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.CategoryDTO;
import com.example.budgetkeeperspring.entity.Category;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:04:53+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category mapToEntity(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.id( categoryDTO.getId() );
        category.version( categoryDTO.getVersion() );
        category.name( categoryDTO.getName() );
        category.useInYearlyCharts( categoryDTO.isUseInYearlyCharts() );
        category.level( categoryDTO.getLevel() );

        return category.build();
    }

    @Override
    public CategoryDTO mapToDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDTO.CategoryDTOBuilder categoryDTO = CategoryDTO.builder();

        categoryDTO.id( category.getId() );
        categoryDTO.version( category.getVersion() );
        categoryDTO.name( category.getName() );
        categoryDTO.useInYearlyCharts( category.isUseInYearlyCharts() );
        categoryDTO.level( category.getLevel() );

        return categoryDTO.build();
    }
}
