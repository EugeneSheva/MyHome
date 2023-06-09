package com.example.myhome.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Locale;

@Data
@Entity
@Table(name = "invoice_components")
public class InvoiceComponents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    private double unit_price;
    private double unit_amount;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @Override
    public String toString() {
        return "InvoiceComponents{" +
                "id=" + id +
                ", service=" + service +
                ", unit_price=" + unit_price +
                ", unit_amount=" + unit_amount +
                '}';
    }

    public Double getTotalPrice() {
        Double mult = this.unit_price * this.unit_amount;
        String formattedMult = String.format(Locale.ROOT, "%.2f", mult);
        System.out.println(formattedMult);
        return Double.parseDouble(formattedMult);
    }

}