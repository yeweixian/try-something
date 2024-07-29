package com.dangerye.rpcclient.elasticjob;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.dangerye.base.utils.SingletonLoader;
import com.dangerye.base.utils.ZookeeperUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CallReportJob implements InitializingBean, SimpleJob {

    private final CuratorFramework zkClient = SingletonLoader.getInstance(ZookeeperUtil.class).getMyRPCServicesZkClient();

    @Override
    public void afterPropertiesSet() throws Exception {
        final ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration("127.0.0.1:2181", "call-report-job");
        final ZookeeperRegistryCenter zookeeperRegistryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        zookeeperRegistryCenter.init();

        final JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder("call-report", "*/5 * * * * ?", 1)
                .build();
        final SimpleJobConfiguration simpleJobConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, this.getClass().getName());
        final JobScheduler jobScheduler = new JobScheduler(zookeeperRegistryCenter, LiteJobConfiguration.newBuilder(simpleJobConfiguration)
                .overwrite(true)
                .build());
        jobScheduler.init();
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        final long nowTime = System.currentTimeMillis();
        try {
            final List<String> nodeList = zkClient.getChildren().forPath("/");
            if (CollectionUtils.isEmpty(nodeList)) {
                return;
            }
            for (String node : nodeList) {
                final byte[] bytes = zkClient.getData().forPath("/" + node);
                if (bytes == null) {
                    continue;
                }
                final String dataMsg = new String(bytes);
                if (StringUtils.isBlank(dataMsg)) {
                    continue;
                }
                final String[] split = dataMsg.split("\\|");
                final long endTime = NumberUtils.toLong(split[0]);
                if (nowTime > endTime + 5000) {
                    zkClient.setData().forPath("/" + node, "".getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
