package service;

import model.Discount;

import java.util.List;

public interface DiscountService {

    boolean create(Discount discount);

    boolean update(Discount discount);

    boolean delete(int discountId);

    Discount findById(int discountId);

    Discount findByCode(String code);

    List<Discount> getAll();

    List<Discount> getActiveDiscounts();

    double calculateDiscount(
            Discount discount,
            double originalAmount
    );

    boolean isValid(
            Discount discount,
            double originalAmount
    );
}