package org.lostfan.ktv.view.report;

import org.lostfan.ktv.domain.Entity;
import org.lostfan.ktv.model.TurnoverReportModel;

public class CreditReportView extends TurnoverReportView {

    public CreditReportView(TurnoverReportModel model) {
        super(model);
    }

    public CreditReportView(TurnoverReportModel model, Entity entity) {
        super(model, entity);
    }
}
