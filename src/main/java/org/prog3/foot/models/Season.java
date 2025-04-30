package org.prog3.foot.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper=true)
@Data
public class Season extends CreateSeason implements Serializable {
    private SeasonStatus status;
    private String id;

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
