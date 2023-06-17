package com.example.myhome.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "cash_box")
public class CashBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long number;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private IncomeExpenseType incomeExpenseType;

    @ManyToOne
    @JoinColumn(name = "income_expense_items_id")
    @JsonIgnore
    private IncomeExpenseItems incomeExpenseItems;

    @ManyToOne
    @JoinColumn(name = "apartment_account_id")
    private ApartmentAccount apartmentAccount;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    private Owner owner;

    private Boolean completed;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Admin manager;

    @Column(columnDefinition = "TEXT")
    @Size(max=500, message="Comment too long")
    private String description;

    private Double amount;

//    public String getIncExpItemName() {
//        return this.incomeExpenseItems.getName();
//    }

    @PreRemove
    void clearTransactionFromAccount() {
        if(this.apartmentAccount != null) {
            this.apartmentAccount.getTransactions().remove(this);
            this.apartmentAccount.removeFromBalance(this.amount);
        }
    }

}
