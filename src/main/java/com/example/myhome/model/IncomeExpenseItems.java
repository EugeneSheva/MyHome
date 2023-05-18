package com.example.myhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
