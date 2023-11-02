package org.lostfan.ktv.dao.impl.postgre;

import org.lostfan.ktv.dao.DAOException;
import org.lostfan.ktv.dao.RenderedServiceDAO;
import org.lostfan.ktv.domain.RenderedService;
import org.lostfan.ktv.model.FixedServices;
import org.lostfan.ktv.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostGreRenderedServiceDAO extends PostgreBaseDao implements RenderedServiceDAO {

    public List<RenderedService> getAll() {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \"rendered_service\" limit 1000 OFFSET  0");
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public RenderedService get(int id) {
        RenderedService renderedService = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"id\" = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                renderedService = constructEntity(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedService;
    }

    public List<RenderedService> getByDate(LocalDate date) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"date\" = ?");
            preparedStatement.setDate(1, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public List<RenderedService> getBySubscriber(int subscriberAccount) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"subscriber_account\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public List<RenderedService> getByService(int serviceId) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ?");
            preparedStatement.setInt(1, serviceId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public List<RenderedService> getAll(int serviceId, int subscriberAccount) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ? AND \"subscriber_account\" = ?");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setInt(2, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public List<RenderedService> getAll(int serviceId, LocalDate date) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ? AND \"date\" = ?");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public List<RenderedService> getAllForMonth(int serviceId, LocalDate date) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ? AND \"date\" >= ? AND \"date\" < ?");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            preparedStatement.setDate(3, Date.valueOf(date.withDayOfMonth(1).plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public Map<Integer, BigDecimal> getAllRenderedServicesPriceInMonthForSubscriber(LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"rendered_service\" where \"date\" >= ? AND \"date\" < ? group by \"subscriber_account\"");
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

    public Map<Integer, BigDecimal> getAllRenderedServicesPriceInMonthForSubscriberByServiceId(int serviceId, LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"rendered_service\" where \"service_id\" = ? AND \"date\" >= ? AND \"date\" < ? group by \"subscriber_account\"");
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

    public Map<Integer, BigDecimal> getAllRenderedServicesPriceBeforeDate(LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT \"subscriber_account\",sum(\"price\") as \"price\" FROM \"rendered_service\" where \"date\" <= ? group by \"subscriber_account\"");
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

    @Override
    public Map<Integer, RenderedService> getAllRenderedServicesBetweenDates(int serviceId, LocalDate beginDate, LocalDate endDate) {
        Map<Integer, RenderedService> subscribersRenderedServices = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT *  FROM \"rendered_service\" where \"service_id\" = ? AND \"date\" >= ? AND \"date\" <= ? ");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribersRenderedServices.put(rs.getInt("subscriber_account"), constructEntity(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribersRenderedServices;
    }

    public Map<Integer, BigDecimal> getAllRenderedServicesPriceForSubscriberByServiceIdBeforeDate(int serviceId, LocalDate date) {
        Map<Integer, BigDecimal> subscribersPricesInMonth = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT s.account, rs.price from (SELECT subscriber_account,sum(rs.price) as price FROM rendered_service rs where (rs.service_id = ? AND rs.date < ?)\n" +
                    "group by subscriber_account) as rs\n" +
                    "right join subscriber s on rs.subscriber_account = s.account");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setDate(2, Date.valueOf(date.withDayOfMonth(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int account = rs.getInt("account");
                BigDecimal price = rs.getBigDecimal("price");
                subscribersPricesInMonth.put(account, price != null ? price : BigDecimal.ZERO);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribersPricesInMonth;
    }

    public List<RenderedService> getAllForMonth(int serviceId, int subscriberAccount, LocalDate date) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ? AND \"subscriber_account\" = ? AND \"date\" >= ? AND \"date\" < ?");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setInt(2, subscriberAccount);
            preparedStatement.setDate(3, Date.valueOf(date.withDayOfMonth(1)));
            preparedStatement.setDate(4, Date.valueOf(date.withDayOfMonth(1).plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService renderedService = constructEntity(rs);
                renderedServices.add(renderedService);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    public RenderedService getFirstRenderedServiceLessDate(int serviceId, int subscriberAccount, LocalDate date) {
        RenderedService renderedService = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"service_id\" = ? AND \"subscriber_account\" = ? AND \"date\" <= ? ORDER BY \"date\" DESC LIMIT 1");
            preparedStatement.setInt(1, serviceId);
            preparedStatement.setInt(2, subscriberAccount);
            preparedStatement.setDate(3, Date.valueOf(date));;
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                renderedService = constructEntity(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedService;
    }


    public void save(RenderedService renderedService) {
        try {
            PreparedStatement preparedStatement;
            if(renderedService.getId() != null) {
                preparedStatement = getConnection().prepareStatement(
                        "INSERT INTO \"rendered_service\" (\"subscriber_account\", \"service_id\", \"date\",  \"price\", \"id\")" +
                                " VALUES(?, ?, ?, ?, ?); ");
                preparedStatement.setInt(5, renderedService.getId());
            } else {
                preparedStatement = getConnection().prepareStatement(
                        "INSERT INTO \"rendered_service\" (\"subscriber_account\", \"service_id\", \"date\",  \"price\")" +
                                " VALUES(?, ?, ?, ?)");
            }
            preparedStatement.setInt(1, renderedService.getSubscriberAccount());
            preparedStatement.setInt(2, renderedService.getServiceId());
            preparedStatement.setDate(3, Date.valueOf(renderedService.getDate()));
            if (renderedService.getPrice() != null) {
                preparedStatement.setBigDecimal(4, renderedService.getPrice());
            } else {
                preparedStatement.setInt(4, 0);
            }
            preparedStatement.executeUpdate();
            if(renderedService.getId() != null) {
                return;
            }
            Statement statement = getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT lastval()");
            resultSet.next();
            renderedService.setId(resultSet.getInt(1));
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }
    }

    public Map<Integer, List<RenderedService>> getServiceAndSubscriberRenderedServiceMap() {
        Map<Integer, List<RenderedService>> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "select \"subscriber_account\", \"service_id\"," +
                            " sum(\"price\")  from \"rendered_service\"" +
                            " group by \"subscriber_account\", \"service_id\"");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService payment = new RenderedService();
                payment.setServiceId(rs.getInt("service_id"));
                payment.setSubscriberAccount(rs.getInt("subscriber_account"));
                payment.setPrice(rs.getBigDecimal("sum"));
                List<RenderedService> payments = hashMap.get(rs.getInt("subscriber_account"));
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

    public Map<Integer, List<RenderedService>> getServiceAndSubscriberRenderedServiceMap(LocalDate date) {
        Map<Integer, List<RenderedService>> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "select \"subscriber_account\", \"service_id\"," +
                            " sum(\"price\")  from \"rendered_service\"" +
                            " where \"date\" <= ? group by \"subscriber_account\", \"service_id\"");
            preparedStatement.setDate(1, Date.valueOf(date));;
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                RenderedService payment = new RenderedService();
                payment.setServiceId(rs.getInt("service_id"));
                payment.setSubscriberAccount(rs.getInt("subscriber_account"));
                payment.setPrice(rs.getBigDecimal("sum"));
                List<RenderedService> payments = hashMap.get(rs.getInt("subscriber_account"));
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

    public void update(RenderedService renderedService) {
        if(get(renderedService.getId()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"rendered_service\" set \"subscriber_account\" = ?, \"service_id\" = ?, \"date\" = ?, \"price\" = ? where \"id\" = ?");
                preparedStatement.setInt(1, renderedService.getSubscriberAccount());
                preparedStatement.setInt(2, renderedService.getServiceId());
                preparedStatement.setDate(3, Date.valueOf(renderedService.getDate()));
                preparedStatement.setBigDecimal(4, renderedService.getPrice());
                preparedStatement.setInt(5, renderedService.getId());
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
                        "DELETE FROM  \"rendered_service\" where \"id\" = ?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
        }
    }

    public List<RenderedService> getByMonth(LocalDate date) {
        List<RenderedService> renderedServices = new ArrayList<>();
        LocalDate newDate = date.withDayOfMonth(1);
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"date\" >= ? AND \"date\" < ?  order by \"date\" ");
            preparedStatement.setDate(1, Date.valueOf(newDate));
            preparedStatement.setDate(2, Date.valueOf(newDate.plusMonths(1)));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                renderedServices.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    @Override
    public List<RenderedService> getSubscriptionFeesByPeriodDate(LocalDate beginDate, LocalDate endDate) {
        List<RenderedService> payments = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where \"date\" >= ? and \"date\" <= ? AND service_id = ? AND \"date\" != '2016-07-02'");
            preparedStatement.setDate(1, Date.valueOf(beginDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            preparedStatement.setInt(3, FixedServices.SUBSCRIPTION_FEE.getId());
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

    @Override
    public void deleteSubscriptionFeesByPeriodDate(LocalDate beginDate, LocalDate endDate) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "DELETE FROM  \"rendered_service\" where \"date\" >= ? and \"date\" <= ? AND service_id = ?");
            preparedStatement.setDate(1, Date.valueOf(beginDate));
            preparedStatement.setDate(2, Date.valueOf(endDate));
            preparedStatement.setInt(3, FixedServices.SUBSCRIPTION_FEE.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }
    }

    @Override
    public List<RenderedService> getAllContainsInName(String str) {
        List<RenderedService> renderedServices = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"rendered_service\" where LOWER(CAST(\"id\" AS varchar(10))) LIKE ? ORDER BY \"id\" LIMIT 20");
            preparedStatement.setString(1, (str + "%").toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                renderedServices.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return renderedServices;
    }

    private RenderedService constructEntity(ResultSet rs) throws SQLException{
        RenderedService renderedService = new RenderedService();
        renderedService.setId(rs.getInt("id"));
        renderedService.setServiceId(rs.getInt("service_id"));
        renderedService.setSubscriberAccount(rs.getInt("subscriber_account"));
        renderedService.setPrice(rs.getBigDecimal("price"));
        renderedService.setDate(rs.getDate("date").toLocalDate());
        return renderedService;
    }
}
