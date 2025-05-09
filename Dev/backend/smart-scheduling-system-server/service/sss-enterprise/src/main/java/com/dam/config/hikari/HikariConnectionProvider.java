package com.dam.config.hikari;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 使用Hikari作为数据库连接池
 */
@Data
public class HikariConnectionProvider implements ConnectionProvider {

    /**
     * JDBC驱动
     */
    public String driver;

    /**
     * JDBC连接字符串
     */
    public String URL;

    /**
     * 数据库用户名
     */
    public String user;

    /**
     * 数据库用户密码
     */
    public String password;

    /**
     * 数据库最大连接数
     */
    public int maxConnections;

    /**
     * SQL查询语句，用于验证数据库连接的有效性。如果设置，将在连接返回到连接池前执行该查询。
     */
    public String validationQuery;

    /**
     * 是否在获取连接时进行有效性验证（即在 {@link #getConnection()} 时执行验证查询）
     */
    private boolean validateOnCheckout;

    /**
     * 空闲连接的验证间隔（秒）。仅当 {@code validationQuery} 不为 null 时有效。
     */
    private int idleConnectionValidationSeconds;

    /**
     * 默认的最大连接数（仅用于内部计算，实际使用 {@link #maxConnections}）。
     */
    public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;

    /**
     * HikariCP 数据源实例。
     */
    private HikariDataSource datasource;


    /**
     * 获取数据库连接。调用此方法时，将从 HikariCP 连接池中获取一个已建立的连接。
     *
     * @return 与数据库建立的连接对象
     * @throws SQLException 如果无法从连接池获取连接或发生其他 SQL 错误
     */
    @Override
    public Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }

    /**
     * 关闭连接池。调用此方法时，将关闭所有与数据库的连接，并释放相关资源。
     */
    @Override
    public void shutdown() {
        datasource.close();
    }

    /**
     * 初始化连接池。此方法负责创建并配置 HikariCP 数据源，设置必要的连接参数。
     *
     * @throws SQLException 如果在创建或配置数据源过程中发生错误
     */
    @Override
    public void initialize() throws SQLException {
        if (this.URL == null) {
            throw new SQLException("URL不能为空");
        }
        if (this.driver == null) {
            throw new SQLException("driver不能为空");
        }
        if (this.maxConnections < 0) {
            throw new SQLException("maxConnections必须大于0");
        }

        // 创建并配置 HikariDataSource
        datasource = new HikariDataSource();
        try {
            datasource.setDriverClassName(this.driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        datasource.setJdbcUrl(this.URL);
        datasource.setUsername(this.user);
        datasource.setPassword(this.password);
        datasource.setMaximumPoolSize(this.maxConnections);
        datasource.setMinimumIdle(1);
        // 最大连接等待时间（毫秒）
        datasource.setConnectionTimeout(0);
        // 默认最大连接生命周期，单位为毫秒
        datasource.setMaxLifetime(DEFAULT_DB_MAX_CONNECTIONS * 1000);
        // 配置验证查询
        if (this.validationQuery != null) {
            // 这个查询语句用于在必要时验证数据库连接是否仍然有效。例如，对于 MySQL，常用的验证查询可能是 "SELECT 1"
            datasource.setConnectionTestQuery(this.validationQuery);
            // 单位转换为毫秒，这个属性定义了执行验证查询时允许的最大等待时间。如果在这个时间内查询仍未完成，HikariCP 将认为连接无效并关闭该连接
            datasource.setValidationTimeout(this.idleConnectionValidationSeconds * 1000);
            // 可选，设置连接泄漏检测阈值，单位为毫秒
            // 该属性指定了 HikariCP 检测到连接可能泄漏所需的时间（即连接在使用后未正确关闭的时间）
            // 超过这个阈值后，HikariCP 将记录一条警告消息，帮助开发者识别和修复潜在的连接泄漏问题
            datasource.setLeakDetectionThreshold(this.idleConnectionValidationSeconds * 1000);
            if (!this.validateOnCheckout) {
                // 启用 JMX 监控，以便在外部工具中查看连接状态（包括“空闲”和“检查出错”的连接）
                datasource.setRegisterMbeans(true);
            }
        }

    }
}
