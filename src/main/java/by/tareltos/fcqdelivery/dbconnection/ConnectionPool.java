package by.tareltos.fcqdelivery.dbconnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.mysql.jdbc.Driver;

public class ConnectionPool {
    private static final String PROPERTIES_FILE_NAME = "db.properties";
    private static final String DB_URL = "url";
    private static final String DB_USER_NAME = "userName";
    private static final String DB_USER_PASSWORD = "password";
    private static final String DB_POOL_SIZE = "poolSize";
    private LinkedBlockingQueue<Connection> connectionPool;
    private static ConnectionPool instance;
    private static AtomicBoolean isNull = new AtomicBoolean(true);
    private static ReentrantLock lock = new ReentrantLock();

    private ConnectionPool() throws ConnectionException {
        try {
            DriverManager.registerDriver(new Driver());
            InputStream inputStream = ConnectionPool.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);
            Properties properties = new Properties();
            if (properties != null) {
                try {
                    properties.load(inputStream);
                } catch (IOException e) {
                    throw new ConnectionException("Exception in loading properties", e);
                }
            }
            String url = properties.getProperty(DB_URL);
            String user = properties.getProperty(DB_USER_NAME);
            String password = properties.getProperty(DB_USER_PASSWORD);
            int poolSize = Integer.parseInt(properties.getProperty(DB_POOL_SIZE));
            connectionPool = new LinkedBlockingQueue<>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                connectionPool.put(DriverManager.getConnection(url, user, password));
            }
        } catch (SQLException | InterruptedException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static ConnectionPool getInstance() throws ConnectionException {
        if (isNull.get()) {
            lock.lock();
            try {
                if (isNull.get()) {
                    instance = new ConnectionPool();
                    isNull.set(false);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Connection getConnection() throws ConnectionException {
        Connection connection = null;
        try {
            connection = connectionPool.take();
        } catch (InterruptedException e) {
            throw new ConnectionException("Can not getConnection", e);
        }
        return connection;
    }

    public void returnConnection(Connection connection) throws ConnectionException {
        try {
            if (!connection.isClosed()) {
                connectionPool.put(connection);
            }
        } catch (SQLException | InterruptedException e) {
            throw new ConnectionException("Can not returnConnection", e);
        }
    }

    public void shutDown() throws ConnectionException {
        for (int i = 0; i < connectionPool.size(); i++) {
            try {
                connectionPool.take().close();
            } catch (SQLException e) {
                throw new ConnectionException("Can not close connection", e);
            } catch (InterruptedException e) {
                throw new ConnectionException("Can not close connection", e);
            }
        }
    }
}