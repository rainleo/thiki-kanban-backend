package org.thiki.kanban.cardTags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xubt on 11/14/16.
 */
@Service
public class CardTagsService {
    public static Logger logger = LoggerFactory.getLogger(CardTagsService.class);

    @Resource
    private CardTagPersistence cardTagPersistence;

    @CacheEvict(value = "card-tag", key = "contains('#boardId')", allEntries = true)
    public List<CardTag> stickTags(List<CardTag> cardTags, String cardId, String boardId, String userName) {
        logger.info("Stick tags to card.tags:{},cardId:{},boardId:{}", cardTags, cardId, boardId);
        cardTagPersistence.removeTagsByCardId(cardId);
        for (CardTag cardTag : cardTags) {
            cardTagPersistence.stick(cardId, cardTag, userName);
        }
        List<CardTag> tags = cardTagPersistence.findByCardId(cardId);
        logger.info("Stick tags:{}", tags);
        return tags;
    }

    public List<CardTag> loadTags(String cardId, String boardId, String procedureId) {
        logger.info("Loading card tags.cardId:{},boardId:{},procedureId{}", cardId, boardId, procedureId);
        return cardTagPersistence.findByCardId(cardId);
    }
}
