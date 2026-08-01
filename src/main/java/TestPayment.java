import model.Discount;
import model.Payment;
import service.impl.DiscountServiceImpl;
import service.impl.PaymentServiceImpl;

public class TestPayment {

    public static void main(String[] args) {

        DiscountServiceImpl discountService =
                new DiscountServiceImpl();

        PaymentServiceImpl paymentService =
                new PaymentServiceImpl();


        // =====================================
        // TẠO MÃ GIẢM 10%
        // =====================================

        Discount discount =
                new Discount();

        discount.setCode(
                "WELCOME10"
        );

        discount.setDescription(
                "Giảm 10% cho học phí"
        );

        discount.setDiscountType(
                "PERCENT"
        );

        discount.setDiscountValue(
                10
        );

        discount.setMaxDiscountAmount(
                500000
        );

        discount.setMinimumAmount(
                2000000
        );

        discount.setActive(
                true
        );


        // =====================================
        // TẠO PAYMENT
        // =====================================

        Payment payment =
                new Payment();

        payment.setStudentId(
                1
        );

        payment.setOriginalAmount(
                3000000
        );


        // =====================================
        // ÁP DỤNG GIẢM GIÁ
        // =====================================

        boolean success =
                paymentService.applyDiscount(
                        payment,
                        discount
                );


        if (success) {

            System.out.println(
                    "Áp dụng mã giảm giá thành công!"
            );

            System.out.println(
                    "Học phí gốc: "
                            + payment.getOriginalAmount()
            );

            System.out.println(
                    "Tiền giảm: "
                            + payment.getDiscountAmount()
            );

            System.out.println(
                    "Phải thanh toán: "
                            + payment.getFinalAmount()
            );

        } else {

            System.out.println(
                    "Mã giảm giá không hợp lệ!"
            );
        }
    }
}