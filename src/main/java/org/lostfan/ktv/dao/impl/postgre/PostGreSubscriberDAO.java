package org.lostfan.ktv.dao.impl.postgre;

import org.lostfan.ktv.dao.DAOException;
import org.lostfan.ktv.dao.SubscriberDAO;
import org.lostfan.ktv.domain.Subscriber;
import org.lostfan.ktv.domain.SubscriberSession;
import org.lostfan.ktv.domain.SubscriberTariff;
import org.lostfan.ktv.model.searcher.SubscriberSearchCriteria;
import org.lostfan.ktv.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostGreSubscriberDAO extends PostgreBaseDao implements SubscriberDAO {

    public List<Subscriber> getAll() {
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \"subscriber\" order by \"account\"");
            while (rs.next()) {
                Subscriber subscriber = constructEntity(rs);
                subscribers.add(subscriber);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscribers;
    }

    public Subscriber get(int id) {
        Subscriber subscriber = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber\" where \"account\" = ?");
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriber = constructEntity(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscriber;
    }

    public void save(Subscriber subscriber) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "INSERT INTO \"subscriber\" (\"account\", \"name\", \"street_id\", \"balance\"," +
                            " \"connected\", \"house\", \"building\", \"flat\", \"index\", \"phone\"," +
                            " \"passport_number\", \"passport_authority\", \"passport_date\" , \"date_of_contract\", \"information\") " +
                            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setInt(1, subscriber.getAccount());
            preparedStatement.setString(2, subscriber.getName());
            if(subscriber.getStreetId() != null) {
                preparedStatement.setInt(3, subscriber.getStreetId());
            }
            if(subscriber.getBalance() != null) {
                preparedStatement.setInt(4, subscriber.getBalance());
            } else {
                preparedStatement.setInt(4, 0);
            }
            if(subscriber.isConnected() != null) {
                preparedStatement.setBoolean(5, subscriber.isConnected());
            } else {
                preparedStatement.setBoolean(5, false);
            }
            if(subscriber.getHouse() != null) {
                preparedStatement.setInt(6, subscriber.getHouse());
            } else {
                preparedStatement.setNull(6, Types.INTEGER);
            }
            preparedStatement.setString(7, subscriber.getBuilding());

            preparedStatement.setString(8, subscriber.getFlat());

            preparedStatement.setString(9, subscriber.getIndex());

            preparedStatement.setString(10, subscriber.getPhone());

            preparedStatement.setString(11, subscriber.getPassportNumber());

            preparedStatement.setString(12, subscriber.getPassportAuthority());

            if (subscriber.getPassportDate() != null) {
                preparedStatement.setDate(13, Date.valueOf(subscriber.getPassportDate()));
            } else {
                preparedStatement.setDate(13, null);
            }
            if (subscriber.getContractDate() != null) {
                preparedStatement.setDate(14, Date.valueOf(subscriber.getContractDate()));
            } else {
                preparedStatement.setDate(14, null);
            }

            preparedStatement.setString(15, subscriber.getInformation());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void update(Subscriber subscriber) {
        if(get(subscriber.getAccount()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"subscriber\" set " +
                                "\"name\" = ?, " +
                                "\"street_id\" = ?, " +
                                "\"balance\" = ?, " +
                                "\"connected\" = ?, " +
                                "\"house\" = ?, " +
                                "\"building\" = ?, " +
                                "\"flat\" = ?, " +
                                "\"index\" = ?, " +
                                "\"phone\" = ?, " +
                                "\"passport_number\" = ?, " +
                                "\"passport_authority\" = ?, " +
                                "\"passport_date\" = ?, " +
                                "\"date_of_contract\" = ?," +
                                "\"information\" = ?" +
                                "where \"account\" = ?");
                preparedStatement.setString(1, subscriber.getName());
                if(subscriber.getStreetId() != null) {
                    preparedStatement.setInt(2, subscriber.getStreetId());
                }
                if(subscriber.getBalance() != null) {
                    preparedStatement.setInt(3, subscriber.getBalance());
                } else {
                    preparedStatement.setInt(3, 0);
                }
                if(subscriber.isConnected() != null) {
                    preparedStatement.setBoolean(4, subscriber.isConnected());
                } else {
                    preparedStatement.setBoolean(5, false);
                }
                if(subscriber.getBalance() != null) {
                    preparedStatement.setInt(5, subscriber.getHouse());
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }

                preparedStatement.setString(6, subscriber.getBuilding());

                preparedStatement.setString(7, subscriber.getFlat());

                preparedStatement.setString(8, subscriber.getIndex());

                preparedStatement.setString(9, subscriber.getPhone());

                preparedStatement.setString(10, subscriber.getPassportNumber());

                preparedStatement.setString(11, subscriber.getPassportAuthority());

                if (subscriber.getPassportDate() != null) {
                    preparedStatement.setDate(12, Date.valueOf(subscriber.getPassportDate()));
                } else {
                    preparedStatement.setDate(12, null);
                }
                if (subscriber.getContractDate() != null) {
                    preparedStatement.setDate(13, Date.valueOf(subscriber.getContractDate()));
                } else {
                    preparedStatement.setDate(13, null);
                }

                preparedStatement.setString(14, subscriber.getInformation());

                preparedStatement.setInt(15, subscriber.getAccount());
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Update nonexistent element");
        }
    }

    public void delete(int subscriberAccount) {
        if(get(subscriberAccount) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"subscriber\" where \"account\" = ?");
                preparedStatement.setInt(1, subscriberAccount);
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("Delete nonexistent element");
        }
    }

    public Double getBalanceByDate(int subscriberId, LocalDate date) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Integer getTariffIdByDate(int subscriberAccount, LocalDate date) {
        if(get(subscriberAccount) != null) {
            Integer tariffId = null;
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_tariff\" where \"subscriber_account\" = ? AND \"connection_date\" <= ?" +
                        " AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?) ORDER BY \"connection_date\" DESC LIMIT 1");
                preparedStatement.setInt(1, subscriberAccount);
                preparedStatement.setDate(2, Date.valueOf(date));
                preparedStatement.setDate(3, Date.valueOf(date));
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    tariffId =  rs.getInt("tariff_id");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return tariffId;
        } else {
            throw new IllegalArgumentException("Subscriber not exist");
        }
    }

    public Integer getSessionIdByDate(int subscriberAccount, LocalDate date) {
        if(get(subscriberAccount) != null) {
            Integer sessionId = null;
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_session\" where \"subscriber_account\" = ? AND \"connection_date\" <= ?" +
                        " AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?) ORDER BY \"connection_date\" DESC LIMIT 1");
                preparedStatement.setInt(1, subscriberAccount);
                preparedStatement.setDate(2, Date.valueOf(date));
                preparedStatement.setDate(3, Date.valueOf(date));
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    sessionId =  rs.getInt("id");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return sessionId;
        } else {
            throw new IllegalArgumentException("Subscriber not exist");
        }
    }

    public List<SubscriberSession> getSubscriberSessions(int subscriberAccount) {
        List<SubscriberSession> subscriberSessions = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_session\" where \"subscriber_account\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                SubscriberSession subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
                if (rs.getObject("disconnection_reason_id") != null) {
                    subscriberSession.setDisconnectionReasonId(rs.getInt("disconnection_reason_id"));
                }
                subscriberSessions.add(subscriberSession);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscriberSessions;
    }

    public List<SubscriberTariff> getSubscriberTariffs(int subscriberAccount) {
        List<SubscriberTariff> subscriberTariffs = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_tariff\" where \"subscriber_account\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                SubscriberTariff subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
                subscriberTariffs.add(subscriberTariff);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscriberTariffs;
    }

    public SubscriberSession getSubscriberSessionByConnectionDate(Integer subscriberAccount, LocalDate connectionDate) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_session\" where \"subscriber_account\" = ? AND \"connection_date\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            preparedStatement.setDate(2, Date.valueOf(connectionDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if (rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return subscriberSession;
    }

    public SubscriberSession getSubscriberSessionByDisconnectionDate(Integer subscriberAccount, LocalDate disconnectionDate) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_session\" where \"subscriber_account\" = ? AND \"disconnection_date\" = ?");
            preparedStatement.setInt(1, subscriberAccount);
            preparedStatement.setDate(2, Date.valueOf(disconnectionDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
                if (rs.getObject("disconnection_reason_id") != null) {
                    subscriberSession.setDisconnectionReasonId(rs.getInt("disconnection_reason_id"));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscriberSession;
    }

    public void saveSubscriberSession(SubscriberSession subscriberSession) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "INSERT INTO \"subscriber_session\" (\"subscriber_account\", \"connection_date\", \"disconnection_date\") VALUES(?, ?, ?)");
            preparedStatement.setInt(1, subscriberSession.getSubscriberAccount());
            preparedStatement.setDate(2, Date.valueOf(subscriberSession.getConnectionDate()));
            if (subscriberSession.getDisconnectionDate() != null) {
                preparedStatement.setDate(3, Date.valueOf(subscriberSession.getDisconnectionDate()));
            } else {
                preparedStatement.setDate(3, null);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public SubscriberSession getSubscriberSessionAfterDate(Integer subscriberId, LocalDate localDate) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND \"connection_date\">? ORDER BY \"connection_date\" LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscriberSession;
    }

    @Override
    public SubscriberSession getNotClosedSubscriberSession(Integer subscriberAccount, LocalDate connectionDate) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND \"connection_date\"<=? AND \"disconnection_date\" IS NULL ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberAccount);
            preparedStatement.setDate(2, Date.valueOf(connectionDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberSession;
    }

    @Override
    public SubscriberSession getClosedSubscriberSession(Integer subscriberAccount, LocalDate date) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND \"connection_date\"<=? AND \"disconnection_date\" <=?  ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberAccount);
            preparedStatement.setDate(2, Date.valueOf(date));
            preparedStatement.setDate(3, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberSession;
    }

    @Override
    public SubscriberSession getSubscriberSessionAtDate(Integer subscriberId, LocalDate localDate) {
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND \"connection_date\"<=? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)  ORDER BY \"connection_date\"  desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            preparedStatement.setDate(3, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberSession;
    }

    public void updateSubscriberSession(SubscriberSession subscriberSession) {
        if(getSubscriberSessionByConnectionDate(subscriberSession.getSubscriberAccount(), subscriberSession.getConnectionDate()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"subscriber_session\" set \"disconnection_date\" = ?, \"disconnection_reason_id\" = ?  where \"subscriber_account\" = ? AND \"connection_date\" = ?");

                if (subscriberSession.getDisconnectionDate() != null) {
                    preparedStatement.setDate(1, Date.valueOf(subscriberSession.getDisconnectionDate()));
                } else {
                    preparedStatement.setDate(1, null);
                }
                if (subscriberSession.getDisconnectionReasonId() != null) {
                    preparedStatement.setInt(2, subscriberSession.getDisconnectionReasonId());
                } else {
                    preparedStatement.setNull(2, Types.INTEGER);
                }
                preparedStatement.setInt(3, subscriberSession.getSubscriberAccount());
                preparedStatement.setDate(4, Date.valueOf(subscriberSession.getConnectionDate()));
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
        }
    }

    @Override
    public void deleteSubscriberSession(Integer subscriberId, LocalDate localDate) {
        if(getSubscriberSessionByConnectionDate(subscriberId, localDate) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"subscriber_session\" where \"subscriber_account\" = ? AND \"connection_date\" = ?");
                preparedStatement.setInt(1, subscriberId);
                preparedStatement.setDate(2, Date.valueOf(localDate));
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
        }
    }

    public SubscriberTariff getSubscriberTariffByConnectionDate(Integer subscriberId, LocalDate localDate) {
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_tariff\" where \"subscriber_account\" = ? AND \"connection_date\" = ?");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    public SubscriberTariff getSubscriberTariffByDisconnectionDate(Integer subscriberId, LocalDate localDate) {
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_tariff\" where \"subscriber_account\" = ? AND \"disconnection_date\" = ?");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public SubscriberTariff getSubscriberTariffAfterDate(Integer subscriberId, LocalDate localDate) {
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND \"connection_date\">? ORDER BY \"connection_date\" LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public SubscriberTariff getNotClosedSubscriberTariff(Integer subscriberId, LocalDate localDate) {
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND \"connection_date\"<? AND \"disconnection_date\" IS NULL ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public SubscriberTariff getSubscriberTariffAtDate(Integer subscriberId, LocalDate localDate) {
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND \"connection_date\"<=? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\"> ?) ORDER  BY \"connection_date\" desc LIMIT 1 ");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(localDate));
            preparedStatement.setDate(3, Date.valueOf(localDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public List<SubscriberTariff> getSubscriberTariffsForInterval(Integer subscriberId, LocalDate beginDate, LocalDate endDate) {
        List<SubscriberTariff> subscriberTariffs = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND" +
                    " ((\"connection_date\"<? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)) OR (\"connection_date\">=? AND \"connection_date\"<?)) ORDER BY \"connection_date\" desc");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(endDate));
            preparedStatement.setDate(3, Date.valueOf(beginDate));
            preparedStatement.setDate(4, Date.valueOf(beginDate));
            preparedStatement.setDate(5, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                SubscriberTariff subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
                subscriberTariffs.add(subscriberTariff);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariffs;
    }

    @Override
    public List<SubscriberSession> getSubscriberSessionsForMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        List<SubscriberSession> subscriberSessions = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND" +
                    " ((\"connection_date\"<? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)) OR (\"connection_date\">=? AND \"connection_date\"<?)) ORDER BY \"connection_date\" desc");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(endDate));
            preparedStatement.setDate(3, Date.valueOf(beginDate));
            preparedStatement.setDate(4, Date.valueOf(beginDate));
            preparedStatement.setDate(5, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                SubscriberSession subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
                subscriberSessions.add(subscriberSession);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberSessions;
    }

    @Override
    public SubscriberTariff getSubscriberTariffAllMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND" +
                    " (\"connection_date\"<? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)) ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                if(rs.getDate("disconnection_date") != null) {
                    subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public SubscriberSession getSubscriberSessionAllMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        SubscriberSession subscriberSession = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"subscriber_account\"=? AND" +
                    " (\"connection_date\"<? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)) ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberSession = new SubscriberSession();
                subscriberSession.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberSession.setConnectionDate(rs.getDate("connection_date").toLocalDate());
                if(rs.getDate("disconnection_date") != null) {
                    subscriberSession.setDisconnectionDate(rs.getDate("disconnection_date").toLocalDate());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberSession;
    }

    @Override
    public SubscriberTariff getSubscriberTariffBeginInPrevMonthEndInCurrentMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND" +
                    " (\"connection_date\"<? AND \"disconnection_date\" > ? AND \"disconnection_date\" < ?) ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(beginDate));
            preparedStatement.setDate(4, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public SubscriberTariff getSubscriberTariffBeginInCurrentMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        SubscriberTariff subscriberTariff = null;
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND" +
                    " (\"connection_date\">? AND \"connection_date\"<=? AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?)) ORDER BY \"connection_date\" desc LIMIT 1");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            preparedStatement.setDate(4, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariff;
    }

    @Override
    public List<SubscriberTariff> getSubscriberTariffsBeginInCurrentMonthEndInCurrentMonth(Integer subscriberId, LocalDate localDate) {
        LocalDate beginDate = localDate.withDayOfMonth(1);
        LocalDate endDate = localDate.withDayOfMonth(1).plusMonths(1);
        List<SubscriberTariff> subscriberTariffs = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_tariff\"  WHERE \"subscriber_account\"=? AND" +
                    " (\"connection_date\">? AND (\"disconnection_date\" < ? AND \"disconnection_date\" IS NOT NULL)) ORDER BY \"connection_date\" desc");
            preparedStatement.setInt(1, subscriberId);
            preparedStatement.setDate(2, Date.valueOf(beginDate));
            preparedStatement.setDate(3, Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                SubscriberTariff subscriberTariff = new SubscriberTariff();
                subscriberTariff.setSubscriberAccount(rs.getInt("subscriber_account"));
                subscriberTariff.setConnectTariff(rs.getDate("connection_date").toLocalDate());
                subscriberTariff.setDisconnectTariff(rs.getDate("disconnection_date").toLocalDate());
                subscriberTariff.setTariffId(rs.getInt("tariff_id"));
                subscriberTariffs.add(subscriberTariff);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscriberTariffs;
    }

    public void saveSubscriberTariff(SubscriberTariff subscriberTariff) {
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "INSERT INTO \"subscriber_tariff\" (\"subscriber_account\", \"connection_date\", \"disconnection_date\", \"tariff_id\") VALUES(?, ?, ?, ?)");
            preparedStatement.setInt(1, subscriberTariff.getSubscriberAccount());
            preparedStatement.setDate(2, Date.valueOf(subscriberTariff.getConnectTariff()));
            if (subscriberTariff.getDisconnectTariff() != null) {
                preparedStatement.setDate(3, Date.valueOf(subscriberTariff.getDisconnectTariff()));
            } else {
                preparedStatement.setDate(3, null);
            }
            preparedStatement.setInt(4, subscriberTariff.getTariffId());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }
    }

    public void updateSubscriberTariff(SubscriberTariff subscriberTariff) {
        if(getSubscriberTariffByConnectionDate(subscriberTariff.getSubscriberAccount(), subscriberTariff.getConnectTariff()) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "UPDATE \"subscriber_tariff\" set  \"disconnection_date\" = ?, \"tariff_id\" = ?  where \"subscriber_account\" = ? AND \"connection_date\" = ?");

                if (subscriberTariff.getDisconnectTariff() != null) {
                    preparedStatement.setDate(1, Date.valueOf(subscriberTariff.getDisconnectTariff()));
                } else {
                    preparedStatement.setDate(1, null);
                }
                preparedStatement.setInt(2, subscriberTariff.getTariffId());
                preparedStatement.setInt(3, subscriberTariff.getSubscriberAccount());
                preparedStatement.setDate(4, Date.valueOf(subscriberTariff.getConnectTariff()));
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
        }
    }

    @Override
    public void deleteSubscriberTariff(Integer subscriberId, LocalDate localDate) {
        if(getSubscriberTariffByConnectionDate(subscriberId, localDate) != null) {
            try {
                PreparedStatement preparedStatement = getConnection().prepareStatement(
                        "DELETE FROM  \"subscriber_tariff\" where \"subscriber_account\" = ? AND \"connection_date\" = ?");
                preparedStatement.setInt(1, subscriberId);
                preparedStatement.setDate(2, Date.valueOf(localDate));
                preparedStatement.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new DAOException();
            }
        }
    }

    public List<Subscriber> getSubscribersByBeginningPartOfName(String str) {
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber\" where LOWER(\"name\") LIKE ?");
            preparedStatement.setString(1, (str + "%").toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Subscriber subscriber = constructEntity(rs);
                subscribers.add(subscriber);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return subscribers;
    }

    @Override
    public HashMap<Integer, BigDecimal> getServicesBalanceBySubscriberIdAndDate(Integer subscriberId, LocalDate date) {
        HashMap<Integer, BigDecimal> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT " +
                    "(CASE WHEN \"payment\".\"service_id\" is NULL THEN \"rendered_service\".\"service_id\" ELSE \"payment\".\"service_id\" END) AS \"service_id\"," +
                    "(CASE WHEN \"payment\".\"payment_price\" is NULL THEN 0 ELSE \"payment\".\"payment_price\" END) AS \"payment_price\"," +
                    "(CASE WHEN \"rendered_service\".\"rendered_service_price\" is NULL THEN 0 ELSE \"rendered_service\".\"rendered_service_price\" END) AS \"rendered_service_price\"," +
                    "(CASE WHEN \"rendered_service\".\"rendered_service_price\" is NULL THEN 0 ELSE \"rendered_service\".\"rendered_service_price\" END) -" +
                    "(CASE WHEN \"payment\".\"payment_price\" is NULL THEN 0 ELSE \"payment\".\"payment_price\" END) AS \"balance\"" +
                    "from(SELECT \"payment\".\"service_id\", \"payment\".\"subscriber_account\", SUM(\"payment\".\"price\") as \"payment_price\"" +
                    "FROM \"payment\" where  \"payment\".\"date\"<?  AND \"payment\".\"subscriber_account\" = ?      group by  \"payment\".\"service_id\", \"payment\". \"subscriber_account\") as \"payment\" FULL JOIN (" +
                    "SELECT \"rendered_service\". \"service_id\", \"rendered_service\".\"subscriber_account\",SUM(\"rendered_service\".\"price\") as \"rendered_service_price\"" +
                    "FROM \"rendered_service\" where  \"rendered_service\".\"date\"<? AND \"rendered_service\".\"subscriber_account\" = ?   group by \"rendered_service\".\"service_id\", \"rendered_service\". \"subscriber_account\") as  \"rendered_service\"" +
                    "ON (\"payment\".\"service_id\" = \"rendered_service\". \"service_id\")");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setInt(2, subscriberId);
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.setInt(4, subscriberId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                hashMap.put(rs.getInt("service_id"), rs.getBigDecimal("balance"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return hashMap;
    }

    @Override
    public HashMap<Integer, BigDecimal> getServicesBalance(Integer subscriberAccount) {
        HashMap<Integer, BigDecimal> hashMap = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT " +
                    "(CASE WHEN \"payment\".\"service_id\" is NULL THEN \"rendered_service\".\"service_id\" ELSE \"payment\".\"service_id\" END) AS \"service_id\"," +
                    "(CASE WHEN \"payment\".\"payment_price\" is NULL THEN 0 ELSE \"payment\".\"payment_price\" END) AS \"payment_price\"," +
                    "(CASE WHEN \"rendered_service\".\"rendered_service_price\" is NULL THEN 0 ELSE \"rendered_service\".\"rendered_service_price\" END) AS \"rendered_service_price\"," +
                    "(CASE WHEN \"rendered_service\".\"rendered_service_price\" is NULL THEN 0 ELSE \"rendered_service\".\"rendered_service_price\" END) -" +
                    "(CASE WHEN \"payment\".\"payment_price\" is NULL THEN 0 ELSE \"payment\".\"payment_price\" END) AS \"balance\"" +
                    "from(SELECT \"payment\".\"service_id\", \"payment\".\"subscriber_account\", SUM(\"payment\".\"price\") as \"payment_price\"" +
                    "FROM \"payment\" where  \"payment\".\"subscriber_account\" = ?      group by  \"payment\".\"service_id\", \"payment\". \"subscriber_account\") as \"payment\" FULL JOIN (" +
                    "SELECT \"rendered_service\". \"service_id\", \"rendered_service\".\"subscriber_account\",SUM(\"rendered_service\".\"price\") as \"rendered_service_price\"" +
                    "FROM \"rendered_service\" where \"rendered_service\".\"subscriber_account\" = ?   group by \"rendered_service\".\"service_id\", \"rendered_service\". \"subscriber_account\") as  \"rendered_service\"" +
                    "ON (\"payment\".\"service_id\" = \"rendered_service\". \"service_id\")");
            preparedStatement.setInt(1, subscriberAccount);
            preparedStatement.setInt(2, subscriberAccount);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                hashMap.put(rs.getInt("service_id"), rs.getBigDecimal("balance"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return hashMap;
    }

    public List<Subscriber> getSubscribersByBeginningPartOfAccount(String str) {
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"account\" where LOWER(\"name\") LIKE ?");
            preparedStatement.setString(1, (str + "%").toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Subscriber subscriber = constructEntity(rs);
                subscribers.add(subscriber);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribers;
    }

    @Override
    public List<Subscriber> getAllContainsInName(String str) {
        List<Subscriber> subscribers = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber\" where LOWER(\"name\") LIKE ?");
            preparedStatement.setString(1, ("%" + str + "%").toLowerCase());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribers.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribers;
    }

    @Override
    public List<Subscriber> search(SubscriberSearchCriteria criteria) {
        List<Subscriber> subscribers = new ArrayList<>();

        try {
            List<Object> params = new ArrayList<>();
            StringBuilder query = new StringBuilder("SELECT * FROM \"subscriber\" ");

            if (criteria.getAccount() != null) {
                query.append("WHERE \"account\"=?");
                params.add(criteria.getAccount());
            }

            if (criteria.getAccount() == null && !criteria.getNameIn().isEmpty()) {
                query.append("WHERE TRUE ");
                for (String name : criteria.getNameIn()) {
                    query.append("AND LOWER(\"name\") LIKE ? ");
                    params.add("%" + name + "%");
                }
            }

            if (criteria.getAccount() == null && !criteria.getStreetIn().isEmpty()) {
                if (criteria.getNameIn().isEmpty()) {
                    query.append("WHERE ");
                } else {
                    query.append("OR ");
                }
                query.append("\"street_id\" IN (SELECT \"id\" FROM \"street\" WHERE TRUE ");

                for (String name : criteria.getStreetIn()) {
                    query.append("AND LOWER(\"name\") LIKE ? ");
                    params.add("%" + name + "%");
                }
                query.append(") ");

                if (criteria.getHouse() != null) {
                    query.append(" AND \"house\"=?");
                    params.add(criteria.getHouse());
                }

                if (criteria.getIndex() != null) {
                    query.append(" AND LOWER(\"index\")=?");
                    params.add(criteria.getIndex());
                }

                if (criteria.getBuilding() != null) {
                    query.append(" AND LOWER(\"building\")=?");
                    params.add(criteria.getBuilding());
                }

                if (criteria.getFlat() != null) {
                    query.append(" AND LOWER(\"flat\")=?");
                    params.add(criteria.getFlat());
                }
            }

            PreparedStatement preparedStatement = getConnection().prepareStatement(query.toString());
            for (int i = 0; i <  params.size(); ++i) {
                Object val = params.get(i);
                if (val instanceof String) {
                    preparedStatement.setString(i + 1, (String) val);
                } else if (val instanceof Integer) {
                    preparedStatement.setInt(i + 1, (Integer) val);
                } else {
                    preparedStatement.setObject(i + 1, val);
                }
            }

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                subscribers.add(constructEntity(rs));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return subscribers;
    }

    public Integer getLastSubscriberAccount() {
        Integer lastAccount = null;
        try {
            Statement statement = getConnection().createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \"subscriber\" order by \"account\" desc limit 1");
            while (rs.next()) {
                lastAccount = rs.getInt("account");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lastAccount;
    }

    public Map<Integer, Integer> getSubscribersWithCurrentTariffsByDate(LocalDate date) {
        Map<Integer, Integer> map = new HashMap<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM \"subscriber_tariff\" where \"connection_date\" <= ?" +
                    " AND (\"disconnection_date\" IS NULL OR \"disconnection_date\" > ?) ORDER BY \"connection_date\"");
            preparedStatement.setDate(1, Date.valueOf(date));
            preparedStatement.setDate(2, Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                map.put(rs.getInt("subscriber_account"), rs.getInt("tariff_id"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return map;
    }

    public List<Integer> getConnectedSubscribers() {
        List<Integer> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"connection_date\"<=? AND \"disconnection_date\" IS NULL");
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("subscriber_account"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return list;
    }

    public List<Integer> getConnectedSubscribers(LocalDate date) {
        List<Integer> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM  \"subscriber_session\"  WHERE \"connection_date\"<=? AND \"disconnection_date\" IS NULL");
            preparedStatement.setDate(1,  Date.valueOf(date));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("subscriber_account"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return list;
    }

    @Override
    public List<Integer> getInactiveSubscribersForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Integer> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = getConnection().prepareStatement(
                    "SELECT s.account\n" +
                    "FROM subscriber s\n" +
                    "WHERE s.account not in (\n" +
                    "    SELECT rs.subscriber_account \n" +
                    "    FROM rendered_service rs\n" +
                    "    WHERE rs.date BETWEEN ? AND ?\n" +
                    "UNION\n" +
                    "    SELECT p.subscriber_account \n" +
                    "    FROM payment p\n" +
                    "    WHERE p.date BETWEEN ? AND ?\n" +
                    ")\n");
            preparedStatement.setDate(1,  Date.valueOf(startDate));
            preparedStatement.setDate(2,  Date.valueOf(endDate));
            preparedStatement.setDate(3,  Date.valueOf(startDate));
            preparedStatement.setDate(4,  Date.valueOf(endDate));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("account"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DAOException();
        }

        return list;
    }

    private Subscriber constructEntity(ResultSet rs) throws SQLException{
        Subscriber subscriber = new Subscriber();
        subscriber.setAccount(rs.getInt("account"));
        subscriber.setName(rs.getString("name"));
        if(rs.getObject("street_id") != null) {
            subscriber.setStreetId(rs.getInt("street_id"));
        }
        subscriber.setBalance(rs.getInt("balance"));
        subscriber.setConnected(rs.getBoolean("connected"));
        subscriber.setHouse(rs.getInt("house"));
        subscriber.setBuilding(rs.getString("building"));
        subscriber.setFlat(rs.getString("flat"));
        subscriber.setIndex(rs.getString("index"));
        subscriber.setPhone(rs.getString("phone"));
        subscriber.setPassportNumber(rs.getString("passport_number"));
        subscriber.setPassportAuthority(rs.getString("passport_authority"));
        Date passportDate = rs.getDate("passport_date");
        if (passportDate != null) {
            subscriber.setPassportDate(passportDate.toLocalDate());
        }
        Date contractDate = rs.getDate("date_of_contract");
        if (contractDate != null) {
            subscriber.setContractDate(contractDate.toLocalDate());
        }
        subscriber.setInformation(rs.getString("information"));
        return subscriber;
    }
}
