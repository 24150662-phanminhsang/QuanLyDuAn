package controller;

import model.Discount;
import service.DiscountService;
import service.impl.DiscountServiceImpl;

import java.util.Collections;
import java.util.List;

public class DiscountController {

    private final DiscountService discountService;

    public DiscountController() {
        this(new DiscountServiceImpl());
    }

    public DiscountController(DiscountService discountService) {
        if (discountService == null) {
            throw new IllegalArgumentException("DiscountService không được null.");
        }
        this.discountService = discountService;
    }

    public boolean create(Discount discount) {
        validate(discount, false);
        if (discountService.findByCode(discount.getCode()) != null) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại.");
        }
        return discountService.create(discount);
    }

    public boolean update(Discount discount) {
        validate(discount, true);
        Discount sameCode = discountService.findByCode(discount.getCode());
        if (sameCode != null && sameCode.getDiscountId() != discount.getDiscountId()) {
            throw new IllegalArgumentException("Mã giảm giá đã được sử dụng.");
        }
        return discountService.update(discount);
    }

    public boolean delete(int discountId) {
        if (discountId <= 0) {
            throw new IllegalArgumentException("ID mã giảm giá không hợp lệ.");
        }
        return discountService.delete(discountId);
    }

    public Discount findById(int discountId) {
        if (discountId <= 0) return null;
        return discountService.findById(discountId);
    }

    public Discount findByCode(String code) {
        if (code == null || code.isBlank()) return null;
        return discountService.findByCode(code.trim());
    }

    public List<Discount> getAll() {
        List<Discount> list = discountService.getAll();
        return list == null ? Collections.emptyList() : list;
    }

    public List<Discount> getActiveDiscounts() {
        List<Discount> list = discountService.getActiveDiscounts();
        return list == null ? Collections.emptyList() : list;
    }

    private void validate(Discount discount, boolean requireId) {
        if (discount == null) throw new IllegalArgumentException("Thông tin mã giảm giá không được null.");
        if (requireId && discount.getDiscountId() <= 0) throw new IllegalArgumentException("ID mã giảm giá không hợp lệ.");
        if (discount.getCode() == null || discount.getCode().isBlank()) throw new IllegalArgumentException("Mã giảm giá không được để trống.");
        if (discount.getDiscountType() == null || discount.getDiscountType().isBlank()) throw new IllegalArgumentException("Loại giảm giá không được để trống.");
        String type = discount.getDiscountType().trim().toUpperCase();
        if (!"PERCENT".equals(type) && !"FIXED".equals(type)) throw new IllegalArgumentException("Loại giảm giá phải là PERCENT hoặc FIXED.");
        if (discount.getDiscountValue() <= 0) throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0.");
        if ("PERCENT".equals(type) && discount.getDiscountValue() > 100) throw new IllegalArgumentException("Phần trăm giảm không được lớn hơn 100.");
        if (discount.getMinimumAmount() < 0 || discount.getMaxDiscountAmount() < 0) throw new IllegalArgumentException("Số tiền tối thiểu và giảm tối đa không được âm.");
        if (discount.getStartDate() != null && discount.getEndDate() != null && discount.getEndDate().before(discount.getStartDate())) throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
    }
}
