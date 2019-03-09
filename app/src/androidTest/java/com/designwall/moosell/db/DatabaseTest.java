package com.designwall.moosell.db;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.designwall.moosell.model.Order.Order;
import com.designwall.moosell.model.Order.OrderStatus;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.j256.ormlite.table.TableUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest /*extends AndroidTestCase*/ {

    private Logger logger;

    private static final String DATASOURCE_ERROR = "Property 'dataSource' is required";

    protected DatabaseType databaseType = new SqliteAndroidDatabaseType();
    protected ConnectionSource connectionSource;
    private TestDatabaseHelper helper;

    private Set<Class<?>> dropClassSet = new HashSet<Class<?>>();
    private Set<DatabaseTableConfig<?>> dropTableConfigSet = new HashSet<DatabaseTableConfig<?>>();

    @Before
    public void setup() throws Exception {
        logger = logger = LoggerFactory.getLogger(getClass());
        helper = new TestDatabaseHelper(InstrumentationRegistry.getTargetContext());
        connectionSource = helper.getConnectionSource();
    }

    @Test
    public void testCreateOrder() throws Exception {
        final Dao<Order, Integer> dao = createDao(Order.class, true);
        assertNotNull(dao);
        Order order = new Order();
        order.setOrder_number(1);
        order.setCreated_at("2019-01-01");
        order.setUpdated_at("2019-01-01");
        order.setCompleted_at("2019-01-01");
        order.setStatus(OrderStatus.Processing.toString());
        order.setCurrency("DZD");
        order.setTotal("2500");
        assertEquals(1, dao.create(order));
        Order order1 = new Order();
        order1.setOrder_number(1);
        order1.setCreated_at("2019-01-02");
        order1.setUpdated_at("2019-01-02");
        order1.setCompleted_at("2019-01-02");
        order1.setStatus(OrderStatus.Processing.toString());
        order1.setCurrency("DZD");
        order1.setTotal("3200");
        dao.create(order1);
        assertEquals(2, dao.queryForAll().size() );
    }

    @After
    public void tearDown() throws Exception {
        if (connectionSource != null) {
            for (Class<?> clazz : dropClassSet) {
                dropTable(clazz, true);
            }
            for (DatabaseTableConfig<?> tableConfig : dropTableConfigSet) {
                dropTable(tableConfig, true);
            }
        }
        closeConnectionSource();
        if (helper != null) {
            helper.close();
        }
    }

    protected void openConnectionSource() {
        if (connectionSource == null) {
            connectionSource = helper.getConnectionSource();
        }
    }

    protected void closeConnectionSource() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
            connectionSource = null;
        }
    }

    protected <T, ID> Dao<T, ID> createDao(Class<T> clazz, boolean createTable) throws Exception {
        if (connectionSource == null) {
            throw new SQLException(DATASOURCE_ERROR);
        }
        @SuppressWarnings("unchecked")
        BaseDaoImpl<T, ID> dao = (BaseDaoImpl<T, ID>) DaoManager.createDao(connectionSource, clazz);
        return configDao(dao, createTable);
    }

    protected <T, ID> Dao<T, ID> createDao(DatabaseTableConfig<T> tableConfig, boolean createTable) throws Exception {
        if (connectionSource == null) {
            throw new SQLException(DATASOURCE_ERROR);
        }
        @SuppressWarnings("unchecked")
        BaseDaoImpl<T, ID> dao = (BaseDaoImpl<T, ID>) DaoManager.createDao(connectionSource, tableConfig);
        return configDao(dao, createTable);
    }

    protected <T> void createTable(Class<T> clazz, boolean dropAtEnd) throws Exception {
        try {
            // first we drop it in case it existed before
            dropTable(clazz, true);
        } catch (SQLException ignored) {
            // ignore any errors about missing tables
        }
        TableUtils.createTable(connectionSource, clazz);
        if (dropAtEnd) {
            dropClassSet.add(clazz);
        }
    }

    private <T> void createTable(DatabaseTableConfig<T> tableConfig, boolean dropAtEnd) throws Exception {
        try {
            // first we drop it in case it existed before
            dropTable(tableConfig, true);
        } catch (SQLException ignored) {
            // ignore any errors about missing tables
        }
        TableUtils.createTable(connectionSource, tableConfig);
        if (dropAtEnd) {
            dropTableConfigSet.add(tableConfig);
        }
    }
    protected <T> void dropTable(Class<T> clazz, boolean ignoreErrors) throws Exception {
        // drop the table and ignore any errors along the way
        TableUtils.dropTable(connectionSource, clazz, ignoreErrors);
    }

    private <T> void dropTable(DatabaseTableConfig<T> tableConfig, boolean ignoreErrors) throws Exception {
        // drop the table and ignore any errors along the way
        TableUtils.dropTable(connectionSource, tableConfig, ignoreErrors);
    }

    private <T, ID> Dao<T, ID> configDao(BaseDaoImpl<T, ID> dao, boolean createTable) throws Exception {
        if (connectionSource == null) {
            throw new SQLException(DATASOURCE_ERROR);
        }
        dao.setConnectionSource(connectionSource);
        if (createTable) {
            DatabaseTableConfig<T> tableConfig = dao.getTableConfig();
            if (tableConfig == null) {
                tableConfig = DatabaseTableConfig.fromClass(connectionSource, dao.getDataClass());
            }
            createTable(tableConfig, true);
        }
        dao.initialize();
        return dao;
    }

}
