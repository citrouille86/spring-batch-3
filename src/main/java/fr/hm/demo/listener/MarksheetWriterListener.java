package fr.hm.demo.listener;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import fr.hm.demo.dto.Marksheet;

@Component
public class MarksheetWriterListener implements ItemWriteListener<Marksheet> {

    private static final Logger log = LoggerFactory.getLogger(MarksheetWriterListener.class);

    @Override
    public void beforeWrite(List<? extends Marksheet> items) {
        log.info("!!! Item will be write !!!");
    }

    @Override
    public void afterWrite(List<? extends Marksheet> items) {
        log.info("!!! Item has been write !!!");
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Marksheet> items) {
        log.error("!!! Writing error occured for these items !!!");
        items.stream().map(Object::toString).collect(Collectors.joining(";"));
    }

}
