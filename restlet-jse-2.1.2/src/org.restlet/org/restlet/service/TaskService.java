/**
 * Copyright 2005-2013 Restlet S.A.S.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet S.A.S.
 */

package org.restlet.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Response;
import org.restlet.engine.Engine;
import org.restlet.routing.VirtualHost;

/**
 * Application service capable of running and scheduling tasks asynchronously.
 * The service instance returned will not invoke the runnable task in the
 * current thread.<br>
 * <br>
 * In addition to allowing pooling, this method will ensure that the threads
 * executing the tasks will have the thread local variables copied from the
 * calling thread. This will ensure that call to static methods like
 * {@link Application#getCurrent()} still work.<br>
 * <br>
 * Also, note that this executor service will be shared among all Restlets and
 * Resources that are part of your context. In general this context corresponds
 * to a parent Application's context. If you want to have your own service
 * instance, you can use the {@link TaskService#wrap(ScheduledExecutorService)}
 * method to ensure that thread local variables are correctly set.
 * 
 * @author Jerome Louvel
 * @author Doug Lea (docs of ExecutorService in public domain)
 * @author Tim Peierls
 */
public class TaskService extends Service implements ScheduledExecutorService {

    /**
     * The default thread factory.
     * 
     * @author Jerome Louvel
     * @author Tim Peierls
     */
    private static class RestletThreadFactory implements ThreadFactory {
        final ThreadFactory factory = Executors.defaultThreadFactory();

        public Thread newThread(Runnable runnable) {
            Thread t = factory.newThread(runnable);

            // Default factory is documented as producing names of the
            // form "pool-N-thread-M".
            t.setName(t.getName().replaceFirst("pool", "restlet"));
            return t;
        }
    }

    /**
     * Wraps a JDK executor service to ensure that the threads executing the
     * tasks will have the thread local variables copied from the calling
     * thread. This will ensure that call to static methods like
     * {@link Application#getCurrent()} still work.
     * 
     * @param executorService
     *            The JDK service to wrap.
     * @return The wrapper service to use.
     */
    public static ScheduledExecutorService wrap(
            final ScheduledExecutorService executorService) {
        return new ScheduledExecutorService() {

            public boolean awaitTermination(long timeout, TimeUnit unit)
                    throws InterruptedException {
                return executorService.awaitTermination(timeout, unit);
            }

            public void execute(final Runnable runnable) {
                // Save the thread local variables
                final Application currentApplication = Application.getCurrent();
                final Context currentContext = Context.getCurrent();
                final Integer currentVirtualHost = VirtualHost.getCurrent();
                final Response currentResponse = Response.getCurrent();

                executorService.execute(new Runnable() {
                    public void run() {
                        // Copy the thread local variables
                        Response.setCurrent(currentResponse);
                        Context.setCurrent(currentContext);
                        VirtualHost.setCurrent(currentVirtualHost);
                        Application.setCurrent(currentApplication);

                        try {
                            // Run the user task
                            runnable.run();
                        } finally {
                            Engine.clearThreadLocalVariables();
                        }
                    }
                });
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public List invokeAll(Collection tasks) throws InterruptedException {
                return executorService.invokeAll(tasks);
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public List invokeAll(Collection tasks, long timeout, TimeUnit unit)
                    throws InterruptedException {
                return executorService.invokeAll(tasks, timeout, unit);
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public Object invokeAny(Collection tasks)
                    throws InterruptedException, ExecutionException {
                return executorService.invokeAny(tasks);
            }

            @SuppressWarnings({ "rawtypes", "unchecked" })
            public Object invokeAny(Collection tasks, long timeout,
                    TimeUnit unit) throws InterruptedException,
                    ExecutionException, TimeoutException {
                return executorService.invokeAny(tasks, timeout, unit);
            }

            public boolean isShutdown() {
                return executorService.isShutdown();
            }

            public boolean isTerminated() {
                return executorService.isTerminated();
            }

            public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                    long delay, TimeUnit unit) {
                return executorService.schedule(callable, delay, unit);
            }

            public ScheduledFuture<?> schedule(Runnable command, long delay,
                    TimeUnit unit) {
                return executorService.schedule(command, delay, unit);
            }

            public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                    long initialDelay, long period, TimeUnit unit) {
                return executorService.scheduleAtFixedRate(command,
                        initialDelay, period, unit);
            }

            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                    long initialDelay, long delay, TimeUnit unit) {
                return executorService.scheduleWithFixedDelay(command,
                        initialDelay, delay, unit);
            }

            public void shutdown() {
                executorService.shutdown();
            }

            public List<Runnable> shutdownNow() {
                return executorService.shutdownNow();
            }

            public <T> Future<T> submit(Callable<T> task) {
                return executorService.submit(task);
            }

            public Future<?> submit(Runnable task) {
                return executorService.submit(task);
            }

            public <T> Future<T> submit(Runnable task, T result) {
                return executorService.submit(task, result);
            }
        };
    }

    /**
     * Allow {@link #shutdown()} and {@link #shutdownNow()} methods to
     * effectively shutdown the wrapped executor service.
     */
    private volatile boolean shutdownAllowed;

    /** The wrapped JDK executor service. */
    private volatile ScheduledExecutorService wrapped;

    /** The core pool size defining the maximum number of threads. */
    private volatile int corePoolSize;

    /**
     * Constructor. Set the core pool size to 4 by default.
     */
    public TaskService() {
        this(4);
    }

    /**
     * Constructor.
     */
    public TaskService(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        this.shutdownAllowed = false;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown request,
     * or the timeout occurs, or the current thread is interrupted, whichever
     * happens first.
     * 
     * @param timeout
     *            The maximum time to wait.
     * @param unit
     *            The time unit.
     * @return True if this executor terminated and false if the timeout elapsed
     *         before termination.
     */
    public boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        startIfNeeded();
        return getWrapped().awaitTermination(timeout, unit);
    }

    /**
     * Creates a new JDK executor service that will be wrapped. By default it
     * calls {@link Executors#newCachedThreadPool(ThreadFactory)}, passing the
     * result of {@link #createThreadFactory()} as a parameter.
     * 
     * @param corePoolSize
     *            The core pool size defining the maximum number of threads.
     * @return A new JDK executor service.
     */
    protected ScheduledExecutorService createExecutorService(int corePoolSize) {
        return Executors.newScheduledThreadPool(corePoolSize,
                createThreadFactory());
    }

    /**
     * Creates a new thread factory that will properly name the Restlet created
     * threads with a "restlet-" prefix.
     * 
     * @return A new thread factory.
     */
    protected ThreadFactory createThreadFactory() {
        return new RestletThreadFactory();
    }

    /**
     * Executes the given command asynchronously.
     * 
     * @param command
     *            The command to execute.
     */
    public void execute(Runnable command) {
        startIfNeeded();
        getWrapped().execute(command);
    }

    /**
     * Returns the core pool size defining the maximum number of threads.
     * 
     * @return The core pool size defining the maximum number of threads.
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Returns the wrapped JDK executor service.
     * 
     * @return The wrapped JDK executor service.
     */
    private ScheduledExecutorService getWrapped() {
        return wrapped;
    }

    /**
     * Executes the given tasks, returning a list of Futures holding their
     * status and results when all complete.<br>
     * <br>
     * Due to a breaking change between Java SE versions 5 and 6, and in order
     * to maintain compatibility both at the source and binary level, we have
     * removed the generic information from this method. You can check the
     * {@link ExecutorService} interface for typing details.
     * 
     * @param tasks
     *            The task to execute.
     * @return The list of futures.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List invokeAll(Collection tasks) throws InterruptedException {
        startIfNeeded();
        return getWrapped().invokeAll(tasks);
    }

    /**
     * Executes the given tasks, returning a list of Futures holding their
     * status and results when all complete or the timeout expires, whichever
     * happens first. Future.isDone() is true for each element of the returned
     * list. Upon return, tasks that have not completed are canceled. Note that
     * a completed task could have terminated either normally or by throwing an
     * exception. The results of this method are undefined if the given
     * collection is modified while this operation is in progress.<br>
     * <br>
     * Due to a breaking change between Java SE versions 5 and 6, and in order
     * to maintain compatibility both at the source and binary level, we have
     * removed the generic information from this method. You can check the
     * {@link ExecutorService} interface for typing details.
     * 
     * @param tasks
     *            The task to execute.
     * @param timeout
     *            The maximum time to wait.
     * @param unit
     *            The time unit.
     * @return The list of futures.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public List invokeAll(Collection tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        startIfNeeded();
        return getWrapped().invokeAll(tasks, timeout, unit);
    }

    /**
     * Executes the given tasks, returning the result of one that has completed
     * successfully (i.e., without throwing an exception), if any do. Upon
     * normal or exceptional return, tasks that have not completed are
     * cancelled. The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     * 
     * Due to a breaking change between Java SE versions 5 and 6, and in order
     * to maintain compatibility both at the source and binary level, we have
     * removed the generic information from this method. You can check the
     * {@link ExecutorService} interface for typing details.
     * 
     * @param tasks
     *            The task to execute.
     * @return The result returned by one of the tasks.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object invokeAny(Collection tasks) throws InterruptedException,
            ExecutionException {
        startIfNeeded();
        return getWrapped().invokeAny(tasks);
    }

    /**
     * Executes the given tasks, returning the result of one that has completed
     * successfully (i.e., without throwing an exception), if any do before the
     * given timeout elapses. Upon normal or exceptional return, tasks that have
     * not completed are cancelled. The results of this method are undefined if
     * the given collection is modified while this operation is in progress.
     * 
     * Due to a breaking change between Java SE versions 5 and 6, and in order
     * to maintain compatibility both at the source and binary level, we have
     * removed the generic information from this method. You can check the
     * {@link ExecutorService} interface for typing details.
     * 
     * @param tasks
     *            The task to execute.
     * @param timeout
     *            The maximum time to wait.
     * @param unit
     *            The time unit.
     * @return The result returned by one of the tasks.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object invokeAny(Collection tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        startIfNeeded();
        return getWrapped().invokeAny(tasks, timeout, unit);
    }

    /**
     * Returns true if this executor has been shut down.
     * 
     * @return True if this executor has been shut down.
     */
    public boolean isShutdown() {
        return (getWrapped() == null) || getWrapped().isShutdown();
    }

    /**
     * Indicates if the {@link #shutdown()} and {@link #shutdownNow()} methods
     * are allowed to effectively shutdown the wrapped executor service. Return
     * false by default.
     * 
     * @return True if shutdown is allowed.
     */
    public boolean isShutdownAllowed() {
        return shutdownAllowed;
    }

    /**
     * Returns true if all tasks have completed following shut down. Note that
     * isTerminated is never true unless either shutdown or shutdownNow was
     * called first.
     * 
     * @return True if all tasks have completed following shut down.
     */
    public boolean isTerminated() {
        return (getWrapped() == null) || getWrapped().isTerminated();
    }

    /**
     * Creates and executes a ScheduledFuture that becomes enabled after the
     * given delay.
     * 
     * @param callable
     *            The function to execute.
     * @param delay
     *            The time from now to delay execution.
     * @param unit
     *            The time unit of the delay parameter.
     * @return a ScheduledFuture that can be used to extract result or cancel.
     * @throws RejectedExecutionException
     *             if task cannot be scheduled for execution.
     * @throws NullPointerException
     *             if callable is null
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay,
            TimeUnit unit) {
        startIfNeeded();
        return getWrapped().schedule(callable, delay, unit);
    }

    /**
     * Creates and executes a one-shot action that becomes enabled after the
     * given delay.
     * 
     * @param command
     *            The task to execute.
     * @param delay
     *            The time from now to delay execution.
     * @param unit
     *            The time unit of the delay parameter.
     * @return a Future representing pending completion of the task, and whose
     *         <tt>get()</tt> method will return <tt>null</tt> upon completion.
     * @throws RejectedExecutionException
     *             if task cannot be scheduled for execution.
     * @throws NullPointerException
     *             if command is null
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay,
            TimeUnit unit) {
        startIfNeeded();
        return getWrapped().schedule(command, delay, unit);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after
     * the given initial delay, and subsequently with the given period; that is
     * executions will commence after <tt>initialDelay</tt> then
     * <tt>initialDelay+period</tt>, then <tt>initialDelay + 2 * period</tt>,
     * and so on. If any execution of the task encounters an exception,
     * subsequent executions are suppressed. Otherwise, the task will only
     * terminate via cancellation or termination of the executor.
     * 
     * @param command
     *            The task to execute.
     * @param initialDelay
     *            The time to delay first execution.
     * @param period
     *            The period between successive executions.
     * @param unit
     *            The time unit of the initialDelay and period parameters
     * @return a Future representing pending completion of the task, and whose
     *         <tt>get()</tt> method will throw an exception upon cancellation.
     * @throws RejectedExecutionException
     *             if task cannot be scheduled for execution.
     * @throws NullPointerException
     *             if command is null
     * @throws IllegalArgumentException
     *             if period less than or equal to zero.
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
            long initialDelay, long period, TimeUnit unit) {
        startIfNeeded();
        return getWrapped().scheduleAtFixedRate(command, initialDelay, period,
                unit);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after
     * the given initial delay, and subsequently with the given delay between
     * the termination of one execution and the commencement of the next. If any
     * execution of the task encounters an exception, subsequent executions are
     * suppressed. Otherwise, the task will only terminate via cancellation or
     * termination of the executor.
     * 
     * @param command
     *            The task to execute.
     * @param initialDelay
     *            The time to delay first execution.
     * @param delay
     *            The delay between the termination of one execution and the
     *            commencement of the next.
     * @param unit
     *            The time unit of the initialDelay and delay parameters
     * @return a Future representing pending completion of the task, and whose
     *         <tt>get()</tt> method will throw an exception upon cancellation.
     * @throws RejectedExecutionException
     *             if task cannot be scheduled for execution.
     * @throws NullPointerException
     *             if command is null
     * @throws IllegalArgumentException
     *             if delay less than or equal to zero.
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
            long initialDelay, long delay, TimeUnit unit) {
        startIfNeeded();
        return getWrapped().scheduleWithFixedDelay(command, initialDelay,
                delay, unit);
    }

    /**
     * Sets the core pool size defining the maximum number of threads.
     * 
     * @param corePoolSize
     *            The core pool size defining the maximum number of threads.
     */
    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * Indicates if the {@link #shutdown()} and {@link #shutdownNow()} methods
     * are allowed to effectively shutdown the wrapped executor service.
     * 
     * @param allowShutdown
     *            True if shutdown is allowed.
     */
    public void setShutdownAllowed(boolean allowShutdown) {
        this.shutdownAllowed = allowShutdown;
    }

    /**
     * Sets the wrapped JDK executor service.
     * 
     * @param wrapped
     *            The wrapped JDK executor service.
     */
    private void setWrapped(ScheduledExecutorService wrapped) {
        this.wrapped = wrapped;
    }

    /**
     * Initiates an orderly shutdown in which previously submitted tasks are
     * executed, but no new tasks will be accepted.
     */
    public void shutdown() {
        if (isShutdownAllowed() && (getWrapped() != null)) {
            getWrapped().shutdown();
        }
    }

    /**
     * Attempts to stop all actively executing tasks, halts the processing of
     * waiting tasks, and returns a list of the tasks that were awaiting
     * execution.
     * 
     * @return The list of tasks that never commenced execution;
     */
    public List<Runnable> shutdownNow() {
        return isShutdownAllowed() && (getWrapped() != null) ? getWrapped()
                .shutdownNow() : Collections.<Runnable> emptyList();
    }

    @Override
    public synchronized void start() throws Exception {
        if ((getWrapped() == null) || getWrapped().isShutdown()) {
            setWrapped(wrap(createExecutorService(getCorePoolSize())));
        }

        super.start();
    }

    /**
     * Starts the task service if needed.
     */
    private void startIfNeeded() {
        if (!isStarted()) {
            try {
                start();
            } catch (Exception e) {
                Context.getCurrentLogger().log(Level.WARNING,
                        "Unable to start the task service", e);
            }
        }
    }

    @Override
    public synchronized void stop() throws Exception {
        super.stop();

        if ((getWrapped() != null) && !getWrapped().isShutdown()) {
            getWrapped().shutdown();
        }
    }

    /**
     * Submits a value-returning task for execution and returns a Future
     * representing the pending results of the task.
     * 
     * @param task
     *            The task to submit.
     * @return A Future representing pending completion of the task, and whose
     *         get() method will return the given result upon completion.
     */
    public <T> Future<T> submit(Callable<T> task) {
        startIfNeeded();
        return getWrapped().submit(task);
    }

    /**
     * 
     * @param task
     *            The task to submit.
     * @return A Future representing pending completion of the task, and whose
     *         get() method will return the given result upon completion.
     */
    public Future<?> submit(Runnable task) {
        startIfNeeded();
        return getWrapped().submit(task);
    }

    /**
     * 
     * @param task
     *            The task to submit.
     * @param result
     *            The result to return.
     * @return A Future representing pending completion of the task, and whose
     *         get() method will return the given result upon completion.
     */
    public <T> Future<T> submit(Runnable task, T result) {
        startIfNeeded();
        return getWrapped().submit(task, result);
    }

}
