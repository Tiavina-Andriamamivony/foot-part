package org.prog3.foot.models;

import lombok.Data;

import static org.prog3.foot.models.SeasonStatus.NOT_STARTED;

@Data
public class Season {
    private SeasonStatus status;
    private Integer year;
    private String alias;

    //Méthode de vérification de la transition du status
    private boolean transitionIsOkay(SeasonStatus pretendingStatus) {

        return  pretendingStatus.ordinal() - this.status.ordinal() == 1;
    }

    public String transitionStatus(SeasonStatus pretendingStatus) {
        if (transitionIsOkay(pretendingStatus)) {
            setStatus(pretendingStatus);
            return "Transition OK and done";
        }
        return "Transition NOT done, error in order of transition";
    }

}
