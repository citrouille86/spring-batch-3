package fr.nchantreau.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import fr.nchantreau.dto.Marksheet;
import fr.nchantreau.dto.Student;

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
