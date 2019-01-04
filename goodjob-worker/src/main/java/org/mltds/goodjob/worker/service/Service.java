package org.mltds.goodjob.worker.service;

/**
 * @author sunyi 2018/11/7.
 */
public interface Service {

    void execute();

    void observe();

    void terminate();

    void test();

}