package com.wiki.framework.mybatis.integrate;

import com.wiki.framework.common.mapping.Mapper;
import com.wiki.framework.common.util.lambda.SFunction;
import com.wiki.framework.mybatis.Application;
import com.wiki.framework.mybatis.mapper.SubjectMapper;
import com.wiki.framework.mybatis.mapper.SubjectVMapper;
import com.wiki.framework.mybatis.po.Subject;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import org.apache.logging.log4j.message.FormattedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class BaseMapperTest {

	private Logger logger = LoggerFactory.getLogger("test");

	@Autowired
	private SubjectVMapper subjectVMapper;

	@Autowired
	private SubjectMapper subjectMapper;

	@Test
	public void testOne() {
		Subject subject = new Subject();
		subject.setName("ggg");
		subject.setAge(3);

		int inserted = subjectMapper.insert(subject);
		logger.info("insert {}", inserted);

	}

	@Test
	public void batchInsert() {
		ArrayList<Subject> subjects = new ArrayList<>();
		Subject subject = new Subject();
		subject.setName("kkk");
		subject.setAge(1);
		subjects.add(subject);

		subject = new Subject();
		subject.setName("kkk");
		subject.setAge(2);
		subjects.add(subject);

		int inserted = subjectMapper.batchInsert(subjects);
		logger.info("inserted {}", inserted);
	}


	@Test
	public void testFind() {
		List<Subject> ggg = subjectMapper.findByProperty(Subject::getName, "ggg");
		for (Subject subject : ggg) {
			logger.info("{}", subject);
		}
	}

	@Test
	public void testUpdate() {
		Subject ggg = subjectMapper.findOne(Subject::getName, "ggg");
		int delete = subjectMapper.delete(ggg.getId());
//		ggg.setIsDeleted(1);
//		int updated = subjectMapper.update(ggg);
		logger.info("{}", delete);
	}

	@Test
	public void testFindTwo() {
		Criteria c = Criteria.create()
				.and(Subject::getName, Operator.equal, "ggg")
				.and(Subject::getAge, Operator.equal, "2");

		List<Subject> ggg = subjectMapper.findByCriteria(c);
		for (Subject subject : ggg) {
			logger.info("{}", subject);
		}
	}

	@Test
	public void updateOne() {
		Subject s = subjectMapper.get("ff8081816db9a40c016db9a40c3c0000");
		s.setName("jjj_2");
		s.setVersion(1L);
		int update = subjectMapper.update(s);
		logger.info("{}", update);
	}
}
