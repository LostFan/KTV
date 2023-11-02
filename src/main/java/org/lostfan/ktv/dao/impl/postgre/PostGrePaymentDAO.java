package org.lostfan.ktv.dao.impl.postgre;

import org.lostfan.ktv.dao.DAOException;
import org.lostfan.ktv.dao.PaymentDAO;
import org.lostfan.ktv.domain.Payment;
import org.lostfan.ktv.domain.PaymentType;
import org.lostfan.ktv.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostGrePaymentDAO extends PostgreBaseDao implements PaymentDAO {

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
            throw new DAOException();
        }

        return payments;
    }

    public List<Payment> getByMonth(LocalDate date) {
        List<Payment> payments = new ArrayList<>();
        LocalDate newDate = date.withDayOfMonth(1);
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"date\" >= ? AND \"date\" < ?  order by \"date\" ");
            preparedStatement.setDate(1, Date.valueOf(newDate));
            preparedStatement.setDate(2, Date.valueOf(newDate.plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
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
            throw new DAOException();
        }

        return payment;
    }

    public List<Payment> getByDate(LocalDate date) {
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
            throw new DAOException();
        }

        return payments;
    }

    public List<Payment> getBySubscriber(int subscriberAccount) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"subscriber_account\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return payments;
    }

    public void save(Payment payment) {
        try {
            PreparedStatement preparedStatement;
            if(payment.getId() != null) {
                preparedStatement = getConnection().prepareStatement(
                        "INSERT INTO \"payment\" (\"subscriber_account\", \"service_id\", \"rendered_service_id\", \"price\", \"date\", \"bank_file_name\", \"id\")" +
                                " VALUES(?, ?, ?, ?, ?, ?, ?) ");
                preparedStatement.setInt(7, payment.getId());
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
            preparedStatement.setBigDecimal(4, payment.getPrice());
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
            throw new DAOException();
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
                } else {
                    preparedStatement.setNull(3, Types.INTEGER);
                }
                if( payment.getPaymentTypeId() != null) {
                    preparedStatement.setInt(4, payment.getPaymentTypeId());
                } else {
                    preparedStatement.setNull(4, Types.INTEGER);
                }
                preparedStatement.setBigDecimal(5, payment.getPrice());
                preparedStatement.setDate(6, Date.valueOf(payment.getDate()));
                preparedStatement.setString(7, payment.getBankFileName());
                preparedStatement.setInt(8, payment.getId());
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
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
                throw new DAOException();
            }
        }
    }

    public void deleteByDate(LocalDate date) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "DELETE FROM  \"payment\" where \"date\" = ?");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

    }

    public List<Payment> getByBankFileName(String bankFileName) {
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
            throw new DAOException();
        }
        return payments;
    }

    public List<Payment> getList(Integer subscriberId, LocalDate date, String bankFileName) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"subscriber_account\" = ? AND \"date\" = ? AND \"bank_file_name\" = ?");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(date));
            if(bankFileName == null) {
                preparedStatement.setNull(3, Types.VARCHAR);
            } else {
                preparedStatement.setString(3, bankFileName);
            }
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }
        return payments;
    }

    public Map<Integer, BigDecimal> getAllPaymentsPriceInMonthForSubscriber(LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"date\" >= ? AND \"date\" < ? group by \"subscriber_account\"");
            preparedStatement.setDate(1, Date.valueOf(date.withDayOfMonth(1)));
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1).plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getBigDecimal("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribersPricesInMonth;
    }

    public Map<Integer, BigDecimal> getAllPaymentsPriceInMonthForSubscriberByServiceId(int serviceId, LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"service_id\" = ? AND \"date\" >= ? AND \"date\" < ? group by \"subscriber_account\"");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            preparedStatement.setDate(3, Date.valueOf(date.withDayOfMonth(1).plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getBigDecimal("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribersPricesInMonth;
    }

    @Override
    public Map<Integer, BigDecimal> getAllPaymentsPriceBeforeDate(LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"date\" <= ? group by \"subscriber_account\"");
            preparedStatement.setDate(1, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getBigDecimal("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribersPricesInMonth;
    }

    public Map<Integer, BigDecimal> getAllPaymentsPriceForSubscriberToDate(int serviceId, LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"payment\" where \"service_id\" = ? AND \"date\" < ? group by \"subscriber_account\"");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersPricesInMonth.put(rs.getInt("subscriber_account"), rs.getBigDecimal("price"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }
        return subscribersPricesInMonth;
    }

    @Override
    public Map<Integer, Payment> getForNotClosedRenderedServices(Integer subscriberAccount, Integer serviceId) {
        Map<Integer, Payment> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "select * from \"rendered_service\" \n" +
                            "LEFT JOIN (\n" +
                            "select \"payment\".\"rendered_service_id\" , sum(\"payment\".\"price\") as \"payment_price\" from payment group by \"payment\".\"rendered_service_id\") as payment\n" +
                            "ON (\"payment\".\"rendered_service_id\" = \"rendered_service\".\"id\")\n" +
                            "where \"rendered_service\".\"service_id\" = ? AND \"rendered_service\".\"price\" != 0 AND \n" +
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
                payment.setPrice(getBigDecimal(rs, "price").add(getBigDecimal(rs, "payment_price").negate()));
                hashMap.put(rs.getInt("id"), payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException();
        }
        return hashMap;
    }

    @Override
    public List<Payment> getPaymentsByPeriodDate(LocalDate beginDate, LocalDate endDate) {
        List<Payment> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"payment\" where \"date\" >= ? and \"date\" <= ?");
            preparedStatement.setDate(1, Date.valueOf(beginDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                payments.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return payments;
    }

    public Map<Integer, List<Payment>> getServiceAndSubscriberPaymentMap() {
        Map<Integer, List<Payment>> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "select \"subscriber_account\", \"service_id\", sum(\"price\")" +
                            " from \"payment\" group by \"subscriber_account\", \"service_id\"");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setServicePaymentId(rs.getInt("service_id"));
                payment.setSubscriberAccount(rs.getInt("subscriber_account"));
                payment.setPrice(rs.getBigDecimal("sum"));
                List<Payment> payments = hashMap.get(rs.getInt("subscriber_account"));
                if (payments == null) {
                    payments = new ArrayList<>();
                }
                payments.add(payment);
                hashMap.put(rs.getInt("subscriber_account"), payments);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException();
        }
        return hashMap;
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
            throw new DAOException();
        }

        return payments;
    }

    private Payment constructEntity(ResultSet rs) throws SQLException{
        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setPrice(rs.getBigDecimal("price"));
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

    private BigDecimal getBigDecimal(ResultSet rs, String field) throws SQLException {
        return rs.getBigDecimal(field) != null ? rs.getBigDecimal(field) : BigDecimal.ZERO;
    }
}
