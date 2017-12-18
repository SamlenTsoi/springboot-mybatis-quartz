# `springboot`整合`quartz`集群
>[quartz官网](http://www.quartz-scheduler.org/)

## 背景
同一份具有定时任务功能(定时发送消息)的代码需要部署到两台或以上的服务器上，同一定时任务只需要在多台的服务运行，避免重复的操作（重复发送）。

## quartz集群
 > 参考：http://blog.csdn.net/wenniuwuren/article/details/45866807  

 Quartz的集群是在同一个数据库下， 由数据库的数据来确定调度任务是否正在执行， 正在执行则其他服务器就不能去执行该行调度数据。 这个跟很多项目是用Zookeeper做集群不一样， 这些项目是靠Zookeeper选举出来的的服务器去执行， 可以理解为Quartz靠数据库选举一个服务器来执行。

## 整合说明

### 关键依赖
```
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.3.0</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.3.12.RELEASE</version>
</dependency>
```
### 工厂类
`SpringJobFactory`:该任务工厂类可以使具体的Job交给spring来管理，因此我们可以在Job类上注入我们需要的业务级别的代码（service类）
```
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * 此类使quart的job能够使用@Autowired spring管理的bean
 *
 * @author samlen_tsoi
 * @date 2017/11/28
 */
@Component
public class SpringJobFactory extends AdaptableJobFactory {

    @Autowired
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }

}
```
`SchedulerConfig`:向`spring`注册`SchedulerFactoryBean`
```
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * @author samlen_tsoi
 */
@Slf4j
@Configuration
@SuppressWarnings("ALL")
public class SchedulerConfig {

    @Bean
    public Properties properties() throws IOException {
        Properties prop = new Properties();
        prop.load(new ClassPathResource("/quartz.properties").getInputStream());
        return prop;
    }

    @Autowired
    private SpringJobFactory springJobFactory;

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //是否覆盖就任务
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //延迟多久启动
        schedulerFactoryBean.setStartupDelay(10);
        //设置基本的配置
        schedulerFactoryBean.setQuartzProperties(properties());
        //是否自动启动
        schedulerFactoryBean.setAutoStartup(Boolean.TRUE);
        //必须设置，具体的任务实例才能交给spring管理
        schedulerFactoryBean.setJobFactory(springJobFactory);
        return schedulerFactoryBean;
    }
}

```

### quartz.properties
```
#============================================================================
# Configure Main Scheduler Properties
#============================================================================
# 属性可为任何值，用在 JDBC JobStore 中来唯一标识实例，但是所有集群节点中必须相同
org.quartz.scheduler.instanceName = MyClusteredScheduler
# 属性为 AUTO即可，基于主机名和时间戳来产生实例 ID
org.quartz.scheduler.instanceId = AUTO
org.quartz.scheduler.makeSchedulerThreadDaemon = true
# 是否使用自己的配置文件
org.quartz.jobStore.useProperties=true


#============================================================================
# Configure ThreadPool
#============================================================================
# 线程池的实现类
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool
# 是否为守护线程
org.quartz.threadPool.makeThreadsDaemons = true
# 线程数
org.quartz.threadPool.threadCount = 25
# 线程的优先级
org.quartz.threadPool.threadPriority = 5

#============================================================================
# Configure JobStore
#============================================================================
# 属性为 JobStoreTX，将任务持久化到数据中。因为集群中节点依赖于数据库来传播 Scheduler 实例的状态，你只能在使用 JDBC JobStore 时应用 Quartz 集群，这意味着你必须使用 JobStoreTX 或是 JobStoreCMT 作为 Job 存储；你不能在集群中使用 RAMJobStore。
org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
# 数据库中quartz表的表名前缀
org.quartz.jobStore.tablePrefix = qrtz_
# 属性为 true，你就告诉了 Scheduler 实例要它参与到一个集群当中。这一属性会贯穿于调度框架的始终，用于修改集群环境中操作的默认行为。
org.quartz.jobStore.isClustered = true
org.quartz.jobStore.dataSource = myDs
org.quartz.jobStore.misfireThreshold = 60000
# 属性定义了Scheduler 实例检入到数据库中的频率(单位：毫秒)。Scheduler 检查是否其他的实例到了它们应当检入的时候未检入；这能指出一个失败的 Scheduler 实例，且当前 Scheduler 会以此来接管任何执行失败并可恢复的 Job。通过检入操作，Scheduler 也会更新自身的状态记录。clusterChedkinInterval 越小，Scheduler 节点检查失败的 Scheduler 实例就越频繁。默认值是 15000 (即15 秒)。
org.quartz.jobStore.clusterCheckinInterval = 5000


#============================================================================
# Configure Datasources  
#============================================================================
org.quartz.dataSource.myDs.driver = com.mysql.jdbc.Driver
org.quartz.dataSource.myDs.URL = jdbc:mysql://localhost:3306/sbquartz?characterEncoding=utf8&useSSL=false
org.quartz.dataSource.myDs.user = root
org.quartz.dataSource.myDs.password = root
org.quartz.dataSource.myDs.maxConnections = 5
org.quartz.dataSource.myDs.validationQuery = select 1
```
### 其他关键类
`samlen.tsoi.sbquartz.web.helper.SchedulerHelper`: 提供了一下方法：
- `createScheduler`: 创建并开启任务

- `deleteScheduler`: 关闭并删除任务

- `updateScheduler`: 更新任务

`samlen.tsoi.sbquartz.entity.JobConfig`: 用于组装任务详情
- `id`: 任务ID

- `JobClass`: 任务的具体类名

- `groupName`: 任务的组名

- `cronTime`: 定时触发时间

- `jobData`: 一个map，存放额外的数据

## 测试
- 建库建表，插数据，相关sql见`db`目录

- 依次启动`samlen.tsoi.sbquartz.web.WebApplicationFirst`,`samlen.tsoi.sbquartz.web.WebApplicationSecond`

- 使用`postman`之类工具访问`samlen.tsoi.sbquartz.web.WebApplicationFirst.JobController`的`restApi`,观察控制台打印的日志

## 参考博文
- `http://blog.csdn.net/wenniuwuren/article/details/45866807`

- `http://blog.csdn.net/musuny/article/details/75808044`

- `http://blog.csdn.net/lisi1129/article/details/76121057`

- `http://tzz6.iteye.com/blog/2210485`

- `http://blog.csdn.net/growing_duck/article/details/75115913`
