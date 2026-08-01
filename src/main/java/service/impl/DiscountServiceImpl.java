package service.impl;

import dao.DiscountDAO;
import dao.impl.DiscountDAOImpl;
import model.Discount;
import service.DiscountService;

import java.util.Date;
import java.util.List;

public class DiscountServiceImpl
        implements DiscountService {

    private final DiscountDAO discountDAO;

    public DiscountServiceImpl() {

        this.discountDAO =
                new DiscountDAOImpl();
    }


    @Override
    public boolean create(
            Discount discount
    ) {

        if (!validateDiscount(discount)) {
            return false;
        }

        return discountDAO.insert(
                discount
        );
    }


    @Override
    public boolean update(
            Discount discount
    ) {

        if (!validateDiscount(discount)) {
            return false;
        }

        return discountDAO.update(
                discount
        );
    }


    @Override
    public boolean delete(
            int discountId
    ) {

        return discountDAO.delete(
                discountId
        );
    }


    @Override
    public Discount findById(
            int discountId
    ) {

        return discountDAO.getById(
                discountId
        );
    }


    @Override
    public Discount findByCode(
            String code
    ) {

        if (code == null
                || code.trim().isEmpty()) {

            return null;
        }

        return discountDAO.getByCode(
                code.trim()
        );
    }


    @Override
    public List<Discount> getAll() {

        return discountDAO.getAll();
    }


    @Override
    public List<Discount>
    getActiveDiscounts() {

        return discountDAO
                .getActiveDiscounts();
    }


    @Override
    public boolean isValid(
            Discount discount,
            double originalAmount
    ) {

        if (discount == null) {
            return false;
        }

        if (!discount.isActive()) {
            return false;
        }

        if (originalAmount < 0) {
            return false;
        }

        // Kiểm tra giá trị đơn hàng tối thiểu
        if (originalAmount
                < discount.getMinimumAmount()) {

            return false;
        }

        Date now =
                new Date();

        // Kiểm tra ngày bắt đầu
        if (discount.getStartDate() != null
                && now.before(
                discount.getStartDate()
        )) {

            return false;
        }

        // Kiểm tra ngày kết thúc
        if (discount.getEndDate() != null
                && now.after(
                discount.getEndDate()
        )) {

            return false;
        }

        // Kiểm tra loại giảm giá
        if (discount.getDiscountType()
                == null) {

            return false;
        }

        String type =
                discount
                        .getDiscountType()
                        .toUpperCase();

        if (!type.equals("PERCENT")
                && !type.equals("FIXED")) {

            return false;
        }

        // Giá trị giảm phải > 0
        if (discount.getDiscountValue()
                <= 0) {

            return false;
        }

        // Nếu phần trăm thì không được > 100%
        if (type.equals("PERCENT")
                && discount.getDiscountValue()
                > 100) {

            return false;
        }

        return true;
    }


    @Override
    public double calculateDiscount(
            Discount discount,
            double originalAmount
    ) {

        if (!isValid(
                discount,
                originalAmount
        )) {

            return 0;
        }

        String type =
                discount
                        .getDiscountType()
                        .toUpperCase();

        double discountAmount;

        if (type.equals("PERCENT")) {

            discountAmount =
                    originalAmount
                            * discount.getDiscountValue()
                            / 100.0;

            // Giới hạn số tiền giảm tối đa
            if (discount.getMaxDiscountAmount()
                    > 0) {

                discountAmount =
                        Math.min(
                                discountAmount,
                                discount
                                        .getMaxDiscountAmount()
                        );
            }

        } else {

            // FIXED
            discountAmount =
                    discount.getDiscountValue();
        }

        // Không cho giảm quá giá trị hóa đơn
        discountAmount =
                Math.min(
                        discountAmount,
                        originalAmount
                );

        return Math.max(
                0,
                discountAmount
        );
    }


    private boolean validateDiscount(
            Discount discount
    ) {

        if (discount == null) {
            return false;
        }

        if (discount.getCode() == null
                || discount.getCode()
                .trim()
                .isEmpty()) {

            return false;
        }

        if (discount.getDiscountType()
                == null) {

            return false;
        }

        String type =
                discount
                        .getDiscountType()
                        .toUpperCase();

        if (!type.equals("PERCENT")
                && !type.equals("FIXED")) {

            return false;
        }

        if (discount.getDiscountValue()
                <= 0) {

            return false;
        }

        if (type.equals("PERCENT")
                && discount.getDiscountValue()
                > 100) {

            return false;
        }

        if (discount.getMinimumAmount()
                < 0) {

            return false;
        }

        if (discount.getMaxDiscountAmount()
                < 0) {

            return false;
        }

        if (discount.getStartDate() != null
                && discount.getEndDate() != null
                && discount.getEndDate()
                .before(
                        discount.getStartDate()
                )) {

            return false;
        }

        return true;
    }
}