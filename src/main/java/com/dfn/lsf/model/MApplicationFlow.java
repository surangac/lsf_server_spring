package com.dfn.lsf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by surangac on 5/5/2015.
 */
public class MApplicationFlow {
    private List<Status> appLevels=null;
    private List<Status> appRejectStatus=null;
    private static MApplicationFlow _instance=null;
    private MApplicationFlow(){
        if(appLevels==null){
            appLevels=new ArrayList<Status>();
        }
        if(appRejectStatus==null){
            appRejectStatus=new ArrayList<Status>();
        }
        addSteps();
        addRejectStatus();
    }
    private void addSteps(){
        Status st0 =new Status();
        st0.setLevelId(1);
        st0.setStatusId(OverrallApprovalStatus.PENDING.statusCode());
        st0.setStatusDescription(OverrallApprovalStatus.PENDING.statusDescription());
        appLevels.add(st0);
        Status st1=new Status();
        st1.setLevelId(2);
        st1.setStatusId(OverrallApprovalStatus.READYFORAPPROVAL.statusCode());
        st1.setStatusDescription(OverrallApprovalStatus.READYFORAPPROVAL.statusDescription());
        appLevels.add(st1);
        Status st2=new Status();
        st2.setLevelId(3);
        st2.setStatusId(OverrallApprovalStatus.LEVEL1APPROVED.statusCode());
        st2.setStatusDescription(OverrallApprovalStatus.LEVEL1APPROVED.statusDescription());
        appLevels.add(st2);
        Status st3=new Status();
        st3.setLevelId(4);
        st3.setStatusId(OverrallApprovalStatus.LEVEL2APPROVED.statusCode());
        st3.setStatusDescription(OverrallApprovalStatus.LEVEL2APPROVED.statusDescription());
        st3.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appLevels.add(st3);
        Status st4=new Status();
        st4.setLevelId(5);
        st4.setStatusId(OverrallApprovalStatus.FAL_APPROVED.statusCode());
        st4.setStatusDescription(OverrallApprovalStatus.FAL_APPROVED.statusDescription());
        appLevels.add(st4);
        Status st5=new Status();
        st5.setLevelId(6);
        st5.setStatusId(OverrallApprovalStatus.FAL_APPROVED_LEVEL1.statusCode());
        st5.setStatusDescription(OverrallApprovalStatus.FAL_APPROVED_LEVEL1.statusDescription());
        appLevels.add(st5);
        Status st6=new Status();
        st6.setLevelId(7);
        st6.setStatusId(OverrallApprovalStatus.FAL_APPROVED_LEVEL2.statusCode());
        st6.setStatusDescription(OverrallApprovalStatus.FAL_APPROVED_LEVEL2.statusDescription());
        appLevels.add(st6);
        Status st7=new Status();
        st7.setLevelId(8);
        st7.setStatusId(OverrallApprovalStatus.GENERATE_IOF.statusCode());
        st7.setStatusDescription(OverrallApprovalStatus.GENERATE_IOF.statusDescription());
        appLevels.add(st7);
        Status st8=new Status();
        st8.setLevelId(9);
        st8.setStatusId(OverrallApprovalStatus.IOF_ACCEPTEDLVL1.statusCode());
        st8.setStatusDescription(OverrallApprovalStatus.IOF_ACCEPTEDLVL1.statusDescription());
        appLevels.add(st8);
        Status st9=new Status();
        st9.setLevelId(10);
        st9.setStatusId(OverrallApprovalStatus.IOF_ACCEPTEDLVL2.statusCode());
        st9.setStatusDescription(OverrallApprovalStatus.IOF_ACCEPTEDLVL2.statusDescription());
        st9.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appLevels.add(st9);
        Status st10=new Status();
        st10.setLevelId(11);
        st10.setStatusId(OverrallApprovalStatus.SUBMIT_COLLATERAL.statusCode());
        st10.setStatusDescription(OverrallApprovalStatus.SUBMIT_COLLATERAL.statusDescription());
        appLevels.add(st10);
        Status st11=new Status();
        st11.setLevelId(12);
        st11.setStatusId(OverrallApprovalStatus.PURCHASE_ORDER_SUBMIT.statusCode());
        st11.setStatusDescription(OverrallApprovalStatus.PURCHASE_ORDER_SUBMIT.statusDescription());
        appLevels.add(st11);
        Status st12=new Status();
        st12.setLevelId(13);
        st12.setStatusId(OverrallApprovalStatus.WAITING_FOR_CUSTOMER_CONFIRMATION.statusCode());
        st12.setStatusDescription(OverrallApprovalStatus.WAITING_FOR_CUSTOMER_CONFIRMATION.statusDescription());
        appLevels.add(st12);
        Status st13=new Status();
        st13.setLevelId(14);
        st13.setStatusId(OverrallApprovalStatus.CUSTOMER_FINAL_CONFIRMATION_GRANTED.statusCode());
        st13.setStatusDescription(OverrallApprovalStatus.CUSTOMER_FINAL_CONFIRMATION_GRANTED.statusDescription());
        appLevels.add(st13);
        Status st20=new Status();
        st20.setLevelId(20);
        st20.setStatusId(OverrallApprovalStatus.COMPLETED.statusCode());
        st20.setStatusDescription(OverrallApprovalStatus.COMPLETED.statusDescription());
        appLevels.add(st20);
    }
    private void addRejectStatus(){
        Status st1=new Status();
        st1.setLevelId(3);
        st1.setStatusId(OverrallApprovalStatus.LEVEL1REJECT.statusCode());
        st1.setStatusDescription(OverrallApprovalStatus.LEVEL1REJECT.statusDescription());
        st1.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st1);
        Status st2=new Status();
        st2.setLevelId(4);
        st2.setStatusId(OverrallApprovalStatus.LEVEL2REJECT.statusCode());
        st2.setStatusDescription(OverrallApprovalStatus.LEVEL2REJECT.statusDescription());
        st2.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st2);
        Status st3=new Status();
        st3.setLevelId(5);
        st3.setStatusId(OverrallApprovalStatus.FAL_REJECTED.statusCode());
        st3.setStatusDescription(OverrallApprovalStatus.FAL_REJECTED.statusDescription());
        st3.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st3);
        Status st4=new Status();
        st4.setLevelId(6);
        st4.setStatusId(OverrallApprovalStatus.FAL_REJECTED_LEVEL1.statusCode());
        st4.setStatusDescription(OverrallApprovalStatus.FAL_REJECTED_LEVEL1.statusDescription());
        appRejectStatus.add(st4);
        Status st5=new Status();
        st5.setLevelId(7);
        st5.setStatusId(OverrallApprovalStatus.FAL_REJECTED_LEVEL2.statusCode());
        st5.setStatusDescription(OverrallApprovalStatus.FAL_REJECTED_LEVEL2.statusDescription());
        appRejectStatus.add(st5);
        Status st6=new Status();
        st6.setLevelId(9);
        st6.setStatusId(OverrallApprovalStatus.IOF_REJECTEDLVL1.statusCode());
        st6.setStatusDescription(OverrallApprovalStatus.IOF_REJECTEDLVL1.statusDescription());
        st6.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st6);
        Status st7=new Status();
        st7.setLevelId(10);
        st7.setStatusId(OverrallApprovalStatus.IOF_REJECTEDLVL2.statusCode());
        st7.setStatusDescription(OverrallApprovalStatus.IOF_REJECTEDLVL2.statusDescription());
        st7.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st7);
        Status st8=new Status();
        st8.setLevelId(14);
        st8.setStatusId(OverrallApprovalStatus.CUSTOMER_FINAL_CONFIRMATION_REJECTED.statusCode());
        st8.setStatusDescription(OverrallApprovalStatus.CUSTOMER_FINAL_CONFIRMATION_REJECTED.statusDescription());
        st8.setNotificationType(NotificationConstants.NOTIFICATION_TYPE_ALL);
        appRejectStatus.add(st8);

    }
    public List<Status> getApplicationFlow(){
        return this.appLevels;
    }
    public Status getNextStep(int levelId){
        Status s=null;
        int nextLevel=levelId+1;
        for(Status st:this.appLevels){
            if(st.getLevelId()==nextLevel){
                s=st;
            }
        }
        return s;
    }
    public Status getRejectStatus(int lavelid){
        int nextLevel=lavelid+1;
        Status s=null;
        for(Status st:this.appRejectStatus){
            if(st.getLevelId()==nextLevel){
                s=st;
            }
        }
        return s;
    }
    public static MApplicationFlow getInstance(){
        if(_instance==null){
            _instance=new MApplicationFlow();
        }
        return _instance;
    }
}
