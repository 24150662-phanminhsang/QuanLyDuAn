package dao;

import model.Discount;

import java.util.List;

public interface DiscountDAO {

    // Thêm mã giảm giá
    boolean insert(Discount discount);

    // Cập nhật mã giảm giá
    boolean update(Discount discount);

    // Xóa mã giảm giá
    boolean delete(int discountId);

    // Tìm mã giảm giá theo ID
    Discount getById(int discountId);

    // Tìm mã giảm giá theo CODE
    Discount getByCode(String code);

    // Lấy tất cả mã giảm giá
    List<Discount> getAll();

    // Lấy các mã giảm giá đang hoạt động
    List<Discount> getActiveDiscounts();
}