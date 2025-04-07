package com.dfn.lsf.model;

import java.util.ArrayList;
import java.util.List;

import com.dfn.lsf.util.OverrallApprovalStatus;
/**
 * Created by surangac on 5/20/2015.
 */
public class LiquidityStatus {
    private List<LiquidityType> liquidTypes;
    private static LiquidityStatus _instance=null;
    private LiquidityStatus(){
        if(liquidTypes==null){
            liquidTypes=new ArrayList<LiquidityType>();
        }
        addLiquidTypes();
    }
    private void addLiquidTypes(){
        LiquidityType t1=new LiquidityType();
        t1.setLiquidId(OverrallApprovalStatus.LIQUID.statusCode());
        t1.setLiquidName(OverrallApprovalStatus.LIQUID.statusDescription());
        liquidTypes.add(t1);
        LiquidityType t2=new LiquidityType();
        t2.setLiquidId(OverrallApprovalStatus.SEMI_LIQUID.statusCode());
        t2.setLiquidName(OverrallApprovalStatus.SEMI_LIQUID.statusDescription());
        liquidTypes.add(t2);
        LiquidityType t3=new LiquidityType();
        t3.setLiquidId(OverrallApprovalStatus.NON_LIQUID.statusCode());
        t3.setLiquidName(OverrallApprovalStatus.NON_LIQUID.statusDescription());
        liquidTypes.add(t3);
    }
    public List<LiquidityType> getLiquidTypes(){
        return  this.liquidTypes;
    }
    public boolean isValidLiquidType(int id){
        return liquidTypes.contains(id);
    }
    public static LiquidityStatus getInstance(){
        if(_instance==null){
            _instance=new LiquidityStatus();
        }
        return _instance;
    }
}
