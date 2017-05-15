package com.envista.msi.api.web.rest.dto.invoicing;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by KRISHNAREDDYM on 5/15/2017.
 */
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "WeekEndDto.getAll", procedureName = "SHP_INV_GET_WEEK_END_INFO_PRO",
                resultClasses = WeekEndDto.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, name = "P_REFCUR_WEEK_END_INFO", type = Void.class)
                })
})

@Entity
public class WeekEndDto implements Serializable {

    @Id
    @Column(name = "WEEK_END_ID")
    private Long id;

    @Column(name = "WEEK_END_DATE")
    private String weekEndDate;

    @Column(name = "STATUS_ID")
    private Long statusId;

    @Column(name = "CLOSE_DATE")
    private String closeDate;

    @Column(name = "CLOSED_BY")
    private String closedBy;

    public WeekEndDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeekEndDate() {
        return weekEndDate;
    }

    public void setWeekEndDate(String weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
    }

    public String getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }
}
