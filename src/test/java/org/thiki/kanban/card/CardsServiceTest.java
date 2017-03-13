package org.thiki.kanban.card;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.thiki.kanban.acceptanceCriteria.AcceptanceCriteriaCodes;
import org.thiki.kanban.acceptanceCriteria.AcceptanceCriteriaService;
import org.thiki.kanban.activity.ActivityService;
import org.thiki.kanban.foundation.exception.BusinessException;
import org.thiki.kanban.stage.Stage;
import org.thiki.kanban.stage.StageCodes;
import org.thiki.kanban.stage.StagesService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Created by xubt on 13/03/2017.
 */
public class CardsServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @InjectMocks
    private CardsService cardsService;
    @Mock
    private StagesService stagesService;
    @Mock
    private CardsPersistence cardsPersistence;
    @Mock
    private AcceptanceCriteriaService acceptanceCriteriaService;
    @Mock
    private ActivityService activityService;

    private String originStageId = "stage-originId";
    private String targetStageId = "stage-targetId";
    private String cardId = "card-fooId";
    private String boardId = "board-fooId";
    private String userName = "someone";
    private Card originCard = new Card(), newCard = new Card();


    @Before
    public void setUp() {
        expectedException.expect(BusinessException.class);
        originCard.setId(cardId);
        newCard.setId(cardId);
        originCard.setStageId(originStageId);
        newCard.setStageId(targetStageId);
        Stage originStage = new Stage(), targetStage = new Stage();
        originStage.setId(originStageId);
        targetStage.setId(targetStageId);
        targetStage.setStatus(StageCodes.STAGE_STATUS_DONE);

        when(cardsPersistence.findById(eq(cardId))).thenReturn(originCard);
        when(stagesService.findById(eq(originStageId))).thenReturn(originStage);
        when(stagesService.findById(eq(targetStageId))).thenReturn(targetStage);
        when(acceptanceCriteriaService.isHasAcceptanceCriterias(eq(cardId))).thenReturn(true);
        when(acceptanceCriteriaService.isAllAcceptanceCriteriasCompleted(eq(cardId))).thenReturn(true);
        doNothing().when(activityService).recordCardModification(any(), any(), any(), any(), any());
    }

    @Test
    public void should_failed_if_unverified_acceptance_criteria_exist() {
        expectedException.expectMessage(CardsCodes.UNVERIFIED_ACCEPTANCE_CRITERIA_EXISTS.message());

        when(acceptanceCriteriaService.isExistSpecifiedPassedStatusAcceptanceCriteria(eq(cardId), eq(AcceptanceCriteriaCodes.STATUS_UNVERIFIED))).thenReturn(true);

        cardsService.modify(cardId, newCard, originStageId, boardId, userName);
    }

    @Test
    public void should_failed_if_unPassed_acceptance_criteria_exist() {
        expectedException.expectMessage(CardsCodes.UNPASSED_ACCEPTANCE_CRITERIA_EXISTS.message());

        when(acceptanceCriteriaService.isExistSpecifiedPassedStatusAcceptanceCriteria(eq(cardId), eq(AcceptanceCriteriaCodes.STATUS_UNVERIFIED))).thenReturn(false);
        when(acceptanceCriteriaService.isExistSpecifiedPassedStatusAcceptanceCriteria(eq(cardId), eq(AcceptanceCriteriaCodes.STATUS_UNPASSED))).thenReturn(true);

        cardsService.modify(cardId, newCard, originStageId, boardId, userName);
    }
}