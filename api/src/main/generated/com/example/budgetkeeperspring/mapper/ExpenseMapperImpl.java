package com.example.budgetkeeperspring.mapper;

import com.example.budgetkeeperspring.dto.ExpenseDTO;
import com.example.budgetkeeperspring.entity.Category;
import com.example.budgetkeeperspring.entity.Expense;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-07T14:04:53+0000",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public Expense mapToEntity(ExpenseDTO expenseDTO) {
        if ( expenseDTO == null ) {
            return null;
        }

        Expense.ExpenseBuilder expense = Expense.builder();

        expense.category( expenseDTOToCategory( expenseDTO ) );
        if ( expenseDTO.getDeleted() != null ) {
            expense.deleted( expenseDTO.getDeleted() );
        }
        else {
            expense.deleted( false );
        }
        expense.transactionDate( stringToLocalDate( expenseDTO.getTransactionDate() ) );
        expense.id( expenseDTO.getId() );
        expense.version( expenseDTO.getVersion() );
        expense.title( expenseDTO.getTitle() );
        expense.payee( expenseDTO.getPayee() );
        expense.amount( expenseDTO.getAmount() );
        expense.note( expenseDTO.getNote() );

        return expense.build();
    }

    @Override
    public ExpenseDTO mapToDto(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseDTO.ExpenseDTOBuilder expenseDTO = ExpenseDTO.builder();

        expenseDTO.deleted( expense.isDeleted() );
        expenseDTO.categoryId( expenseCategoryId( expense ) );
        expenseDTO.categoryName( expenseCategoryName( expense ) );
        expenseDTO.id( expense.getId() );
        expenseDTO.version( expense.getVersion() );
        if ( expense.getTransactionDate() != null ) {
            expenseDTO.transactionDate( DateTimeFormatter.ISO_LOCAL_DATE.format( expense.getTransactionDate() ) );
        }
        expenseDTO.title( expense.getTitle() );
        expenseDTO.payee( expense.getPayee() );
        expenseDTO.amount( expense.getAmount() );
        expenseDTO.note( expense.getNote() );

        return expenseDTO.build();
    }

    protected Category expenseDTOToCategory(ExpenseDTO expenseDTO) {
        if ( expenseDTO == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.id( expenseDTO.getCategoryId() );
        category.name( expenseDTO.getCategoryName() );

        return category.build();
    }

    private Long expenseCategoryId(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        Category category = expense.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String expenseCategoryName(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        Category category = expense.getCategory();
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
