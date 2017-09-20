/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package io.shardingjdbc.example.jdbc;

import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingjdbc.core.jdbc.core.datasource.ShardingDataSource;
import io.shardingjdbc.example.jdbc.algorithm.ModuloShardingTableAlgorithm;
import io.shardingjdbc.example.jdbc.repository.RawJdbcRepository;
import io.shardingjdbc.example.jdbc.util.DataSourceUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class RawJdbcJavaShardingTableOnlyMain {
    
    // CHECKSTYLE:OFF
    public static void main(final String[] args) throws SQLException {
    // CHECKSTYLE:ON
        new RawJdbcRepository(getShardingDataSource()).testAll();
    }
    
    private static ShardingDataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
        orderTableRuleConfig.setLogicTable("t_order");
        orderTableRuleConfig.setActualTables("t_order_${[0, 1]}");
        orderTableRuleConfig.setKeyGeneratorColumnName("order_id");
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);
        TableRuleConfiguration orderItemTableRuleConfig = new TableRuleConfiguration();
        orderItemTableRuleConfig.setLogicTable("t_order_item");
        orderItemTableRuleConfig.setActualTables("t_order_item_${[0, 1]}");
        shardingRuleConfig.getTableRuleConfigs().add(orderItemTableRuleConfig);
        shardingRuleConfig.getBindingTableGroups().add("t_order, t_order_item");
        StandardShardingStrategyConfiguration tableShardingStrategyConfig = new StandardShardingStrategyConfiguration();
        tableShardingStrategyConfig.setShardingColumn("order_id");
        tableShardingStrategyConfig.setPreciseAlgorithmClassName(ModuloShardingTableAlgorithm.class.getName());
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(tableShardingStrategyConfig);
        return new ShardingDataSource(shardingRuleConfig.build(createDataSourceMap()));
    }
    
    private static Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>(1, 1);
        result.put("ds_jdbc", DataSourceUtil.createDataSource("ds_jdbc"));
        return result;
    }
}
