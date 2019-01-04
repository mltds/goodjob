package org.mltds.goodjob.trigger.dao.test;

import org.mltds.goodjob.trigger.dao.JobSnapshotDAO;
import org.mltds.goodjob.common.dataobject.dataobject.JobSnapshot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

/**
 * @author sunyi
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring/goodjob-dao-test.xml"})
public class JobSnapshotDAOTest {

	@Autowired
	public JobSnapshotDAO dao;

	@Test
	public void mainTest() {
		Assert.notNull(dao);
//
//		JobSnapshotQuery param = new JobSnapshotQuery();
//		param.setGroup("TEST");
//		param.setStatus(JobSnapshotStatusEnum.COMPLETED);
//		param.setRows(10);
//		List<JobSnapshot> byParam = dao.findByParam(param);
//
//		System.out.println(byParam.size());

		JobSnapshot jobSnapshot = new JobSnapshot();
		jobSnapshot.setId(2016114L);
		jobSnapshot.setDetail("123123\n");

		int i = dao.updateByIdAndConcatDetail(jobSnapshot);
		System.out.println(i);


	}
}
