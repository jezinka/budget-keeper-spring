package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.GoalDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Goal;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:01:28+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class GoalMapperImpl implements GoalMapper {

    @Override
    public Goal mapToEntity(GoalDTO goalDTO) {
        if ( goalDTO == null ) {
            return null;
        }

        Goal.GoalBuilder goal = Goal.builder();

        goal.category( goalDTOToCategory( goalDTO ) );
        goal.id( goalDTO.getId() );
        goal.amount( goalDTO.getAmount() );
        goal.date( goalDTO.getDate() );

        return goal.build();
    }

    @Override
    public GoalDTO mapToDto(Goal goal) {
        if ( goal == null ) {
            return null;
        }

        GoalDTO.GoalDTOBuilder goalDTO = GoalDTO.builder();

        goalDTO.categoryName( goalCategoryName( goal ) );
        goalDTO.id( goal.getId() );
        goalDTO.date( goal.getDate() );
        goalDTO.amount( goal.getAmount() );

        return goalDTO.build();
    }

    protected Category goalDTOToCategory(GoalDTO goalDTO) {
        if ( goalDTO == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( goalDTO.getCategoryName() );

        return category.build();
    }

    private String goalCategoryName(Goal goal) {
        if ( goal == null ) {
            return null;
        }
        Category category = goal.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
