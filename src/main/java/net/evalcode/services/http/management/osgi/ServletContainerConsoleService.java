package net.evalcode.services.http.management.osgi;


import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.evalcode.services.http.HttpComponentModule;
import net.evalcode.services.http.internal.servlet.ServletContainer;
import net.evalcode.services.manager.annotation.Component;
import net.evalcode.services.manager.management.logging.Log;
import net.evalcode.services.manager.management.osgi.ConsoleService;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.ServiceException;


/**
 * ServletContainerConsoleService
 *
 * @author carsten.schipke@gmail.com
 */
@Singleton
@Component(module=HttpComponentModule.class)
public class ServletContainerConsoleService implements ConsoleService
{
  // PREDEFINED PROPERTIES
  private static final long INTERVAL_PRINT_STATUS_UPDATE=500L;
  private static final String DESCRIPTION="Servlet Container Manager";
  private static final String COMMAND="http";


  // MEMBERS
  @Inject
  ServletContainer servletContainer;


  // ACCESSORS/MUTATORS
  @ConsoleService.Method(command="status", description="Show Servlet Container Status")
  public void status(final CommandInterpreter commandInterpreter)
  {
    String state="stopped";
    if(servletContainer.isStarted())
      state="started";

    commandInterpreter.println(String.format("Embedded Servlet Container - Status: %1$s\n", state));
  }

  @Log
  @ConsoleService.Method(command="start", description="Start Servlet Container")
  public void start(final CommandInterpreter commandInterpreter)
  {
    commandInterpreter.print("Starting ");

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run()
      {
        try
        {
          servletContainer.start();
        }
        catch(final ServiceException e)
        {
          commandInterpreter.println("Unable to start servlet container.");
          commandInterpreter.printStackTrace(e);
        }
      }
    });

    while(!servletContainer.isStarted())
    {
      commandInterpreter.print(".");

      try
      {
        Thread.sleep(INTERVAL_PRINT_STATUS_UPDATE);
      }
      catch(final InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }

    commandInterpreter.println(" OK");
  }

  @Log
  @ConsoleService.Method(command="stop", description="Stop Servlet Container")
  public void stop(final CommandInterpreter commandInterpreter)
  {
    commandInterpreter.print("Stopping ");

    Executors.newSingleThreadExecutor().execute(new Runnable() {
      @Override
      public void run()
      {
        try
        {
          servletContainer.stop();
        }
        catch(final ServiceException e)
        {
          commandInterpreter.println("Unable to stop servlet container.");
          commandInterpreter.printStackTrace(e);
        }
      }
    });

    while(!servletContainer.isStopped())
    {
      commandInterpreter.print(".");

      try
      {
        Thread.sleep(INTERVAL_PRINT_STATUS_UPDATE);
      }
      catch(final InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }

    commandInterpreter.println(" OK");
  }


  // OVERRIDES/IMPLEMENTS
  @Override
  public String getDescription()
  {
    return DESCRIPTION;
  }

  @Override
  public String getCommand()
  {
    return COMMAND;
  }
}
