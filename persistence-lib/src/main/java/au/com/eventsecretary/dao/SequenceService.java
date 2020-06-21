package au.com.eventsecretary.dao;

import au.com.eventsecretary.persistence.BusinessObjectPersistence;
import org.springframework.stereotype.Component;

/**
 * Generates the next sequence number for a specific name.
 *
 * @author Warwick Slade
 */
@Component
public class SequenceService {

    private final BusinessObjectPersistence persistence;

    public SequenceService(BusinessObjectPersistence persistence
            ) {
        this.persistence = persistence;
    }

    public static class Sequence {
        private String id;
        private int sequence;
        private String name;

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public void reset() {
        Sequence searchSequence = new Sequence();
        persistence.deleteObject(searchSequence);
    }

    public synchronized String deriveNext(String name) {
        Sequence searchSequence = new Sequence();
        searchSequence.setName(name);
        Sequence currentSequence = persistence.findObject(searchSequence);
        if (currentSequence == null) {
            currentSequence = new Sequence();
            currentSequence.setName(name);
            currentSequence.setSequence((name.contains(".") ? 1 : 16290));
        } else {
            currentSequence.setSequence(currentSequence.getSequence() + 1);
        }

        persistence.storeObject(currentSequence);
        String format = name.contains(".") ? "%03d" : "%05d";
        return String.format(format, currentSequence.getSequence());
    }
}
