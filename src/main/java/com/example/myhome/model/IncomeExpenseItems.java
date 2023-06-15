package com.example.myhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

// --- СТАТЬИ ПРИХОДОВ/РАСХОДОВ ---

@Data
@Entity
@Table(name = "income_expense_items")
public class IncomeExpenseItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Необходимо указать название статьи")
    @Size(min=2, message="Название должно быть длиннее 2 символов!")
    @Size(max=50, message="Название должно быть короче 50 символов!")
    private String name;

    @Enumerated(EnumType.STRING)
    private IncomeExpenseType incomeExpenseType;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "incomeExpenseItems")
    private List<CashBox> transactions;

    public IncomeExpenseItems() {
    }

    public IncomeExpenseItems(String name, IncomeExpenseType incomeExpenseType) {
        this.name = name;
        this.incomeExpenseType = incomeExpenseType;
    }

    @PreRemove
    public void clearTransactions() {
        transactions.forEach(t -> t.setIncomeExpenseItems(null));
    }
}
