package fr.hm.demo.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import fr.hm.demo.dto.Marksheet;
import fr.hm.demo.dto.Student;

public class StudentItemProcessor implements ItemProcessor<Student, Marksheet> {

    private static final Logger log = LoggerFactory.getLogger(StudentItemProcessor.class);

    @Override
    public Marksheet process(final Student student) throws Exception {
        // Sum marks
        int totalMark = student.getSubMarkOne() + student.getSubMarkTwo();
        log.info("student id:" + student.getStdId() + " and Total mark:" + totalMark);
        Marksheet marksheet = new Marksheet(student.getStdId(), totalMark);
        return marksheet;
    }

}
