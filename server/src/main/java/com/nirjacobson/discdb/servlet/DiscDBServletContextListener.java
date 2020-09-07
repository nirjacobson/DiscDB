package com.nirjacobson.discdb.servlet;

import com.nirjacobson.discdb.svc.MongoSvc;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DiscDBServletContextListener implements ServletContextListener {

  @Inject private MongoSvc _mongoSvc;

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    _mongoSvc.close();
  }
}
