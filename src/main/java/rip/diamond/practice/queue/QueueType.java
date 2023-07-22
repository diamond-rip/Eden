package rip.diamond.practice.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import rip.diamond.practice.config.Language;

@Getter
@AllArgsConstructor
public enum QueueType {

    UNRANKED(Language.QUEUE_TYPE_UNRANKED.toString()),
    RANKED(Language.QUEUE_TYPE_RANKED.toString()),
    ;

    private final String readable;

}
