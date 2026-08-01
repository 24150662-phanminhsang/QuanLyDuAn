import model.Discount;
import service.DiscountService;
import service.impl.DiscountServiceImpl; // Bổ sung import

import java.util.Calendar;
import java.util.Date;

public class TestDiscountService {

    public static void main(String[] args) {

        Discount discount = new Discount();
        discount.setCode("WELCOME10");
        discount.setDescription("Giảm 10% tối đa 500.000");
        discount.setDiscountType("PERCENT");
        discount.setDiscountValue(10);
        discount.setMaxDiscountAmount(500000);
        discount.setMinimumAmount(1000000);
        discount.setStartDate(new Date(2026 - 1900, Calendar.JANUARY, 1));
        discount.setEndDate(new Date(2026 - 1900, Calendar.DECEMBER, 31));
        discount.setActive(true);

        // 1. ĐÃ SỬA: Khởi tạo bằng DiscountServiceImpl
        DiscountService service = new DiscountServiceImpl();

        double originalAmount = 3000000;

        // Kiểm tra tính hợp lệ
        boolean valid = service.isValid(discount, originalAmount);
        System.out.println("Mã hợp lệ: " + valid);

        // Tính tiền giảm
        double discountAmount = service.calculateDiscount(discount, originalAmount);
        System.out.println("Tiền giảm: " + String.format("%,.0f ₫", discountAmount));

        // 2. ĐÃ SỬA: Tính thành tiền trực tiếp (không gọi hàm không tồn tại)
        double finalAmount = Math.max(0, originalAmount - discountAmount);
        System.out.println("Tiền phải trả: " + String.format("%,.0f ₫", finalAmount));

        // 3. ĐÃ SỬA: In thông báo dựa trên kết quả kiểm tra
        if (valid) {
            System.out.println("Thông báo: Mã giảm giá áp dụng thành công!");
        } else {
            System.out.println("Thông báo: Mã giảm giá không đủ điều kiện sử dụng!");
        }
    }
}