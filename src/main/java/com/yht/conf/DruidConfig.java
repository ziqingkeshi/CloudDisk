package com.yht.conf;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * className:DruidConfig
 * description:
 * Druid是一款优秀的数据库连接池。Druid能够提供强大的数据库监控和扩展功能
 * author:严浩天
 * createTime:2018-11-23 16:04
 */
@Configuration//@Configuration标注在类上，相当于把该类作为spring的xml配置文件中的<beans>，作用为：配置spring容器(应用上下文)
public class DruidConfig {

	/**
	 * @Bean是一个方法级别上的注解，主要用在@Configuration注解的类里
	 * 这个配置就等同于之前在xml里的配置
	 * <beans>
			<bean id="transferService" class="com.acme.TransferServiceImpl"/>
	   </beans>
	 * @return
	 */
	@Bean
	@SuppressWarnings("all")
	public ServletRegistrationBean druidServlet() {

		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
		//登录查看信息的账号密码.

		servletRegistrationBean.addInitParameter("loginUsername","admin");

		servletRegistrationBean.addInitParameter("loginPassword","123456");
		return servletRegistrationBean;
	}

	@Bean
	@SuppressWarnings("all")
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}
}