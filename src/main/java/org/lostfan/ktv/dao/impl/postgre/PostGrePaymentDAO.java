package org.lostfan.ktv.dao.impl.postgre;

import org.lostfan.ktv.dao.PaymentDAO;
import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.domain.PaymentType;
import org.lostfan.ktv.utils.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostGrePaymentDAO implements PaymentDAO {

    private Connection getConnection() {
        return ConnectionManager.getManager().getConnection();
    }

    public List<Payment> getAll() {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\"  order by \"date\" limit 100");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payments;
    }

    public List<Payment> getByMonth(LocalDate date) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"date\" >= ? AND \"date\" < ?  order by \"date\" ");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setDate(2, Date.valueOf(date.plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payments;
    }

    public Payment get(int id) {
        Payment payment = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"id\" = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payment = constructEntity(rs);

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payment;
    }

    public List<Payment> getPaymentsByDate(LocalDate date) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"date\" = ?");
            preparedStatement.setDate(1, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payments.add(constructEntity(rs));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payments;
    }

    public List<Payment> getPaymentsBySubscriberId(int paymentId) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"subscriber_account\" = ?");
            preparedStatement.setInt(1, paymentId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payments.add(constructEntity(rs));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payments;
    }

    public void save(Payment payment) {
        try {
            PreparedStatement preparedStatement;
            if(payment.getId() != null) {
                preparedStatement = getConnection().prepareStatement(
                        "INSERT INTO \"payment\" (\"subscriber_account\", \"service_id\", \"rendered_service_id\", \"price\", \"date\", \"bank_file_name\", \"id\")" +
                                " VALUES(?, ?, ?, ?, ?, ?, ?); " +
//                                "ALTER SEQUENCE serial_payment RESTART WITH ?;");
                                "");
                preparedStatement.setInt(7, payment.getId());
//                preparedStatement.setInt(7, payment.getId() + 1);
            } else {
                preparedStatement = getConnection().prepareStatement(
                        "INSERT INTO \"payment\" (\"subscriber_account\", \"service_id\", \"rendered_service_id\", \"price\", \"date\", \"bank_file_name\")" +
                                " VALUES(?, ?, ?, ?, ?, ?)");
            }
            preparedStatement.setInt(1, payment.getSubscriberAccount());
            preparedStatement.setInt(2, payment.getServicePaymentId());
            if(payment.getRenderedServicePaymentId() != null) {
                preparedStatement.setInt(3, payment.getRenderedServicePaymentId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }
            preparedStatement.setInt(4, payment.getPrice());
            preparedStatement.setDate(5, Date.valueOf(payment.getDate()));
            preparedStatement.setString(6, payment.getBankFileName());
            preparedStatement.executeUpdate();
            if(payment.getId() != null) {
                return;
            }
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT lastval()");
            resultSet.next();
            payment.setId(resultSet.getInt(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(Payment payment) {
        if(get(payment.getId()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"payment\" set \"subscriber_account\" = ?, \"service_id\" = ?, \"rendered_service_id\" = ?, \"payment_type_id\" = ?, \"price\" = ?, \"date\" = ?, \"bank_file_name\" = ? where \"id\" = ?");
                preparedStatement.setInt(1, payment.getSubscriberAccount());
                preparedStatement.setInt(2, payment.getServicePaymentId());
                if( payment.getRenderedServicePaymentId() != null) {
                    preparedStatement.setInt(3, payment.getRenderedServicePaymentId());
                }
                if( payment.getPaymentTypeId() != null) {
                    preparedStatement.setInt(4, payment.getPaymentTypeId());
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }
                preparedStatement.setInt(5, payment.getPrice());
                preparedStatement.setDate(6, Date.valueOf(payment.getDate()));
                preparedStatement.setString(7, payment.getBankFileName());
                preparedStatement.setInt(8, payment.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Update nonexistent element");
        }
    }

    public void delete(int id) {
        if(get(id) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"payment\" where \"id\" = ?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Delete nonexistent element");
        }
    }

    public List<Payment> getPaymentsByBankFileName(String bankFileName) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"bank_file_name\" = ?");
            preparedStatement.setString(1, bankFileName);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payments.add(constructEntity(rs));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return payments;
    }

    public Map<Integer, Integer> getAllPaymentsPriceInMonthForSubscriberByServiceId(int serviceId, LocalDate date) {
        Map<Integer, Integer> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"service_id\" = ? AND \"date\" >= ? AND \"date\" < ? group by \"subscriber_account\"");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            preparedStatement.setDate(3, Date.valueOf(date.withDayOfMonth(1).plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getInt("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscribersPricesInMonth;
    }

    public Map<Integer, Integer> getAllPaymentsPriceForSubscriberByServiceIdBeforeDate(int serviceId, LocalDate date) {
        Map<Integer, Integer> subscribersPricesInMonth = new HashMap<>();
        Long sum=0L;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"service_id\" = ? AND \"date\" < ? group by \"subscriber_account\"");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                sum+=rs.getLong("price");
//                System.out.println(rs.getInt("subscriber_account") + "   " + rs.getInt("price"));
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getInt("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
//        System.out.println("sum = " + sum);
        return subscribersPricesInMonth;
    }

    @Override
    public Map<Integer, Payment> getPaymentsForNotClosedRenderedServicesBySubscriberIdAndServiceId(Integer subscriberAccount, Integer serviceId) {
        Map<Integer, Payment> hashMap = new HashMap<>();

        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "select * from \"rendered_service\" \n" +
                            "LEFT JOIN (\n" +
                            "select \"payment\".\"rendered_service_id\" , sum(\"payment\".\"price\") as \"payment_price\" from payment group by \"payment\".\"rendered_service_id\") as payment\n" +
                            "ON (\"payment\".\"rendered_service_id\" = \"rendered_service\".\"id\")\n" +
                            "where \"rendered_service\".\"service_id\" = ? AND\n" +
                            "\"rendered_service\".\"subscriber_account\" = ? AND (\"payment\".\"payment_price\" < \"rendered_service\".\"price\" OR \"payment\".\"payment_price\" is null)" +
                            " order by \"rendered_service\".\"date\"");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setInt(2, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setServicePaymentId(rs.getInt("service_id"));
                payment.setRenderedServicePaymentId(rs.getInt("id"));
                payment.setSubscriberAccount(rs.getInt("subscriber_account"));
                payment.setPrice(rs.getInt("price") - rs.getInt("payment_price"));
                hashMap.put(rs.getInt("id"), payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public List<PaymentType> getAllPaymentTypes() {
        List<PaymentType> paymentTypes = new ArrayList<>();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \"payment_type\"");
            while (rs.next()) {
                PaymentType paymentType = new PaymentType();
                paymentType.setId(rs.getInt("id"));
                paymentType.setName(rs.getString("name"));
                paymentTypes.add(paymentType);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return paymentTypes;
    }

    public PaymentType getPaymentType(int id) {
        PaymentType paymentType = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment_type\" where \"id\" = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                paymentType = new PaymentType();
                paymentType.setId(rs.getInt("id"));
                paymentType.setName(rs.getString("name"));

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return paymentType;
    }

    public void savePaymentType(PaymentType paymentType) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "INSERT INTO \"payment_type\" (\"name\") VALUES(?)");
            preparedStatement.setString(1, paymentType.getName());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updatePaymentType(PaymentType paymentType) {
        if(getPaymentType(paymentType.getId()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"payment_type\" set \"name\" = ? where \"id\" = ?");
                preparedStatement.setString(1, paymentType.getName());
                preparedStatement.setInt(2, paymentType.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Update nonexistent element");
        }
    }

    public void deletePaymentType(int paymentTypeId) {
        if(getPaymentType(paymentTypeId) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"payment_type\" where \"id\" = ?");
                preparedStatement.setInt(1, paymentTypeId);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Delete nonexistent element");
        }
    }

    @Override
    public List<Payment> getAllContainsInName(String str) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where LOWER(\"id\" AS varchar(10)) LIKE ?");
            preparedStatement.setString(1, ("%" + str + "%").toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return payments;
    }

    private Payment constructEntity(ResultSet rs) throws SQLException{
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setPrice(rs.getInt("price"));
        payment.setDate(rs.getDate("date").toLocalDate());
        if(rs.getObject("payment_type_id") != null) {
            payment.setPaymentTypeId(rs.getInt("payment_type_id"));
        }
        payment.setSubscriberAccount(rs.getInt("subscriber_account"));
        if(rs.getObject("rendered_service_id") != null) {
            payment.setRenderedServicePaymentId(rs.getInt("rendered_service_id"));
        }
        payment.setServicePaymentId(rs.getInt("service_id"));
        payment.setBankFileName(rs.getString("bank_file_name"));
        return payment;
    }
}
