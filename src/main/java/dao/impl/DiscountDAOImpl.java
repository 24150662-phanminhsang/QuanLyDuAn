package dao.impl;

import dao.DiscountDAO;
import model.Discount;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAOImpl implements DiscountDAO {

    @Override
    public boolean insert(Discount discount) {

        String sql = """
                INSERT INTO DISCOUNTS
                (
                    code,
                    description,
                    discount_type,
                    discount_value,
                    max_discount_amount,
                    minimum_amount,
                    start_date,
                    end_date,
                    active
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, discount.getCode());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getDiscountType());
            ps.setDouble(4, discount.getDiscountValue());
            ps.setDouble(5, discount.getMaxDiscountAmount());
            ps.setDouble(6, discount.getMinimumAmount());

            if (discount.getStartDate() != null) {
                ps.setTimestamp(
                        7,
                        new Timestamp(discount.getStartDate().getTime())
                );
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (discount.getEndDate() != null) {
                ps.setTimestamp(
                        8,
                        new Timestamp(discount.getEndDate().getTime())
                );
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setBoolean(9, discount.isActive());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean update(Discount discount) {

        String sql = """
                UPDATE DISCOUNTS
                SET
                    code = ?,
                    description = ?,
                    discount_type = ?,
                    discount_value = ?,
                    max_discount_amount = ?,
                    minimum_amount = ?,
                    start_date = ?,
                    end_date = ?,
                    active = ?
                WHERE discount_id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, discount.getCode());
            ps.setString(2, discount.getDescription());
            ps.setString(3, discount.getDiscountType());
            ps.setDouble(4, discount.getDiscountValue());
            ps.setDouble(5, discount.getMaxDiscountAmount());
            ps.setDouble(6, discount.getMinimumAmount());

            if (discount.getStartDate() != null) {
                ps.setTimestamp(
                        7,
                        new Timestamp(discount.getStartDate().getTime())
                );
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (discount.getEndDate() != null) {
                ps.setTimestamp(
                        8,
                        new Timestamp(discount.getEndDate().getTime())
                );
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setBoolean(9, discount.isActive());

            ps.setInt(10, discount.getDiscountId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean delete(int discountId) {

        String sql =
                "DELETE FROM DISCOUNTS WHERE discount_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, discountId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Discount getById(int discountId) {

        String sql =
                "SELECT * FROM DISCOUNTS WHERE discount_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, discountId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapDiscount(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Discount getByCode(String code) {

        String sql =
                "SELECT * FROM DISCOUNTS WHERE code = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapDiscount(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Discount> getAll() {

        List<Discount> list = new ArrayList<>();

        String sql =
                "SELECT * FROM DISCOUNTS ORDER BY discount_id DESC";

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                list.add(mapDiscount(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public List<Discount> getActiveDiscounts() {

        List<Discount> list = new ArrayList<>();

        String sql = """
                SELECT *
                FROM DISCOUNTS
                WHERE active = 1
                  AND (start_date IS NULL OR start_date <= GETDATE())
                  AND (end_date IS NULL OR end_date >= GETDATE())
                ORDER BY discount_id DESC
                """;

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                list.add(mapDiscount(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Discount mapDiscount(ResultSet rs)
            throws SQLException {

        Discount d = new Discount();

        d.setDiscountId(
                rs.getInt("discount_id")
        );

        d.setCode(
                rs.getString("code")
        );

        d.setDescription(
                rs.getString("description")
        );

        d.setDiscountType(
                rs.getString("discount_type")
        );

        d.setDiscountValue(
                rs.getDouble("discount_value")
        );

        d.setMaxDiscountAmount(
                rs.getDouble("max_discount_amount")
        );

        d.setMinimumAmount(
                rs.getDouble("minimum_amount")
        );

        Timestamp startDate =
                rs.getTimestamp("start_date");

        if (startDate != null) {
            d.setStartDate(startDate);
        }

        Timestamp endDate =
                rs.getTimestamp("end_date");

        if (endDate != null) {
            d.setEndDate(endDate);
        }

        d.setActive(
                rs.getBoolean("active")
        );

        return d;
    }
}